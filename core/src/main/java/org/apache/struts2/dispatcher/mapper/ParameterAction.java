package org.apache.struts2.dispatcher.mapper;

/**
 * Defines a parameter action prefix.  This is executed when the configured prefix key is matched in a parameter
 * name, allowing the implementation to manipulate the action mapping accordingly.  For example, if the "action:foo"
 * parameter name was found, and a ParameterAction implementation was registered to handle the "action" prefix, the
 * execute method would be called, allowing the implementation to set the "method" value on the ActionMapping.
 * 
 * @since 2.1.0
 */
public interface ParameterAction {
    void execute(String key, ActionMapping mapping);
}
