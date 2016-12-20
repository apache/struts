package org.apache.struts2.dispatcher.multipart;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.LocalizedMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Abstract class with some helper methods, it should be used
 * when starting development of another implementation of {@link MultiPartRequest}
 */
public abstract class AbstractMultiPartRequest implements MultiPartRequest {

    private static final Logger LOG = LogManager.getLogger(AbstractMultiPartRequest.class);

    /**
     * Defines the internal buffer size used during streaming operations.
     */
    public static final int BUFFER_SIZE = 10240;

    /**
     * Internal list of raised errors to be passed to the the Struts2 framework.
     */
    protected List<LocalizedMessage> errors = new ArrayList<>();

    /**
     * Specifies the maximum size of the entire request.
     */
    protected long maxSize;
    protected boolean maxSizeProvided;

    /**
     * Specifies the buffer size to use during streaming.
     */
    protected int bufferSize = BUFFER_SIZE;

    protected String defaultEncoding;

    /**
     * Localization to be used regarding errors.
     */
    protected Locale defaultLocale = Locale.ENGLISH;

    /**
     * @param bufferSize Sets the buffer size to be used.
     */
    @Inject(value = StrutsConstants.STRUTS_MULTIPART_BUFFERSIZE, required = false)
    public void setBufferSize(String bufferSize) {
        this.bufferSize = Integer.parseInt(bufferSize);
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String enc) {
        this.defaultEncoding = enc;
    }

    /**
     * @param maxSize Injects the Struts multiple part maximum size.
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
    public void setMaxSize(String maxSize) {
        this.maxSizeProvided = true;
        this.maxSize = Long.parseLong(maxSize);
    }

    /**
     * @param provider Injects the Struts locale provider.
     */
    @Inject
    public void setLocaleProvider(LocaleProvider provider) {
        defaultLocale = provider.getLocale();
    }

    /**
     * @param request Inspect the servlet request and set the locale if one wasn't provided by
     * the Struts2 framework.
     */
    protected void setLocale(HttpServletRequest request) {
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
    }

    /**
     * Build error message.
     *
     * @param e the Throwable/Exception
     * @param args arguments
     * @return error message
     */
    protected LocalizedMessage buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.messages.upload.error." + e.getClass().getSimpleName();
        LOG.debug("Preparing error message for key: [{}]", errorKey);

        return new LocalizedMessage(this.getClass(), errorKey, e.getMessage(), args);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getErrors()
    */
    public List<LocalizedMessage> getErrors() {
        return errors;
    }

}
