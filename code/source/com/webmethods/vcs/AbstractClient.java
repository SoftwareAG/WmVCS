package com.webmethods.vcs;

import java.io.*;
import java.util.*;


/**
 * VCS support for generic client.
 */
public abstract class AbstractClient implements VCSClient
{
    /**
     * Returns whether the given entry name is valid to submit to Perforce. 
     * Directories and generated files (such as .class) are not valid. The file
     * does <em>not</em> need to exist to be considered valid.
     */
    protected boolean isValidEntry(String name)
    {
        return !(new File(name)).isDirectory() && !name.endsWith(".class");
    }

    /**
     * Returns a list of the file names that pass <code>isValidEntry</code>.
     */
    protected List getValidEntries(List fileNames) throws VCSException
    {
        List valid = new ArrayList();
        Iterator fit = fileNames.iterator();
        while (fit.hasNext()) {
            String entry = (String)fit.next();
            if (isValidEntry(entry)) {
                valid.add(entry);
            }
            else {
                // skipping ignored file/directory
            }
        }
        return valid;
    }

    /**
     * Returns a list containing only one of each value in <code>list</code>.
     */
    public static List toUniqueList(List list)
    {
        List unique = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (!unique.contains(obj)) {
                unique.add(obj);
            }
        }
        return unique;
    }

    /**
     * Returns a list of strings, each of which has the given suffix added.
     */
    protected static List addSuffix(List source, String suffix)
    {
        List suffixed = new ArrayList();
        Iterator it = source.iterator();
        while (it.hasNext()) {
            String str = (String)it.next();
            suffixed.add(str + suffix);
        }
        return suffixed;
    }
}
