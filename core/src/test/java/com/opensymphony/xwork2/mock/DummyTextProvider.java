package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.List;
import java.util.ResourceBundle;

public class DummyTextProvider implements TextProvider {
    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public String getText(String key) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, String obj) {
        return null;
    }

    @Override
    public String getText(String key, List<?> args) {
        return null;
    }

    @Override
    public String getText(String key, String[] args) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return null;
    }

    @Override
    public ResourceBundle getTexts(String bundleName) {
        return null;
    }

    @Override
    public ResourceBundle getTexts() {
        return null;
    }
}
