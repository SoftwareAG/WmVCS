package com.webmethods.vcs.resources;

import com.webmethods.vcs.VCSLog;
import com.wm.util.B2BListResourceBundle;

public class VCSExceptionBundle extends B2BListResourceBundle {
	// so our entries can start numbering where the IS ones do
	public static final int BASE_NUM = EXCEPTION_BASE_NUM;

	private static final Object[][] CONTENTS = {
		{ VCSLog.EXC_MESSAGE, "{0}" },
		{ VCSLog.EXC_MESSAGEIID, "({0}) {1}" },

        { VCSLog.EXC_EXIT_VALUE_NONZERO,             "nonzero exit value {0}" },
        { VCSLog.EXC_TIMEOUT_LIMIT,                  "Command reached timeout limit {0}" },
        { VCSLog.EXC_PROPERTY_NOT_SET,               "no property set for {0}" },
        { VCSLog.EXC_VSS_FOLDER_NOT_SET,             "no SourceSafe folder property set" },
        { VCSLog.EXC_ERROR_RUNNING_COMMAND,          "error running command {0}; exit value: {1}; output: {2}" },
        { VCSLog.EXC_ERROR_GETTING_LOG,              "error getting log for {0}: {1}" },
        { VCSLog.EXC_NODE_OUT_OF_DATE,               "node out of sync with VCS" },
        { VCSLog.EXC_VSS_FOLDER_DOES_NOT_EXIST,      "folder {0} does not exist" },
        { VCSLog.EXC_VSS_FOLDER_NOT_A_DIRECTORY,     "folder {0} is not a directory" },
        { VCSLog.EXC_NO_WRITE_ACL_PRIVILEGES,        "Cannot perform operation without Write ACL privileges on {0}" },
        { VCSLog.EXC_INVALID_VCS_USER_NAME,          "Invalid VCS user name: {0}" },
        { VCSLog.EXC_INVALID_DEVELOPER_NAME,         "Invalid Developer name: {0}" },
        { VCSLog.EXC_ILLEGAL_LABEL_CHAR,             "Invalid character \"{0}\" in label \"{1}\"" },
        { VCSLog.EXC_FILE_CHECKED_OUT_BY_OTHER_USER, "File {0} checked out by another user" },
        { VCSLog.EXC_NO_SUCH_USER,                   "No such user: {0}" },
        { VCSLog.EXC_NO_USERS_SPECIFIED,             "No users specified" },
        { VCSLog.EXC_INVALID_REVISION,               "Label {0} is not valid value for this package." },
        { VCSLog.EXC_NO_USER_MAPPING,             	 "No VCS user mapping for user {0} " }
    };

	// for IS bundles, this returns "IS"
	public String getProdGroup() {
		return "ISS";
	}

	// on IS bundles this returns "C", "S", or "D"
	public String getProdComponent() {
		return "";
	}

	// on IS bundles, this returns -1
	public int getFacility() {
		return VCSLog.VCS_FAC;
	}

	public Object[][] getContents() {
		return CONTENTS;
	}
}
