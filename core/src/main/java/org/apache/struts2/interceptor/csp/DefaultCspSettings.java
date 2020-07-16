package org.apache.struts2.interceptor.csp;

import com.opensymphony.xwork2.ActionContext;

import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;


public class DefaultCspSettings implements CspSettings {

    private static final int NONCE_LENGTH = 18;

    public void addCspHeaders(HttpServletResponse response) {
        createNonce();
        response.addHeader(CSP_HEADER, getPolicyString());
    }

    public String getNonceString() {
        Map<String, Object> session = ActionContext.getContext().getSession();
        return (String) session.get("nonce");
    }

    protected void createNonce() {
        String nonceValue = Base64.getUrlEncoder().encodeToString(getRandomBytes(NONCE_LENGTH));
        Map<String, Object> session = ActionContext.getContext().getSession();
        session.put("nonce", nonceValue);
    }

    private String getPolicyString() {
        //TODO add reportURI
        return String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '$s';",
                OBJECT_SRC, NONE,
                SCRIPT_SRC, getNonceString(), STRICT_DYNAMIC, HTTP, HTTPS,
                BASE_URI, NONE
        );
    }

    private byte[] getRandomBytes(int length)
    {
        SecureRandom sRand  = new SecureRandom();
        byte[] ret = new byte[length];
        sRand.nextBytes(ret);
        return ret;
    }
}
