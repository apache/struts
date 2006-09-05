package example;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class LoginTest extends ConfigTest {

    public void FIXME_testLoginConfig() throws Exception {
        ActionConfig config = assertClass("Login", "tutorial.Login");
        assertResult(config, ActionSupport.SUCCESS, "Menu");
        assertResult(config, ActionSupport.INPUT, "/tutorial/Login.jsp");
    }

    public void testLoginSubmit() throws Exception {
        Login login = new Login();
        login.setUsername("username");
        login.setPassword("password");
        String result = login.execute();
        AssertSuccess(result);
    }

}
