package org.apache.struts2.json;

import com.mockobjects.servlet.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;


/**
 * StrutsMockHttpServletResponse
 *
 */
public class StrutsMockHttpServletResponse extends MockHttpServletResponse {
    private Locale locale;
    private PrintWriter writer;
    private int status;
    private String redirectURL;
    private String contentType;
    private String encoding;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getContentType() {
        return contentType;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
    }

    public PrintWriter getWriter() throws IOException {
        if (writer == null)
            return new PrintWriter(new ByteArrayOutputStream());
        else
            return writer;
    }

    public void setCharacterEncoding(String string) {
        this.encoding = string;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public String encodeURL(String s) {
        return s;
    }

    public String encodeRedirectURL(String s) {
        return s;
    }

    public String encodeUrl(String s) {
        return s;
    }

    public void setStatus(int i) {
        this.status = i;
        super.setStatus(i);
    }

    public int getStatus() {
        return status;
    }


    public String getRedirectURL() {
        return redirectURL;
    }

    public void sendRedirect(String redirectURL) throws IOException {
        this.redirectURL = redirectURL;
        super.sendRedirect(redirectURL);
    }

    @Override
    public String getCharacterEncoding() {
        return encoding;
    }
}