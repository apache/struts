package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by cabaden on 12/06/2017.
 */
public class StubTextProvider implements TextProvider {

    private final Map<String, String> map;

    public StubTextProvider(final Map<String, String> map) {
        this.map = map;
    }

    @Override
    public boolean hasKey(final String key) {
        return map.containsKey(key);
    }

    @Override
    public String getText(final String key) {
        return map.get(key);
    }

    @Override
    public String getText(final String key, final String defaultValue) {
        final String text = this.getText(key);
        return text == null? defaultValue : text;
    }

    @Override
    public String getText(final String key, final String defaultValue, final String obj) {
        return this.getText(key, defaultValue);
    }

    @Override
    public String getText(final String key, final List<?> args) {
        return this.getText(key);
    }

    @Override
    public String getText(final String key, final String[] args) {
        return this.getText(key);
    }

    @Override
    public String getText(final String key, final String defaultValue, final List<?> args) {
        return this.getText(key);
    }

    @Override
    public String getText(final String key, final String defaultValue, final String[] args) {
        return this.getText(key);
    }

    @Override
    public String getText(final String key, final String defaultValue, final List<?> args, final ValueStack stack) {
        return this.getText(key, defaultValue);
    }

    @Override
    public String getText(final String key, final String defaultValue, final String[] args, final ValueStack stack) {
        return this.getText(key, defaultValue);
    }

    @Override
    public ResourceBundle getTexts(final String bundleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceBundle getTexts() {
        throw new UnsupportedOperationException();
    }
}
