package com.opensymphony.xwork2.ognl;

import junit.framework.TestCase;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SecurityMemberAccessTest extends TestCase {

    private Map context;
    private FooBar target;

    @Override
    public void setUp() throws Exception {
        context = new HashMap();
        target = new FooBar();
    }

    public void testWithoutClassExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue(accessible);
    }

    public void testClassExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "stringField";
        Member member = FooBar.class.getDeclaredMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        Set<Class<?>> excluded = new HashSet<Class<?>>();
        excluded.add(FooBar.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse(accessible);
    }

    public void testObjectClassExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "toString";
        Member member = FooBar.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("toString() from Object is accessible!!!", accessible);
    }

    public void testObjectOverwrittenMethodsExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "hashCode";
        Member member = FooBar.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("hashCode() from FooBar isn't accessible!!!", accessible);
    }

    public void testInterfaceInheritanceExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        Set<Class<?>> excluded = new HashSet<Class<?>>();
        excluded.add(BarInterface.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("barLogic() from BarInterface is accessible!!!", accessible);
    }

    public void testMiddleOfInheritanceExclusion1() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "fooLogic";
        Member member = FooBar.class.getMethod(propertyName);

        Set<Class<?>> excluded = new HashSet<Class<?>>();
        excluded.add(BarInterface.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("fooLogic() from FooInterface isn't accessible!!!", accessible);
    }

    public void testMiddleOfInheritanceExclusion2() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        Set<Class<?>> excluded = new HashSet<Class<?>>();
        excluded.add(BarInterface.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("barLogic() from BarInterface is accessible!!!", accessible);
    }

    public void testMiddleOfInheritanceExclusion3() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

/*
        Set<Class<?>> excluded = new HashSet<Class<?>>();
        excluded.add(BarInterface.class);
        sma.setExcludedClasses(excluded);
*/

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("barLogic() from BarInterface isn't accessible!!!", accessible);
    }

    public void testMiddleOfInheritanceExclusion4() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        Set<Class<?>> excluded = new HashSet<Class<?>>();
        excluded.add(FooBarInterface.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("barLogic() from BarInterface is accessible!!!", accessible);
    }

    public void testPackageExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        Set<Pattern> excluded = new HashSet<Pattern>();
        excluded.add(Pattern.compile("^" + FooBar.class.getPackage().getName().replaceAll("\\.", "\\\\.") + ".*"));
        sma.setExcludedPackageNamePatterns(excluded);

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("stringField is accessible!", actual);
    }

    public void testAccessEnum() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        // when
        Member values = MyValues.class.getMethod("values");
        boolean actual = sma.isAccessible(context, MyValues.class, values, null);

        // then
        assertTrue("Access to enums is blocked!", actual);
    }

}

class FooBar implements FooBarInterface {

    private String stringField;

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public String fooLogic() {
        return "fooLogic";
    }

    public String barLogic() {
        return "barLogic";
    }

    @Override
    public int hashCode() {
        return 1;
    }

}

interface FooInterface {

    String fooLogic();

}

interface BarInterface {

    String barLogic();

}

interface FooBarInterface extends FooInterface, BarInterface {

}

enum MyValues {
    ONE, TWO, THREE
}