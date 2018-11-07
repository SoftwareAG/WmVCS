/**
 * 
 */
package com.webmethods.vcs.util;

import java.util.*;
import java.io.*;

/**
 * @author inaja
 *
 */
public class StreamReaderThread extends Thread {
    InputStream is;
    List<String> output;
    private boolean finished;
    
    public StreamReaderThread(InputStream is, List<String> type){
        this.is = is;
        this.output = type;
    }
    
    public void run() {
        try{
        	synchronized (this) {
                this.finished = false;
            }
        	if (is == null) {
                System.out.println("no stream: " + is);
                return;
            }
            else if (is instanceof FileInputStream) {
                FileInputStream fis = (FileInputStream)is;
                if (!fis.getFD().valid()) {
                    return;
                }
            }
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null){
            	output.add(line);    
            }
        }
        catch (IOException ioe){
           ioe.printStackTrace();  
        }finally{
        	close(is);
        	synchronized (this) {
                this.finished = true;
                notify();
            }
        }
        
    }
    
    public synchronized boolean isFinished() {
        return this.finished;
    }
    public synchronized void waitFor()
	    throws InterruptedException {
	    while (!isFinished()) {
	        wait();
	    }
	}
    
    protected void close(InputStream is) {	
    	try{
	        if (is != null) {
	            is.close();
	        }
    	}catch (IOException ioe){
          //No action  
         }
    }
}
