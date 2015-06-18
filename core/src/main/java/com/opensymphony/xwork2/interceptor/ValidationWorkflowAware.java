package com.opensymphony.xwork2.interceptor;

/**
 * ValidationWorkflowAware classes can programmatically change result name when errors occurred
 *
 * This interface can be only applied to action which already implements {@link ValidationAware} interface!
 */
public interface ValidationWorkflowAware {

    String getInputResultName();

}
