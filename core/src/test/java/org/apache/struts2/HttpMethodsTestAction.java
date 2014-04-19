package org.apache.struts2;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.httpmethod.AllowedMethod;
import org.apache.struts2.interceptor.httpmethod.GetOnly;
import org.apache.struts2.interceptor.httpmethod.GetPostOnly;
import org.apache.struts2.interceptor.httpmethod.HttpMethod;
import org.apache.struts2.interceptor.httpmethod.HttpMethodAware;
import org.apache.struts2.interceptor.httpmethod.PostOnly;

import static org.apache.struts2.interceptor.httpmethod.HttpMethod.POST;

@AllowedMethod(POST)
public class HttpMethodsTestAction extends ActionSupport implements HttpMethodAware {

    private String resultName = null;

    public HttpMethodsTestAction() {
    }

    public HttpMethodsTestAction(String resultName) {
        this.resultName = resultName;
    }

    @GetOnly
    public String onGetOnly() {
        return "onGetOnly";
    }

    @PostOnly
    public String onPostOnly() {
        return "onPostOnly";
    }

    @GetPostOnly
    public String onGetPostOnly() {
        return "onGetPostOnly";
    }

    public void setMethod(HttpMethod httpMethod) {

    }

    public String getBadRequestResultName() {
        return resultName;
    }
}
