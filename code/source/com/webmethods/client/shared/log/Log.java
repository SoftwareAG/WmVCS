package com.webmethods.client.shared.log;

import com.webmethods.client.shared.util.ANSI;


public class Log extends ANSI
{
    public static int fileWidth = 35;
    public static int lineWidth = 5;
    public static int funcWidth = 90;

    public static boolean verbose = false;

    public static boolean log() 
    {
        return log(null, "", 2);
    }

    public static boolean log(String msg) 
    {
        return log(null, msg, 2);
    }

    public static boolean log(String[] msg) 
    {
        for (int i = 0; i < msg.length; ++i) {
            log(null, msg[i], 2);
        }
        return true;
    }

    public static boolean log(Object whence, String msg) 
    {
        return log(whence, msg, 1);
    }

    public static boolean log(Object whence, String msg, int depth) 
    {
        StackTraceElement[] stack = (new Exception("")).getStackTrace();
        if (depth < stack.length) {
            display(whence, msg, stack, depth);
        }
        return true;
    }

    public static boolean stack() 
    {
        return stack(null, "", 2, 5);
    }

    public static boolean stack(String msg) 
    {
        return stack(null, msg, 2, 5);
    }

    public static boolean stack(int depth) 
    {
        return stack(null, "", 2, depth);
    }

    public static boolean stack(String msg, int depth) 
    {
        return stack(null, msg, 2, depth);
    }

    public static boolean stack(Object whence, String msg, int startDepth, int numFrames) 
    {
        StackTraceElement[] stack = (new Exception("")).getStackTrace();
        for (int si = startDepth; si < startDepth + numFrames && si < stack.length; ++si) {
            display(whence, si == startDepth ? msg : "\"\"", stack, si);
        }
        return true;
    }

    public static boolean logColor(String color, String msg)
    {
        return log(null, color + msg + NONE, 2);
    }

    public static void setWidths(int file, int line, int func)
    {
        fileWidth = file;
        lineWidth = line;
        funcWidth = func;
    }

    protected static boolean display(Object whence, String msg, StackTraceElement[] stack, int stackIndex)
    {
        if (verbose) {
            String className  = stack[stackIndex].getClassName();
            String fileName   = stack[stackIndex].getFileName();
            int    lineNumber = stack[stackIndex].getLineNumber();
            String methodName = stack[stackIndex].getMethodName();

            // How could Sun take C and leave out printf?

            // should be:
            //     String fmt = "[%" + fileWidth + "s:%" + lineWidth + "d] {%" + funcWidth + "s} %s";
            //     String str = FormattedString.toString(fmt, fileName, lineNumber, className + "#" + methodName, msg);

            StringBuffer flBuf = new StringBuffer("[");
            if (fileName == null || lineNumber == -1) {
                flBuf.append("<source not available>");
            }
            else {
                flBuf.append(fileName).append(":").append(lineNumber);
            }
            
            for (int fi = flBuf.length(); fi < fileWidth - 1 + lineWidth; ++fi) {
                flBuf.append(" ");
            }
            flBuf.append("] ");
            
            StringBuffer cmBuf = new StringBuffer("{");

            if (className.startsWith("com.") || className.startsWith("org.")) {
                int pos = className.indexOf('.', 4);
                className = ".." + className.substring(pos);
            }

            cmBuf.append(className).append("#").append(methodName);
            for (int ci = cmBuf.length(); ci < funcWidth - 2; ++ci) {
                cmBuf.append(" ");
            }
            cmBuf.append("} ");

            String outstr = flBuf.toString() + cmBuf.toString() + msg;
            System.out.println(outstr);
        }
        return true;
    }

}
