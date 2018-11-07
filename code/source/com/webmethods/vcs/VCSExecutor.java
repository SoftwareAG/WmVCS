package com.webmethods.vcs;

import com.wm.app.b2b.util.ServerIf;
import com.wm.data.*;
import java.util.*;


public class VCSExecutor
{
    private IData errors = null;

    private IData warnings = null;

    private List successfulNodes = null;
    
    private List failedNodes = null;

    private boolean isSuccessful = true;

    private List results = null;

    public VCSExecutor()
    {
        errors          = IDataFactory.create();
        warnings        = IDataFactory.create();
        successfulNodes = new ArrayList();
        failedNodes     = new ArrayList();
        isSuccessful    = true;
        results         = new ArrayList();
    }

	public void addSuccess(String fullNodeName, Object result)
    {
        results.add(result);
        successfulNodes.add(fullNodeName);
    }

    public void addFailure(String fullNodeName, Exception e)
    {
        addFailure(fullNodeName, e.getLocalizedMessage());
    }

    public void addFailure(String fullNodeName, String msg)
    {
        isSuccessful = false;
        failedNodes.add(fullNodeName);
        IDataUtil.put(errors.getCursor(), fullNodeName, msg);
    }

    public void addWarning(String fullNodeName, String msg)
    {
        failedNodes.add(fullNodeName);
        IDataUtil.put(warnings.getCursor(), fullNodeName, msg);
    }

    /**
     * Adds a summary of the successful and failed nodes to the pipeline.
     */
    public void addSummary(IDataCursor cursor)
    {
        String   success                  = new Boolean(isSuccessful).toString();
        String[] successfulNodesAsStrings = (String[])successfulNodes.toArray(new String[0]);
        String[] failedNodesAsStrings     = (String[])failedNodes.toArray(new String[0]);

        IDataUtil.put(cursor, ServerIf.KEY_IS_SUCCESSFUL,     success);
        IDataUtil.put(cursor, ServerIf.KEY_SUCCESSFUL_NODES,  successfulNodesAsStrings);
        IDataUtil.put(cursor, ServerIf.KEY_FAILED_NODES,      failedNodesAsStrings);
        IDataUtil.put(cursor, ServerIf.KEY_ERRORS,            errors);
        IDataUtil.put(cursor, ServerIf.KEY_WARNINGS,          warnings);
        if (results != null) {
            IData[] resultData = (IData[])results.toArray(new IData[0]);
            IDataUtil.put(cursor, ServerIf.KEY_RESULTS, resultData);
        }
    }

}
