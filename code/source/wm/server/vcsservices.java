
// -----( IS Java Code Template v1.2
// -----( CREATED: 2007-06-06 14:20:03 GMT+05:30
// -----( ON-HOST: xpsingh-opt170.webm.webmethods.com
package wm.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.webmethods.vcs.Config;
import com.webmethods.vcs.util.Users;
import com.wm.app.b2b.server.Server;
import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.util.ValidationException;
import com.wm.util.ValidationsUtil;
import com.wm.util.Values;

public final class vcsservices

{
	// ---( internal utility methods )---

	final static vcsservices _instance = new vcsservices();

	static vcsservices _newInstance() {
		return new vcsservices();
	}

	static vcsservices _cast(Object o) {
		return (vcsservices) o;
	}

	// ---( server methods )---

	/**
	 * The service is used for mapping multiple Integration Server users to respective VCS Users
	 * @param isUsername - Integration Server Username to be mapped with VCS Username
	 * @param vcsUsername - VCS Username to be mapped by Integration Server Username (One VCS Username
	 * can be mapped by multiple Integration Server Username)
	 * @param pwdVCS - Password for VCS Username
	 * Optional field, if provided then will be used for authentication
	 * @note respective file is getting updated with the new data
	 * @exception com.wm.app.b2b.server.ServiceException
	 */

	public static final void addMultipleUsers(IData pipeline)
			throws ServiceException {
		// --- <<IS-START(addMultipleUsers)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required isUsersname
		// [i] field:0:required vcsUsername
		// [i] field:0:required pwdVCS hints[{field_usereditable,true},{field_largerEditor,false},{field_password,true}]
		// pipeline

		IDataCursor pipelineCursor = pipeline.getCursor();
		String isUser = IDataUtil.getString(pipelineCursor, "isUsername").trim();
		String vcsUser = IDataUtil.getString(pipelineCursor, "vcsUsername").trim();
		String pwd = IDataUtil.getString(pipelineCursor, "pwdVCS").trim();
		String[] userPass = isUser.split("\n");
		int added_count = 0;
		try {
			for (int i = 0; i < userPass.length; i++) {
				String[] pair = userPass[i].trim().split(";");
                if (pair.length == 0) {
                    continue;
                }
                
                String isUserName  = pair[0].trim();
                String vcsUserName = pair.length > 1 ? pair[1].trim() : vcsUser;
                String password    = pair.length > 2 ? pair[2].trim() : pwd;

                if (!isUserName.isEmpty() && !vcsUserName.isEmpty() && !password.isEmpty()) {
                    ValidationsUtil.checkForEndOfLineCharacters("IS user name", isUserName);
                    ValidationsUtil.checkForEndOfLineCharacters("VCS user name", vcsUserName);
                        
                    Users.getInstance().setUser(isUserName, vcsUserName, password);
                    Users.getInstance().writeFile();
                    added_count++;
                }
			}
			String count = String.valueOf(added_count);
			pipelineCursor.insertAfter("addedCounts", count);

		}
        catch (IOException | ValidationException ex) {
			throw new ServiceException(ex);
		}
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

	}

	/**
	 * The service is used for mapping single Integration Server user to VCS User
	 * @param isUsername - Integration Server Username to be mapped with VCS Username
	 * @param vcsUsername - VCS Username to be mapped by Integration Server Username
	 * @param pwdVCS - Password for VCS Username
	 * @note respective file is getting updated with the new data
	 * Optional field, if provided then will be used for authentication
	 * @exception com.wm.app.b2b.server.ServiceException
	 */

	public static final void addUser(IData pipeline) throws ServiceException {
		// --- <<IS-START(addUser)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required isUsername
		// [i] field:0:required vcsUsername
		// [i] field:0:required pwdVCS hints[{field_usereditable,true},{field_largerEditor,false},{field_password,true}]
		// pipeline

		IDataCursor pipelineCursor = pipeline.getCursor();
		String isUser = IDataUtil.getString(pipelineCursor, "isUsername");
		String vcsUser = IDataUtil.getString(pipelineCursor, "vcsUsername");
		String password = IDataUtil.getString(pipelineCursor, "pwdVCS");
		try {
			if (isUser != null) {
                ValidationsUtil.checkForEndOfLineCharacters("IS user name", isUser);
                ValidationsUtil.checkForEndOfLineCharacters("VCS user name", vcsUser);
                
				Users.getInstance().setUser(isUser, vcsUser, password);
				Users.getInstance().writeFile();
			}
		}
        catch (IOException | ValidationException ex) {
			throw new ServiceException(ex);
		}
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

	}

	/**
	 * The service is used for configuration of ClearCase settings
	 * @param timeout - Command Timeout, This value specifies the number of milliseconds a command is allowed to
	 * execute before termination,The default value is 60000 (one minute).
	 * @param ccFolder - ClearCase View Directory , If the working folder is specified,
	 * the value in this field refers to the mapping folder in a ClearCase Dynamic or Snapshot view.
	 * @param cWorkingFolder - ClearCase Working Directory, If the user is working on files that are
	 * residing on a folder which is not part of a Dynamic or Snapshot view, the name of the folder can be specified
	 * here. This field is optional and if this field is left blank, the Integration Server assumes that the files
	 * are present in a Dynamic or SnapShot view.
	 * @param checkOutMode - feature provided by ClearCase for reserving and unreserving the branch
	 * @param branchName - ClearCase branch name, If the files that need to be version controlled is not residing on
	 * the main branch of the ClearCase, the user can specify the branch name. The field is optional and if the Branch
	 * Name is not specified; by default the main branch will be considered
	 * @note all settings are saved in respective file
	 * @exception com.wm.app.b2b.server.ServiceException
	 */

	public static final void clearcaseConfiguration(IData pipeline)
			throws ServiceException {
		// --- <<IS-START(clearcaseConfiguration)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required ccFolder
		// [i] field:0:required cWorkingFolder
		// [i] field:0:required checkOutMode
		// [i] field:0:required branchName

		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String timeout = IDataUtil.getString(pipelineCursor, "timeout");
		String ccFolder = IDataUtil.getString(pipelineCursor, "ccFolder");
		String cWorkingFolder = IDataUtil.getString(pipelineCursor,
				"cWorkingFolder");
		String checkOutMode = IDataUtil.getString(pipelineCursor,
				"checkOutMode");
		String branchName = IDataUtil.getString(pipelineCursor, "branchName");
		try {
			Map timeOut = new HashMap();
			timeOut.put("watt.vcs.command.timeout", timeout);
			Config.getInstance().writeProperties("WmVCS", "vcs.cnf", timeOut);

			Map clearcaseList = new HashMap();
			clearcaseList.put("watt.vcs.cc.view.rootdirectory", ccFolder);
			clearcaseList.put("watt.vcs.cc.workingdirectory", cWorkingFolder);
			clearcaseList.put("watt.vcs.cc.checkoutmode", checkOutMode);
			clearcaseList.put("watt.vcs.cc.branch", branchName);
			Config.getInstance().writeProperties("WmClearCase",
					"clearcase.cnf", clearcaseList);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

	}

	/**
	 * The service is used for deleting the Integration Server user mapping with VCS USer
	 * @param isUsername - Integration Server Username to be mapped with VCS Username
	 * @note updated settings are updated in the respective file
	 * @exception com.wm.app.b2b.server.ServiceException
	 */

	public static final void deleteUser(IData pipeline) throws ServiceException {
		// --- <<IS-START(deleteUser)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required isUsername
		// pipeline

		IDataCursor pipelineCursor = pipeline.getCursor();
		String isUser = IDataUtil.getString(pipelineCursor, "isUsername");
		try {
			if (isUser != null) {
				Users.getInstance().removeUser(isUser);
				Users.getInstance().writeFile();
			} else {

			}

		} catch (IOException ioe) {
			throw new ServiceException(ioe);
		}
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

	}

	/**
	 * The service is used for reading the various stored configuration of Version Control System type,
	 * Microsoft Visual SourceSafe settings, ClearCase settings and Subversion settings
	 * @note all settings are put in the pipeline
	 * @exception com.wm.app.b2b.server.ServiceException
	 */

	public static final void readVCS(IData pipeline) throws ServiceException {
		// --- <<IS-START(readVCS)>> ---
		// @subtype unknown
		// @sigtype java 3.5

		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		Values out = null;
		Map vssMap = null;
		Map clearcaseMap = null;
		Map vcsMap = null;
		Map svnMap = null;
		String clearcase = null;
		String timeout = null;
		String vssWorkingFolder = null;
		String vssProjectName = null;
		String vssCheckoutMode = null;
		String vcsType = null;
		String clearcaseViewDir = null;
		String clearcaseWorkingFolder = null;
		String clearcaseBranchName = null;
		String checkoutMode = null;
		String svnConfigured = null;
		String svnRepositoryLocation = null;
		vcsMap = Config.getInstance().readProperties("WmVCS", "vcs.cnf");

		// VCS specific configurations:
		vssMap = Config.getInstance().readProperties("WmSourceSafe",
				"sourcesafe.cnf");
		clearcaseMap = Config.getInstance().readProperties("WmClearCase",
				"clearcase.cnf");
		svnMap = Config.getInstance().readProperties("WmSubversion", "subversion.cnf");

		out = new Values(System.getProperties());

		// Checking VCS type
		if (vcsMap != null) {
			if (vcsMap.containsKey("watt.vcs.type"))
				vcsType = vcsMap.get("watt.vcs.type").toString();
			if (vcsType == null) {
				vcsType = "---None---";
			} else if (vcsType.equals("vss")) {
				vcsType = "Microsoft Visual SourceSafe";
			} else if (vcsType.equals("clearcase")) {
				vcsType = "ClearCase";
			} else if(vcsType.equals("svn")){
				vcsType = "Subversion";
			} else {
				vcsType = "---None---";
			}
		}

		// VSS Configuration
		if (vssMap != null) {
			timeout = (String) vcsMap.get("watt.vcs.command.timeout");
			vssWorkingFolder = (String) vssMap.get("watt.vcs.vss.folder");
			vssProjectName = (String) vssMap.get("watt.vcs.vss.project");
			vssCheckoutMode = (String) vssMap
					.get("watt.vcs.vss.allowmultiplecheckouts");
		}

		// ClearCase Configuration
		if (clearcaseMap != null) {
			timeout = (String) vcsMap.get("watt.vcs.command.timeout");
			clearcaseViewDir = (String) clearcaseMap
					.get("watt.vcs.cc.view.rootdirectory");
			;
			clearcaseWorkingFolder = (String) clearcaseMap
					.get("watt.vcs.cc.workingdirectory");
			clearcaseBranchName = (String) clearcaseMap
					.get("watt.vcs.cc.branch");
			checkoutMode = (String) clearcaseMap
					.get("watt.vcs.cc.checkoutmode");
		}

		// Subversion Configuration
		if(svnMap != null){
			svnConfigured = (String)svnMap.get("watt.vcs.svn.configured");
			if(!"true".equalsIgnoreCase(svnConfigured)){
				svnConfigured = Boolean.FALSE.toString();
			}
			svnRepositoryLocation = (String)svnMap.get("watt.vcs.svn.svnRepositoryLocation");
		}

		pipelineCursor.insertAfter("vcsList", vcsMap);
		pipelineCursor.insertAfter("vssList", vssMap);
		pipelineCursor.insertAfter("svnList", svnMap);
		pipelineCursor.insertAfter("clearcaseList", clearcaseMap);
		pipelineCursor.insertAfter("vcsType", vcsType);
		pipelineCursor.insertAfter("timeout", timeout);
		pipelineCursor.insertAfter("vssWorkingFolder", vssWorkingFolder);
		pipelineCursor.insertAfter("vssProjectName", vssProjectName);
		pipelineCursor.insertAfter("vssCheckoutMode", vssCheckoutMode);
		pipelineCursor.insertAfter("clearcaseViewDir", clearcaseViewDir);
		pipelineCursor.insertAfter("clearcaseWorkingFolder",
				clearcaseWorkingFolder);
		pipelineCursor.insertAfter("clearcaseBranchName",
				clearcaseBranchName);
		pipelineCursor.insertAfter("checkoutMode", checkoutMode);
		pipelineCursor.insertAfter("svnConfigured", svnConfigured);
		pipelineCursor.insertAfter("svnRepositoryLocation", svnRepositoryLocation);
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

	}

	/**
	 * The service is used for saving type of Version Control System selected by the user
	 * @param vcs - Version Control System type, among three options Microsoft Visual SourceSafe, ClearCase, Subversion or None user
	 * can use any of them
	 * @note Version Control System type settings is saved in respective file
	 * @exception com.wm.app.b2b.server.ServiceException
	 */

	public static final void saveVCSType(IData pipeline)
			throws ServiceException {
		// --- <<IS-START(saveVCSType)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required vcs
		// pipeline

		IDataCursor pipelineCursor = pipeline.getCursor();
		String vcs = IDataUtil.getString(pipelineCursor, "vcs");
		String message = null;
		ResourceBundle bundle = BundleUtil
				.getBundle("com.wm.app.b2b.util.resources.SupportInfoBundle");
		Map<String, String> vcsList = new LinkedHashMap<String, String>();

		String lockingMode = "full";
		String vcsType = "none";

		if (vcs != null) { // vcs is either: vss, clearCase or svn
			if(vcs.equals("vss")){
				lockingMode = "vcs";
				vcsType = "vss";
			}
			else if(vcs.equals("clearCase")){
				lockingMode = "vcs";
				vcsType = "clearcase";
			}
			else if(vcs.equals("svn")){
				lockingMode = "vcs";
				vcsType = "svn";
			}

			// Write configuration to server
			com.wm.util.Config.setProperty(
					"watt.server.ns.lockingMode", lockingMode);
			boolean isError = false;
			try {
				Server.saveConfiguration();
			} catch (IOException ioe) {
				isError = true;
			}
			Server.checkProperties();
			if (isError) {
				message = BundleUtil.getString(bundle,
						"unableToSaveSettings");
				throw new ServiceException(message);
			}
		}

		vcsList.put("watt.vcs.type", vcsType);
		Config.getInstance().writeProperties("WmVCS", "vcs.cnf", vcsList);

		pipelineCursor.destroy();
		// --- <<IS-END>> ---

	}

	/**
	 * The service is used for configuration of Microsoft Visual SourceSafe settings
	 * @param timeout - Command Timeout, This value specifies the number of milliseconds a command is allowed to
	 * execute before termination,The default value is 60000 (one minute).
	 * @param vssWorkingFolder - Visual SourceSafe working folder in the Integration Server file system where
	 * user's working files will be stored. webMethods recommends that you specify the
	 * ..\webMethods6\IntegrationServer\packages directory, or an individualnpackage in that directory.
	 * The folder must be specified as a fully qualified path
	 * @param vssProjectName - VSS Project Name, name of the project in the Visual SourceSafe server that contains
	 * all of the files and directories being worked on. webMethods recommends that the project name refer to the
	 * ..\packages directory or a specific package in that directory. Typically this path begins with a dollar
	 * sign ($), indicating the Visual SourceSafe root project. For example:$/Project/Team/Module.
	 * @param vssAllowMultipleCheckouts - If selected, user can check out the same file multiple times. The VSS server
	 * should also be configured accordingly to support this option.
	 * @note all settings are saved in respective file
	 * @exception com.wm.app.b2b.server.ServiceException
	 */

	public static final void vssConfiguration(IData pipeline)
			throws ServiceException {
		// --- <<IS-START(vssConfiguration)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required timeout
		// [i] field:0:required vssWorkingFolder
		// [i] field:0:required vssProjectName
		// [i] field:0:required vssAllowMultipleCheckouts

		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String timeout = IDataUtil.getString(pipelineCursor, "timeout");
		String vssWorkingFolder = IDataUtil.getString(pipelineCursor,
				"vssWorkingFolder");
		String vssProjectName = IDataUtil.getString(pipelineCursor,
				"vssProjectName");
		String vssAllowMultipleCheckouts = IDataUtil.getString(pipelineCursor,
				"vssAllowMultipleCheckouts");
		try {
			if (vssAllowMultipleCheckouts == null) {
				vssAllowMultipleCheckouts = "false";
			} else {
				vssAllowMultipleCheckouts = "true";
			}

			Map vssList = new HashMap();
			Map timeOut = new HashMap();
			timeOut.put("watt.vcs.command.timeout", timeout);
			Config.getInstance().writeProperties("WmVCS", "vcs.cnf", timeOut);
			vssList.put("watt.vcs.vss.folder", vssWorkingFolder);
			vssList.put("watt.vcs.vss.project", vssProjectName);
			vssList.put("watt.vcs.vss.allowmultiplecheckouts",
					vssAllowMultipleCheckouts);
			Config.getInstance().writeProperties("WmSourceSafe",
					"sourcesafe.cnf", vssList);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

	}
}
