package com.webmethods.vcs;

import java.util.*;
// import org.apache.oro.io.GlobFilenameFilter;

/**
 * A single VCS client, initialized to an VCSClient instance, depending on the
 * system type property.
 */
public class VCSManager implements VCSClient
{
    public final static String SYSTEM_TYPE_KEY = "watt.vcs.type";
    
    private static VCSManager instance = null;

    private List clients = new ArrayList();

    /**
     * Maps from a client name to the list of packages (as strings) that the
     * client handles.
     */
    private Map packages = new HashMap();

    /** 
     * Singleton accessor.
     */
    public static VCSManager getInstance()
    {
        if (instance == null) {
            instance = new VCSManager();
        }
        return instance;
    }

    private VCSManager()
    {
    }

    /**
     * Returns whether this manager has a client.
     */
    public boolean hasClient()
    {
        return clients.size() > 0;
    }
    
    /**
     * Sets the client.
     */
    public void setClient(VCSClient client, String[] pkgs)
    {
        addClient(client, pkgs);
    }

    /**
     * Adds the given client.
     */
    public void addClient(VCSClient client, String[] pkgs)
    {
        clients.add(client);
        packages.put(client, pkgs);
    }

    /**
     * Returns general VCS information.
     */
    public List getInfo() throws VCSException
    {	
    	return getClient().getInfo();
    }

    /**
     * Checks out the files.
     */
    public void checkout(List fileNames) throws VCSException
    {
    	if(!hasClient()){
    		VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
    		return;
    	}
        getClient().checkout(fileNames);
    }

    /**
     * Returns the log for the given file.
     */
    public List getLog(String fileName) throws VCSException
    {
    	return getClient().getLog(fileName);
    }

    /**
     * Checks the files in.
     */
    public void checkin(List fileNames, String description) throws VCSException
    {
    	if(!hasClient()){
    		VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
    		return;
    	}
        getClient().checkin(fileNames, description);
    }

    /**
     * Loads the directories by date.
     *
     * @param dirNames The directory names.
     * @param date The date.
     */
    public void load(List dirNames, List fileNames, Date date) throws VCSException
    {
    	if(!hasClient()){
    		VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
    		return;
    	}
        getClient().load(dirNames, fileNames, date);
    }

    /**
     * Loads the directories by revision.
     *
     * @param dirNames The directory names.
     * @param revision The revision.
     * @param isInterface Whether the given directories represent an interface.
     */
    public void load(List dirNames, List fileNames, String revision, boolean isInterface) throws VCSException
    {
    	if(!hasClient()){
    		VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
    		return;
    	}
        getClient().load(dirNames, fileNames, revision, isInterface);
    }

    /**
     * Loads the directories by label.
     *
     * @param dirNames The directory names.
     * @param label The label.
     */
    public void loadByLabel(List dirNames, List fileNames, String label) throws VCSException
    {
    	if(!hasClient()){
    		VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
    		return ;
    	}
        getClient().loadByLabel(dirNames, fileNames, label);
    }

    /**
     * Loads the current version of the directories.
     */
    public void load(List dirNames, List fileNames) throws VCSException
    {
    	if(!hasClient()){
    		VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
    		return;
    	}
        getClient().load(dirNames, fileNames);
    }

    /**
     * Deletes the given files.
     *
     * @param fileNames The file names.
     */
    public void delete(boolean isPredelete, List fileNames, String comment) throws VCSException
    {
    	if(!hasClient()){
    		VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
    		return;
    	}
        getClient().delete(isPredelete, fileNames, comment);
    }

    /**
     * Reverts the given files.
     *
     * @param fileNames The file names.
     */
    public void revert(List fileNames) throws VCSException
    {
    	if(!hasClient()){
    		VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
    		return ;
    	}
        getClient().revert(fileNames);
    }

    /**
     * Returns whether the given file is checked out.
     */
    public boolean isCheckedOut(String fileName) throws VCSException
    {
    	return getClient().isCheckedOut(fileName);
    }

    /**
     * Returns whether the client must delete existing files before loading an
     * entire directory.
     */
    public boolean mustDeleteBeforeLoad() throws VCSException
    {
        return getClient().mustDeleteBeforeLoad();
    }

    /**
     * Returns the client.
     */
    public VCSClient getClient()
    {
        return (VCSClient)clients.get(0);
    }

    public Map getClientHandlers(Collection files)
    {
        Map handlers = new HashMap();

        Iterator fit = files.iterator();
        while (fit.hasNext()) {
            String fname = (String)fit.next();
            Iterator cit = clients.iterator();
            while (cit.hasNext()) {
                VCSClient client = (VCSClient)cit.next();
                String[] pkgs = (String[])packages.get(client);
                for (int pi = 0; pi < pkgs.length; ++pi) {
                    String pkg = pkgs[pi];
                }
            }
        }

        return handlers;
    }

}
