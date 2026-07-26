# WW-5474 — `struts.multipart.maxFiles` counts files only, plus new `maxParameterCount`

- **Ticket:** [WW-5474](https://issues.apache.org/jira/browse/WW-5474) — *struts.multipart.maxFiles does not work as described/expected*
- **Type:** Bug
- **Fix version:** 7.3.0
- **Date:** 2026-07-22

## 1. Problem

`struts.multipart.maxFiles` (default `256`) is documented as a cap on the **number of uploaded files**. In practice it does not behave that way, and the two Jakarta parsers disagree:

- **`jakarta` parser (`JakartaMultiPartRequest`, the default)** passes the value to commons-fileupload2 `setMaxFileCount(maxFiles)`. In commons-fileupload2 `2.0.0-M5`, `AbstractFileUpload.parseRequest(RequestContext)` throws `FileUploadFileCountLimitException` when `itemList.size() == maxFileCount`, and `itemList` holds **every** part — form fields *and* files. So `maxFiles` actually caps the **total number of parameters**, firing spuriously on forms with many normal fields and few (or zero) files. This is the exact defect reported in WW-5474.

- **`jakarta-stream` parser (`JakartaStreamMultiPartRequest`)** calls `getItemIterator()`. In `2.0.0-M5` the streaming iterator (`FileItemInputIteratorImpl`) enforces only `sizeMax` and `fileSizeMax` — it **ignores `maxFileCount` entirely**. The class instead has its own `exceedsMaxFiles()` which compares `maxFiles` against `uploadedFiles.size()` — the number of **distinct field names**, not the number of files. Multiple files sharing one field name collapse to one, so this both under-counts and diverges from the `jakarta` parser.

Net result: neither parser matches the documented "maximum number of files," and the two parsers behave differently from each other.

### Side effect being preserved

Today the `jakarta` parser's total-part cap of 256 *accidentally* guards against a parameter-count flooding DoS. Making `maxFiles` count files only would remove that incidental guard. `maxSize` (default 2 MB) bounds total bytes but not the number of tiny parts, so we replace the incidental guard with an explicit one (see §3).

## 2. Goal

1. `struts.multipart.maxFiles` counts **file parts only**, identically in both parsers, matching the documentation.
2. Add `struts.multipart.maxParameterCount` (default `256`) counting **non-file form fields only**, restoring explicit DoS protection.
3. The two limits are orthogonal: a request may carry up to `maxFiles` files **and** up to `maxParameterCount` form fields.
4. Exceeding **either** limit is **fail-closed**: the request is rejected with a recorded upload error and the action receives **no partial data**.

Out of scope: the deprecated `cos` parser (untouched); the Struts website documentation (lives in the separate `struts-site` repo — only in-repo config comments, JavaDoc, and message bundles are updated here).

## 3. New configuration

- **Constant:** `StrutsConstants.STRUTS_MULTIPART_MAX_PARAMETER_COUNT = "struts.multipart.maxParameterCount"`.
- **Default:** `default.properties` → `struts.multipart.maxParameterCount=256`. Update the existing `struts.multipart.maxFiles` comment to state it limits *files only*.
- **Injection:** new field `protected Long maxParameterCount` on `AbstractMultiPartRequest` with an `@Inject(StrutsConstants.STRUTS_MULTIPART_MAX_PARAMETER_COUNT)` setter, following the existing `maxFiles` setter pattern.

| Setting | Counts | Default |
|---|---|---|
| `struts.multipart.maxFiles` | file parts only | 256 |
| `struts.multipart.maxParameterCount` (new) | non-file form fields only | 256 |

## 4. Shared enforcement (`AbstractMultiPartRequest`)

Two helpers, called **before** accepting each item, used by both parsers so behavior is identical:

- `enforceMaxFiles(int currentFileCount, String fileName)` — throws `FileUploadFileCountLimitException` (commons) when accepting one more file would exceed `maxFiles`.
- `enforceMaxParameterCount(int currentParameterCount, String fieldName)` — throws a **new** `FileUploadParameterCountLimitException` when accepting one more field would exceed `maxParameterCount`.

Both exceptions extend `FileUploadException` (which `extends IOException`), so a breach unwinds the parse loop and is caught by the existing `parse()` handler. Each helper is a no-op when its limit is unset (`null`).

### New exception

`org.apache.struts2.dispatcher.multipart.FileUploadParameterCountLimitException extends FileUploadException`, carrying `permitted` and `actual` counts (mirroring `FileUploadFileCountLimitException`'s shape) for the localized message args.

## 5. Fail-closed handling in `parse()`

`AbstractMultiPartRequest.parse()` already catches `FileUploadException` and records a `LocalizedMessage`. Changes:

1. **Discard partial data on any abort:** clear `parameters` and `uploadedFiles` in the `FileUploadException` path so the action sees only the upload error, never a partially-populated request. Temp files are reclaimed by the existing `cleanUp()` (items are tracked *before* the limit check — see §6). This tightens *all* abort paths (`maxSize`, `maxFileSize`, the new limits); today those maps happen to be empty on abort, so it is a safe hardening rather than a behavior change for existing limits.
2. **Message wiring:** add a branch mapping `FileUploadParameterCountLimitException` to args `{permitted, actual}`, and add message key `struts.messages.upload.error.FileUploadParameterCountLimitException` to `struts-messages.properties` (matching the existing `FileUploadFileCountLimitException` entry).

## 6. Per-parser wiring

### `jakarta` (`JakartaMultiPartRequest`, non-streaming)

- Stop using `setMaxFileCount` as the *file* cap.
- Keep a cheap early-abort **backstop** against gross floods by setting commons `setMaxFileCount = maxFiles + maxParameterCount` (a total-parts ceiling — commons cannot distinguish categories). This aborts pathological requests before the full item list is materialized.
- In the `processUpload` loop, track each `DiskFileItem` for cleanup **first**, then maintain separate file and field counters and call `enforceMaxFiles` / `enforceMaxParameterCount` before processing the item. Precise, category-correct errors fire before the coarse backstop in all normal over-limit cases.

**Wrinkle (accepted):** a *gross* flood exceeding `maxFiles + maxParameterCount` combined surfaces the generic `FileUploadFileCountLimitException` with the combined total rather than a precise per-category message. Normal over-limit cases (e.g. the 257th file, or 257th field) still produce precise messages. This is an acceptable defense-in-depth tradeoff given the non-streaming parser cannot count per category before `parseRequest` returns.

### `jakarta-stream` (`JakartaStreamMultiPartRequest`)

- Remove the buggy `exceedsMaxFiles` (field-name counting).
- Maintain true file and field counters during iteration and call the shared `enforceMaxFiles` / `enforceMaxParameterCount` helpers. The streaming iterator aborts naturally and early on the throw.

## 7. Testing

JUnit 4 (`org.junit.Test`) — the multipart tests are plain JUnit 4, not `XWorkTestCase`, so new `@Test` methods run.

Add to **both** `JakartaMultiPartRequestTest` and `JakartaStreamMultiPartRequestTest`:

- Many form fields + few files (e.g. 300 fields, 2 files) with `maxFiles=256` now **passes** (regression for the reported bug).
- More than `maxFiles` files → fails with key `struts.messages.upload.error.FileUploadFileCountLimitException`.
- More than `maxParameterCount` fields → fails with key `struts.messages.upload.error.FileUploadParameterCountLimitException`.
- Multiple files under a single field name are counted individually (guards the stream-parser regression).
- On breach the request exposes no parameters and no files (fail-closed): `getParameterNames()` / `getFileParameterNames()` empty, error present.

Add to `AbstractMultiPartRequestTest`: the new setter parses and stores `maxParameterCount`; default resolves to 256.

## 8. Files touched

- `core/.../StrutsConstants.java` — new constant.
- `core/.../default.properties` — new default + corrected `maxFiles` comment.
- `core/.../struts-messages.properties` — new message key.
- `core/.../multipart/AbstractMultiPartRequest.java` — new field/setter, two `enforce…` helpers, fail-closed clearing, message arg mapping.
- `core/.../multipart/FileUploadParameterCountLimitException.java` — new exception.
- `core/.../multipart/JakartaMultiPartRequest.java` — counters + backstop wiring.
- `core/.../multipart/JakartaStreamMultiPartRequest.java` — replace `exceedsMaxFiles`, add counters.
- Tests: `JakartaMultiPartRequestTest`, `JakartaStreamMultiPartRequestTest`, `AbstractMultiPartRequestTest`.
