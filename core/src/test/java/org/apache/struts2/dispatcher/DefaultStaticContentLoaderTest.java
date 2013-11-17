package org.apache.struts2.dispatcher;

import org.apache.struts2.StrutsInternalTestCase;

import java.util.List;

public class DefaultStaticContentLoaderTest extends StrutsInternalTestCase {

    public void testParsePackages() throws Exception {

        DefaultStaticContentLoader filterDispatcher = new DefaultStaticContentLoader();
        List<String> result1 = filterDispatcher.parse("foo.bar.package1 foo.bar.package2 foo.bar.package3");
        List<String> result2 = filterDispatcher.parse("foo.bar.package1\tfoo.bar.package2\tfoo.bar.package3");
        List<String> result3 = filterDispatcher.parse("foo.bar.package1,foo.bar.package2,foo.bar.package3");
        List<String> result4 = filterDispatcher.parse("foo.bar.package1    foo.bar.package2  \t foo.bar.package3   , foo.bar.package4");

        assertEquals(result1.get(0), "foo/bar/package1/");
        assertEquals(result1.get(1), "foo/bar/package2/");
        assertEquals(result1.get(2), "foo/bar/package3/");

        assertEquals(result2.get(0), "foo/bar/package1/");
        assertEquals(result2.get(1), "foo/bar/package2/");
        assertEquals(result2.get(2), "foo/bar/package3/");

        assertEquals(result3.get(0), "foo/bar/package1/");
        assertEquals(result3.get(1), "foo/bar/package2/");
        assertEquals(result3.get(2), "foo/bar/package3/");

        assertEquals(result4.get(0), "foo/bar/package1/");
        assertEquals(result4.get(1), "foo/bar/package2/");
        assertEquals(result4.get(2), "foo/bar/package3/");
        assertEquals(result4.get(3), "foo/bar/package4/");
    }

}
