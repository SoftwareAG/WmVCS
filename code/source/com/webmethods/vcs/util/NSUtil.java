package com.webmethods.vcs.util;

import com.webmethods.client.shared.io.FileExt;
import com.wm.app.b2b.server.*;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.ns.HasPersistedFiles;
import com.wm.app.b2b.server.ns.Interface;
import com.wm.app.b2b.server.ns.NSLockManager;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.data.*;
import com.wm.lang.ns.*;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Various utilities related to the com.wm.lang.ns.* and
 * com.wm.app.b2b.server.ns.* classes.
 */
public class NSUtil
{
    public static final int INCLUDE_PARENT = 1;
    public static final int INCLUDE_SIBLINGS = 2;
    public static final int EXISTING_ONLY = 4;
    
    //This needs to be replaced later by finding out the correct
    //constant reference
    private static final String BLAZE_SUB_TYPE = "blaze";  
    //Added for PIE-25715
    public static final String WSD_TYPE_KEY = "webServiceDescriptor";

    public static List getDirectoryNames(Package pkg, NSNode node) throws ServiceException
    {
        if (node == null) {
            return Arrays.asList(new String[] { getDirectoryName(pkg) });
        }
        else {
            List files       = getEntries(pkg, node, 0);
            List directories = getDirectoryNames(files);
            return directories;
        }
    }

    public static String getPackagePath(Package pkg, String subdir) throws ServiceException
    {
        File dir = getPackageFile(pkg, subdir);

        try {
            return dir.getCanonicalPath();
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
    }

    public static File getPackageFile(Package pkg, String subdir) throws ServiceException
    {
        Resources resources = Server.getResources();
        File      pkgDir    = resources.getPackageDir(pkg.getName());
        File      dir       = subdir == null ? pkgDir : resources.getFile(pkgDir, subdir);
        
        return dir;
    }

    public static String getDirectoryName(Package pkg) throws ServiceException
    {
        File pkgDir = Server.getResources().getPackageNSDir(pkg.getName());
        try {
            return pkgDir.getCanonicalPath();
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
    }

    public static List getDirectoryNames(List fileNames) throws ServiceException
    {
        List directories = new ArrayList();
        try {
            Iterator it = fileNames.iterator();
            while (it.hasNext()) {
                String fileName = (String)it.next();
                File   file     = new File(fileName);
                String dir      = file.getCanonicalFile().getParent();
                if (directories.contains(dir)) {
                    // already contained, so not re-adding
                }
                else {
                    directories.add(dir);
                }
            }
            return directories;
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
    }

    /**
     * Essentially, a "find . -type f" on the node.
     */
    public static List findAllPersistenceEntities(Package pkg, NSNode node) throws IOException
    {
        List entities = getPersistenceEntities(pkg, node, EXISTING_ONLY | INCLUDE_PARENT);
        if (entities == null) {
            entities = new ArrayList();
        }

        if (node == null) {
            // get all files for each node in the package.
            List     nodes = Namespace.current().getNodes(pkg);
            Iterator it    = nodes.iterator();
            while (it.hasNext()) {
                NSNode snode = (NSNode)it.next();
                List   ents  = findAllPersistenceEntities(pkg, snode);
                entities.addAll(ents);
            }
        }
        else if (node instanceof Interface) {
            NSNode[] subnodes = ((Interface)node).getNodes();
            for (int ni = 0; ni < subnodes.length; ++ni) {
                List subent = findAllPersistenceEntities(pkg, subnodes[ni]);
                entities.addAll(subent);
            }
        }
        else {
            // NOT an interface
        }

        return entities;
    }

    /**
     * Given a node and its containing package, searches for all the
     * "controlling" files of that node, and returns whether they are writable
     * or not. This is essentially right from nsimpl, but takes into account
     * that NSNodes might know what entities are associated with them.
     *
     * @param node The node that owns the returned files.
     * @param pkg  The package of the given node.
     * @param mask See the fields at top.
     */
    public static List getEntries(Package pkg, NSNode node, int mask) throws ServiceException
    {
        try {
            return getPersistenceEntities(pkg, node, mask);
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
    }

    /**
     * Given a node and its containing package, searches for all the
     * "controlling" files of that node, and returns whether they are writable
     * or not. This is essentially right from nsimpl, but takes into account
     * that NSNodes might know what entities are associated with them.
     *
     * @param node The node that owns the returned files.
     * @param pkg  The package of the given node.
     * @param mask See the fields at top.
     */
    public static List getPersistenceEntities(Package pkg, NSNode node, int mask) throws IOException
    {
        if (pkg == null || node == null) {
            return null;
        }
        else {
            NSType type     = node.getNodeTypeObj();
            NSName nodeName = node.getNSName();
            NSName ifcName  = nodeName.getInterfaceNSName();
            String pkgName  = pkg.getName();

            Set controlFiles = new HashSet();

            boolean existingOnly = (mask & EXISTING_ONLY) != 0;

            if (node instanceof HasPersistedFiles) {
                // A plugin can implement the HasPersistedFiles interface, to
                // define the files associated with the plugin node.
                HasPersistedFiles pers = (HasPersistedFiles)node;
                    
                java.util.List files = pers.getPersistedFiles();
                    
                if (files != null) {
                    Iterator it = files.iterator();
                    while (it.hasNext()) {
                        File file = (File)it.next();
                        // Possibly bypass the "existing file" check, since this
                        // method could be called before an associated file
                        // exists for a service.
                        addFile(controlFiles, file, existingOnly);
                    }
                }
            }
        
            if (type.equals(NSNode.NODE_UNKNOWN_TYPE)) {
                return null;
            }
            else if (type.equals(NSService.TYPE)) {
                File ifcDir = pkg.getStore().getNodePath(ifcName);
                File svcDir = pkg.getStore().getNodePath(nodeName);

                // node.ndf
                addFile(controlFiles, svcDir, PackageStore.NDF_FILE, existingOnly);
            
                NSService     nss     = (NSService)node;
                NSServiceType svctype = nss.getServiceType();

                if (svctype.isJavaService()) {
                    Resources r = Server.getResources();

                    if ((mask & INCLUDE_SIBLINGS) != 0) {
                        List siblings = JavaServiceUtil.getJavaSiblings((JavaService)nss);
                        Iterator sit = siblings.iterator();
                        while (sit.hasNext()) {
                            JavaService sib = (JavaService)sit.next();

                            NSName sibName = sib.getNSName();                            
                            File sibDir = pkg.getStore().getNodePath(sibName);
                            
                            // java.frag
                            addFile(controlFiles, sibDir, PackageStore.JAV_FILE, existingOnly);

                            // node.ndf
                            addFile(controlFiles, sibDir, PackageStore.NDF_FILE, existingOnly);
                        }
                    }
                    else {
                        // only this service; not its siblings.
                        
                        // java.frag
                        addFile(controlFiles, svcDir, PackageStore.JAV_FILE, existingOnly);
                    }
                
                    // this is a hack, since existingOnly is used when we're
                    // about to delete files, but the .java and node.idf files
                    // should be deleted only if there are no more siblings.

                    if ((mask & INCLUDE_PARENT) != 0) {
                        // returning .java and node.idf files

                        // Foo.java
                        addFile(controlFiles, r.getNSNodeJavaSource(pkgName, nodeName), existingOnly);
                        
                        // Foo.class is ignored, because it's a generated file.
                        // addFile(controlFiles, r.getNSNodeJavaClass(pkgName, nodeName), existingOnly);
                    
                        // node.idf
                        addFile(controlFiles, ifcDir, PackageStore.IDF_FILE, existingOnly);
                    }
                }
                else if (svctype.isFlowService()) {
                    // flow.xml
                    addFile(controlFiles, svcDir, PackageStore.FLW_FILE, existingOnly);
                }else if (svctype.getSubtype()!= null && svctype.getSubtype().equals(BLAZE_SUB_TYPE)){
                    addFile(controlFiles, svcDir, PackageStore.FLW_FILE, existingOnly);
                }
            }
            else if (type.equals(NSInterface.TYPE)) {
                File ifcDir = pkg.getStore().getNodePath(nodeName);

                // node.idf
                addFile(controlFiles, ifcDir, PackageStore.IDF_FILE, existingOnly);
            }
            else{
                // type == NODE_RECORD, NODE_SCHEMA
                File nodeDir = pkg.getStore().getNodePath(nodeName);
            
                // node.ndf
                addFile(controlFiles, nodeDir, PackageStore.NDF_FILE, existingOnly);
                //For web service descriptor , it is mandatory to include wsdl file.
                if(type.equals(WSD_TYPE_KEY))
                {
                	 addFile(controlFiles, nodeDir, "wsdl0", existingOnly);
                	//if there is xsd files , those also need to be deleted - existingOnly useful here
                  	addFile(controlFiles, nodeDir, "xsd0", existingOnly);
                   	addFile(controlFiles, nodeDir, "xsd1", existingOnly);
                }
            }

            return new ArrayList(controlFiles);
        }
    }

    public static String getNodeIdf(NSService svc) throws IOException
    {
        NSName    nodeName = svc.getNSName();
        NSName    ifcName  = nodeName.getInterfaceNSName();
        Package   pkg      = (Package)svc.getPackage();
        File      ifcDir   = pkg.getStore().getNodePath(ifcName);
        File      idfFile  =  new File(ifcDir, PackageStore.IDF_FILE);

        return idfFile.getCanonicalPath();
    }

    /**
     * Adds the given file to the collection. If <code>existingOnly</code> is
     * true, the file is added only if it exists.
     */ 
    protected static void addFile(Collection c, File file, boolean existingOnly) throws IOException
    {
        if (existingOnly && !file.exists()) {
            // file does not exist
        }
        else {
            c.add(file.getCanonicalPath());
        }
    }

    /**
     * Adds the given file to the collection.
     */ 
    protected static void addFile(Collection c, File dir, String fileName, boolean existingOnly) throws IOException
    {
        File file = new File(dir, fileName);
        addFile(c, file, existingOnly);
    }

    /**
     * Returns the persistent entities to put in a VCS, recursively from the
     * node.
     */
    public static List findAllEntries(Package pkg, NSNode node) throws ServiceException
    {
        try {
            return findAllPersistenceEntities(pkg, node);
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
    }

    /**
     * Returns the persistent entities to put in a VCS.
     */
    public static List findAllEntries(String pkgName, String nodeName) throws ServiceException
    {
        Package pkg  = getPackage(pkgName);
        NSNode  node = getNode(nodeName);
        return findAllEntries(pkg, node);
    }

    /**
     * Compiles the package, if there are any .java files under code/source in
     * the given package.
     */
    public static void compilePackage(String pkgName) throws ServiceException
    {
        File srcDir = Server.getResources().getPackageSourceDir(pkgName);
        boolean hasJava = FileExt.findFiles(srcDir, ".java", 1).size() > 0;

        if (hasJava) {
            try {
                NodeUtil        frag = new NodeUtil(Server.getHomeDir());
                CharArrayWriter log  = new CharArrayWriter();

                frag.setVerbose(log);
                frag.makeall(pkgName);
            }
            catch (Exception e) {
                throw new ServiceException(e);
            }
        }
        else {
            // nothing to do.
        }
    }

    /**
     * Recompiles and reloads the packages in the list.
     */
    public static void refreshPackages(Collection packages) throws ServiceException
    {
        Iterator pit = packages.iterator();
        while (pit.hasNext()) {
            String pkgName = (String)pit.next();
            refreshPackage(pkgName);
        }
    }

    /**
     * Recompiles and reloads the package.
     */
    public static void refreshPackage(String pkgName) throws ServiceException
    {
        compilePackage(pkgName);
                    
        IData data = IDataFactory.create();
        IDataUtil.put(data.getCursor(), "package", pkgName);
        try {
            Service.doInvoke("wm.server.packages", "packageReload", data);
        }
        catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Returns the package for the current namespace.
     */
    public static Package getPackage(String pkgName)
    {
        return pkgName == null ? null : (Package)Namespace.current().getPackage(pkgName);
    }

    /**
     * Returns the node for the current namespace.
     */
    public static NSNode getNode(String nodeName)
    {
        if (nodeName == null) {
            return null;
        }
        else {
            NSName name = NSName.create(nodeName);
            NSNode node = name == null ? null : Namespace.current().getNode(name);
            return node;
        }
    }

    public static boolean isLockedByOtherUser(NSNode node)
    {
        if (node == null) {
            return false;
        }

        String nsn = node.getNSName().getFullName();
        
        IData lockDetails = NSLockManager.getLockDetails(nsn);

        // This is copied from NSLockManager, and seems to allow the condition
        // of not having write ACL if the node is locked by the user. WTF?

        if (!ACLManager.writeAcl(nsn)) {
            if (lockDetails == null) {
                return true;
            }
            else {
                Integer lockStatus = NSLockManager.getLockStatus(lockDetails);
                if (lockStatus.intValue() != NSNode.LOCKED_USER_LOCKED) {
                    return true;
                }
            }
        }

        if (node instanceof JavaService) {
            JavaService javaSvc  = (JavaService)node;
            NSName      ifcName  = javaSvc.getNSName().getInterfaceNSName();
            List        siblings = JavaServiceUtil.getJavaSiblings(javaSvc);
            Iterator    sit      = siblings.iterator();

            while (sit.hasNext()) {
                JavaService sibSvc = (JavaService)sit.next();
                String  sibName        = sibSvc.getNSName().getFullName();        
                IData   sibLockDetails = NSLockManager.getLockDetails(sibName);
                Integer sibLockStatus  = NSLockManager.getLockStatus(sibLockDetails);
                if (sibLockStatus.intValue() != NSNode.UNLOCKED && sibLockStatus.intValue() != NSNode.LOCKED_USER_LOCKED) {
                    return true;
                }
            }

            // no siblings are locked.
            return false;
        }
        else {
            Integer lockStatus     = NSLockManager.getLockStatus(lockDetails);
            boolean elsewiseLocked = lockStatus.intValue() != NSNode.UNLOCKED && lockStatus.intValue() != NSNode.LOCKED_USER_LOCKED;
            return elsewiseLocked;
        }
    }

    public static NSInterface getInterface(NSNode node)
    {
        NSName      nsName  = node.getNSName();
        NSName      ifcName = nsName.getInterfaceNSName();
        NSInterface ifc     = (NSInterface)Namespace.current().getNode(ifcName);
        return ifc;
    }

    /**
     * Returns the siblings of the given service, including itself, that are in
     * the same package. If the <code>siblingType</code> parameter is not null,
     * only siblings of the given type will be returned.
     */
    public static List getSiblingServices(NSService svc, Class siblingType)
    {
        NSInterface ifc      = getInterface(svc);
        NSNode[]    siblings = ifc.getNodes();
        String      pkg      = svc.getPackage().getName();
        List        inPkg    = new ArrayList();

        for (int i = 0; i < siblings.length; ++i) {
            NSNode    sibling = siblings[i];
            NSPackage sibPkg  = sibling.getPackage();
            // interfaces might not have packages, but they're not siblings anyway.
            if (sibPkg       != null && pkg.equals(sibPkg.getName()) &&
                (siblingType == null || siblingType.isInstance(sibling))) {
                inPkg.add(sibling);
            }
        }

        return inPkg;
    }

    /**
     * Returns the siblings of the given service, including itself.
     */
    public static List getSiblingServices(NSService svc)
    {
        return getSiblingServices(svc, null);
    }


    /**
     * Returns whether the given node is in the package.
     */
    public static boolean isNodeInPackage(Package pkg, NSNode node) 
    {
        if (node == null) {
            return false;
        }
        else if (node.getNodeTypeObj().equals(NSInterface.TYPE)) {
            return pkg.getStore().descriptionPathExists(node.getNSName());
        }
        else {
            return pkg == (Package)node.getPackage();
        }
    }
    
    /**
     * Returns all nodes under the given interface, in the given package.
     */
    public static List getNodesInPackage(Package pkg, NSInterface iface, List nodesList)
    {
        List nList = nodesList;
        if (nList == null) {
            nList = new ArrayList();
        }
        
        NSNode[] nodes = iface.getNodes();

        for (int ni = 0; ni < nodes.length; ++ni) {
            if (isNodeInPackage(pkg, nodes[ni])) {
                nList.add(nodes[ni]);
                if (nodes[ni] instanceof NSInterface) {
                    getNodesInPackage(pkg, (NSInterface)nodes[ni], nList);
                }
            }
        }

        return nList;
    }

    /**
     * Returns all nodes in the given package.
     */
    public static List getNodesInPackage(Package pkg, List nodesList)
    {
        return getNodesInPackage(pkg, Namespace.getInterface(null), nodesList);
    }

    /**
     * Returns a list of the node names for the given nodes, getting interfaces
     * and packages hierarchically. If <code>nodeName</code> is null, all nodes
     * from the given package will be returned.
     */
    public static List getNodeNameList(String pkgName, String nodeName)
    {
        Package pkg = getPackage(pkgName);
        NSNode nsNode = NSUtil.getNode(nodeName);
        return getNodeNameList(pkg, nsNode);
    }

    /**
     * Returns a list of the node names for the given nodes, getting interfaces
     * and packages hierarchically. If <code>nodeName</code> is null, all nodes
     * from the given package will be returned.
     */
    public static List getNodeNameList(Package pkg, NSNode node)
    {
        List nodes = new ArrayList();
        if (node == null) {
            nodes = getNodesInPackage(pkg, null);
        }
        else {
            if (node instanceof Interface) {
                nodes = getNodesInPackage(pkg, (NSInterface)node, null);
            }
            else if (node != null) {
                nodes.add(node);
            }
        }

        List names = new ArrayList();
        
        Iterator nit = nodes.iterator();
        while (nit.hasNext()) {
            NSNode n = (NSNode)nit.next();
            names.add(n.getNSName().getFullName());
        }

        return names;
    }

}
