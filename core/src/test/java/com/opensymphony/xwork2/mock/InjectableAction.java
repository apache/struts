package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.inject.Inject;

public class InjectableAction {

    private TextProvider textProvider;

    @Inject
    public InjectableAction(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    public TextProvider getTextProvider() {
        return textProvider;
    }
}
