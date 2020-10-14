package it.org.apache.struts2.showcase;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FileUploadTest {

    @Test
    public void testEmptyFile() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/doUpload.action");
            final HtmlForm form = page.getFormByName("doUpload");
            HtmlInput captionInput = form.getInputByName("caption");
            HtmlFileInput uploadInput = form.getInputByName("upload");
            captionInput.type("some caption");
            File tempFile = File.createTempFile("testEmptyFile", ".tmp");
            tempFile.deleteOnExit();
            uploadInput.setValueAttribute(tempFile.getAbsolutePath());
            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage resultPage = button.click();
            DomElement errorMessage = resultPage.getFirstByXPath("//span[@class='errorMessage']");
            Assert.assertNotNull(errorMessage);
            Assert.assertEquals("File cannot be empty", errorMessage.getVisibleText());
        }
    }

}
