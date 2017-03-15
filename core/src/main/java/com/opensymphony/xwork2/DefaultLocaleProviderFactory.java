package com.opensymphony.xwork2;

public class DefaultLocaleProviderFactory implements LocaleProviderFactory {

    @Override
    public LocaleProvider createLocaleProvider() {
        return new DefaultLocaleProvider();
    }

}
