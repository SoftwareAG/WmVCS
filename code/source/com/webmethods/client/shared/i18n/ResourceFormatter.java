package com.webmethods.client.shared.i18n;

import com.webmethods.client.shared.log.Log;
import java.io.*;
import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.*;


/**
 * Formats resource messages. This is the core of the resource handler and
 * message formatter. For methods that are more user-friendly, see
 * <code>StringResourceFactory</code>.
 * 
 * @see com.webmethods.client.shared.i18n.StringResourceFactory
 */
public class ResourceFormatter
{
    /**
     * The resource bundle.
     */
    protected ResourceBundle bundle;

    /**
     * Writes this bundle.
     */
    private ResourceBundleWriter writer = null;

    /**
     * Whether this is operating in strict mode, where resources not found
     * result in exceptions.
     */
    private boolean strict;

    /**
     * Creates the message formatter for the given resource bundle, with an
     * object to accept exceptions.
     */
    public ResourceFormatter(String bundleName) 
    {
        Locale locale = Locale.getDefault();
        bundle = ResourceBundle.getBundle(bundleName, locale);
        writer = new ResourceBundleWriter(bundle);
        strict = false;
    }

    /**
     * Returns the resource string for the given key. If an internal exception
     * is thrown (i.e., from the ResourceBundle class), then the
     * <code>handleException</code> method is called, and the return value of
     * this method will be that of the <code>handleException</code> method.
     */
    public String getValue(String key) 
    {
        try {
            return bundle.getString(key);
        }
        catch (MissingResourceException e) {
            if (strict) {
                throw e;
            }
            else {
                return null;
            }
        }
    }

    /**
     * Returns a formatted string for the given key, with no arguments.
     */
    public String getString(String key) 
    {
        return getString(key, new Object[] {});
    }

    /**
     * Returns a formatted string for the given key and the array of arguments. 
     * If a resource value is not found for the given key, the key itself is
     * used, and a warning is generated.
     */
    public String getString(String key, Object[] args) 
    {
        String msg = getValue(key);
        if (msg == null) {
            // Log.log("WARNING: no resource value found for key '" + key + "'");

            if (writer == null) {
                // Log.log("no writer ... cannot update code");
            }
            else {
                writer.addKey(key);
                writer.update();
            }

            msg = key;
        }
        return MessageFormat.format(msg, args);
    }

    /**
     * Returns the writer for this bundle.
     */
    public ResourceBundleWriter getWriter()
    {
        return writer;
    }

    /**
     * Sets whether to operate in strict mode, where resource values not found
     * result in exceptions.
     */
    public void setStrict(boolean strict)
    {
        this.strict = strict;
    }

}
