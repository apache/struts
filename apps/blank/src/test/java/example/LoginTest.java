package example;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.Map;
import java.util.Collection;
import java.util.List;

public class LoginTest extends ConfigTest {

    public void FIXME_testLoginConfig() throws Exception {
        ActionConfig config = assertClass("example", "Login_input", "example.Login");
        assertResult(config, ActionSupport.SUCCESS, "Menu");
        assertResult(config, ActionSupport.INPUT, "/example/Login.jsp");
    }

    public void testLoginSubmit() throws Exception {
        Login login = new Login();
        login.setUsername("username");
        login.setPassword("password");
        String result = login.execute();
        assertSuccess(result);
    }

    // Needs access to an envinronment that includes validators
    public void FIXME_testLoginSubmitInput() throws Exception {
        Login login = new Login();
        String result = login.execute();
        assertInput(result);
        Map errors = assertFieldErrors(login);
        assertFieldError(errors,"username","Username is required.");
        assertFieldError(errors,"password","Password is required.");
    }

}
