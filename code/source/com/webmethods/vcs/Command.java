package com.webmethods.vcs;

import com.webmethods.vcs.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a generic command line command.
 */
public abstract class Command
{
    public final static String TIMEOUT_KEY = "watt.vcs.command.timeout";
    
    private static int timeout = 60000;
    
    private List _input;
    
    private List _output;

    private List _error;

    static {
        String timeoutProp = Config.getInstance().getProperty(TIMEOUT_KEY);
        if (timeoutProp != null) {
            try {
                Integer in = new Integer(timeoutProp.trim());
                timeout = in.intValue();
            }
            catch (NumberFormatException nfe) {
                // use the default timeout.
            }
        }
    }
    
    public Command(List input)
    {
        _input = input;
        _output = new ArrayList();
        _error = new ArrayList();
    }

    public Command()
    {
        this(new ArrayList());
    }

    public List exec() throws VCSException
    {
        return exec(getCommand(), getInput(), getOutput(), getError());
    }

    public int execute() throws VCSException
    {
        return execute(getCommand(), getInput(), getOutput(), getError());
    }
    
    public abstract List getCommand();

    public List getInput()
    {
        return _input;
    }

    public List getOutput()
    {
        return _output;
    }

    public List getError()
    {
        return _error;
    }

    /**
     * Runs the command, but logs the special loggable command instead, and
     * passes back the output and error contents. This method can be used when
     * the command should not be logged; for example, when it contains
     * passwords.
     */
    public static int execute(List command, List logCmd, List input, List output, List error) throws VCSException
    {
        String[] cmdArray = (String[])command.toArray(new String[0]);

        VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "command: " + logCmd);

        try {
            TimedExecutor te = new TimedExecutor(cmdArray, input, timeout);
            if (te.exec()) {
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_EXEC_COMPLETED_NORMALLY);
                if (output != null) {
                    output.addAll(te.getOutput());
                }
                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "stdout: " + te.getOutput());

                if (error != null) {
                    error.addAll(te.getError());
                }
                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "stderr: " + te.getError());

                return te.getExitCode();
            }
            else {
                VCSLog.log(VCSLog.WARN, VCSLog.VCS_EXEC_COMPLETED_TERMINATED);
                throw new VCSException(VCSLog.EXC_TIMEOUT_LIMIT, new Object[] { new Integer(timeout) }, error);
            }
        }
        catch (IOException ioe) {
        	//Explicitly set the msg to null;otherwise VCSException looks up to msg as the key in the RB. 
            throw new VCSException(ioe, null,error);
        }
        catch (InterruptedException ie) {
        	//Explicitly set the msg to null;otherwise VCSException looks up to msg as the key in the RB.
            throw new VCSException(ie, error);
        }
    }
    
    
    public static int execute(List<String> command, List<String> files, List<String> logCmd, List<String> input, List<String> output, List<String> error) throws VCSException
    {
    	//try the usual execute first. 
    	List<String> commandList = new ArrayList<String>(command);
    	List<String> logList = new ArrayList<String>(logCmd);
    	int returnCode = 0;  
    	commandList.addAll(files);
    	logList.addAll(files);
    	boolean switchToAlternate = false;
    	try{
    		returnCode = execute(commandList, logList, input, output, error);
    	}catch(VCSException e){
    		Throwable cause = e.getCause();
    		if (cause != null && cause.getMessage() != null && cause.getMessage().contains("CreateProcess error=")){
    			switchToAlternate = true;
    		}else{
    			throw e;
    		}
    	}
    	
    	if (switchToAlternate){
    		VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "Switching to per-file checkin mode ");
    		//Get the alternate approach
    		List<String> thisCmd = null;
    		List<String> thisLog = null;
    		for (String file : files) {
				thisCmd = new ArrayList<String>(command);
				thisLog = new ArrayList<String>(logCmd);
				thisCmd.add(file);
				thisLog.add(file);
				try{
					int rtnVal = execute(thisCmd,thisLog,input,output,error);
					if (rtnVal != 0){
						returnCode = rtnVal;
					}
					
				}catch(VCSException e){
					throw e;
				}
				thisCmd = null;
				thisLog = null;
			}
    	}
    	
    	//If all the cmd executions go well with no error, the returnCode would be ZERO,even for a list of files given
    	return returnCode;
    }
    
    
    /**
     * Runs the command and passes back the output and error contents. Unlike
     * <code>exec</code>, this method does not throw an exception if the exit
     * value of the command is not zero.
     */
    public static int execute(List command, List input, List output, List error) throws VCSException
    {
        return execute(command, command, input, output, error);
    }

    /**
     * Runs the command, logging <code>logCmd</code> instead of
     * <code>command</code>, and passes back the output and error contents. 
     * Throws a VCSException if the exit value of the executed command is not
     * zero.
     */
    public static List exec(List command, List logCmd, List input, List output, List error) throws VCSException
    {
        int exitValue = execute(command, logCmd, input, output, error);
        if (exitValue != 0) {
            throw new VCSException(VCSLog.EXC_ERROR_RUNNING_COMMAND, new Object[] { logCmd, new Integer(exitValue), error }, error);
        }
        else {
            return output;
        }
    }

    public static List exec(List command,List files, List logCmd, List input, List output, List error) throws VCSException
    {
        int exitValue = execute(command,files, logCmd, input, output, error);
        if (exitValue != 0) {
            throw new VCSException(VCSLog.EXC_ERROR_RUNNING_COMMAND, new Object[] { logCmd, new Integer(exitValue), error }, error);
        }
        else {
            return output;
        }
    }    
    
    /**
     * Runs the command and passes back the output and error contents. Throws a
     * VCSException if the exit value of the executed command is not zero.
     */
    public static List exec(List command, List input, List output, List error) throws VCSException
    {
        return exec(command, command, input, output, error);
    }

}
