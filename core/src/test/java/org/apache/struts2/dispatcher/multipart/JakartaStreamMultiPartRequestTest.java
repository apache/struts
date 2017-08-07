package org.apache.struts2.dispatcher.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.LocalizedMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.testng.Assert;

public class JakartaStreamMultiPartRequestTest {

    private JakartaStreamMultiPartRequest multiPart;
    private Path tempDir;
    
    @Before
    public void initialize() {
        multiPart = new JakartaStreamMultiPartRequest();
        tempDir = Paths.get("target", "multi-part-test");
    }
    
    /**
     * Number of bytes in files greater than 2GB overflow the {@code int} primative.
     * The {@link HttpServletRequest#getContentLength()} returns {@literal -1} 
     * when the header is not present or the size is greater than {@link Integer#MAX_VALUE}.
     * @throws IOException 
     */
    @Test
    public void unknownContentLength() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getContentType()).thenReturn("multipart/form-data; charset=utf-8; boundary=__X_BOUNDARY__");
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getContentLength()).thenReturn(Integer.valueOf(-1));
        StringBuilder entity = new StringBuilder();
        entity.append("\r\n--__X_BOUNDARY__\r\n");
        entity.append("Content-Disposition: form-data; name=\"upload\"; filename=\"test.csv\"\r\n");
        entity.append("Content-Type: text/csv\r\n\r\n1,2\r\n\r\n");
        entity.append("--__X_BOUNDARY__\r\n");
        entity.append("Content-Disposition: form-data; name=\"upload2\"; filename=\"test2.csv\"\r\n");
        entity.append("Content-Type: text/csv\r\n\r\n3,4\r\n\r\n");
        entity.append("--__X_BOUNDARY__--\r\n");
        Mockito.when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(new ByteArrayInputStream(entity.toString().getBytes(StandardCharsets.UTF_8))));
        multiPart.setMaxSize("4");
        multiPart.parse(request, tempDir.toString());
        LocalizedMessage next = multiPart.getErrors().iterator().next();
        Assert.assertEquals(next.getTextKey(), "struts.messages.upload.error.SizeLimitExceededException");
    }
}
