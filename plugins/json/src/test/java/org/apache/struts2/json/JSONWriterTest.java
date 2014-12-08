package org.apache.struts2.json;

import static java.time.Month.DECEMBER;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.json.annotations.JSONFieldBridge;
import org.apache.struts2.json.bridge.StringBridge;
import org.junit.Test;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
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
    public void testCanSerializeALocalDate() throws Exception {
        SingleLocalDateBean dateBean = new SingleLocalDateBean();
        dateBean.setLocalDate(LocalDate.of(2012, DECEMBER, 23));

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);

        String json = jsonWriter.write(dateBean);
        assertEquals("{\"localDate\":\"2012-12-23T00:00:00\"}", json);
    }
    
    @Test
    public void testCanSerializeALocalDateTime() throws Exception {
        SingleLocalDateTimeBean dateBean = new SingleLocalDateTimeBean();
        dateBean.setLocalDate(
            LocalDateTime.of(2012, DECEMBER, 23, 10, 10, 10).
            atOffset(ZoneOffset.UTC).
            toLocalDateTime());

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        String json = jsonWriter.write(dateBean);
        assertEquals("{\"localDateTime\":\"2012-12-23T10:10:10\"}", json);
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
