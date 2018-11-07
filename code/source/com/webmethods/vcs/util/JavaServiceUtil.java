package com.webmethods.vcs.util;

import com.wm.app.b2b.server.*;
import com.wm.app.b2b.server.Package;
import com.wm.data.*;
import com.wm.lang.ns.*;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Various utilities related to the com.wm.lang.ns.* and
 * com.wm.app.b2b.server.ns.* classes.
 */
public class JavaServiceUtil extends NSUtil
{
    /**
     * Returns the .idf and the .java (not java.frag) files for the given Java
     * service.
     */
    public static List getParentEntries(Package pkg, JavaService js) throws ServiceException
    {
        List   entries  = new ArrayList();
        NSName nodeName = js.getNSName();
        NSName ifcName  = nodeName.getInterfaceNSName();
        String pkgName  = pkg.getName();
        File   ifcDir   = pkg.getStore().getNodePath(ifcName);
        File   svcDir   = pkg.getStore().getNodePath(nodeName);
        
        Resources r = Server.getResources();

        try {
            addFile(entries, r.getNSNodeJavaSource(pkgName, nodeName), true);
            addFile(entries, ifcDir, PackageStore.IDF_FILE,            true);
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }

        return entries;
    }

    public static String getSourceFile(Package pkg, JavaService js) throws IOException
    {
        if (pkg == null || js == null) {
            return null;
        }
        else {
            Resources r = Server.getResources();

            String pkgName  = pkg.getName();
            NSName nodeName = js.getNSName();

            // Foo.java
            return r.getNSNodeJavaSource(pkgName, nodeName).getCanonicalPath();
        }
    }

    /**
     * Returns the siblings (including this service) of this Java service.
     */
    public static List getJavaSiblings(JavaService javaSvc)
    {
        return NSUtil.getSiblingServices(javaSvc, JavaService.class);
    }

    public static List getJavaEntries(Package pkg,
                                      JavaService js, 
                                      boolean existingOnly,
                                      boolean includeSiblings, 
                                      boolean includeJavaSrc, 
                                      boolean includeNodeIdf) throws IOException
    {
        List entries = new ArrayList();

        List javas = new ArrayList();

        if (includeSiblings) {
            javas = getJavaSiblings(js);
        }
        else {
            javas.add(js);
        }

        Iterator jit = javas.iterator();
        while (jit.hasNext()) {
            JavaService sib = (JavaService)jit.next();
            
            NSName sibName = sib.getNSName();                            
            File sibDir = pkg.getStore().getNodePath(sibName);
            
            // java.frag
            addFile(entries, sibDir, PackageStore.JAV_FILE, existingOnly);
            
            // node.ndf
            addFile(entries, sibDir, PackageStore.NDF_FILE, existingOnly);
        }
        
        NSName nodeName = js.getNSName();

        if (includeJavaSrc) {
            Resources r = Server.getResources();
            String pkgName = pkg.getName();

            // Foo.java
            addFile(entries, r.getNSNodeJavaSource(pkgName, nodeName), existingOnly);
        }

        if (includeNodeIdf) {
            NSName ifcName = nodeName.getInterfaceNSName();
            File   ifcDir  = pkg.getStore().getNodePath(ifcName);

            // node.idf
            addFile(entries, ifcDir, PackageStore.IDF_FILE, existingOnly);
        }

        return entries;
    }

}
