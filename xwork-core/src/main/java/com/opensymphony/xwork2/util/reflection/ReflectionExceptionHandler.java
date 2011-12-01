package org.apache.struts2.xwork2.util.reflection;

/**
 * Declares a class that wants to handle its own reflection exceptions
 */
public interface ReflectionExceptionHandler {

    /**
     * Handles a reflection exception
     * 
     * @param ex The reflection exception
     */
    void handle(ReflectionException ex);
}
