package org.apache.struts2.components;

public interface RemoteUICallBean {

    /**
     * Topic that will trigger the remote call
     *
     * @s.tagattribute required="false" type="String"
     */
    void setListenTopics(String topics);

    /**
     * Topic that will published when the remote call completes
     *
     * @s.tagattribute required="false" type="String"
     */
    void setNotifyTopics(String topics);

    /**
     * The URL to call to obtain the content. Note: If used with ajax context, the value must be set as an url tag value.
     * @s.tagattribute required="false" type="String"
     */
    void setHref(String href);

    /**
     * The text to display to the user if the is an error fetching the content
     * @s.tagattribute required="false" type="String"
     */
    void setErrorText(String errorText);

    /**
     * Javascript code name that will be executed after the content has been fetched
     * @s.tagattribute required="false" type="String"
     */
    void setAfterLoading(String afterLoading);

    /**
     * Javascript code that will be executed before the content has been fetched
     * @s.tagattribute required="false" type="String"
     */
    void setBeforeLoading(String beforeLoading);

    /**
     * Javascript code in the fetched content will be executed
     * @s.tagattribute required="false" type="Boolean" default="false"
     */
    void setExecuteScripts(String executeScripts);

    /**
     * Text to be shown while content is being fetched
     *
     * @s.tagattribute required="false" type="String" default="Loading..."
     */
    void setLoadingText(String loadingText);

    /**
     * Javascript function name that will make the request
     *
     * @s.tagattribute required="false" type="String"
     */
    void setHandler(String handler);

    /**
     * Function name used to filter the fields of the form.
     * This function takes as a parameter the element and returns true if the element
     * must be included.
     * @s.tagattribute required="false" type="String"
     */
    void setFormFilter(String formFilter);

    /**
     * Form id whose fields will be serialized and passed as parameters
     *
     * @s.tagattribute required="false" type="String"
     */
    void setFormId(String formId);

    /**
     * Set whether errors will be shown or not
     *
     * @s.tagattribute required="false" type="Boolean"
     */
    void setShowErrorTransportText(String showError);

    /**
     * Id of element that will be shown while making request
     *
     * @s.tagattribute required="false" type="String"
     */
    void setIndicator(String indicator);

}
