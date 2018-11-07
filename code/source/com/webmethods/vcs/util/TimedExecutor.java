package com.webmethods.vcs.util;

import com.webmethods.io.StreamExt;
import java.io.*;
import java.util.*;


/**
 * Runs an executable for a given amount of time, then terminates it if it
 * hasn't ended.
 */
public class TimedExecutor
{
    private Process process;

    private String[] cmd;

    private List input;

    private int timeLimit;
    
    private List output;

    private List error;

    private int exitCode;

    public TimedExecutor(String[] cmd, List input, int timeLimit)
    {
        this.cmd = cmd;
        this.timeLimit = timeLimit;
        this.input = input;
    }

    public List getOutput()
    {
        return output;
    }

    public List getError()
    {
        return error;
    }

    public int getExitCode()
    {
        return exitCode;
    }
    
    public boolean exec() throws IOException, InterruptedException
    {
        try {
            process = Runtime.getRuntime().exec(cmd);

            ProcessDestroyer pd = null;
            if (timeLimit > 0) {
                pd = new ProcessDestroyer(process, timeLimit);
                pd.start();
            }

            if (input == null || input.size() == 0) {
                // no input
            }
            else {
                StreamExt.write(process.getOutputStream(), input);
            }
            read(process);
            exitCode = process.waitFor();
            if (pd != null) {
                pd.interrupt();
            }
            return pd == null || !pd.wasDestroyed();
        }
        finally {
            if (process != null) {
                close(process.getOutputStream());
            }
        }
    }

    protected void close(OutputStream os) throws IOException
    {
        if (os != null) {
            os.close();
        }
    }

    protected void close(InputStream is) throws IOException
    {
        if (is != null) {
            is.close();
        }
    }

    /**
     * Reads the stream into the list, where each element is a line of output.
     * @throws InterruptedException 
     */
    public void read(Process process) throws IOException, InterruptedException
	{
    	output = new ArrayList();
	    StreamReaderThread outputReader = new StreamReaderThread(process.getInputStream(), output );
	    error = new ArrayList();
	    StreamReaderThread errorReader = new StreamReaderThread(process.getErrorStream(), error);
	    outputReader.start();
        errorReader.start();
        outputReader.waitFor();
        errorReader.waitFor();
    }

    public static void main(String[] args)
    {
        for (int ai = 0; ai < args.length; ++ai) {
            System.out.println("arg[" + ai + "]: " + args[ai]);
        }

        try {
            TimedExecutor ex = new TimedExecutor(args, null, 500);
            ex.exec();
        }
        catch (Throwable t) {
            t.printStackTrace(System.out);
        }
    }  

}
