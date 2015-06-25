package com.opensymphony.xwork2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * ObjectFactory that returns a FooProxy in the buildBean if the clazz is FooAction 
 */
public class ProxyObjectFactory extends ObjectFactory {

    /**
     * It returns an instance of the bean except if the class is FooAction. 
     * In this case, it returns a FooProxy of it.
     */
    @Override
    public Object buildBean(Class clazz, Map<String, Object> extraContext)
        throws Exception {
        Object bean = super.buildBean(clazz, extraContext);
        if(clazz.equals(ProxyInvocationAction.class)) {
            return Proxy.newProxyInstance(bean.getClass()
                .getClassLoader(), bean.getClass().getInterfaces(),
                new ProxyInvocationProxy(bean));

        }
        return bean;
    }
    
    /**
     * Simple proxy that just invokes the method on the target on the invoke method
     */
    public class ProxyInvocationProxy implements InvocationHandler {

        private Object target;

        public ProxyInvocationProxy(Object target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
            return m.invoke(target, args);
        }
    }
}
