package org.apache.struts2;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.httpmethod.AllowedHttpMethod;
import org.apache.struts2.interceptor.httpmethod.HttpDelete;
import org.apache.struts2.interceptor.httpmethod.HttpGet;
import org.apache.struts2.interceptor.httpmethod.HttpGetOrPost;
import org.apache.struts2.interceptor.httpmethod.HttpMethod;
import org.apache.struts2.interceptor.httpmethod.HttpMethodAware;
import org.apache.struts2.interceptor.httpmethod.HttpPost;
import org.apache.struts2.interceptor.httpmethod.HttpPut;

import static org.apache.struts2.interceptor.httpmethod.HttpMethod.POST;

@AllowedHttpMethod(POST)
public class HttpMethodsTestAction extends ActionSupport implements HttpMethodAware {

    private String resultName = null;

    public HttpMethodsTestAction() {
    }

    public HttpMethodsTestAction(String resultName) {
        this.resultName = resultName;
    }

    @HttpGet
    public String onGetOnly() {
        return "onGetOnly";
    }

    @HttpPost
    public String onPostOnly() {
        return "onPostOnly";
    }

    @HttpGetOrPost
    public String onGetPostOnly() {
        return "onGetPostOnly";
    }

    @HttpPut @HttpPost
    public String onPutOrPost() {
        return "onPutOrPost";
    }

    @HttpDelete
    public String onDelete() {
        return "onDelete";
    }

    public void setMethod(HttpMethod httpMethod) {

    }

    public String getBadRequestResultName() {
        return resultName;
    }
}
