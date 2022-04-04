package org.apache.struts2.views.jsp.ui;

import java.util.Arrays;
import java.util.List;

import org.apache.struts2.TestAction;

import com.opensymphony.xwork2.Action;

public class LocaleTestAction extends TestAction implements Action {

    @SuppressWarnings("unused")
    public List<LocaleKeyValueTest> getSelectValues() {
        return Arrays.asList(LocaleKeyValueTest.values());
    }

}
