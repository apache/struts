package org.apache.struts2.interceptor.csp;

import com.opensymphony.xwork2.ActionContext;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.apache.struts2.interceptor.csp.CspSettings.CSP_HEADER;

public class DefaultCspSettings implements CspSettings {

    public void addCspHeaders(HttpServletResponse response) {
        createNonce();
        response.addHeader(CSP_HEADER, getPolicyString();
        //TODO add nonces to the script / style tags
    }

    public String getNonceString() {
        Map<String, Object> session = ActionContext.getContext().getSession();
        return (String) session.get("nonce");
    }

    protected void createNonce() {
        Map<String, Object> session = ActionContext.getContext().getSession();
        session.put("nonce", "r4and0m");
        //TODO generate random nonce
    }

    private String getPolicyString() {
        return String.format("%s '%s'; %s 'nonce-%s' '%s'; %s '$s';",
                OBJECT_SRC, NONE,
                SCRIPT_SRC, getNonceString(), STRICT_DYNAMIC,
                BASE_URI, NONE
        );
    }
}
