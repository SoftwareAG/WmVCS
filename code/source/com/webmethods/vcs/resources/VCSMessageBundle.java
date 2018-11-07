package com.webmethods.vcs.resources;

import java.util.ListResourceBundle;
import com.webmethods.vcs.VCSLog;


/**
 * Log messages for the server runtime.  Each entry is a twofer: the
 * first element is an ID in the form "X.Y" where X is the log facility
 * an Y is a message number.  We have four facilities corresponding to
 * the major components of the VCS.  They are define as static constants
 * in the JournalLogger class.  IMPORTANT: this class is referenced by
 * com.wm.util.JournalLogger; if you rename it, update the reference!
 */
public class VCSMessageBundle extends ListResourceBundle
{
	// actual messages go in this array.  observe the way keys are
	// formed so as to avoid collisions.
	private static final Object [][] CONTENTS =
    {
		// general
		{ VCSLog.string(VCSLog.VCS_GENERAL_LINE), "-----------------------------" },
		{ VCSLog.string(VCSLog.VCS_GENERAL_ERROR), "ERROR: {0}" },
		{ VCSLog.string(VCSLog.VCS_GENERAL_MESSAGE), "{0}" },

        { VCSLog.string(VCSLog.VCS_PKG_INITIALIZING), "VCS package initializing" },
        { VCSLog.string(VCSLog.VCS_PKG_SHUTTING_DOWN), "VCS package shutting down" },
        { VCSLog.string(VCSLog.VCS_PKG_LOADING_CONFIG_FILE), "loading configuration file: {0}" },
        { VCSLog.string(VCSLog.VCS_PKG_NO_CONFIG_FILE), "no configuration file" },
        { VCSLog.string(VCSLog.VCS_PKG_ERROR_LOADING_CONFIG_FILE), "error loading configuration file: {0}" },
        { VCSLog.string(VCSLog.VCS_PKG_ENABLED), "VCS enabled" },
        { VCSLog.string(VCSLog.VCS_PKG_NOT_ENABLED), "VCS not enabled" },
        { VCSLog.string(VCSLog.VCS_PKG_LOADING_USERS_FILE), "loading users file: {0}" },
        { VCSLog.string(VCSLog.VCS_PKG_NO_USERS_FILE), "no users file" },
        { VCSLog.string(VCSLog.VCS_PKG_ERROR_LOADING_USERS_FILE), "error loading users file: {0}" },
        { VCSLog.string(VCSLog.VCS_PKG_SAVING_USERS_FILE), "saving users file: {0}" },
        { VCSLog.string(VCSLog.VCS_PKG_ERROR_SAVING_USERS_FILE), "error saving users file: {0}" },

        { VCSLog.string(VCSLog.VCS_PKG_GETTING_INFO), "retrieving information from VCS server" },
        { VCSLog.string(VCSLog.VCS_PKG_NO_CONFIG_FILE_READ), "no configuration file read for package: {0}; expecting: {1}" },

        { VCSLog.string(VCSLog.VCS_PKG_OVERWRITING_EXISTING_PROPERTY), "overwriting existing property: {0}; new value: {1}" },

        { VCSLog.string(VCSLog.VCS_FILE_NOT_CHECKED_OUT), "file not checked out: {0}" },
        { VCSLog.string(VCSLog.VCS_FILE_CHECKED_OUT), "file checked out: {0}" },
        { VCSLog.string(VCSLog.VCS_FILE_OUT_OF_DATE), "file out of date: {0}" },
        { VCSLog.string(VCSLog.VCS_FILE_CHECKED_OUT_BY_OTHER_USER), "file checked out by another user: {0}" },

		// --------------------------------------
		// client
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_CLIENT_SETTING), "setting VCS client as {0}" },
        { VCSLog.string(VCSLog.VCS_CLIENT_NONE), "no current VCS client" },
        { VCSLog.string(VCSLog.VCS_CLIENT_INVALID), "invalid VCS client: {0}" },
        { VCSLog.string(VCSLog.VCS_CLIENT_TYPE_NOT_SET), "no VCS client type set" },

		// --------------------------------------
		// load
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_LOAD_STARTING), "load nodes starting" },
        { VCSLog.string(VCSLog.VCS_LOAD_DONE), "load nodes done" },
        { VCSLog.string(VCSLog.VCS_LOAD_FILES_TO_DELETE), "files to delete: {0}" },

		// --------------------------------------
		// delete
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_DELETE_STARTING), "delete nodes starting" },
        { VCSLog.string(VCSLog.VCS_DELETE_DONE), "delete nodes done" },
        { VCSLog.string(VCSLog.VCS_DELETE_DEFAULT_COMMENT), "Deleted by %value dev_user% on %value is_host% at %value is_time%" },
        { VCSLog.string(VCSLog.VCS_DELETE_DEFAULT_METACOMMENT), "%value usercomment%%nl%dev_user : %value dev_user%%nl%is_host : %value is_host%%nl%dev_host: %value dev_host%%nl%is_time    : %value is_time%" },
        { VCSLog.string(VCSLog.VCS_DELETE_FAILED_REVERTING), "delete failed; attempting to revert from opened list" },

		// --------------------------------------
		// checkout
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_CHECKOUT_STARTING), "checkout nodes starting" },
        { VCSLog.string(VCSLog.VCS_CHECKOUT_DONE), "checkout nodes done" },
        { VCSLog.string(VCSLog.VCS_CHECKOUT_IGNORING_BUILD_MODE), "not locking in build mode (no package specified)" },
        { VCSLog.string(VCSLog.VCS_CHECKOUT_IGNORING_DELETE_MODE), "not locking in delete mode" },
        { VCSLog.string(VCSLog.VCS_CHECKOUT_FROM_VCS), "running checkout from VCS" },

		// --------------------------------------
		// checkin
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_CHECKIN_STARTING), "checkin nodes starting" },
        { VCSLog.string(VCSLog.VCS_CHECKIN_DONE), "checkin nodes done" },
        { VCSLog.string(VCSLog.VCS_CHECKIN_NO_NODE_DATA), "no node data" },
        { VCSLog.string(VCSLog.VCS_CHECKIN_DEFAULT_COMMENT), "Checked in by %value dev_user% on %value is_host% at %value is_time%" },
        { VCSLog.string(VCSLog.VCS_CHECKIN_DEFAULT_METACOMMENT), "%value usercomment%%nl%dev_user : %value dev_user%%nl%is_host : %value is_host%%nl%dev_host: %value dev_host%%nl%is_time    : %value is_time%" },
        { VCSLog.string(VCSLog.VCS_CHECKIN_TO_VCS), "running checkin to VCS" },

		// --------------------------------------
		// getlogs
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_GETLOGS_STARTING), "getlogs starting" },
        { VCSLog.string(VCSLog.VCS_GETLOGS_DONE), "getlogs done" },

		// --------------------------------------
		// getfilestodelete
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_GETFILESTODELETE_STARTING), "getFilesToDelete starting" },
        { VCSLog.string(VCSLog.VCS_GETFILESTODELETE_DONE), "getFilesToDelete done" },

		// --------------------------------------
		// predelete
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_PREDELETE_STARTING), "preDelete starting" },
        { VCSLog.string(VCSLog.VCS_PREDELETE_DONE), "preDelete done" },
        { VCSLog.string(VCSLog.VCS_PREDELETE_SKIPPING), "not deleting files during preDelete" },

		// --------------------------------------
		// getcommenttext
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_GETCOMMENTTEXT_STARTING), "getCommentText starting" },
        { VCSLog.string(VCSLog.VCS_GETCOMMENTTEXT_DONE), "getCommentText done" },

		// --------------------------------------
		// ischeckedout
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_ISCHECKEDOUT_STARTING), "isCheckedOut starting" },
        { VCSLog.string(VCSLog.VCS_ISCHECKEDOUT_DONE), "isCheckedOut done" },

		// --------------------------------------
		// submit
		// --------------------------------------
        { VCSLog.string(VCSLog.VCS_SUBMIT), "submitting changelist" },
        { VCSLog.string(VCSLog.VCS_SUBMIT_NO_ENTRIES), "No entries to submit" },

        { VCSLog.string(VCSLog.VCS_LOAD_DIRECTORIES), "loading directories: {0}; version: {1}" },

        { VCSLog.string(VCSLog.VCS_EXEC_COMPLETED_NORMALLY), "exec completed normally" },
        { VCSLog.string(VCSLog.VCS_EXEC_COMPLETED_TERMINATED), "exec terminated" },

        { VCSLog.string(VCSLog.VCS_P4_CLIENT_NOT_FOUND), "Perforce client not found" },
        { VCSLog.string(VCSLog.VCS_P4_USER_NOT_FOUND), "Perforce user not found" },

        { VCSLog.string(VCSLog.VCS_EXEC_COMMAND), "For {0}, command: {1}" },

        { VCSLog.string(VCSLog.VCS_FILE_ADDING), "Adding file {0}" },
        { VCSLog.string(VCSLog.VCS_PARENT_EXISTS), "Parent exists: {0}" },
        { VCSLog.string(VCSLog.VCS_PARENT_NOT_EXISTS), "Parent does not exist: {0}" },

        { VCSLog.string(VCSLog.VCS_NO_SUCH_NODE), "Node not found for version" },

		// --------------------------------------
		// -- Config Settings
		// --------------------------------------
		// -- UI messages for the config page; don't need to use Journal logger for these.
		{ "acs.noPropsLoad", "There are no properties to load. File does not exist.  Creating file and setting to default properties." },
		{ "acs.errorSave", "Error in saving configuration settings for VCS" },
		{ "acs.propsSaved", "New VCS properties have been saved." },
		{ "acs.defaultSaved", "VCS properties have been reset to the default properties."},
		{ "acs.reloadVCS", "Reload WmVCS package for changes to take effect." },
	};

	public Object[][] getContents()
    {
		return CONTENTS;
	}

}
