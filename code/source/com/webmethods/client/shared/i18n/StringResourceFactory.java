package com.webmethods.client.shared.i18n;


/**
 * Creates string resources and strings, with methods for accepting the precise
 * arguments, by type. Subclasses must specify a resource resource bundle name.
 */
public abstract class StringResourceFactory extends ResourceFormatter
{
    public StringResourceFactory(String resourceBundleName)
    {
        super(resourceBundleName);
    }

    public String str(String key, Object[] args)
    {
        return getString(key, args);
    }

    /**
     * Takes no arguments.
     */
    public String str(String key) 
    {
        return str(key, new Object[] {});
    }

    /**
     * Takes one Object argument.
     */
    public String str(String key, Object arg) 
    {
        return str(key, new Object[] { arg });
    }

    /**
     * Takes an int (integer) argument.
     */
    public String str(String key, int arg) 
    {
        return str(key, new Object[] { new Integer(arg) });
    }

    /**
     * Takes two Object arguments.
     */
    public String str(String key, Object arg0, Object arg1) 
    {
        return str(key, new Object[] { arg0, arg1 });
    }

    /**
     * Takes an Object argument, and an int
     * (integer) argument.
     */
    public String str(String key, Object arg0, int arg1) 
    {
        return str(key, new Object[] { arg0, new Integer(arg1) });
    }

    /**
     * Takes an int (integer) argument, and an
     * Object argument.
     */
    public String str(String key, int arg0, Object arg1) 
    {
        return str(key, new Object[] { new Integer(arg0), arg1 });
    }

    /**
     * Takes two int (integer) arguments.
     */
    public String str(String key, int arg0, int arg1) 
    {
        return str(key, new Object[] { new Integer(arg0), new Integer(arg1) });
    }

    /**
     * Takes three Object arguments.
     */
    public String str(String key, Object arg0, Object arg1, Object arg2) 
    {
        return str(key, new Object[] { arg0, arg1, arg2 });
    }

    /**
     * Takes four Object arguments.
     */
    public String str(String key, Object arg0, Object arg1, Object arg2, Object arg3) 
    {
        return str(key, new Object[] { arg0, arg1, arg2, arg3 });
    }

    /**
     * Takes five Object arguments.
     */
    public String str(String key, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) 
    {
        return str(key, new Object[] { arg0, arg1, arg2, arg3, arg4 });
    }

}
