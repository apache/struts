package org.apache.struts2.showcase.filedownload;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class FileDownloadActionTest {

	private FileDownloadAction fileDownloadAction;

	@Before
	public void setUp() {
	    this.fileDownloadAction = new FileDownloadAction();
	}

	@Test
	public void testSanitizeInputPathShouldAllowSimpleParameter() throws Exception {
		assertEquals("foo", fileDownloadAction.sanitizeInputPath("foo"));
	}

	@Test
	public void testSanitizeInputPathShouldReturnNullForNullInput() throws Exception {
		assertNull(fileDownloadAction.sanitizeInputPath(null));
	}

	@Test
	public void testSanitizeInputPathShouldReturnNullForLeadingWebInf() throws Exception {
		assertNull(fileDownloadAction.sanitizeInputPath("WEB-INF/foo"));
	}

	@Test
	public void testSanitizeInputPathShouldReturnNullForNonLeadingWebInf() throws Exception {
		assertNull(fileDownloadAction.sanitizeInputPath("./WEB-INF/foo"));
	}

	@Test
	public void testSanitizeInputPathShouldReturnNullForNonUppercaseWebInf() throws Exception {
		assertNull(fileDownloadAction.sanitizeInputPath("./wEB-Inf/foo"));
	}
}
