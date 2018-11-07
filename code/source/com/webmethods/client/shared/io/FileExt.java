package com.webmethods.client.shared.io;

import java.io.*;
import java.util.*;


public class FileExt
{
    /**
     * Reads the file into a single string, which is null on error. The returned
     * string will contain end-of-line characters. The <code>arg</code> argument
     * is just so we can overload based on return type.
     */
    public static String readFile(String fileName, String arg)
    {
        return readFile(new File(fileName), arg);
    }

    /**
     * Reads the file into a string array, without end-of-line characters
     * (sequences). The array is null on error. The <code>arg</code> argument is
     * just so we can overload based on return type.
     */
    public static String[] readFile(String fileName, String[] arg)
    {
        return readFile(new File(fileName), arg);
    }

    /**
     * Reads the file into a single string, which is null on error.The
     * <code>arg</code> argument is just so we can overload based on return
     * type.
     */
    public static String readFile(File file, String arg)
    {
        String[] contents = readFile(file, new String[] {});
        if (contents == null) {
            return null;
        }
        else {
            StringBuffer buf      = new StringBuffer();
            String       lineSep  = System.getProperty("line.separator");
            
            for (int i = 0; contents != null && i < contents.length; ++i) {
                buf.append(contents[i] + lineSep);
            }
            
            return buf.toString();
        }
    }

    /**
     * Reads the file into a string array, without end-of-line characters
     * (sequences). The <code>arg</code> argument is just so we can overload
     * based on return type.
     */
    public static String[] readFile(File file, String[] arg)
    {
        try {
            BufferedReader br  = new BufferedReader(new FileReader(file));
            Vector         vec = new Vector();

            String in;
            while ((in = br.readLine()) != null) {
                vec.addElement(in);
            }

            return (String[])vec.toArray(new String[] {});
        }
        catch (IOException e) {
            // e.printStackTrace(System.out);

            // yes, we return null, for no such file, since a 0-length array
            // could denote an existing, empty file:
            return null;
        }
    }

    /**
     * Returns a list of files that match the given suffix. Directories are
     * processed recursively, collecting files with the suffix of
     * <code>suffix</code>. If set to greater than zero, the <code>limit</code>
     * parameter is used in order to restrict the number of matches.
     */
    public static List findFiles(File dir, final String suffix, int limit)
    {
        List files = new ArrayList();
        String[] contents = dir.list();

        for (int ci = 0; (limit <= 0 || files.size() < limit) && ci < contents.length; ++ci) {
            File fd = new File(dir, contents[ci]);
            if (fd.isDirectory()) {
                List subfiles = findFiles(fd, suffix, limit > 0 ? limit - files.size() : limit);
                files.addAll(subfiles);
            }
            else if (fd.isFile() && contents[ci].endsWith(suffix)) {
                files.add(fd);
            }
        }
        return files;
    }

    /**
     * Returns a list of files and/or directories, hierarchically.
     */
    public static List findFiles(File dir, boolean includeDirectories, boolean includeFiles)
    {
        List files = new ArrayList();
        String[] contents = dir.list();

        if (includeDirectories) {
            files.add(dir);
        }

        for (int ci = 0; ci < contents.length; ++ci) {
            File fd = new File(dir, contents[ci]);
            if (fd.isDirectory()) {
                List subfiles = findFiles(fd, includeDirectories, includeFiles);
                files.addAll(subfiles);
            }
            else if (fd.isFile() && includeFiles) {
                files.add(fd);
            }
        }
        return files;
    }


    /**
     * Returns collection of every file & directory present in the given directory. Always recursive.
     * @param dir
     * @param filter
     * @return
     */
    public static List<File> dirTree(File dir, FileFilter filter) {
        File rootDir = dir;
        File[] filesArray = rootDir.listFiles(filter);
        List<File> files = new ArrayList<File>();
        files.add(rootDir);
        for (File file : filesArray) {
            if (file.isDirectory()) {
                files.addAll(dirTree(file, filter));
            }else{
                files.add(file);
            }
        }
        return files;
    }

}
