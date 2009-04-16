package actions.osgi;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.osgi.interceptor.BundleContextAware;
import org.apache.struts2.osgi.interceptor.ServiceAware;
import org.osgi.framework.BundleContext;

import java.util.List;

@ResultPath("/content")
public class HelloWorldAction extends ActionSupport {
    private Message message;

    @Action("hello-convention")
    public String execute() {
        return SUCCESS;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getSimpleMessage() {
        return "Hello!!!";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{message:");
        sb.append(message != null ? message.getText() : "null");
        sb.append("}");
        return sb.toString();
    }
}