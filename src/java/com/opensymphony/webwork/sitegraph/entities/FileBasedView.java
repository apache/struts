package com.opensymphony.webwork.sitegraph.entities;

import com.opensymphony.util.FileUtils;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.sitegraph.model.Link;
import com.opensymphony.webwork.WebWorkConstants;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 2:07:43 PM
 */
public abstract class FileBasedView implements View {
    private String name;
    private String contents;

    public FileBasedView(File file) {
        this.name = file.getName();
        // get the contents as a single line
        this.contents = FileUtils.readFile(file).replaceAll("[\r\n ]+", " ");
    }

    public String getName() {
        return name;
    }

    public Set getTargets() {
        TreeSet targets = new TreeSet();

        // links
        matchPatterns(getLinkPattern(), targets, Link.TYPE_HREF);

        // actions
        matchPatterns(getActionPattern(), targets, Link.TYPE_ACTION);

        // forms
        matchPatterns(getFormPattern(), targets, Link.TYPE_FORM);

        return targets;
    }

    protected Pattern getLinkPattern() {
        Object ext = Configuration.get(WebWorkConstants.WEBWORK_ACTION_EXTENSION);
        String actionRegex = "([A-Za-z0-9\\._\\-\\!]+\\." + ext + ")";
        return Pattern.compile(actionRegex);
    }

    private void matchPatterns(Pattern pattern, Set targets, int type) {
        Matcher matcher = pattern.matcher(contents);
        while (matcher.find()) {
            String target = matcher.group(1);
            targets.add(new Target(target, type));
        }
    }

    protected abstract Pattern getActionPattern();

    protected abstract Pattern getFormPattern();
}
