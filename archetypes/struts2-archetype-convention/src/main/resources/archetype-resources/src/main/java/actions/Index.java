package ${package}.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
        @Result(name = Action.SUCCESS, location = "${redirectName}", type = "redirectAction")
})
public class Index extends ActionSupport {

    private String redirectName;

    public String execute() {
        redirectName = "hello";
        return Action.SUCCESS;
    }

    public String getRedirectName() {
        return redirectName;
    }

}
