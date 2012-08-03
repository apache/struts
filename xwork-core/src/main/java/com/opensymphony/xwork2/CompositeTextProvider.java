package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.*;


/**
 * This is a composite {@link TextProvider} that takes in an array or {@link java.util.List} of {@link TextProvider}s, it will
 * consult each of them in order to get a composite result. To know how each method behaves, please refer to the
 * javadoc for each methods.
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class CompositeTextProvider implements TextProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CompositeTextProvider.class);

    private List<TextProvider> textProviders = new ArrayList<TextProvider>();

    /**
     * Instantiates a {@link CompositeTextProvider} with some predefined <code>textProviders</code>.
     *
     * @param textProviders
     */
    public CompositeTextProvider(List<TextProvider> textProviders) {
        this.textProviders.addAll(textProviders);
    }

    /**
     * Instantiates a {@link CompositeTextProvider} with some predefined <code>textProviders</code>.
     *
     * @param textProviders
     */
    public CompositeTextProvider(TextProvider[] textProviders) {
        this(Arrays.asList(textProviders));
    }

    /**
     * @param key The key to lookup in ressource bundles.
     * @return <tt>true</tt>, if the requested key is found in one of the ressource bundles.
     * @see {@link com.opensymphony.xwork2.TextProvider#hasKey(String)}
     *      It will consult each individual {@link TextProvider}s and return true if either one of the
     *      {@link TextProvider} has such a <code>key></code> else false.
     */
    public boolean hasKey(String key) {
        // if there's a key in either text providers we are ok, else try the next text provider
        for (TextProvider tp : textProviders) {
            if (tp.hasKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>
     *
     * @param key The key to lookup in ressource bundles.
     * @return The i18n text for the requested key.
     * @see {@link com.opensymphony.xwork2.TextProvider#getText(String)}
     */
    public String getText(String key) {
        return getText(key, key, Collections.emptyList());
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code> before returning <code>defaultValue</code> if every else fails.
     *
     * @param key
     * @param defaultValue
     * @return
     * @see {@link com.opensymphony.xwork2.TextProvider#getText(String, String)}
     */
    public String getText(String key, String defaultValue) {
        return getText(key, defaultValue, Collections.emptyList());
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>
     * if every else fails.
     *
     * @param key
     * @param defaultValue
     * @param obj
     * @return
     * @see {@link com.opensymphony.xwork2.TextProvider#getText(String, String, String)}
     */
    public String getText(String key, String defaultValue, final String obj) {
        return getText(key, defaultValue, new ArrayList<Object>() {
            {
                add(obj);
            }


        });
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>.
     *
     * @param key
     * @param args
     * @return
     * @see {@link com.opensymphony.xwork2.TextProvider#getText(String, java.util.List)}
     */
    public String getText(String key, List<?> args) {
        return getText(key, key, args);
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>.
     *
     * @param key
     * @param args
     * @return
     * @see {@link com.opensymphony.xwork2.TextProvider#getText(String, String[])}
     */
    public String getText(String key, String[] args) {
        return getText(key, key, args);
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>
     *
     * @param key
     * @param defaultValue
     * @param args
     * @return
     * @see {@link com.opensymphony.xwork2.TextProvider#getText#getText(String, String, java.util.List)}
     */
    public String getText(String key, String defaultValue, List<?> args) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>.
     *
     * @param key
     * @param defaultValue
     * @param args
     * @return
     * @see {@link com.opensymphony.xwork2.TextProvider#getText(String, String, String[])}
     */
    public String getText(String key, String defaultValue, String[] args) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>
     *
     * @param key
     * @param defaultValue
     * @param args
     * @param stack
     * @return
     * @see {@link com.opensymphony.xwork2.TextProvider#getText(String, String, java.util.List, com.opensymphony.xwork2.util.ValueStack)}
     */
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args, stack);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>
     *
     * @param key
     * @param defaultValue
     * @param args
     * @param stack
     * @return
     * @see {@link com.opensymphony.xwork2.TextProvider#getText(String, String, String[], com.opensymphony.xwork2.util.ValueStack)}
     */
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args, stack);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first non-null {@link ResourceBundle}.
     *
     * @param bundleName
     * @return
     * @see {@link TextProvider#getTexts(String)}
     */
    public ResourceBundle getTexts(String bundleName) {
        // if there's one text provider that gives us a non-null resource bunlde for this bundleName, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            ResourceBundle bundle = textProvider.getTexts(bundleName);
            if (bundle != null) {
                return bundle;
            }
        }
        return null;
    }

    /**
     * It will consult each {@link com.opensymphony.xwork2.TextProvider}s and return the first non-null {@link ResourceBundle}.
     *
     * @return
     * @see {@link TextProvider#getTexts()}
     */
    public ResourceBundle getTexts() {
        // if there's one text provider that gives us a non-null resource bundle, we are ok, else try the next
        // text provider
        for (TextProvider textProvider : textProviders) {
            ResourceBundle bundle = textProvider.getTexts();
            if (bundle != null) {
                return bundle;
            }
        }
        return null;
    }
}


