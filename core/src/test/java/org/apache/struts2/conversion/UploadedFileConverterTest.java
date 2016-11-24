package org.apache.struts2.conversion;

import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

public class UploadedFileConverterTest {

    private Map<String, Object> context;
    private Class target;
    private Member member;
    private String propertyName;
    private File tempFile;

    @Before
    public void setUp() throws Exception {
        context = Collections.emptyMap();
        target = File.class;
        member = File.class.getMethod("length");
        propertyName = "ignore";
        tempFile = File.createTempFile("struts", "test");
    }

    @After
    public void tearDown() throws Exception {
        tempFile.delete();
    }

    @Test
    public void convertUploadedFileToFile() throws Exception {
        // given
        UploadedFileConverter ufc = new UploadedFileConverter();
        UploadedFile uploadedFile = new StrutsUploadedFile(tempFile);

        // when
        Object result = ufc.convertValue(context, target, member, propertyName, uploadedFile, File.class);

        // then
        assertThat(result).isInstanceOf(File.class);
        File file = (File) result;
        assertThat(file.length()).isEqualTo(tempFile.length());
        assertThat(file.getAbsolutePath()).isEqualTo(tempFile.getAbsolutePath());
    }

    @Test
    public void convertUploadedFileArrayToFile() throws Exception {
        // given
        UploadedFileConverter ufc = new UploadedFileConverter();
        UploadedFile[] uploadedFile = new UploadedFile[] { new StrutsUploadedFile(tempFile) };

        // when
        Object result = ufc.convertValue(context, target, member, propertyName, uploadedFile, File.class);

        // then
        assertThat(result).isInstanceOf(File.class);
        File file = (File) result;
        assertThat(file.length()).isEqualTo(tempFile.length());
        assertThat(file.getAbsolutePath()).isEqualTo(tempFile.getAbsolutePath());
    }

}