package com.webmethods.vcs;

import java.util.Date;
import java.util.List;


/**
 * Common VCS client capability.
 */
public interface VCSClient
{
    /**
     * Returns general VCS information.
     */
    public List getInfo() throws VCSException;

    /**
     * Checks out the files.
     */
    public void checkout(List fileNames) throws VCSException;

    /**
     * Returns the log for the given file.
     */
    public List getLog(String fileName) throws VCSException;

    /**
     * Checks the files in.
     */
    public void checkin(List fileNames, String description) throws VCSException;

    /**
     * Loads the directories by date.
     */
    public void load(List dirNames, List fileNames, Date date) throws VCSException;

    /**
     * Loads the directories by revision.
     */
    public void load(List dirNames, List fileNames, String revision, boolean isInterface) throws VCSException;

    /**
     * Loads the current version of the directories.
     */
    public void load(List dirNames, List fileNames) throws VCSException;

    /**
     * Loads the directories by label.
     */
    public void loadByLabel(List dirNames, List fileNames, String label) throws VCSException;

    /**
     * Deletes the given files.
     */
    public void delete(boolean isPredelete, List fileNames, String comment) throws VCSException;

    /**
     * Reverts the given files.
     */
    public void revert(List fileNames) throws VCSException;

    /**
     * Returns whether the given file is checked out.
     */
    public boolean isCheckedOut(String fileName) throws VCSException;

    /**
     * Returns whether this client must delete existing files before loading an
     * entire directory.
     */
    public boolean mustDeleteBeforeLoad() throws VCSException;

}
