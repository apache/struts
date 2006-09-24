// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.Messages;

import com.opensymphony.xwork2.DefaultTextProvider;
import com.opensymphony.xwork2.TextProvider;

public class MessagesImpl implements Messages {

    final TextProvider textProvider = DefaultTextProvider.INSTANCE;
    Map<String, Messages> fieldMap = new HashMap<String, Messages>();
    Map<Severity, List<String>> severityMap = new EnumMap<Severity, List<String>>(Severity.class);

    public Messages forField(String fieldName) {
        Messages forField = fieldMap.get(fieldName);
        if (forField == null) {
            forField = new MessagesImpl();
            fieldMap.put(fieldName, forField);
        }
        return forField;
    }

    public Map<String, Messages> forFields() {
        return fieldMap;
    }

    public void addInformation(String key) {
        forSeverity(Severity.INFO).add(textProvider.getText(key));
    }

    public void addInformation(String key, String... arguments) {
        forSeverity(Severity.INFO).add(textProvider.getText(key, arguments));
    }

    public void addWarning(String key) {
        forSeverity(Severity.WARN).add(textProvider.getText(key));
    }

    public void addWarning(String key, String... arguments) {
        forSeverity(Severity.WARN).add(textProvider.getText(key, arguments));
    }

    public void addError(String key) {
        forSeverity(Severity.ERROR).add(textProvider.getText(key));
    }

    public void addError(String key, String... arguments) {
        forSeverity(Severity.ERROR).add(textProvider.getText(key, arguments));
    }

    public void add(Severity severity, String key) {
        forSeverity(severity).add(textProvider.getText(key));
    }

    public void add(Severity severity, String key, String... arguments) {
        forSeverity(severity).add(textProvider.getText(key, arguments));
    }

    public Set<Severity> getSeverities() {
        Set<Severity> severities = EnumSet.noneOf(Severity.class);
        for (Severity severity : Severity.values()) {
            List<String> messages = severityMap.get(severity);
            if (messages != null && !messages.isEmpty()) {
                severities.add(severity);
            }
        }
        return Collections.unmodifiableSet(severities);
    }

    public List<String> forSeverity(Severity severity) {
        List<String> messages = severityMap.get(severity);
        if (messages == null) {
            messages = new ArrayList<String>();
            severityMap.put(severity, messages);
        }
        return messages;
    }

    public List<String> getErrors() {
        return forSeverity(Severity.ERROR);
    }

    public List<String> getWarnings() {
        return forSeverity(Severity.WARN);
    }

    public List<String> getInformation() {
        return forSeverity(Severity.INFO);
    }

    public boolean hasErrors() {
        return !isEmpty(Severity.ERROR);
    }

    public boolean hasWarnings() {
        return !isEmpty(Severity.WARN);
    }

    public boolean hasInformation() {
        return !isEmpty(Severity.INFO);
    }

    public boolean isEmpty() {
        for (Severity severity : Severity.values())
            if (!isEmpty(severity))
                return false;

        return true;
    }

    public boolean isEmpty(Severity severity) {
        List<String> messages = severityMap.get(severity);
        if (messages != null && !messages.isEmpty()) {
            return false;
        }

        for (Messages fieldMessages : fieldMap.values())
            if (!fieldMessages.isEmpty(severity))
                return false;

        return true;
    }
}
