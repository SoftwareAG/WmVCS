package com.webmethods.client.shared.i18n;

import java.text.MessageFormat;


/**
 * Formats messages and offers more advanced exception handling than
 * java.text.MessageFormat.
 */
public class MessageFormatter
{
    /**
     * Returns the string formatted with the given arguments.
     */
    public String format(String msg, Object[] args) 
    {
        return MessageFormat.format(msg, args);
    }

    /**
     * Returns the formatted string, with no arguments.
     */
    public String format(String msg) 
    {
        return format(msg, new Object[] {});
    }

}
