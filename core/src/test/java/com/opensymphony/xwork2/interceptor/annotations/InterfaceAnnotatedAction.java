package com.opensymphony.xwork2.interceptor.annotations;

public interface InterfaceAnnotatedAction {
    @Before
    String interfaceBefore();

    @BeforeResult(priority=3)
    void interfaceBeforeResult();

    @After(priority=3)
    void interfaceAfter();
}
