package mailreader2;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import java.util.List;

/**
 * Verify that essential resources are available.
 */
public class Welcome extends MailreaderSupport  {

    public String execute() {

        // Confirm message resources loaded
        String message = getText(Constants.ERROR_DATABASE_MISSING);
        if (Constants.ERROR_DATABASE_MISSING.equals(message)) {
            addActionError(Constants.ERROR_MESSAGES_NOT_LOADED);
        }

        // Confirm database loaded
        if (null == getDatabase()) {
            addActionError(Constants.ERROR_DATABASE_NOT_LOADED);
        }

        if (hasErrors()) {
            return ERROR;
        } else {
            return SUCCESS;
        }
    }
}
