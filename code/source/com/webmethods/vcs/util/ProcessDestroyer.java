package com.webmethods.vcs.util;

import java.io.*;


public class ProcessDestroyer extends Thread
{
    private Process process;
    private long timeout;
    private boolean destroyed = false;

    public ProcessDestroyer(Process p, long timeout)
    {
        this.process = p;
        this.timeout = timeout;
    }

    public void run()
    {
        try {
            sleep(timeout);
            synchronized (this) {
                process.destroy();
                destroyed = true;
            }
        }
        catch (InterruptedException ie) {
            // interrupted, just like we should be
        }
    }

    public boolean wasDestroyed()
    {
        return destroyed;
    }

}
