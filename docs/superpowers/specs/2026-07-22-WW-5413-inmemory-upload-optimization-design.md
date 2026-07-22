# WW-5413 — In-memory multipart upload optimization

**Jira:** [WW-5413](https://issues.apache.org/jira/browse/WW-5413) · **Fix version:** 7.3.0 · **Component:** Core
**Date:** 2026-07-22

## Background

WW-5413 was originally filed against 6.3.0: commons-io 2.16.0 broke `DeferredFileOutputStream`/`ThresholdingOutputStream`, cascading through commons-fileupload's `DiskFileItem` so multipart uploads were read as empty. A prior Struts workaround forced the disk-spill threshold to `-1`, spilling *every* field to disk. The ticket proposed dropping the `-1` threshold, handling the `isInMemory()` case properly, and avoiding unnecessary filesystem writes.

**The root-cause bug is already resolved on `main`.** Struts has since migrated to **commons-fileupload2 2.0.0-M5** + **commons-io 2.22.0**. `AbstractMultiPartRequest.createJakartaFileUpload()` sets no threshold, so it uses `DiskFileItemFactory`'s default (~8 KB); small uploads legitimately stay in memory (`isInMemory() == true`).

**What remains** is the performance half of the ticket. `JakartaMultiPartRequest.processFileField()` still takes every in-memory item and eagerly writes it to a temp file via `FileOutputStream`, then wraps that `File` in `StrutsUploadedFile`. The redundant filesystem write the ticket complained about is still present — it just moved from the fileupload layer into Struts' own code, because `UploadedFile`/`StrutsUploadedFile` are `File`-backed with no in-memory representation.

## Goal

Eliminate the redundant temp-file write for small (in-memory) uploads, while keeping 100% backward compatibility for existing `UploadedFile` consumers — including the legacy `File`-typed action property path and third-party `UploadedFile` implementations.

## Constraints & compatibility facts

- `UploadedFile` was designed for this: `isFile()` documents "real file or maybe just in-memory stream", `getContent()` returns `Object`, `getAbsolutePath()` is "if possible". `UploadedFile extends Serializable`.
- `getContent()` de-facto returns a `java.io.File` everywhere today: `UploadedFileConverter` (legacy `File`-typed action properties), `apps/showcase` actions (`FileUploadAction.getContent()`), and user actions in the wild. **The runtime type of `getContent()` must remain `File`** — a size-dependent `byte[]`/`File` return would break these consumers non-deterministically. This is why we do **not** expose bytes through `getContent()`.
- `AbstractMultiPartRequest.cleanUp()` deletes an uploaded file by calling `UploadedFile.delete()` **only when `isFile()` is true**. The lazy design plugs into this existing hook.
- The in-memory `DiskFileItem` buffer stays valid until `cleanUp()` runs (after action processing), so reading `item.get()` at parse time is safe.

## Approach: lazy materialization + a streaming accessor

Keep `getContent()`/`getAbsolutePath()` returning a `File` (materialized on demand), and add a new, correctly-typed door for reading bytes without forcing a disk write.

### 1. `UploadedFile` interface — new `getInputStream()`

Add one method as a **`default`** so existing third-party implementations keep compiling:

```java
default InputStream getInputStream() throws IOException {
    Object c = getContent();
    if (c instanceof File f)   return new FileInputStream(f);
    if (c instanceof byte[] b) return new ByteArrayInputStream(b);
    throw new IOException("No content stream available for " + getName());
}
```

This is the type-safe "give me the bytes without forcing a file" path. `getContent()` still returns a `File` for every implementation, so no existing consumer changes.

### 2. New `StrutsInMemoryUploadedFile`

A second `UploadedFile` implementation beside `StrutsUploadedFile` — each class stays single-purpose; `Struts*` naming per project convention. It holds:

- `byte[] content` — the small upload's bytes, from `item.get()`
- `Path saveDir` — where a temp file will be written if ever demanded
- a **stable temp-file name chosen at construction** (`upload_<uuid>.tmp`) — only the *write* is deferred, so `getName()` is stable and matches the eventual file
- metadata: `contentType`, `originalName`, `inputName`
- `transient File materializedFile` — populated lazily, cached

`byte[]` + `Path` keep the object `Serializable` (a `DiskFileItem` reference would not).

| Method | Behavior | Touches disk? |
|---|---|---|
| `getInputStream()` | `new ByteArrayInputStream(content)` | **No** |
| `length()` | `content.length` | No |
| `getName()` | the pre-chosen `upload_<uuid>.tmp` | No |
| `getContent()` | `materialize()` → returns the `File` | **Yes, once** (cached) |
| `getAbsolutePath()` | `materialize()` → path string | **Yes, once** (cached) |
| `isFile()` | true only *after* materialization | No |
| `getContentType()` / `getOriginalName()` / `getInputName()` | metadata | No |
| `delete()` | deletes the materialized file if it exists; no-op otherwise | — |

`materialize()` is `synchronized`, writes the bytes once to the pre-chosen path in `saveDir`, and caches the resulting `File`. The temp file is created with the project's secure UUID-named pattern (`upload_<uuid>.tmp`); the naming logic is shared with / extracted alongside `AbstractMultiPartRequest.createTemporaryFile` to avoid divergence.

**Net effect:** rejected uploads, size/type checks (`length()`), and `getInputStream()` consumers **never write**; legacy `File`/`getContent()` consumers write exactly once — same as today, but deferred.

### 3. `JakartaMultiPartRequest.processFileField` + cleanup simplification

The `item.isInMemory()` branch stops writing a temp file eagerly:

```java
if (item.isInMemory()) {
    values.add(StrutsInMemoryUploadedFile.Builder
        .create(item.get(), Path.of(saveDir))
        .withOriginalName(item.getName())
        .withContentType(item.getContentType())
        .withInputName(item.getFieldName())
        .build());
} else {
    // unchanged File-based path via item.getPath()
}
```

Because in-memory files no longer create temp files eagerly:

- the `temporaryFiles` field, `cleanUpTemporaryFiles()`, and the `FileOutputStream` write block in `JakartaMultiPartRequest` are removed;
- cleanup of any *materialized* file happens through the existing `AbstractMultiPartRequest.cleanUp()` loop, which already calls `delete()` when `isFile()` is true — no new cleanup path is added;
- `createTemporaryFile` remains in `AbstractMultiPartRequest` (`JakartaStreamMultiPartRequest` still uses it).

`StrutsUploadedFile` keeps its current `File`-backed behavior; it may override `getInputStream()` to return `new FileInputStream(file)` directly rather than relying on the interface default.

`JakartaStreamMultiPartRequest` is unchanged: it always streams to disk (no in-memory threshold) and stays `File`-backed via `StrutsUploadedFile`, inheriting `getInputStream()` for free.

### 4. Error-handling behavior change (explicit)

`getContent()`/`getAbsolutePath()` have no `throws` clause, so a materialization **write failure** surfaces as an unchecked `StrutsException` **during consumption**, rather than as a gracefully-collected `LocalizedMessage` at parse time (today's behavior). This affects only the rare "cannot write to `saveDir`" case, and only on the legacy `File`/`getContent()` path — the `getInputStream()` fast path never writes. Accepted as a reasonable trade for the optimization.

## Testing

**`StrutsInMemoryUploadedFile` unit tests**

- `getInputStream()` returns the exact bytes and creates **no** file on disk
- `getContent()` and `getAbsolutePath()` create the temp file **exactly once** and cache it (second call reuses the same `File`)
- `isFile()` is false before materialization, true after
- `length()` and metadata accessors do **not** materialize
- `delete()` removes a materialized file and is a safe no-op when nothing was materialized

**`JakartaMultiPartRequest` tests**

- a small (in-memory) upload leaves **no** temp file on disk after `parse()`, until `getContent()`/`getAbsolutePath()` is called
- content is readable via both `getInputStream()` and `getContent()`
- a large (on-disk) upload keeps the existing `File`-backed path unchanged
- legacy `File`-typed action-property conversion (`UploadedFileConverter`) still works for a small upload
- `cleanUp()` removes any materialized file and leaves nothing behind

Tests follow the existing multipart test base (core tests are JUnit4 / `XWorkTestCase`-style — an `@Test` added to a `TestCase` subclass silently never runs).

## Out of scope

- Migrating existing consumers (showcase actions, converter) onto `getInputStream()` — they continue using `getContent()`.
- Changing the disk-spill threshold or `JakartaStreamMultiPartRequest` streaming strategy.
- Any change to `getContent()`'s runtime type (must remain `File`).
