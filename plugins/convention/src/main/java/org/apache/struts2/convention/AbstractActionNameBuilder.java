package org.apache.struts2.convention;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractActionNameBuilder implements ActionNameBuilder {

    private Set<String> actionSuffix = Collections.singleton("Action");

    /**
     * @param   actionSuffix (Optional) Classes that end with these value will be mapped as actions
     *          (defaults to "Action")
     */
    @Inject(value = ConventionConstants.CONVENTION_ACTION_SUFFIX, required = false)
    public void setActionSuffix(String actionSuffix) {
        if (StringUtils.isNotBlank(actionSuffix)) {
            this.actionSuffix = TextParseUtil.commaDelimitedStringToSet(actionSuffix);
        }
    }


    protected void checkActionName(String actionName) {
        for (String suffix : actionSuffix) {
            if (actionName.equals(suffix)) {
                throw new IllegalStateException("The action name cannot be the same as the action suffix [" + suffix + "]");
            }
        }
    }

    protected String truncateSuffixIfMatches(String name) {
        String actionName = name;
        for (String suffix : actionSuffix) {
            if (actionName.endsWith(suffix)) {
                actionName = actionName.substring(0, actionName.length() - suffix.length());
            }
        }
        return actionName;
    }

}
