# WW-5474 — Files-only `maxFiles` + new `maxParameterCount` Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make `struts.multipart.maxFiles` count uploaded *files only* (identically in the `jakarta` and `jakarta-stream` parsers) and add `struts.multipart.maxParameterCount` to cap non-file form fields, failing closed on breach.

**Architecture:** Shared enforcement lives in `AbstractMultiPartRequest` as two throwing helpers (`enforceMaxFiles`, `enforceMaxParameterCount`) plus fail-closed cleanup in `parse()`. Each parser counts file parts and form-field parts separately and calls the helpers before accepting an item. The `jakarta` parser keeps a coarse commons-fileupload2 total-parts backstop (`maxFiles + maxParameterCount`) for early abort; the `jakarta-stream` parser aborts naturally during iteration.

**Tech Stack:** Java 17, commons-fileupload2 `2.0.0-M5` (core + jakarta-servlet6), JUnit 4 + AssertJ (multipart unit tests), JUnit 3 / XWorkTestCase (interceptor tests), Maven.

## Global Constraints

- Ticket prefix on every commit: `WW-5474 <type>(<scope>): <desc>`; end commit body with `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.
- New `.java` files MUST carry the ASF license header (copy verbatim from `JakartaMultiPartRequest.java` lines 1–18).
- New config constant value: `struts.multipart.maxParameterCount`; default `256`.
- New message key: `struts.messages.upload.error.FileUploadParameterCountLimitException`.
- Build/test command: `mvn test -DskipAssembly -pl core -Dtest=<ClassName>[#<method>]`.
- Semantics: `maxFiles` = number of file parts (parts with a non-empty filename); `maxParameterCount` = number of non-file form-field parts (each value counts). A limit that is `null` is not enforced.
- Fail-closed: on any limit breach, the request exposes **no** parameters and **no** files; only the recorded upload error remains.

---

### Task 1: `jakarta` parser — files-only `maxFiles` + new `maxParameterCount`

Adds all shared infrastructure and wires the default (`jakarta`) parser.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java` (near line 227, after `STRUTS_MULTIPART_MAX_FILES`)
- Modify: `core/src/main/resources/org/apache/struts2/default.properties:70-71`
- Modify: `core/src/main/resources/org/apache/struts2/struts-messages.properties` (after the `FileUploadFileCountLimitException` entry, ~line 65)
- Create: `core/src/main/java/org/apache/struts2/dispatcher/multipart/FileUploadParameterCountLimitException.java`
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequest.java`
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequest.java`
- Test: `core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequestTest.java`
- Test: `core/src/test/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequestTest.java`

**Interfaces:**
- Produces (consumed by Task 2):
  - `protected void AbstractMultiPartRequest.enforceMaxFiles(int currentFileCount, String fileName) throws FileUploadFileCountLimitException`
  - `protected void AbstractMultiPartRequest.enforceMaxParameterCount(int currentParameterCount, String fieldName) throws FileUploadParameterCountLimitException`
  - `public void AbstractMultiPartRequest.setMaxParameterCount(String)` injected from `struts.multipart.maxParameterCount`
  - `FileUploadParameterCountLimitException(String message, long permitted, long actual)` with `long getPermitted()` / `long getActual()`

- [ ] **Step 1: Add the constant**

In `StrutsConstants.java`, immediately after the `STRUTS_MULTIPART_MAX_FILES` declaration (line 227):

```java
    /**
     * The maximum number of non-file form fields (parameters) allowed in a multipart request.
     */
    public static final String STRUTS_MULTIPART_MAX_PARAMETER_COUNT = "struts.multipart.maxParameterCount";
```

- [ ] **Step 2: Add the default property + fix the maxFiles comment**

In `default.properties`, replace lines 69-71:

```properties
struts.multipart.maxSize=2097152
struts.multipart.maxFiles=256
struts.multipart.maxStringLength=4096
```

with:

```properties
struts.multipart.maxSize=2097152
# Maximum number of uploaded files (files only, not form fields)
struts.multipart.maxFiles=256
# Maximum number of non-file form fields (parameters)
struts.multipart.maxParameterCount=256
struts.multipart.maxStringLength=4096
```

- [ ] **Step 3: Add the message key**

In `struts-messages.properties`, after the `FileUploadFileCountLimitException` line (~line 64) add:

```properties
# FileUploadParameterCountLimitException
# 0 - limit
struts.messages.upload.error.FileUploadParameterCountLimitException=Request exceeded allowed number of parameters! Permitted number of parameters is: {0}!
```

- [ ] **Step 4: Create the new exception**

Create `FileUploadParameterCountLimitException.java` (with the ASF header copied from `JakartaMultiPartRequest.java` lines 1–18):

```java
package org.apache.struts2.dispatcher.multipart;

import org.apache.commons.fileupload2.core.FileUploadException;

/**
 * Thrown when a multipart request contains more non-file form fields (parameters)
 * than allowed by {@code struts.multipart.maxParameterCount}.
 */
public class FileUploadParameterCountLimitException extends FileUploadException {

    private final long permitted;
    private final long actual;

    public FileUploadParameterCountLimitException(final String message, final long permitted, final long actual) {
        super(message);
        this.permitted = permitted;
        this.actual = actual;
    }

    public long getPermitted() {
        return permitted;
    }

    public long getActual() {
        return actual;
    }
}
```

- [ ] **Step 5: Add the field, setter, and enforcement helpers to `AbstractMultiPartRequest`**

Add the import (with the other `fileupload2.core` imports near line 27):

```java
import org.apache.commons.fileupload2.core.FileUploadFileCountLimitException;
```

(`FileUploadFileCountLimitException` is already imported at line 27 — verify; if present, skip.)

Add a field after `maxFiles` (line 94):

```java
    /**
     * Specifies the maximum number of non-file form fields (parameters) in one request.
     */
    protected Long maxParameterCount;
```

Add a setter after `setMaxFiles` (after line 161):

```java
    /**
     * @param maxParameterCount Injects the Struts maximum number of non-file form fields.
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAX_PARAMETER_COUNT)
    public void setMaxParameterCount(String maxParameterCount) {
        this.maxParameterCount = Long.parseLong(maxParameterCount);
    }
```

Add the two helpers after `exceedsMaxStringLength` (after line 299):

```java
    /**
     * Fail-closed guard: throws when accepting another file would exceed {@link #maxFiles}.
     *
     * @param currentFileCount number of files already accepted in this request
     * @param fileName         name of the file being considered (for logging)
     */
    protected void enforceMaxFiles(int currentFileCount, String fileName) throws FileUploadFileCountLimitException {
        if (maxFiles != null && currentFileCount >= maxFiles) {
            LOG.debug("Cannot accept another file: {} as it would exceed max files: {}", normalizeSpace(fileName), maxFiles);
            throw new FileUploadFileCountLimitException(
                    String.format("Request exceeds allowed number of files, permitted: %s", maxFiles),
                    maxFiles, currentFileCount + 1L);
        }
    }

    /**
     * Fail-closed guard: throws when accepting another form field would exceed {@link #maxParameterCount}.
     *
     * @param currentParameterCount number of form fields already accepted in this request
     * @param fieldName             name of the field being considered (for logging)
     */
    protected void enforceMaxParameterCount(int currentParameterCount, String fieldName) throws FileUploadParameterCountLimitException {
        if (maxParameterCount != null && currentParameterCount >= maxParameterCount) {
            LOG.debug("Cannot accept another parameter: {} as it would exceed max parameter count: {}", normalizeSpace(fieldName), maxParameterCount);
            throw new FileUploadParameterCountLimitException(
                    String.format("Request exceeds allowed number of parameters, permitted: %s", maxParameterCount),
                    maxParameterCount, currentParameterCount + 1L);
        }
    }
```

- [ ] **Step 6: Fail-closed cleanup + message mapping in `parse()`**

In `AbstractMultiPartRequest.parse()` (lines 307-336), add a branch for the new exception and clear collected data on abort. Replace the `FileUploadContentTypeException` else-if block and the trailing message-building lines (lines 324-330) with:

```java
            } else if (e instanceof FileUploadContentTypeException ex) {
                exClass = ex.getClass();
                args = new Object[]{ex.getContentType()};
            } else if (e instanceof FileUploadParameterCountLimitException ex) {
                exClass = ex.getClass();
                args = new Object[]{ex.getPermitted(), ex.getActual()};
            }

            LocalizedMessage errorMessage = buildErrorMessage(exClass, e.getMessage(), args);
            addErrorIfAbsent(errorMessage);
            clearCollectedData();
```

Add the private helper after `addErrorIfAbsent` (after line 342). It deletes any partial upload files before clearing so the stream parser does not leak temp files:

```java
    /**
     * Fail-closed: discards everything collected so far so a rejected request exposes
     * no partial parameters or files to the action. Deletes partial upload files first
     * to avoid leaking temporary files.
     */
    private void clearCollectedData() {
        for (List<UploadedFile> files : uploadedFiles.values()) {
            for (UploadedFile file : files) {
                if (file.isFile() && !file.delete()) {
                    LOG.warn("Could not delete partial upload file: {}", file.getName());
                }
            }
        }
        uploadedFiles.clear();
        parameters.clear();
    }
```

- [ ] **Step 7: `jakarta` backstop in `prepareServletFileUpload`**

In `AbstractMultiPartRequest.prepareServletFileUpload` (lines 221-238), replace the `if (maxFiles != null) { ... setMaxFileCount(maxFiles); }` block (lines 229-232) with a coarse total-parts backstop that only constrains when both limits are set (otherwise leaving commons unlimited so it cannot re-introduce the all-parts bug):

```java
        if (maxFiles != null && maxParameterCount != null) {
            long maxParts = maxFiles + maxParameterCount;
            LOG.debug("Applies total parts backstop: {} to file upload request", maxParts);
            servletFileUpload.setMaxFileCount(maxParts);
        }
```

- [ ] **Step 8: Write the failing `jakarta` tests**

Add to `JakartaMultiPartRequestTest` (uses `formFile`, `formField`, `boundary`, `endline`, `tempDir`, `mockRequest`, `multiPart` from the base class):

```java
    @Test
    public void manyFormFieldsWithFewFilesAreAccepted() throws IOException {
        // Regression for WW-5474: maxFiles must not count form fields.
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            content.append(formField("field" + i, "value" + i));
        }
        content.append(formFile("file1", "test1.csv", "1,2,3,4"));
        content.append(formFile("file2", "test2.csv", "5,6,7,8"));
        content.append(endline).append("--").append(boundary).append("--");
        mockRequest.setContent(content.toString().getBytes(StandardCharsets.UTF_8));

        multiPart.setMaxFiles("2"); // only 2 files, but 10 fields present
        multiPart.parse(mockRequest, tempDir);

        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.getFileParameterNames().asIterator()).toIterable()
                .asInstanceOf(InstanceOfAssertFactories.LIST).containsOnly("file1", "file2");
    }

    @Test
    public void exceedsMaxFilesIsFailClosed() throws IOException {
        String content = formField("param1", "value1") +
                formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                endline + "--" + boundary + "--";
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        multiPart.setMaxFiles("1");
        multiPart.parse(mockRequest, tempDir);

        assertThat(multiPart.getErrors()).map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadFileCountLimitException");
        assertThat(multiPart.getFileParameterNames().asIterator()).toIterable().isEmpty();
        assertThat(multiPart.getParameterNames().asIterator()).toIterable().isEmpty();
    }

    @Test
    public void exceedsMaxParameterCountIsFailClosed() throws IOException {
        String content = formField("field1", "a") +
                formField("field2", "b") +
                formField("field3", "c") +
                endline + "--" + boundary + "--";
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        multiPart.setMaxParameterCount("2");
        multiPart.parse(mockRequest, tempDir);

        assertThat(multiPart.getErrors()).map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadParameterCountLimitException");
        assertThat(multiPart.getParameterNames().asIterator()).toIterable().isEmpty();
    }

    @Test
    public void multipleFilesUnderOneFieldNameAreCounted() throws IOException {
        String content = formFile("file", "a.csv", "1") +
                formFile("file", "b.csv", "2") +
                formFile("file", "c.csv", "3") +
                endline + "--" + boundary + "--";
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        multiPart.setMaxFiles("2"); // 3 files share one field name -> still 3 files
        multiPart.parse(mockRequest, tempDir);

        assertThat(multiPart.getErrors()).map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadFileCountLimitException");
    }
```

Ensure these imports exist in `JakartaMultiPartRequestTest` (add any missing): `org.assertj.core.api.InstanceOfAssertFactories`, `org.apache.struts2.dispatcher.LocalizedMessage`, `java.nio.charset.StandardCharsets`, `java.io.IOException`, `static org.assertj.core.api.Assertions.assertThat`.

Add to `AbstractMultiPartRequestTest` (runs for both parsers; verifies only the setter, safe before Task 2):

```java
    @Test
    public void maxParameterCountSetterStoresValue() {
        multiPart.setMaxParameterCount("42");
        assertThat(multiPart.maxParameterCount).isEqualTo(42L);
    }
```

- [ ] **Step 9: Run the new tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=JakartaMultiPartRequestTest#manyFormFieldsWithFewFilesAreAccepted+exceedsMaxParameterCountIsFailClosed`
Expected: compile error / FAIL (methods `setMaxParameterCount`, `FileUploadParameterCountLimitException` key not yet wired into the parser). If Steps 1–7 are already applied, `manyFormFieldsWithFewFilesAreAccepted` fails because the parser is not yet wired (Step 10).

- [ ] **Step 10: Wire `JakartaMultiPartRequest.processUpload`**

Replace the `for` loop body in `processUpload` (lines 118-132) with per-category counting:

```java
        int fileCount = 0;
        int parameterCount = 0;
        for (DiskFileItem item : servletFileUpload.parseRequest(requestContext)) {
            // Track all DiskFileItem instances for cleanup - this is critical for security
            // as it ensures temporary files are properly cleaned up even if processing fails
            diskFileItems.add(item);

            LOG.debug(() -> "Processing a form field: " + normalizeSpace(item.getFieldName()));
            if (item.isFormField()) {
                // Process regular form fields (text inputs, checkboxes, etc.)
                if (item.getFieldName() != null) {
                    enforceMaxParameterCount(parameterCount, item.getFieldName());
                    parameterCount++;
                }
                processNormalFormField(item, charset);
            } else {
                // Process file upload fields (only count parts that carry an actual file)
                LOG.debug(() -> "Processing a file: " + normalizeSpace(item.getFieldName()));
                if (item.getName() != null && !item.getName().trim().isEmpty()) {
                    enforceMaxFiles(fileCount, item.getName());
                    fileCount++;
                }
                processFileField(item, saveDir);
            }
        }
```

`processUpload` already declares `throws IOException`; the enforcement exceptions extend `FileUploadException extends IOException`, so no signature change is needed.

- [ ] **Step 11: Run the full jakarta + regression suites**

Run: `mvn test -DskipAssembly -pl core -Dtest=JakartaMultiPartRequestTest`
Expected: PASS (all four new tests + existing).

Run: `mvn test -DskipAssembly -pl core -Dtest=AbstractMultiPartRequestTest`
Expected: PASS (shared `maxFiles()` still yields exactly one `FileUploadFileCountLimitException`; new setter test passes for both subclasses).

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest`
Expected: PASS. In particular `testUnacceptedNumberOfFiles` (4 files, `maxFiles=3`) still reports null files + one action error `Request exceeded allowed number of files! Permitted number of files is: 3!`.

- [ ] **Step 12: Commit**

```bash
git add core/src/main/java/org/apache/struts2/StrutsConstants.java \
        core/src/main/resources/org/apache/struts2/default.properties \
        core/src/main/resources/org/apache/struts2/struts-messages.properties \
        core/src/main/java/org/apache/struts2/dispatcher/multipart/FileUploadParameterCountLimitException.java \
        core/src/main/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequest.java \
        core/src/main/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequest.java \
        core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequestTest.java \
        core/src/test/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequestTest.java
git commit -m "$(cat <<'EOF'
WW-5474 fix(multipart): count files only for maxFiles, add maxParameterCount (jakarta)

The jakarta parser passed maxFiles to commons-fileupload2 setMaxFileCount,
which counts every part (fields + files), so maxFiles wrongly limited total
parameters. Enforce a files-only count and non-file field count in Struts,
failing closed on breach; keep a total-parts commons backstop.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

### Task 2: `jakarta-stream` parser — same enforcement

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/multipart/JakartaStreamMultiPartRequest.java`
- Test: `core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaStreamMultiPartRequestTest.java`

**Interfaces:**
- Consumes (from Task 1): `enforceMaxFiles(int, String)`, `enforceMaxParameterCount(int, String)`, fail-closed `parse()` behavior, `FileUploadParameterCountLimitException`.

- [ ] **Step 1: Update the existing `exceedsMaxFilesPath` test to fail-closed**

In `JakartaStreamMultiPartRequestTest`, the current test asserts `uploadedFiles` retains 1 file after breach. Under fail-closed it retains none. Replace the assertions block (the `// then` section, currently `assertThat(multiPart.uploadedFiles).hasSize(1);` and the errors assertion) with:

```java
        // then - fail-closed: no partial files, one error
        assertThat(multiPart.uploadedFiles).isEmpty();
        assertThat(multiPart.getErrors())
                .map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadFileCountLimitException");
```

Add the import if missing: `import org.apache.struts2.dispatcher.LocalizedMessage;`

- [ ] **Step 2: Add the failing stream-specific tests**

Add to `JakartaStreamMultiPartRequestTest`:

```java
    @Test
    public void streamManyFormFieldsWithFewFilesAreAccepted() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            content.append(formField("field" + i, "value" + i));
        }
        content.append(formFile("file1", "test1.csv", "1,2,3,4"));
        content.append(endline).append("--").append(boundary).append("--");
        mockRequest.setContent(content.toString().getBytes(StandardCharsets.UTF_8));

        multiPart.setMaxFiles("1");
        multiPart.parse(mockRequest, tempDir);

        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.getFileParameterNames().asIterator()).toIterable()
                .asInstanceOf(InstanceOfAssertFactories.LIST).containsOnly("file1");
    }

    @Test
    public void streamMultipleFilesUnderOneFieldNameAreCounted() throws IOException {
        String content = formFile("file", "a.csv", "1") +
                formFile("file", "b.csv", "2") +
                formFile("file", "c.csv", "3") +
                endline + "--" + boundary + "--";
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        multiPart.setMaxFiles("2");
        multiPart.parse(mockRequest, tempDir);

        // Field-name counting bug would keep all 3 under one key; files-only counting rejects.
        assertThat(multiPart.uploadedFiles).isEmpty();
        assertThat(multiPart.getErrors()).map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadFileCountLimitException");
    }

    @Test
    public void streamExceedsMaxParameterCountIsFailClosed() throws IOException {
        String content = formField("field1", "a") +
                formField("field2", "b") +
                formField("field3", "c") +
                endline + "--" + boundary + "--";
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        multiPart.setMaxParameterCount("2");
        multiPart.parse(mockRequest, tempDir);

        assertThat(multiPart.getErrors()).map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadParameterCountLimitException");
        assertThat(multiPart.getParameterNames().asIterator()).toIterable().isEmpty();
    }
```

Ensure imports: `org.assertj.core.api.InstanceOfAssertFactories`, `java.nio.charset.StandardCharsets`, `java.io.IOException`, `static org.assertj.core.api.Assertions.assertThat`.

- [ ] **Step 3: Run to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=JakartaStreamMultiPartRequestTest#streamMultipleFilesUnderOneFieldNameAreCounted+streamExceedsMaxParameterCountIsFailClosed`
Expected: FAIL — the old `exceedsMaxFiles` counts field names (so `file`×3 passes) and there is no parameter-count enforcement yet.

- [ ] **Step 4: Rewire the stream parser**

In `JakartaStreamMultiPartRequest`, remove the entire `exceedsMaxFiles(FileItemInput)` method (lines 151-169) and the unused `FileUploadFileCountLimitException` import if it becomes unused (leave it if still referenced).

Replace `processUpload` (lines 63-81) so counters are tracked across items (instance fields, reset per parse):

```java
    private int fileCount;
    private int parameterCount;

    @Override
    protected void processUpload(HttpServletRequest request, String saveDir) throws IOException {
        Charset charset = readCharsetEncoding(request);
        Path location = Path.of(saveDir);
        fileCount = 0;
        parameterCount = 0;

        JakartaServletDiskFileUpload servletFileUpload =
                prepareServletFileUpload(charset, location);

        LOG.debug("Using Jakarta Stream API to process request");
        servletFileUpload.getItemIterator(request).forEachRemaining(item -> {
            if (item.isFormField()) {
                LOG.debug(() -> "Processing a form field: " + normalizeSpace(item.getFieldName()));
                processFileItemAsFormField(item);
            } else {
                LOG.debug(() -> "Processing a file: " + normalizeSpace(item.getFieldName()));
                processFileItemAsFileField(item, location);
            }
        });
    }
```

In `processFileItemAsFormField` (lines 126-140), after the `fieldName == null` guard, enforce and count before reading:

```java
    protected void processFileItemAsFormField(FileItemInput fileItemInput) throws IOException {
        String fieldName = fileItemInput.getFieldName();
        if (fieldName == null) {
            LOG.warn("Form field has null fieldName, skipping");
            return;
        }

        enforceMaxParameterCount(parameterCount, fieldName);
        parameterCount++;

        String fieldValue = readStream(fileItemInput.getInputStream());
        if (exceedsMaxStringLength(fieldName, fieldValue)) {
            return;
        }

        List<String> values = parameters.computeIfAbsent(fieldName, k -> new ArrayList<>());
        values.add(fieldValue);
    }
```

In `processFileItemAsFileField` (lines 216-249), replace the `if (exceedsMaxFiles(fileItemInput)) { return; }` block (lines 229-231) with the shared guard, counting after the empty-name / null-fieldName guards already above it:

```java
        enforceMaxFiles(fileCount, fileItemInput.getName());
        fileCount++;
```

Leave the rest of `processFileItemAsFileField` (temp file creation, empty-file rejection, size checks, `createUploadedFile`) unchanged.

- [ ] **Step 5: Run the stream suite**

Run: `mvn test -DskipAssembly -pl core -Dtest=JakartaStreamMultiPartRequestTest`
Expected: PASS (updated `exceedsMaxFilesPath` + three new tests + existing).

- [ ] **Step 6: Run the full multipart + interceptor regression**

Run: `mvn test -DskipAssembly -pl core -Dtest=AbstractMultiPartRequestTest+JakartaMultiPartRequestTest+JakartaStreamMultiPartRequestTest+ActionFileUploadInterceptorTest`
Expected: PASS for all.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/multipart/JakartaStreamMultiPartRequest.java \
        core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaStreamMultiPartRequestTest.java
git commit -m "$(cat <<'EOF'
WW-5474 fix(multipart): apply files-only maxFiles + maxParameterCount to stream parser

Replace the field-name-based exceedsMaxFiles with the shared files-only
enforcement and add parameter-count enforcement, matching the jakarta parser
and failing closed on breach.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>
EOF
)"
```

---

## Self-Review

**Spec coverage:**
- §2 files-only `maxFiles` both parsers → Task 1 Step 10, Task 2 Step 4. ✓
- §3 new constant/default/injection → Task 1 Steps 1-2, 5. ✓
- §4 shared `enforce*` helpers + new exception → Task 1 Steps 4-5. ✓
- §5 fail-closed clearing + message wiring → Task 1 Steps 3, 6. ✓
- §6 jakarta backstop + counters; stream rewire → Task 1 Steps 7, 10; Task 2 Step 4. ✓
- §7 tests (both parsers, many-fields, over-limit files, over-limit params, multi-file-one-field, fail-closed, setter) → Task 1 Step 8, Task 2 Steps 1-2. ✓
- §7 note about JUnit 4 multipart tests honored; interceptor JUnit 3 suite run for regression (Task 1 Step 11). ✓

**Placeholder scan:** No TBD/TODO; all steps carry concrete code and exact commands. ✓

**Type consistency:** `enforceMaxFiles(int, String)` / `enforceMaxParameterCount(int, String)` and `FileUploadParameterCountLimitException(String, long, long)` with `getPermitted()`/`getActual()` are defined in Task 1 and consumed identically in Task 2 and in `parse()`. Message key string matches across property file, tests, and exception mapping. ✓

**Known wrinkles (from the spec, intentional):** a gross flood over `maxFiles + maxParameterCount` on the jakarta parser surfaces the generic `FileUploadFileCountLimitException` via the commons backstop rather than a per-category message; `clearCollectedData()` tightens all `FileUploadException` abort paths (safe — no test asserts partial retention).
