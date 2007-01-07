package org.apache.struts2.components;


public interface RemoteUICallBean {

    void setListenTopics(String topics);

    void setNotifyTopics(String topics);

    void setHref(String href);

    void setErrorText(String errorText);

    void setAfterLoading(String afterLoading);

    void setBeforeLoading(String beforeLoading);

    void setExecuteScripts(String executeScripts);

    void setLoadingText(String loadingText);

    void setHandler(String handler);

    void setFormFilter(String formFilter);

    void setFormId(String formId);

    void setShowErrorTransportText(String showError);

    void setIndicator(String indicator);

}
