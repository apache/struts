package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkTestCase;
import org.easymock.EasyMock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

public class CookieProviderInterceptorTest extends XWorkTestCase {

    public void testPreResultListenerAddition() throws Exception {
        // given
        ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
        CookieProviderInterceptor interceptor = new CookieProviderInterceptor();

        invocation.addPreResultListener(interceptor);

        EasyMock.replay(invocation);

        // when
        interceptor.intercept(invocation);

        // then
        EasyMock.verify(invocation);
    }

    public void testCookieCreation() throws Exception {
        // given
        CookieProviderInterceptor interceptor = new CookieProviderInterceptor();

        final Cookie cookie = new Cookie("foo", "bar");

        CookieProvider action = new CookieProvider() {
            public Set<Cookie> getCookies() {
                Set<Cookie> cookies = new HashSet<Cookie>();
                cookies.add(cookie);
                return cookies;
            }
        };

        HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
        response.addCookie(cookie);
        EasyMock.replay(response);

        // when
        interceptor.addCookiesToResponse(action, response);

        // then
        EasyMock.verify(response);
    }

}
