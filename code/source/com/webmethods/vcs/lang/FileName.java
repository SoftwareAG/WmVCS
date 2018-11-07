package com.webmethods.vcs.lang;

import java.io.*;


/**
 * A file (or directory) name.
 */
public class FileName extends String
{
    public FileName(java.lang.String str)
    {
        super(str);
    }

    public FileName()
    {
        this(null);
    }

    public FileName getParent()
    {
        return new FileName((new File(toString())).getParent());
    }

}
