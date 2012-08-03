package org.apache.struts2.cdi;

import javax.inject.Inject;

/**
 * FooConsumer.
 */
public class FooConsumer {

    @Inject
    FooService fooService;
    
    public void foo() {
        System.out.println(fooService.getHello());
    }
}
