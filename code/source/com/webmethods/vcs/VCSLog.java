package com.webmethods.vcs;

import com.webmethods.vcs.resources.VCSExceptionBundle;
import com.wm.util.JournalLogger;
import com.wm.util.LocalizedException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ResourceBundle;


/**
 * A simpler facade to the JournalLogger.
 */
public class VCSLog implements VCSMessageKeys
{
	// ==============================================================
	// CONSTANTS
	// ==============================================================

	// resource bundle name
	public static final String MSG_BUNDLE = "com.webmethods.vcs.resources.VCSMessageBundle";
	public static final String EXCEPTION_BUNDLE = "com.webmethods.vcs.resources.VCSExceptionBundle";

	// VCS logging facility
	public static final int VCS_FAC = JournalLogger.FAC_VCS;

    // aliases for severity levels
    public static final int CRITICAL = JournalLogger.CRITICAL; // 0
    public static final int ERROR    = JournalLogger.ERROR;       // 1
    public static final int WARN     = JournalLogger.WARNING;      // 2
    public static final int INFO     = JournalLogger.INFO;         // 3
    public static final int DEBUG4   = JournalLogger.DEBUG;       // 4
    public static final int DEBUG5   = JournalLogger.VERBOSE1;   // 5
    public static final int DEBUG6   = JournalLogger.VERBOSE2;   // 6
    public static final int DEBUG7   = JournalLogger.VERBOSE3;   // 7
    public static final int DEBUG8   = JournalLogger.VERBOSE4;   // 8
    public static final int DEBUG9   = JournalLogger.VERBOSE5;   // 9
    public static final int DEBUG10  = JournalLogger.VERBOSE6;   // 10
    public static final int DEBUG11  = JournalLogger.VERBOSE7;   // 11
    public static final int DEBUG12  = JournalLogger.VERBOSE8;   // 12
    public static final int DEBUG13  = JournalLogger.VERBOSE9;   // 13
    public static final int DEBUG14  = JournalLogger.VERBOSE10;  // 14

    // ==============================================================
    // JOURNAL LOG MESSAGE NUMBERS
    // ==============================================================

    /** 
     * Turns a message number into a string. Used within the message resource
     * bundle to satisfy the constraints of the journal logger.
     */
    public static String string(int msg)
    {
        return VCS_FAC + "." + msg;
    }

    // ==============================================================
    // Logging
    // ==============================================================

    public static void log(int sev, int msg) 
    {
        log(sev, msg, null); 
    }

    public static void log(int sev, int msg, Object o1) 
    {
        log(sev, msg, ary(o1)); 
    }

    public static void log(int sev, int msg, Object o1, Object o2) 
    {
        log(sev, msg, ary(o1, o2)); 
    }

    public static void log(int sev, int msg, Object o1, Object o2, Object o3) 
    {
        log(sev, msg, ary(o1, o2, o3)); 
    }

    public static void log(int sev, int msg, Object o1, Object o2, Object o3, Object o4) 
    {
        log(sev, msg, ary(o1, o2, o3, o4)); 
    }

    public static void log(int sev, int msg, Object o1, Object o2, Object o3, Object o4, Object o5) 
    {
        log(sev, msg, ary(o1, o2, o3, o4, o5)); 
    }

    public static void log(int sev, int msg, Object [] args)
    {
        JournalLogger.log(msg, VCS_FAC, sev, args);
    }

    // ==============================================================
    // EXCEPTION MESSAGE CODES
    // ==============================================================

    // the values of these codes are total voodoo.  most of the IS
    // resource bundles use this same scheme (BASE_NUM is an alias
    // for an IS-defined constant), so everyone seems to be using
    // the same exception codes.  oh, well.

    // general
    private static int excNum = 111 + VCSExceptionBundle.BASE_NUM;
    
    public static final String EXC_MESSAGE                        = String.valueOf(excNum++);
    public static final String EXC_MESSAGEIID                     = String.valueOf(excNum++);

    public static final String EXC_EXIT_VALUE_NONZERO             = String.valueOf(excNum++);
    public static final String EXC_TIMEOUT_LIMIT                  = String.valueOf(excNum++);
    public static final String EXC_PROPERTY_NOT_SET               = String.valueOf(excNum++);
    public static final String EXC_VSS_FOLDER_NOT_SET             = String.valueOf(excNum++);
    public static final String EXC_ERROR_RUNNING_COMMAND          = String.valueOf(excNum++);
    public static final String EXC_ERROR_GETTING_LOG              = String.valueOf(excNum++);
    public static final String EXC_NODE_OUT_OF_DATE               = String.valueOf(excNum++);

    public static final String EXC_VSS_FOLDER_DOES_NOT_EXIST      = String.valueOf(excNum++);
    public static final String EXC_VSS_FOLDER_NOT_A_DIRECTORY     = String.valueOf(excNum++);
    
    public static final String EXC_MISSING_PARAM                  = String.valueOf(excNum++);
    public static final String EXC_BAD_PARAM_VAL                  = String.valueOf(excNum++);
    public static final String EXC_SVCERR                         = String.valueOf(excNum++);

    public static final String EXC_NO_WRITE_ACL_PRIVILEGES        = String.valueOf(excNum++);
    public static final String EXC_INVALID_VCS_USER_NAME          = String.valueOf(excNum++);
    public static final String EXC_INVALID_DEVELOPER_NAME         = String.valueOf(excNum++);
    public static final String EXC_ILLEGAL_LABEL_CHAR             = String.valueOf(excNum++);
    public static final String EXC_FILE_CHECKED_OUT_BY_OTHER_USER = String.valueOf(excNum++);
    public static final String EXC_NO_SUCH_USER                   = String.valueOf(excNum++);
    public static final String EXC_NO_USERS_SPECIFIED             = String.valueOf(excNum++);
    public static final String EXC_INVALID_REVISION               = String.valueOf(excNum++);
    public static final String EXC_NO_USER_MAPPING                = String.valueOf(excNum++);
    // ==============================================================
    // Utilities
    // ==============================================================

    // used to format a string out of the message bundle, without logging it
    public static String getMessage(int msg) 
    { 
        return getMessage(msg, null);
    }
    
    public static String getMessage(int msg, Object o1)
    {
        return getMessage(msg, ary(o1));
    }
    
    public static String getMessage(int msg, Object o1, Object o2)
    { 
        return getMessage(msg, ary(o1, o2));
    }
    
    public static String getMessage(int msg, Object o1, Object o2, Object o3)
    {
        return getMessage(msg, ary(o1, o2, o3));
    }
    
    public static String getMessage(int msg, Object o1, Object o2, Object o3, Object o4)
    {
        return getMessage(msg, ary(o1, o2, o3, o4));
    }
    
    public static String getMessage(int msg, Object o1, Object o2, Object o3, Object o4, Object o5)
    {
        return getMessage(msg, ary(o1, o2, o3, o4, o5));
    }
    
    public static String getMessage(int msg, Object[] args)
    {
        String txt = null;
        ResourceBundle bundle = ResourceBundle.getBundle(MSG_BUNDLE);
        if (bundle != null) {
            String format = (String)bundle.getObject(string(msg));
            if (format != null) {
                MessageFormat mformat = new MessageFormat(format);
                txt = mformat.format(args);
            }
        }
        return txt;
    }

    public static String getExceptionMessage(String key) 
    {
        return getExceptionMessage(key, null); 
    }

    public static String getExceptionMessage(String key, Object o1) 
    {
        return getExceptionMessage(key, ary(o1)); 
    }

    public static String getExceptionMessage(String key, Object o1, Object o2) 
    {
        return getExceptionMessage(key, ary(o1, o2)); 
    }

    public static String getExceptionMessage(String key, Object o1, Object o2, Object o3) 
    {
        return getExceptionMessage(key, ary(o1, o2, o3)); 
    }

    public static String getExceptionMessage(String key, Object o1, Object o2, Object o3, Object o4) 
    {
        return getExceptionMessage(key, ary(o1, o2, o3, o4)); 
    }

    public static String getExceptionMessage(String key, Object o1, Object o2, Object o3, Object o4, Object o5) 
    {
        return getExceptionMessage(key, ary(o1, o2, o3, o4, o5)); 
    }
    
    public static String getExceptionMessage(String key, Object[] args)
    {
        String txt = null;
        ResourceBundle bundle = ResourceBundle.getBundle(EXCEPTION_BUNDLE);
        if (bundle != null) {
            String format = (String)bundle.getObject(key);
            if (format != null) {
                MessageFormat mformat = new MessageFormat(format);
                txt = mformat.format(args);
            }
        }
        return txt;
    }

    // building object arrays
    public static Object[] ary(Object o1) 
    {
        return new Object[] { o1 };
    }

    public static Object[] ary(Object o1, Object o2) 
    {
        return new Object[] { o1, o2 };
    }

    public static Object[] ary(Object o1, Object o2, Object o3) 
    {
        return new Object[] { o1, o2, o3 };
    }

    public static Object[] ary(Object o1, Object o2, Object o3, Object o4) 
    {
        return new Object[] { o1, o2, o3, o4 };
    }

    public static Object[] ary(Object o1, Object o2, Object o3, Object o4, Object o5) 
    {
        return new Object[] { o1, o2, o3, o4, o5 };
    }
    
    // get a stack trace as a string.  if the target is a localized
    // exception with a wrapped exception, this method will become
    // recursive.  otherwise, the stacktrace of "t" is returned.
    public static String stack(Throwable t)
    {
        Throwable w = null;
        if (t instanceof LocalizedException) {
            LocalizedException l = (LocalizedException) t;
            w = l.getWrappedException();
            if (w != null) {
                StringBuffer s = new StringBuffer();
                s.append(l.toString()).append("\n");
                s.append(stack(w));
                return s.toString();
            }
        }

        StringWriter s = new StringWriter();
        t.printStackTrace(new PrintWriter(s));
        return s.toString();
    }

    // get a simple thread ID for the current thread
    private static int tcount = 0;
    
    private static HashMap tids = new HashMap();
    
    public static String jlid()
    {
        Thread t = Thread.currentThread();
        String tid = null;
        synchronized (tids) {
            tid = (String) tids.get(t);
            if (tid == null) {
                tid = "T" + Integer.toString(tcount++);
                tids.put(t, tid);
            }
        }
        return tid;
    }

}
