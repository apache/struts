package org.apache.struts2.json;

import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.json.annotations.JSONFieldBridge;
import org.apache.struts2.json.bridge.StringBridge;
import org.junit.Test;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
    public void testWriteExcludeNull() throws Exception {
        BeanWithMap bean1=new BeanWithMap();
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);

        Map m = new LinkedHashMap();
        m.put("a", "x");
        m.put("b", null);
        m.put("c", "z");
        bean1.setMap(m);

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);
        jsonWriter.setIgnoreHierarchy(false);
        String json = jsonWriter.write(bean1, null, null, true);
        TestUtils.assertEquals(JSONWriter.class.getResource("jsonwriter-write-bean-03.txt"), json);
    }

    private class BeanWithMap extends Bean{
        private Map map;

        public Map getMap() {
            return map;
        }

        public void setMap(Map map) {
            this.map = map;
        }
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

    @Test
    public void testWriteBeanWithList() throws Exception {
        BeanWithList bean1 = new BeanWithList();
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);
        List<String> errors = new ArrayList<String>();
        errors.add("Field is required");
        bean1.setErrors(errors);

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);
        jsonWriter.setIgnoreHierarchy(false);
        String json = jsonWriter.write(bean1);
        TestUtils.assertEquals(JSONWriter.class.getResource("jsonwriter-write-bean-04.txt"), json);
    }

    private class BeanWithList extends Bean {
        private List<String> errors;

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
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

    @Test
    public void testCanSerializeADate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        SingleDateBean dateBean = new SingleDateBean();
        dateBean.setDate(sdf.parse("2012-12-23 10:10:10 GMT"));

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        String json = jsonWriter.write(dateBean);
        assertEquals("{\"date\":\"2012-12-23T10:10:10\"}", json);
    }

    @Test
    public void testCanSetDefaultDateFormat() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        SingleDateBean dateBean = new SingleDateBean();
        dateBean.setDate(sdf.parse("2012-12-23 10:10:10 GMT"));

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);
        jsonWriter.setDateFormatter("MM-dd-yyyy");
        String json = jsonWriter.write(dateBean);
        assertEquals("{\"date\":\"12-23-2012\"}", json);
    }

}
