package com.webmethods.vcs.util;

import com.wm.app.b2b.server.Package;
import com.wm.lang.ns.NSNode;


public class NSElement
{
    private Package pkg;

    private NSNode node;
    
    public NSElement(Package pkg, NSNode node)
    {
        this.pkg = pkg;
        this.node = node;
    }
    
    public NSElement(Package pkg)
    {
        this(pkg, null);
    }

    public NSElement(NSNode node)
    {
        this(null, node);
    }

    public Package getPackage()
    {
        return pkg;
    }

    public NSNode getNode()
    {
        return node;
    }

}
