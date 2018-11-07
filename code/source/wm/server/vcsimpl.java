package wm.server;

// -----( B2B Java Code Template v1.2
// -----( CREATED: Thu Sep 03 12:00:00 GMT 1752
// -----( ON-HOST: hemlock.east.webmethods.com

import com.wm.app.b2b.server.ns.Interface;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.Resources;
import com.wm.app.b2b.server.Server;
import com.wm.data.*;
import com.wm.util.template.Reporter;
// --- <<B2B-START-IMPORTS>> ---
import com.webmethods.client.shared.io.FileExt;
import com.webmethods.vcs.Config;
import com.webmethods.vcs.VCSException;
import com.webmethods.vcs.VCSExecutor;
import com.webmethods.vcs.VCSLog;
import com.webmethods.vcs.VCSManager;
import com.webmethods.vcs.util.*;
import com.wm.app.b2b.broker.sync.SyncState;
import com.wm.app.b2b.broker.sync.SyncStateManager;
import com.wm.app.b2b.server.*;
import com.wm.app.b2b.server.ns.NSLockManager;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.app.b2b.util.ServerIf;
import com.wm.lang.ns.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import wm.server.resources.WmRootExceptionBundle;
// --- <<B2B-END-IMPORTS>> ---

public final class vcsimpl
{
	// ---( internal utility methods )---

	final static vcsimpl _instance = new vcsimpl();

	static vcsimpl _newInstance() { return new vcsimpl(); }

	static vcsimpl _cast(Object o) { return (vcsimpl)o; }

	// ---( server methods )---


	public static final void load(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(load)>> ---
		// @sigtype java 3.5
        // [i] record:1:required nodes
        // [i] - field:0:required node_nsName
        // [i] - field:0:required node_pkg
        // [i] field:0:optional revision
        // [i] field:0:optional date
        // [i] field:0:optional label

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_LOAD_STARTING);

		IDataCursor cur  = pipeline.getCursor();
        
        VCSExecutor exec = new VCSExecutor();

        IData[] nodesData = IDataUtil.getIDataArray(cur, ServerIf.KEY_NODES);

        Set packagesToRefresh = new HashSet();

        Date   date     = (Date)  IDataUtil.get(cur, "date");
        String revision = (String)IDataUtil.get(cur, "revision");
        String label    = (String)IDataUtil.get(cur, "label");

        // sanity check the label for chained commands:
        final String BAD_LABEL_CHARS = "&;";
        if (label != null) {
            for (int ci = 0; ci < BAD_LABEL_CHARS.length(); ++ci) {
                char ch = BAD_LABEL_CHARS.charAt(ci);
                if (label.indexOf(ch) >= 0) {
                    VCSException vcse = new VCSException(VCSLog.EXC_ILLEGAL_LABEL_CHAR, new Object[] { String.valueOf(ch), label });
                    throw new ServiceException(vcse);
                }
            }
        }

        VCSManager vcsMgr = VCSManager.getInstance();

        // the nodes and packages we'll try to load.
        List loadees = new ArrayList();

        // Ugh.
        Map idatas = new HashMap();
        
        for (int i = 0; i < nodesData.length; i++) {
            IDataCursor cursor      = nodesData[i].getCursor();
            String      packageName = IDataUtil.getString(cursor, ServerIf.KEY_LOCK_NODE_PKG);
            Package     pkg         = (Package)Namespace.current().getPackage(packageName);
            String      name        = IDataUtil.getString(cursor, ServerIf.KEY_LOCK_NSNODE_NAME);

            VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "package name: " + packageName + "; pkg: " + pkg + "; name: " + name);

            NSNode      nsNode      = NSUtil.getNode(name);
            String      fullName    = name == null ? pkg.getName() : getFullNameWithPackage(name, packageName);
            NSElement   elem        = null;
            
            if (nsNode == null) {
                // reloading a package, not just a node
                elem = new NSElement(pkg);
            }
            else if (!ACLManager.writeAcl(nsNode.getNSName().getFullName())) { 
                // can't write to the node.
                exec.addFailure(fullName, new ServiceException(WmRootExceptionBundle.class,
                                                               WmRootExceptionBundle.NS_NO_WRITE_PRIVS,
                                                               "", 
                                                               nsNode.getNSName().toString()));
            }
            else if (NSUtil.isLockedByOtherUser(nsNode)) {
                exec.addFailure(fullName, "Node locked");
            }
            else if (isCheckedOut(pkg, nsNode)) {
                exec.addFailure(fullName, "Node checked out");
            }
            else if (findCheckoutSubnode(pkg, nsNode) != null) {
                exec.addFailure(fullName, "Subnode(s) checked out");
            }
            else {
                // OK
                elem = new NSElement(pkg, nsNode);
            }

            if (elem != null) {
                loadees.add(elem);
                idatas.put(elem, nodesData[i]);
            }

            cursor.destroy();
        }
        
        try {
            if (vcsMgr.mustDeleteBeforeLoad()) {
                List files = fetchAllFiles(loadees);
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_LOAD_FILES_TO_DELETE, files.toString());
                deleteAllFiles(files);
            }
        }
        catch (VCSException ve) {
            throw new ServiceException(ve);
        }

        // A memento of the possibly-affected nodes.
        List origNodes = new ArrayList();
        
        Iterator lit1 = loadees.iterator();
        while (lit1.hasNext()) {
            NSElement nse = (NSElement)lit1.next();
            origNodes.addAll(NSUtil.getNodeNameList(nse.getPackage(), nse.getNode()));
        }

        Resources resources = Server.getResources();

        Iterator lit2 = loadees.iterator();
        while (lit2.hasNext()) {
            NSElement nse         = (NSElement)lit2.next();
            Package   pkg         = nse.getPackage();
            NSNode    nsNode      = nse.getNode();
            String    packageName = pkg.getName();

            NSName    nsName      = nsNode == null ? null          : nsNode.getNSName();
            String    name        = nsName == null ? null          : nsName.getFullName();
            String    fullName    = name   == null ? pkg.getName() : getFullNameWithPackage(name, packageName);

            VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "package name: " + packageName + "; pkg: " + pkg + "; name: " + name);

            List files = new ArrayList();
            List dirs = new ArrayList();

            if (nsNode == null) {
                // entries = pkg
                dirs.add(NSUtil.getPackagePath(pkg, null));
            }
            else {
                // entries = pkg/ns/.../service
                // files = pkg/.../service files

                File nsNodeDir = resources.getNSNodeDir(packageName, nsName);
                dirs.add(getFileName(nsNodeDir));

                if (nsNode instanceof Interface) {
                    File sourceDir = getNSNodeSourceDir(packageName, nsName);
                    dirs.add(getFileName(sourceDir));

                    File sourceFile = getNSNodeJavaSource(packageName, nsName);
                    files.add(getFileName(sourceFile));
                }
                else if (nsNode instanceof JavaService) {
                    File sourceFile = getNSNodeJavaSource(packageName, nsName);
                    files.add(getFileName(sourceFile));

                    JavaService js = (JavaService)nsNode;
                    List siblings = JavaServiceUtil.getJavaSiblings(js);
                    Iterator sit = siblings.iterator();
                    while (sit.hasNext()) {
                        JavaService sib = (JavaService)sit.next();
                        File sibNodeDir = resources.getNSNodeDir(packageName, sib.getNSName());
                        dirs.add(getFileName(sibNodeDir));
                    }
                }
            }
            
            boolean ok = true;

            if (ok) {
                packagesToRefresh.add(packageName);

                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "dirs: " + dirs);
                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "files: " + files);
                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "date: " + date + "; revision: " + revision + "; label: " + label);

                try {
                    if (date != null) {
                        vcsMgr.load(dirs, files, date);
                    }
                    else if (revision != null) {
                        boolean isInterface = nsNode instanceof Interface;
                        vcsMgr.load(dirs, files, revision, isInterface);
                    }
                    else if (label != null) {
                        vcsMgr.loadByLabel(dirs, files, label);
                    }
                    else {
                        vcsMgr.load(dirs, files);
                    }

                    IData nodeData = (IData)idatas.get(nse);
                    exec.addSuccess(fullName, nodeData);
                }
                catch (Exception e) {
                    exec.addFailure(fullName, e);
                }
            }
        }

        NSUtil.refreshPackages(packagesToRefresh);

        List newNodes = new ArrayList();

        Iterator lit3 = loadees.iterator();
        while (lit3.hasNext()) {
            NSElement nse    = (NSElement)lit3.next();
            Package   pkg    = nse.getPackage();
            NSNode    node   = nse.getNode();
            NSName    nsName = node   == null ? null : node.getNSName();
            String    name   = nsName == null ? null : nsName.getFullName();

            // We have to look up by *name* (a String), not by the NSNode
            // itself, since we want to get the *new* nodes, based on their
            // names. That is, we're doing a query by ID instead of getting
            // objects by reference.

            List nodeNames = NSUtil.getNodeNameList(pkg.getName(), name);
            newNodes.addAll(nodeNames);

            // all newly-loaded publishable document types are set as updated on IS:

            Iterator nit = nodeNames.iterator();
            while (nit.hasNext()) {
                String nodeName = (String)nit.next();
                NSNode nsNode = NSUtil.getNode(nodeName);

                if (nsNode instanceof NSRecord) {
                    NSRecord rec = (NSRecord)nsNode;
                    if (rec.isPublishable()) {
                        SyncState state = SyncStateManager.current().getSyncState(rec.getNSName());
                        state.setUpdatedOnIS();
                    }
                }
            }
        }

        Iterator oit = origNodes.iterator();
        while (oit.hasNext()) {
            String origName = (String)oit.next();
            if (!newNodes.contains(origName)) {
                exec.addWarning(origName, VCSLog.getMessage(VCSLog.VCS_NO_SUCH_NODE));
            }
        }

        exec.addSummary(cur);
        
        cur.destroy();

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_LOAD_DONE);

		// --- <<B2B-END>> ---
	}


	public static final void delete(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(delete)>> ---
		// @sigtype java 3.5
        // [i] field:1:required vcsFiles
        // [i] field:0:optional description

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_DELETE_STARTING);

		IDataCursor cur = pipeline.getCursor();

        if (cur.first("vcsEditFiles")) {
            List editFiles = (List)cur.getValue();

            if (editFiles != null && editFiles.size() > 0) {
                try {
                    VCSManager.getInstance().checkin(editFiles, "updated for delete");

                    // we have to do this, because Developer can update files
                    // (such as .java and node.idf) after a node has been
                    // deleted.
                    // VCSManager.getInstance().checkout(editFiles);
                }
                catch (VCSException vcse) {
                    throw new ServiceException(vcse);
                }
            }
        }

        if (cur.first("vcsFiles")) {
            List files = (List)cur.getValue();

            if (files == null || files.size() == 0) {
                // no files to delete
            }
            else {
                String description = IDataUtil.getString(cur, "description");
                String fmtdCmt = getFormattedDeleteComment(description);
                
                delete(false, files, fmtdCmt);
            }

            IDataUtil.remove(cur, "vcsFiles");
        }
        
        cur.destroy();
        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_DELETE_DONE);
        
		// --- <<B2B-END>> ---
	}


	public static final void checkinFile(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(checkinFile)>> ---
		// @sigtype java 3.5
        // [i] field:0:required filename
        // [i] field:0:optional description

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_CHECKIN_STARTING);

        IDataCursor cur = pipeline.getCursor();

        VCSManager vcsMgr = VCSManager.getInstance();

        String fileName = (String)IDataUtil.get(cur, "filename");
        List files = Arrays.asList(new String[] { fileName });

        try {
            String description = null;
            if (cur.first("description")) {
                description = (String)cur.getValue();
            }
            String fmtdCmt = getFormattedCheckinComment(description);
        
            vcsMgr.checkin(files, fmtdCmt);
        }
        catch (VCSException vsce) {
            throw new ServiceException(vsce);
        }

		// --- <<B2B-END>> ---
	}




	public static final void checkin(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(checkin)>> ---
		// @sigtype java 3.5
        // [i] field:0:optional package
        // [i] record:1:optional nodes
        // [i] - field:0:required node_nsName
        // [i] - field:0:required node_pkg
        // [i] field:0:optional description

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_CHECKIN_STARTING);

        IDataCursor cur = pipeline.getCursor();

        VCSManager vcsMgr = VCSManager.getInstance();

        if (cur.first("package")) {
            String pkgName   = (String)cur.getValue();
            List   fileNames = getPackageFiles(pkgName, false, false);

            String description = null;
            if (cur.first("description")) {
                description = (String)cur.getValue();
            }
            String fmtdCmt = getFormattedCheckinComment(description);
            
            try {
                vcsMgr.checkin(fileNames, fmtdCmt);
            }
            catch (VCSException vcse) {
                throw new ServiceException(vcse);
            }
        }
        else {
            VCSExecutor exec = new VCSExecutor();

            IData[] nodesData = IDataUtil.getIDataArray(cur, ServerIf.KEY_NODES);
            if (nodesData == null) {
                // no data as multiple nodes; defaulting to single node invocation
                if (cur.first("VCScheckin") && "false".equals(cur.getValue())) {
                    VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "skipping checkin");
                }
                else {
                    nodesData = new IData[] { pipeline };
                }
            }

            if (nodesData == null) {
                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_CHECKIN_NO_NODE_DATA);
            }
            else if (!vcsMgr.hasClient()) {
                VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
            }
            else {
                List allEntries  = new ArrayList();
                Set allPackages = new HashSet();

                for (int i = 0; i < nodesData.length; ++i) {
                    IDataCursor ndc      = nodesData[i].getCursor();
                    String      name     = IDataUtil.getString(ndc, ServerIf.KEY_LOCK_NSNODE_NAME);
                    NSNode      node     = NSUtil.getNode(name);
                    Package     pkg      = null;
                    String      pkgName  = IDataUtil.getString(ndc, ServerIf.KEY_LOCK_NODE_PKG);

                    List nodes = new ArrayList();

                    if (!ACLManager.writeAcl(name)) {
                        VCSException vcse = new VCSException(VCSLog.EXC_NO_WRITE_ACL_PRIVILEGES, new Object[] { name });
                        throw new ServiceException(vcse);
                    }
                    else if (NSUtil.isLockedByOtherUser(node)) {
                        // node is locked by another user
                        VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "node is locked by another user");
                    }
                    else {
                        // node is NOT locked
                        if (node instanceof JavaService) {
                            NSName nodeName = node.getNSName();
                            NSName ifcName  = nodeName.getInterfaceNSName();
                            List siblings = NSLockManager.getSiblingJavaServices((JavaService)node, ifcName);
                    
                            // the siblings will include the target node, AKA, the value of the variable "node"
                    
                            Iterator sit = siblings.iterator();
                            while (sit.hasNext()) {
                                String sibName = (String)sit.next();
                                NSNode sibNode = NSUtil.getNode(sibName);
                                nodes.add(sibNode);
                            }
                        }
                        else {
                            nodes.add(node);
                        }

                        // we don't always get the package name in the pipeline;
                        // specifically, when this is invoked from Locked User
                        // Nodes in Developer. So then we'll look at the node
                        // itself:
                        if (pkgName == null) {
                            pkg = (Package)node.getPackage();
                            if (pkg == null) {
                                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "no package for node");
                            }
                            else {
                                pkgName = pkg.getName();
                            }
                        }
                        else {
                            pkg = NSUtil.getPackage(pkgName);
                        }

                        Collection entries = new HashSet();
                        Iterator nit = nodes.iterator();
                        while (nit.hasNext()) {
                            NSNode n = (NSNode)nit.next();
                            List e = NSUtil.getEntries(pkg, n, NSUtil.INCLUDE_PARENT | NSUtil.INCLUDE_SIBLINGS);

                            if (e != null) {
                                Iterator eit = e.iterator();
                                while (eit.hasNext()) {
                                    String file = (String)eit.next();
                                
                                    // We used to check here that the file is checked
                                    // out. But now the VCSClients have to do the add,
                                    // checkin, or skip appropriately.
                                
                                    if ((new File(file)).exists()) {
                                        entries.add(file);
                                    }
                                }
                            }
                        }

                        String fullName = name + "/" + pkgName;
                        exec.addSuccess(fullName, nodesData[i]);

                        if (entries.size() > 0) {
                            allEntries.addAll(entries);
                            allPackages.add(pkgName);
                        }
                        else {
                            // no entries
                            VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "no entries for node");
                        }
                    }
                }

                try {
                    boolean isRevert = cur.first("revert") && "true".equals(cur.getValue());
                    if (isRevert) {
                        VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "reverting");

                        if (allEntries.size() > 0) {
                            // delete the files first:
                            deleteAllFiles(allEntries);

                            // now revert them, which gets fresh copies:
                            vcsMgr.revert(allEntries);
                            NSUtil.refreshPackages(allPackages);

                            // make the status of all PDTs (publishable NS records) as "Updated Locally".
                            Iterator pit = allPackages.iterator();
                            while (pit.hasNext()) {
                                String  pkgName  = (String)pit.next();
                                Package pkg      = NSUtil.getPackage(pkgName);
                                List    newNodes = NSUtil.getNodesInPackage(pkg, null);

                                Iterator nit = newNodes.iterator();
                                while (nit.hasNext()) {
                                    NSNode node = (NSNode)nit.next();
                                    if (node instanceof NSRecord) {
                                        NSRecord rec = (NSRecord)node;
                                        if (rec.isPublishable()) {
                                            SyncState state = SyncStateManager.current().getSyncState(rec.getNSName());
                                            state.setUpdatedOnIS();
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            // ignoring revert of empty list of entries
                            VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "ignoring revert of empty list of entries");
                        }
                    }
                    else {
                        String description = null;
                        if (cur.first("description")) {
                            description = (String)cur.getValue();
                        }
                        String fmtdCmt = getFormattedCheckinComment(description);
                        
                        vcsMgr.checkin(allEntries, fmtdCmt);
                    }
                }
                catch (VCSException vsce) {
                    throw new ServiceException(vsce);
                }
            
                // don't override the settings in the pipeline:
                // exec.addSummary(cur);
                cur.destroy();
            }
        }

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_CHECKIN_DONE);

		// --- <<B2B-END>> ---
	}

	public static final void checkout(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(checkout)>> ---
		// @sigtype java 3.5
        // [i] record:1:required nodes
        // [i] - field:0:required node_nsName
        // [i] - field:0:required node_pkg

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_CHECKOUT_STARTING);

        IDataCursor cur = pipeline.getCursor();
        if (cur.first("build")) {
            VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_CHECKOUT_IGNORING_BUILD_MODE);
        }
        else if (cur.first("deleteFromVCS")) {
            // Another workaround for Java services: in this case, we check out
            // the siblings of any Java service being deleted.
            NSNode node = getNode(cur);
            if (node instanceof JavaService) {
                JavaService js = (JavaService)node;

                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "checking out Java sibling services");

                NSName nodeName = js.getNSName();
                NSName ifcName  = nodeName.getInterfaceNSName();
                List   siblings = NSLockManager.getSiblingJavaServices(js, ifcName);
                
                if (siblings.size() > 1) {
                    Iterator sit = siblings.iterator();
                    while (sit.hasNext()) {
                        String sibName = (String)sit.next();
                        NSNode sibNode = NSUtil.getNode(sibName);
                        if (sibNode != js) {
                            checkout(sibNode, cur, false);
                        }
                    }
                }
            }
        }
        else {
            NSNode node = getNode(cur);
            if (NSUtil.isLockedByOtherUser(node)) {
                // cannot lock node
            }
            else  {
                checkout(node, cur, true);
            }
        }
        cur.destroy();
        
        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_CHECKOUT_DONE);

		// --- <<B2B-END>> ---
	}

	public static final void getLogs(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(getLogs)>> ---
		// @sigtype java 3.5
        // [i] record:1:required nodes
        // [i] - field:0:required node_nsName
        // [i] - field:0:required node_pkg
        // [o] record:1:required vcsData

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_GETLOGS_STARTING);
        
        if (hasClient()) {
            IDataCursor cur      = pipeline.getCursor();
            NSNode      node     = getNode(cur);
            String      pkgName  = IDataUtil.getString(cur, NSNode.KEY_NSN_PACKAGE);
            Package     pkg      = pkg = (Package)Namespace.current().getPackage(pkgName);

            List persistenceEnt = NSUtil.getEntries(pkg, node, NSUtil.EXISTING_ONLY);
            if (persistenceEnt != null) {
                IData logs = getLogs(persistenceEnt);
                IDataUtil.put(cur, "vcsData", logs);
            }
        }
        else {
            VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
        }

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_GETLOGS_DONE);
        
		// --- <<B2B-END>> ---
	}

	public static final void getFilesToDelete(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(getFilesToDelete)>> ---
		// @sigtype java 3.5
        // [i] field:0:required node_nsName
        // [i] field:0:required node_pkg
        // [o] field:0:optional deleteFromVCS {"true", "false"}

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_GETFILESTODELETE_STARTING);
        
        preDelete(pipeline);

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_GETFILESTODELETE_DONE);

		// --- <<B2B-END>> ---
    }   


	public static final void preDelete(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(preDelete)>> ---
		// @sigtype java 3.5
        // [i] field:0:required node_nsName
        // [i] field:0:required node_pkg
        // [o] field:0:optional deleteFromVCS {"true", "false"}

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PREDELETE_STARTING);

        IDataCursor cur = pipeline.getCursor();
        if (cur.first("deleteFromVCS") && "false".equals(cur.getValue())) {
            VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PREDELETE_SKIPPING);
        }
        else {
            List delFiles = new ArrayList();
            List editFiles = new ArrayList();

            boolean isPackageDelete = cur.first("package");

            if (isPackageDelete) {
                String pkg = (String)cur.getValue();
                delFiles = getPackageFiles(pkg, true, true);
            }
            else {
                fetchFilesForDelete(pipeline, delFiles, editFiles);
                
                delFiles = new ArrayList(new HashSet(delFiles));
                editFiles = new ArrayList(new HashSet(editFiles));
            }

            cur.insertAfter("vcsFiles", delFiles);
            cur.insertAfter("vcsEditFiles", editFiles);

            if (editFiles != null && editFiles.size() > 0) {
                try {
                    VCSManager.getInstance().checkout(new ArrayList(editFiles));
                }
                catch (VCSException vcse) {
                    throw new ServiceException(vcse);
                }
            }
            
            delete(true, delFiles, getDefaultDeleteComment());
        }
        cur.destroy();

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PREDELETE_DONE);

		// --- <<B2B-END>> ---
    }   


	public static final void isCheckedOut(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(isCheckedOut)>> ---
		// @sigtype java 3.5
        // [i] field:0:required node_nsName
        // [i] field:0:required node_pkg
        // [o] field:0:optional checkedOut {"true", "false"}

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_ISCHECKEDOUT_STARTING);
        
        IDataCursor cur        = pipeline.getCursor();
        NSNode      node       = getNode(cur);
        String      pkgName    = IDataUtil.getString(cur, NSNode.KEY_NSN_PACKAGE);
        Package     pkg        = NSUtil.getPackage(pkgName);
        boolean     checkedOut = isCheckedOut(pkg, node);
        
        IDataUtil.put(cur, "checkedOut", new Boolean(checkedOut).toString());

        cur.destroy();
        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_ISCHECKEDOUT_DONE);

		// --- <<B2B-END>> ---
    }   


	public static final void getCommentText(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(getCommentText)>> ---
		// @sigtype java 3.5
        // [o] field:0:required deleteComment
        // [o] field:0:required checkinComment

        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_GETCOMMENTTEXT_STARTING);

        // we used to give the user a default comment, but now we don't.

        // String undoCmt = getDefaultCheckinComment();
        // String delCmt  = getDefaultDeleteComment();

        IDataCursor cur = pipeline.getCursor();
        
        cur.insertAfter("deleteComment",  "");
        cur.insertAfter("checkinComment", "");
       
        cur.destroy();
        VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_GETCOMMENTTEXT_DONE);

		// --- <<B2B-END>> ---
    }   


	// --- <<B2B-START-SHARED>> ---


    /**
     * Gets the node from the IData.
     */
    public static final NSNode getNode(IDataCursor cur)
    {
        String nodeName = IDataUtil.getString(cur, NSNode.KEY_NSN_NSNAME);
        NSNode node     = NSUtil.getNode(nodeName);
        return node;
    }

    public static final void checkinInterface(Package pkg, Interface iface) throws ServiceException
    {
        List       entries = NSUtil.getEntries(pkg, iface, NSUtil.EXISTING_ONLY);
        String     pkgName = pkg.getName();
        VCSManager vcsMgr  = VCSManager.getInstance();

        boolean ok = true;
        Iterator eit = entries.iterator();
        while (ok && eit.hasNext()) {
            String file = (String)eit.next();
            try {
                if (vcsMgr.isCheckedOut(file)) {
                    VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_FILE_CHECKED_OUT, file);
                }
                else {
                    VCSLog.log(VCSLog.ERROR, VCSLog.VCS_FILE_NOT_CHECKED_OUT, file);
                    ok = false;
                }
            }
            catch (VCSException ve) {
                throw new ServiceException(ve);
            }
        }

        if (ok) {
            String fmtdCmt = getFormattedCheckinComment(null);
            
            try {
                vcsMgr.checkin(entries, fmtdCmt);
            }
            catch (VCSException vsce) {
                throw new ServiceException(vsce);
            }
        }
        else {
            // no files checked out
        }
    }

    public static final void checkout(NSNode node, IDataCursor cursor, boolean includeSiblings) throws ServiceException
    {
        if (hasClient()) {
            try {
                Package pkg = null;
                // misleading. MOVE_NODE_TARGET == COPY_NODE_TARGET == "target"
                if (cursor.first(ServerIf.MOVE_NODE_TARGET)) {
                    String srcName = IDataUtil.getString(cursor, ServerIf.MOVE_NODE_SOURCE);

                    if (node.getNSName().getFullName().equals(srcName)) {
                        // ignoring attempted checkout of source node
                    }
                    else {
                        int mask = NSUtil.INCLUDE_PARENT;
                        if (includeSiblings) {
                            mask |= NSUtil.INCLUDE_SIBLINGS;
                        }
                        pkg = getPackage(cursor, ServerIf.COPY_NODE_TARGET_PKG, node);
                        List files = NSUtil.getEntries(pkg, node, mask);

                        // here we could handle move specifically, instead of just
                        // doing a checkout on the new nodes, such as mapping from
                        // old to new names, and calling:
                        // VCSManager.getInstance().move(fileMap);

                        VCSManager.getInstance().checkout(files);
                    }
                }
                else {
                    pkg = getPackage(cursor, NSNode.KEY_NSN_PACKAGE, node);
                    int mask = NSUtil.EXISTING_ONLY | NSUtil.INCLUDE_PARENT;
                    if (includeSiblings) {
                        mask |= NSUtil.INCLUDE_SIBLINGS;
                    }
                    
                    List files = NSUtil.getEntries(pkg, node, mask);
                    
                    VCSManager.getInstance().checkout(files);
                }

                // interfaces are submitted immediately, since the user isn't
                // likely to check them in.
//                 if (node instanceof Interface) {
//                     // checkinInterface(pkg, (Interface)node);
//                 }
            }
            catch (VCSException vcse) {
                throw new ServiceException(vcse);
            }
        }
        else {
            VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
        }
    }

	public static final boolean hasClient()
	{
        return VCSManager.getInstance().hasClient();
	}

    /**
     * Gets the logs for the given files. The IData will have the fileName as
     * the key, and the log as the associated value. Files without logs will not
     * be in the resulting array.
     */
    protected static final IData getLogs(Collection fileNames) throws ServiceException
    {
        if (hasClient()) {
            IData       logs   = IDataFactory.create();
            IDataCursor cursor = logs.getCursor();
            Iterator    it     = fileNames.iterator();
            while (it.hasNext()) {
                String fileName = (String)it.next();
                List   log = null;
                try {
                    log = VCSManager.getInstance().getLog(fileName);
                }
                catch (VCSException vcse) {
                    log = vcse.getErrorOutput();
                    throw new ServiceException(vcse);
                }
                if (log != null) {
                    cursor.insertAfter(fileName, log);
                }
            }
            cursor.destroy();
            return logs;
        }
        else {
            VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
            return null;
        }
    }

    /**
     * Deletes the given files from the VCS.
     */
    public static final void delete(boolean isPreDelete, Collection fileNames, String description) throws ServiceException
    {
        String fmtdCmt = getFormattedDeleteComment(description);

        if (hasClient()) {
            try {
                if (fileNames != null && fileNames.size() > 0) {
                    VCSManager.getInstance().delete(isPreDelete, new ArrayList(fileNames), description);
                }
            }
            catch (VCSException vcse) {
                throw new ServiceException(vcse);
            }
        }
        else {
            VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
        }
    }

    /**
     * 
     */
    public static final void fetchPackageFilesForDelete(IData pipeline, List delFiles, List editFiles) 
         throws ServiceException
    {
        if (hasClient()) {
            // These files could belong to the other node (the source, if this is in
            // the context of a moveNode), in which case, we don't delete them:

            IDataCursor cur = pipeline.getCursor();
            
            cur.first("package");
            String  pkgName  = (String)cur.getValue();
            Package pkg      = PackageManager.getPackage(pkgName);
            List    pkgNodes = NSUtil.getNodesInPackage(pkg, null);
            
            if (pkgNodes != null) {
                Iterator nit   = pkgNodes.iterator();
                while (nit.hasNext()) {
                    NSNode node      = (NSNode)nit.next();
                    List   nodeFiles = NSUtil.getEntries(pkg, node, NSUtil.INCLUDE_PARENT);
                    delFiles.addAll(nodeFiles);
                }
            }
        }
    }

    /**
     * 
     */
    public static final void fetchFilesForDelete(IData pipeline, List delFiles, List editFiles) 
         throws ServiceException
    {
        if (hasClient()) {
            // These files could belong to the other node (the source, if this is in
            // the context of a moveNode), in which case, we don't delete them:

            IDataCursor cur = pipeline.getCursor();
            
            IData[] nodesData = IDataUtil.getIDataArray(cur, ServerIf.KEY_NODES);
            if (nodesData == null) {
                nodesData = new IData[] { pipeline };
            }

            List svcsBeingDeleted = new ArrayList();

            for (int i = 0; i < nodesData.length; ++i) {
                IDataCursor ndc = nodesData[i].getCursor();
                NSNode srcNode = getNode(ndc);
                if (srcNode instanceof JavaService) {
                    svcsBeingDeleted.add(srcNode);
                }
            }

            for (int i = 0; i < nodesData.length; ++i) {
                IDataCursor ndc = nodesData[i].getCursor();
                NSNode srcNode = getNode(ndc);

                if (NSUtil.isLockedByOtherUser(srcNode)) {
                    VCSLog.log(VCSLog.WARN, VCSLog.VCS_GENERAL_MESSAGE, "file: " + srcNode + " locked");
                }
                else {
                    // see if we got a package - required for interface nodes
                    String srcPkgName = IDataUtil.getString(ndc, NSNode.KEY_NSN_PACKAGE);
                    Package srcPkg = NSUtil.getPackage(srcPkgName);

                    NSNode node = null;

                    // get the files to delete ...
                    List toDelete = null;
                    List toEdit = null;

                    // Deleting a java service with no siblings remaining? Then
                    // include the interface as being deleted..
                    if (srcNode instanceof JavaService) {
                        JavaService javaSvc = (JavaService)srcNode;

                        // If you're maintaining this code, my condolences. This
                        // is highly confusing.
                        
                        List siblings = JavaServiceUtil.getJavaSiblings(javaSvc);

                        if (svcsBeingDeleted.containsAll(siblings)) {
                            // all java siblings are being deleted, so
                            // edit the node.idf file, and delete the .java file
                            
                            boolean existingOnly    = false;
                            boolean includeSiblings = true;
                            boolean includeJavaSrc  = true;
                            boolean includeNodeIdf  = false;
                            
                            try {
                                toDelete = JavaServiceUtil.getJavaEntries(srcPkg, javaSvc, existingOnly, includeSiblings, includeJavaSrc, includeNodeIdf);
                                toEdit   = new ArrayList();
                                toEdit.add(NSUtil.getNodeIdf(javaSvc));
                            }
                            catch (IOException ioe) {
                                throw new ServiceException(ioe);
                            }
                        }
                        else {
                            // A Java service with "surviving" siblings will
                            // edit its interface, its .java file, and the
                            // node.ndf and java.frag files of its siblings.
                            
                            toDelete = NSUtil.getEntries(srcPkg, srcNode, 0);
                            toEdit   = JavaServiceUtil.getParentEntries(srcPkg, javaSvc);

                            Iterator sit = siblings.iterator();
                            while (sit.hasNext()) {
                                JavaService sib = (JavaService)sit.next();

                                if (!svcsBeingDeleted.contains(sib)) {
                                    try {
                                        toEdit.addAll(JavaServiceUtil.getJavaEntries(srcPkg, sib, false, false, false, false));
                                    }
                                    catch (IOException ioe) {
                                        throw new ServiceException(ioe);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        toDelete = NSUtil.getEntries(srcPkg, srcNode, 0);

                        // do we (always?) edit the interface when the subnode is being deleted?
                        
                        if (srcNode instanceof Interface) {
                            // no files to edit for an interface being deleted.
                        }
                        else {
                        	if(null != srcNode)
                        	{
                        		NSNode iface = NSUtil.getInterface(srcNode);
                                toEdit = NSUtil.getEntries(srcPkg, iface, 0);
                        	}                            
                        }
                    }
                    
                    String tgtName    = IDataUtil.getString(ndc, "target");
                    String tgtPkgName = IDataUtil.getString(ndc, "targetpkg");
                    ndc.destroy();
                
                    if (tgtName == null || tgtPkgName == null) {
                        // no target name or package, so no cleanup needed
                    }
                    else {
                        List tgtFiles = NSUtil.getEntries(NSUtil.getPackage(tgtPkgName), 
                                                          NSUtil.getNode(tgtName), 
                                                          NSUtil.EXISTING_ONLY);
                        
                        Iterator it = tgtFiles.iterator();
                        while (it.hasNext()) {
                            String tgtFileName = (String)it.next();
                            toDelete.remove(tgtFileName);
                        }
                    }

                    if (toDelete != null) {
                        delFiles.addAll(toDelete);
                    }
                    if (toEdit != null) {
                        editFiles.addAll(toEdit);
                    }
                }
            }

            cur.destroy();
        }
        else {
            VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
        }
    }        

    /**
     * Returns all files for the given nodes, recursively (for interfaces).
     */
    public static final List fetchAllFiles(IData pipeline) throws ServiceException
    {
        if (hasClient()) {
            IDataCursor cur = pipeline.getCursor();

            List allFiles = new ArrayList();
            
            IData[] nodesData = IDataUtil.getIDataArray(cur, ServerIf.KEY_NODES);
            if (nodesData == null) {
                nodesData = new IData[] { pipeline };
            }

            for (int i = 0; i < nodesData.length; ++i) {
                IDataCursor ndc = nodesData[i].getCursor();
                NSNode srcNode = getNode(ndc);

                // see if we got a package - required for interface nodes
                String  srcPkgName = IDataUtil.getString(ndc, NSNode.KEY_NSN_PACKAGE);
                Package srcPkg     = NSUtil.getPackage(srcPkgName);

                List files = NSUtil.findAllEntries(srcPkg, srcNode);

                allFiles.addAll(files);
            }
            
            cur.destroy();
            return allFiles;
        }
        else {
            VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
            return null;
        }
    }        

    /**
     * Returns all files for the given nodes, recursively (for interfaces).
     */
    public static final List fetchAllFiles(List nodes) throws ServiceException
    {
        if (hasClient()) {
            List allFiles = new ArrayList();
            
            Iterator nit = nodes.iterator();
            while (nit.hasNext()) {
                NSElement nse   = (NSElement)nit.next();
                Package   pkg   = nse.getPackage();
                NSNode    node  = nse.getNode();
                List      files = NSUtil.findAllEntries(pkg, node);

                allFiles.addAll(files);
            }
            
            return allFiles;
        }
        else {
            VCSLog.log(VCSLog.ERROR, VCSLog.VCS_CLIENT_NONE);
            return null;
        }
    }        

    /**
     * Returns the package from the pipeline or gets it from the node object. 
     * Throws an exception if the package is not found.
     */
    public static Package getPackage(IDataCursor cr, String key, NSNode node) throws ServiceException
    {
        Package pkg = null;
        if (cr != null && cr.first(key)) {
            String pkgName = (String)cr.getValue();
            if (pkgName != null && pkgName.length() > 0) {
                pkg = NSUtil.getPackage(pkgName);
            }
        }
        else {
            pkg = (Package)node.getPackage();
        }

        return pkg;
    }

    /**
     * Adds a summary of the successful and failed nodes to the pipeline.
     */
    protected static void addSummary(IDataCursor cursor, 
                                     boolean isSuccessful, 
                                     List successfulNodes, 
                                     List failedNodes, 
                                     IData errors, 
                                     IData warnings,
                                     List results)
    {
        String   success                  = new Boolean(isSuccessful).toString();
        String[] successfulNodesAsStrings = (String[])successfulNodes.toArray(new String[0]);
        String[] failedNodesAsStrings     = (String[])failedNodes.toArray(new String[0]);

        IDataUtil.put(cursor, ServerIf.KEY_IS_SUCCESSFUL,    success);
        IDataUtil.put(cursor, ServerIf.KEY_SUCCESSFUL_NODES, successfulNodesAsStrings);
        IDataUtil.put(cursor, ServerIf.KEY_FAILED_NODES,     failedNodesAsStrings);
        IDataUtil.put(cursor, ServerIf.KEY_ERRORS,           errors);
        IDataUtil.put(cursor, ServerIf.KEY_WARNINGS,         warnings);
        
        if (results != null) {
            IData[] resultData = (IData[])results.toArray(new IData[0]);
            IDataUtil.put(cursor, ServerIf.KEY_RESULTS, resultData);
        }
    }

    protected static String getFullNameWithPackage(String nodeName, String pkg) 
    {
        if (nodeName == null) {
            return null;
        }
        else {
            return pkg == null ? nodeName : pkg + "/" + nodeName;
        }
    }

    protected static String getResourceString(int key)
    {
        Session        session = InvokeState.getCurrentSession();
        Locale         locale  = session.getLocale();
        ResourceBundle rb      = ResourceBundle.getBundle(VCSLog.MSG_BUNDLE, locale);
        String         str     = rb.getString(VCSLog.string(key));

        return str;
    }

    protected static String getDefaultCheckinComment()
    {
        return getDefaultComment("watt.vcs.comment.checkin", 
                                 VCSLog.VCS_CHECKIN_DEFAULT_COMMENT);
    }

    protected static String getDefaultDeleteComment()
    {
        return getDefaultComment("watt.vcs.comment.delete", 
                                 VCSLog.VCS_DELETE_DEFAULT_COMMENT);
    }

    protected static String getDefaultComment(String property, int defCmtKey)
    {
        return getFormattedComment(property, new String[][] {}, defCmtKey);
    }

    protected static String getFormattedDeleteComment(String description)
    {
        if (description == null) {
            description = getDefaultDeleteComment();
        }
        
        String fmtdDesc = getFormattedComment("watt.vcs.metacomment.delete", 
                                              new String[][] { 
                                                  { "usercomment", description }
                                              },
                                              VCSLog.VCS_DELETE_DEFAULT_METACOMMENT);
        
        return fmtdDesc;
    }

    protected static String getFormattedCheckinComment(String description)
    {
        if (description == null) {
            description = getDefaultCheckinComment();
        }
        
        String fmtdDesc = getFormattedComment("watt.vcs.metacomment.checkin", 
                                              new String[][] { 
                                                  { "usercomment", description }
                                              },
                                              VCSLog.VCS_CHECKIN_DEFAULT_METACOMMENT);
        
        return fmtdDesc;
    }
    
    protected static String getFormattedComment(String property, String[][] data, int defCmtKey)
    {
        String cmt = Config.getInstance().getProperty(property);

        if (cmt == null) {
            cmt = getResourceString(defCmtKey);
        }

        Reporter         r       = new Reporter(cmt);
        IData            in      = IDataFactory.create();
        IDataCursor      inc     = in.getCursor();

        InvokeState      state   = InvokeState.getCurrentState();
        Socket           socket  = InvokeState.getCurrentSocket();
        String           devhost = socket == null ? "unknown" : socket.getInetAddress().getHostName();
        String           user    = state.getUser().getName();
        String           ishost  = ServerAPI.getServerName();
        String           fmtStr  = System.getProperty("watt.server.dateStampFmt", "yyyy-MM-d HH:mm:ss z");
        SimpleDateFormat fmt     = new SimpleDateFormat(fmtStr);
        String           timeStr = fmt.format(new Date());

        IDataUtil.put(inc, "dev_user", user);
        IDataUtil.put(inc, "dev_host", devhost);
        IDataUtil.put(inc, "is_host",  ishost);
        IDataUtil.put(inc, "is_time",  timeStr);
        
        // and whatever the user defined:
        for (int di = 0; di < data.length; ++di) {
            IDataUtil.put(inc, data[di][0], data[di][1]);
        }

        byte[] ary = r.reportIData(in);
		//Trax 1-14Q712 && 1-1U9P3Q - changes begin
		String str = null;
        try{
        	str = new String(ary,"UTF-8");//Trax 1-14Q712. Unencoded comment
        }
        catch (UnsupportedEncodingException e) {
        	str = new String(ary);
        }
        
		//Trax 1-14Q712 && 1-1U9P3Q - changes end
        return str;
    }

    /**
     * Returns whether the node has a checked out file.
     */
    protected static boolean isCheckedOut(Package pkg, NSNode node) throws ServiceException
    {
        VCSManager vcsMgr = VCSManager.getInstance();

        List entries = NSUtil.getEntries(pkg, node, NSUtil.EXISTING_ONLY | NSUtil.INCLUDE_PARENT);
        
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            String fName = (String)it.next();
            try {
                if (vcsMgr.isCheckedOut(fName)) {
                    return true;
                }
            }
            catch (VCSException ve) {
                // ignore
            }
        }

        return false;
    }

    /**
     * Returns the node, or any of its subnodes, that has a checked out file.
     */
    protected static NSNode findCheckoutSubnode(Package pkg, NSNode node) throws ServiceException
    {
        VCSManager vcsMgr = VCSManager.getInstance();

        if (isCheckedOut(pkg, node)) {
            return node;
        }
        else if (node instanceof Interface) {
            NSNode[] nodes = ((Interface)node).getNodes();
            for (int ni = 0; ni < nodes.length; ++ni) {
                NSNode n = findCheckoutSubnode(pkg, nodes[ni]);
                if (n != null) {
                    return n;
                }
            }
        }
        
        return null;
    }

    /**
     * Deletes the files.
     * 
     * @param fileNames A collection of Strings representing the file names.
     */
    protected static void deleteAllFiles(Collection fileNames)
    {
        VCSManager mgr = VCSManager.getInstance();
        Iterator   fit = fileNames.iterator();
        while (fit.hasNext()) {
            String fname = (String)fit.next();
            try {
                boolean checkedOut = mgr.isCheckedOut(fname);
                File    file       = new File(fname);
                boolean del        = file.delete();
                VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_GENERAL_MESSAGE, "file: " + file + " deleted? " + del);
            }
            catch (VCSException vcse) {
            }
        }

        // Hmm. Delete directories too?
    }

    /**
     * Returns the files for the package, in canonical form.
     */
    protected static List getPackageFiles(String pkgName, final boolean includeDirectories, boolean recurse) throws ServiceException
    {
        File pkgDir = new File(PackageManager.getPackageDir(), pkgName);
        List files;

        if (recurse) {
            files = FileExt.dirTree(pkgDir, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return (!pathname.getAbsolutePath().contains("code" + File.separator + "classes"));
                }
            });
        }
        else {
            File[] fileArray = pkgDir.listFiles(new FileFilter() {
                    public boolean accept(File f) {
                        return f.isFile() || (includeDirectories && f.isDirectory());
                    }
                });
            files = Arrays.asList(fileArray);
        }

        List fileNames = new ArrayList();
        Iterator it = files.iterator();
        while (it.hasNext()) {
            File f = (File)it.next();
            try {
                fileNames.add(f.getCanonicalPath());
            }
            catch (IOException ioe) {
                throw new ServiceException(ioe);
            }
        }

        return fileNames;
    }
    
    public static String getFileName(File file) throws ServiceException
    {
        try {
            return file.getCanonicalPath();
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
    }

    public static String getPath(String name)
	{
		return name.replace('.', File.separatorChar);
	}

	public static File getFile(File base, String path)
	{
		return new File(base, path.replace('/', File.separatorChar));
	}

	public static File getNSNodeSourceDir(String pkg, NSName node)
    {
        File nodeDir = Server.getResources().getPackageSourceDir(pkg);
        NSName parent  = node.getParent();

        if (parent != null) {
            nodeDir = new File(nodeDir, getPath(parent.toString()));
        }

        return nodeDir;
    }

    public static File getNSNodeJavaSource(String pkg, NSName node)
    {
        Resources resources = Server.getResources();

        File psd = resources.getPackageSourceDir(pkg);

        String baseName = node.isInterface() ? node.toString() : node.getInterfaceNSName().toString();

        return getFile(psd, getPath(baseName) + ".java");
    }

    public final static int DELETE = 0;

    public final static int EDIT = 1;

	// --- <<B2B-END-SHARED>> ---
}
