package com.webmethods.io;

import java.io.*;
import java.util.*;


/**
 * Generic VCS client capability. Essentially a wrapper to a command-line
 * interface.
 */
public class StreamExt
{
    public static void read(InputStream is, List contents)
    {
        try {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rdr.readLine()) != null) {
                contents.add(line);
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.out);
        }
    }

    public static void write(OutputStream os, List contents)
    {
        try {
            Writer wrtr = new OutputStreamWriter(os);
            Iterator it = contents.iterator();
            while (it.hasNext()) {
                String line = (String)it.next();
                wrtr.write(line);
            }

            wrtr.flush();

            os.close();
            wrtr.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.out);
        }
    }

}
