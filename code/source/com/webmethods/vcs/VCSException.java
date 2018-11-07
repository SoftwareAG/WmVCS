package com.webmethods.vcs;

import com.webmethods.vcs.resources.VCSExceptionBundle;
import com.wm.util.LocalizedException;
import java.util.List;


/**
 * Represents an exception within the VCS framework.
 */
public class VCSException extends LocalizedException
{
    private List _errorOutput;
 
	// this is the class of the resource bundle
	public static final Class BUNDLE = VCSExceptionBundle.class;

	// this is a parameter to the constructor of the LocalizedException
	// that (as far as I can tell) is completely unused
	public static final String SOURCEID = "VCS";

    public VCSException(Throwable cause, String msg, Object[] params, List errorOutput)
    {
		super(BUNDLE, msg, SOURCEID, cause, params);
        
        _errorOutput = errorOutput;

        if (msg == null) {
            if (errorOutput != null && errorOutput.size() > 0) {
                VCSLog.log(VCSLog.ERROR, VCSLog.VCS_GENERAL_ERROR, "error output: " + errorOutput);
            }
        }
        else {
            VCSLog.log(VCSLog.ERROR, VCSLog.VCS_GENERAL_ERROR, "message: " + getMessage());
        }
    }
    
    public VCSException(Throwable cause, String msg, List errorOutput)
    {
		this(cause, msg, null, errorOutput);
    }

    public VCSException(Throwable cause, List errorOutput)
    {
        this(cause, cause.getMessage(), null, errorOutput);
    }

    public VCSException(Throwable cause)
    {
        this(cause, cause.getMessage(), null, null);
    }

    public VCSException(String msg, Object[] params, List errorOutput)
    {
        this(null, msg, params, errorOutput);
    }

    public VCSException(String msg, List errorOutput)
    {
        this(null, msg, null, errorOutput);
    }

    public VCSException(String msg, Object[] params)
    {
        this(null, msg, params, null);
    }

    public VCSException(String msg)
    {
        this(null, msg, null, null);
    	
    }
    
    public VCSException(String msg,boolean isLocalizedMessage)
    {
       super(msg);
    	
    }
    
    public List getErrorOutput()
    {
        return _errorOutput;
    }
    
   
}
