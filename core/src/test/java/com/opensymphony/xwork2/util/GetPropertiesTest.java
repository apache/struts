/*
 * Created on Jan 23, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Gabe
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetPropertiesTest extends XWorkTestCase {

    public void testGetCollectionProperties()  {
        doGetCollectionPropertiesTest(new ArrayList());
        doGetCollectionPropertiesTest(new HashSet());
        
    }
    
    public void doGetCollectionPropertiesTest(Collection c) {
        ValueStack vs = ActionContext.getContext().getValueStack();
        Foo foo = new Foo();
        foo.setBarCollection(c);
        vs.push(foo);
        assertEquals(Boolean.TRUE, vs.findValue("barCollection.isEmpty"));
        assertEquals(Boolean.TRUE, vs.findValue("barCollection.empty"));
        assertEquals(new Integer(0), vs.findValue("barCollection.size"));
        assertTrue(vs.findValue("barCollection.iterator") instanceof java.util.Iterator);
    }
}
