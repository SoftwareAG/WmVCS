package com.webmethods.vcs.lang;


/**
 * java.lang.String is final, making it impossible to be subclassed. This isn't.
 */
public class String
{
    private java.lang.String _str;
    
    public String(java.lang.String str)
    {
        _str = str;
    }

    public String()
    {
        this(null);
    }

    public java.lang.String toString()
    {
        return _str;
    }
}
