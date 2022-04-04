/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on 6/10/2003
 *
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.FooBarConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.mock.MockObjectTypeDeterminer;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.Bar;
import com.opensymphony.xwork2.util.Cat;
import com.opensymphony.xwork2.util.Foo;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.Ognl;

import java.util.*;


/**
 * @author CameronBraid and Gabe
 * @author tm_jee
 */
public class SetPropertiesTest extends XWorkTestCase {
    
    private OgnlUtil ognlUtil;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        ognlUtil = container.getInstance(OgnlUtil.class);
        ((OgnlValueStack)ActionContext.getContext().getValueStack()).setDevMode("true");
    }
    public void testOgnlUtilEmptyStringAsLong() {
        Bar bar = new Bar();
        Map context = Ognl.createDefaultContext(bar);
        context.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        bar.setId(null);

        HashMap props = new HashMap();
        props.put("id", "");

        ognlUtil.setProperties(props, bar, context);
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());

        props.put("id", new String[]{""});

        bar.setId(null);
        ognlUtil.setProperties(props, bar, context);
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());
    }

    public void testSetCollectionByConverterFromArray() {
        Foo foo = new Foo();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        XWorkConverter c = (XWorkConverter)((OgnlTypeConverterWrapper) Ognl.getTypeConverter(vs.getContext())).getTarget();
        c.registerConverter(Cat.class.getName(), new FooBarConverter());
        vs.push(foo);

        vs.setValue("cats", new String[]{"1", "2"});
        assertNotNull(foo.getCats());
        assertEquals(2, foo.getCats().size());
        assertEquals(Cat.class, foo.getCats().get(0).getClass());
        assertEquals(Cat.class, foo.getCats().get(1).getClass());
    }

    public void testSetCollectionByConverterFromCollection() {
        Foo foo = new Foo();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        XWorkConverter c = (XWorkConverter)((OgnlTypeConverterWrapper) Ognl.getTypeConverter(vs.getContext())).getTarget();
        c.registerConverter(Cat.class.getName(), new FooBarConverter());
        vs.push(foo);

        HashSet s = new HashSet();
        s.add("1");
        s.add("2");
        vs.setValue("cats", s);
        assertNotNull(foo.getCats());
        assertEquals(2, foo.getCats().size());
        assertEquals(Cat.class, foo.getCats().get(0).getClass());
        assertEquals(Cat.class, foo.getCats().get(1).getClass());
    }
    
    public void testValueStackSetValueEmptyStringAsLong() {
        Bar bar = new Bar();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.push(bar);

        vs.setValue("id", "");
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());

        bar.setId(null);

        vs.setValue("id", new String[]{""});
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());
    }
    public void testAddingToListsWithObjectsTrue() {
        doTestAddingToListsWithObjects(true);
    }
    public void testAddingToListsWithObjectsFalse() {
        doTestAddingToListsWithObjects(false);

    }
    public void doTestAddingToListsWithObjects(final boolean allowAdditions) {

        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                    LocatableProperties props) throws ConfigurationException {
                builder.factory(ObjectTypeDeterminer.class, new Factory() {
                    public Object create(Context context) throws Exception {
                        return new MockObjectTypeDeterminer(null,Cat.class,null,allowAdditions);
                    }
                    
                });
            }
        });

        Foo foo = new Foo();
        foo.setMoreCats(new ArrayList());
        String spielname = "Spielen";
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.getContext().put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.TRUE);
        vs.push(foo);
        try {
            vs.setValue("moreCats[2].name", spielname);
        } catch (IndexOutOfBoundsException e) {
            if (allowAdditions) {
                throw e;
            }
        }
        Object setCat = null;
        if (allowAdditions) {
             setCat = foo.getMoreCats().get(2);


            assertNotNull(setCat);
            assertTrue(setCat instanceof Cat);
            assertTrue(((Cat) setCat).getName().equals(spielname));
        }	else {
            assertTrue(foo.getMoreCats()==null || foo.getMoreCats().size()==0);
        }

        //now try to set a lower number
        //to test setting after a higher one
        //has been created
        if (allowAdditions) {
            spielname = "paws";
            vs.setValue("moreCats[0].name", spielname);
            setCat = foo.getMoreCats().get(0);
            assertNotNull(setCat);
            assertTrue(setCat instanceof Cat);
            assertTrue(((Cat) setCat).getName().equals(spielname));
        }

    }

    
    public void testAddingToMapsWithObjectsTrue() throws Exception {
        doTestAddingToMapsWithObjects(true);
    }
    
    public void testAddingToMapsWithObjectsFalse() throws Exception {
        doTestAddingToMapsWithObjects(false);

    }

    public void doTestAddingToMapsWithObjects(boolean allowAdditions) throws Exception {

        loadButAdd(ObjectTypeDeterminer.class, new MockObjectTypeDeterminer(Long.class,Cat.class,null,allowAdditions));

        Foo foo = new Foo();
        foo.setAnotherCatMap(new HashMap());
        String spielname = "Spielen";
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.getContext().put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.TRUE);
        vs.push(foo);
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.setValue("anotherCatMap[\"3\"].name", spielname);
        Object setCat = foo.getAnotherCatMap().get(new Long(3));
        if (allowAdditions) {
            assertNotNull(setCat);
            assertTrue(setCat instanceof Cat);
            assertTrue(((Cat) setCat).getName().equals(spielname));
        }	else {
            assertNull(setCat);
        }


    }
    
    
    public void testAddingAndModifyingCollectionWithObjectsSet() {
        doTestAddingAndModifyingCollectionWithObjects(new HashSet());
    }
    public void testAddingAndModifyingCollectionWithObjectsList() {
        doTestAddingAndModifyingCollectionWithObjects(new ArrayList());

    }
    public void doTestAddingAndModifyingCollectionWithObjects(Collection barColl) {

        ValueStack vs = ActionContext.getContext().getValueStack();
        Foo foo = new Foo();

        foo.setBarCollection(barColl);
        Bar bar1 = new Bar();
        bar1.setId(new Long(11));
        barColl.add(bar1);
        Bar bar2 = new Bar();
        bar2.setId(new Long(22));
        barColl.add(bar2);
        //try modifying bar1 and bar2
        //check the logs here to make sure
        //the Map is being created
        ReflectionContextState.setCreatingNullObjects(vs.getContext(), true);
        ReflectionContextState.setReportingConversionErrors(vs.getContext(), true);
        vs.push(foo);
        String bar1Title = "The Phantom Menace";
        String bar2Title = "The Clone Wars";
        vs.setValue("barCollection(22).title", bar2Title);
        vs.setValue("barCollection(11).title", bar1Title);
        for (Object aBarColl : barColl) {
            Bar next = (Bar) aBarColl;
            if (next.getId().intValue() == 22) {
                assertEquals(bar2Title, next.getTitle());
            } else {
                assertEquals(bar1Title, next.getTitle());
            }
        }
        //now test adding to a collection
        String bar3Title = "Revenge of the Sith";
        String bar4Title = "A New Hope";
        vs.setValue("barCollection.makeNew[4].title", bar4Title, true);
        vs.setValue("barCollection.makeNew[0].title", bar3Title, true);

        assertEquals(4, barColl.size());

        for (Object aBarColl : barColl) {
            Bar next = (Bar) aBarColl;
            if (next.getId() == null) {
                assertNotNull(next.getTitle());
                assertTrue(next.getTitle().equals(bar4Title)
                        || next.getTitle().equals(bar3Title));
            }
        }

    }
    public void testAddingToCollectionBasedOnPermission() {
        final MockObjectTypeDeterminer determiner = new MockObjectTypeDeterminer(Long.class,Bar.class,"id",true);
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                    LocatableProperties props) throws ConfigurationException {
                builder.factory(ObjectTypeDeterminer.class, new Factory() {
                    public Object create(Context context) throws Exception {
                        return determiner;
                    }
                    
                }, Scope.SINGLETON);
            }
        });

        Collection barColl=new HashSet();

        ValueStack vs = ActionContext.getContext().getValueStack();
        ReflectionContextState.setCreatingNullObjects(vs.getContext(), true);
        ReflectionContextState.setReportingConversionErrors(vs.getContext(), true);
        Foo foo = new Foo();

        foo.setBarCollection(barColl);

        vs.push(foo);

        String bar1Title="title";
        vs.setValue("barCollection(11).title", bar1Title);

        assertEquals(1, barColl.size());
        Object bar=barColl.iterator().next();
        assertTrue(bar instanceof Bar);
        assertEquals(((Bar)bar).getTitle(), bar1Title);
        assertEquals(((Bar)bar).getId(), new Long(11));

        //now test where there is no permission
        determiner.setShouldCreateIfNew(false);

        String bar2Title="another title";
        vs.setValue("barCollection(22).title", bar1Title);

        assertEquals(1, barColl.size());
        bar=barColl.iterator().next();
        assertTrue(bar instanceof Bar);
        assertEquals(((Bar)bar).getTitle(), bar1Title);
        assertEquals(((Bar)bar).getId(), new Long(11));


    }

}
