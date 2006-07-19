package mailreader2;

import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Action;
import java.util.Map;
import org.apache.struts.apps.mailreader.dao.User;

public class AuthenticationInterceptor implements Interceptor  {

    public void destroy () {}

    public void init() {}

    public String intercept(ActionInvocation actionInvocation) throws Exception {

        Map session = actionInvocation.getInvocationContext().getSession();

        User user = (User) session.get(Constants.USER_KEY);

        boolean isAuthenticated = (null!=user) && (null!=user.getDatabase());

        if (!isAuthenticated) {
            return Action.LOGIN;            
        }
        else {
            return actionInvocation.invoke();
        }

    }
}
