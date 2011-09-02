package org.apache.struts2.json;

import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.json.annotations.JSONFieldBridge;
import org.apache.struts2.json.bridge.StringBridge;
import org.junit.Test;

import java.net.URL;

public class JSONWriterTest extends StrutsTestCase{
    @Test
    public void testWrite() throws Exception {
        Bean bean1=new Bean();
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);
        String json = jsonWriter.write(bean1);
        TestUtils.assertEquals(JSONWriter.class.getResource("jsonwriter-write-bean-01.txt"), json);
    }

    @Test
    public void testWriteAnnotatedBean() throws Exception {
        AnnotatedBean bean1=new AnnotatedBean();
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);
        bean1.setUrl(new URL("http://www.google.com"));

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);
        jsonWriter.setIgnoreHierarchy(false);
        String json = jsonWriter.write(bean1);
        TestUtils.assertEquals(JSONWriter.class.getResource("jsonwriter-write-bean-02.txt"), json);
    }

    private class AnnotatedBean extends Bean{
        private URL url;

        @JSONFieldBridge(impl = StringBridge.class)
        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }
    }
}
