package wm.server;

// -----( B2B Java Code Template v1.2
// -----( CREATED: Thu Sep 03 12:00:00 GMT 1752
// -----( ON-HOST: hemlock.east.webmethods.com

// --- <<B2B-START-IMPORTS>> ---
import com.webmethods.vcs.*;
import com.webmethods.vcs.util.*;
import com.wm.app.b2b.server.ACLManager;
import com.wm.app.b2b.server.Server;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
import com.wm.data.*;
import com.wm.util.Values;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import com.wm.lang.ns.NSName;
// --- <<B2B-END-IMPORTS>> ---

public final class vcsadmin
{
	// ---( internal utility methods )---

	final static vcsadmin _instance = new vcsadmin();

	static vcsadmin _newInstance()
	{
		return new vcsadmin();
	}

	static vcsadmin _cast(Object o)
	{
		return (vcsadmin)o;
	}

	// ---( server methods )---

	public static final void startup(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(startup)>> ---
		// @sigtype java 3.5

        VCSLog.log(VCSLog.INFO, VCSLog.VCS_PKG_INITIALIZING);

        ACLManager.setAclGroup("wm.server.vcsimpl",                 "Developers");
		ACLManager.setAclGroup("wm.server.vcsadmin",                "Administrators");

        final String[][] ACLS = new String[][] {
            // name                                 list (browse)    read         write        execute
            { "pub.vcs.admin:setCurrentUser",      "Internal",       "WmPrivate", "WmPrivate", "Internal"       },
            { "pub.vcs.admin:removeCurrentUser",   "Internal",       "WmPrivate", "WmPrivate", "Internal"       },
            { "pub.vcs.admin:setMultipleUsers",    "Administrators", "WmPrivate", "WmPrivate", "Administrators" },
            { "pub.vcs.admin:removeMultipleUsers", "Administrators", "WmPrivate", "WmPrivate", "Administrators" },
            { "pub.vcs.admin:getUsers",            "Administrators", "WmPrivate", "WmPrivate", "Administrators" },
        };

        for (int ai = 0; ai < ACLS.length; ++ai) {
            // alas, setAclGroup really means setAclExecuteGroup
            String name  = ACLS[ai][0];
            String list  = ACLS[ai][1];
            String read  = ACLS[ai][2];
            String write = ACLS[ai][3];
            String exec  = ACLS[ai][4];

            ACLManager.setBrowseAclGroup(name, list);
            ACLManager.setReadAclGroup(name,   read);
            ACLManager.setWriteAclGroup(name,  write);
            ACLManager.setAclGroup(name,       exec); // means setExecAclGroup
        }

		ACLManager.setAclGroup("wm.vcs", "WmPrivate");
 		ACLManager.setBrowseAclGroup("wm.vcs", "WmPrivate");

		// --- <<B2B-END>> ---
	}

	public static final void shutdown(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(shutdown)>> ---
		// @sigtype java 3.5

        VCSLog.log(VCSLog.INFO, VCSLog.VCS_PKG_SHUTTING_DOWN);
        setEnabled(false);
       // Added for removing VCS from Solution link
       IData data = IDataFactory.create();
		IDataCursor dc = data.getCursor();
		IDataUtil.put(dc, "callback", "wm.server.vcsui:solutionMenu");
		        try {
					Service.doInvoke(NSName.create("wm.server.ui:removeSolution"), data);
		            }
		        catch (Exception e) {
		            throw new ServiceException(e);
        }

		// --- <<B2B-END>> ---
	}

	public static final void getSettings(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(getSettings)>> ---
		// @sigtype java 3.5

        VCSLog.log(VCSLog.DEBUG9, VCSLog.VCS_PKG_GETTING_INFO);

        IDataCursor cur = pipeline.getCursor();
        try {
            IDataUtil.put(cur, "info", VCSManager.getInstance().getInfo());
        }
        catch (VCSException vcse) {
            throw new ServiceException(vcse);
        }
        cur.destroy();

		// --- <<B2B-END>> ---
	}

	public static final void setSettings(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(setSettings)>> ---
		// @sigtype java 3.5
		// --- <<B2B-END>> ---
	}


	// --- <<B2B-START-SHARED>> ---

	public static final void setEnabled(boolean enabled) throws ServiceException
	{
		try {
			Values enArgs = new Values(new Object[][] {
                { "enabled", Boolean.toString(enabled) }
            });

            Service.doInvoke("wm.server.VCS", "setEnabled", enArgs);
		}
        catch (Exception e)	{
            throw new ServiceException(e);
		}
    }

	public static final boolean isEnabled() throws ServiceException
	{
		try {
			Values args = new Values(new Object[][] {});
            Service.doInvoke("wm.server.VCS", "isEnabled", args);
            String enabled = IDataUtil.getString(args.getCursor(), "enabled");
            return Boolean.TRUE.toString().equals(enabled);
		}
        catch (Exception e)	{
            throw new ServiceException(e);
		}
    }

	public static final void checkConnection() throws ServiceException
    {
        // See if we can ping the VCS; note that this doesn't check the
        // configuration such as username, host, port, and client. But at least
        // we'll know if there is a server that can be reached.
        try {
            VCSManager.getInstance().getInfo();
        }
        catch (VCSException vcse) {
            setEnabled(false);
            throw new ServiceException(vcse);
        }
    }

    /**
     * Runs the startup procedure for a VCS client.
     *
     * @param pkgName The name of the VCS client package, such as WmPerforce.
     * @param names   The names that should match the watt.vcs.type property for this VCS client.
     * @param cfgNames The names of the configuration files for this VCS client.
     * @param clientType The class of the VCS client. Should be a subclass of VCSClient, and take
     *                   no parameters in its constructor.
     * @param adminServices The administrator-only-accessible services in the VCS client package.
     * @param developerServices The developer-accessible services in the VCS client package.
     */
    public static final void clientStartup(String pkgName,
                                           String[] names,
                                           String[] cfgNames,
                                           Class clientType,
                                           String adminServices,
                                           String developerServices) throws ServiceException
    {
        setACLs(adminServices, developerServices);

        // The code in wm.VCS already sets itself as enabled, but it does not
        // get invoked if this package gets reloaded. So this is a call back to
        // the wm.VCS initialization code. Note that this will get ignored if
        // the locking mode isn't set to vcs.
        setEnabled(true);

        if (isEnabled()) {
            if (false) {
                // any no-ops to start up?
                Clients clients = Clients.getInstance();
                Iterator nit = clients.getNames().iterator();
                while (nit.hasNext()) {
                    String name = (String)nit.next();
                    String type = clients.getType(name);
                    for (int ni = 0; ni < names.length; ++ni) {
                        String vcsName  = names[ni];
                        if (vcsName.equals(type)) {
                            String[] packages = (String[])clients.getPackages(name);
                            String[][] params = (String[][])clients.getParameters(name);
                        }
                    }
                }
            }

            boolean foundFile = false;
            File cnf = null;
            for (int fi = 0; fi < cfgNames.length; ++fi) {
                cnf = new File(Server.getResources().getPackageConfigDir(pkgName), cfgNames[fi]);
                boolean read = Config.getInstance().readFile(cnf);
                foundFile = foundFile ? true : read;
            }

            if (!foundFile) {
                VCSLog.log(VCSLog.INFO, VCSLog.VCS_PKG_NO_CONFIG_FILE_READ, pkgName, cnf.getPath());
            }

            String vcsType = Config.getInstance().getProperty(VCSManager.SYSTEM_TYPE_KEY);

            // either a single one, or multiples.

            if (match(names, vcsType)) {
                VCSLog.log(VCSLog.INFO, VCSLog.VCS_CLIENT_SETTING, vcsType);

                // VCS clients can assume that their configuration has been
                // loaded into the system properties before they are created, so
                // we load the configuration files first. (Thanks, Dan.)

                createClient(clientType, null, null);

                VCSLog.log(VCSLog.INFO, VCSLog.VCS_PKG_ENABLED);
            }
        }
        else {
            VCSLog.log(VCSLog.INFO, VCSLog.VCS_PKG_NOT_ENABLED);
        }
    }

    protected static void createClient(Class clientType, String[] packages, String[][] properties)
         throws ServiceException
    {
        String aliasName = null;

        try {
            Constructor ctor = clientType.getConstructor(new Class[] { String.class });
            VCSClient client = (VCSClient)ctor.newInstance(new Object[] { aliasName });
            VCSManager.getInstance().addClient(client, packages);
        }
        catch (NoSuchMethodException nsme) {
            throw new ServiceException(nsme);
        }
        catch (IllegalArgumentException iae) {
            throw new ServiceException(iae);
        }
        catch (IllegalAccessException iae) {
            throw new ServiceException(iae);
        }
        catch (InstantiationException ie) {
            throw new ServiceException(ie);
        }
        catch (InvocationTargetException ite) {
            throw new ServiceException(ite);
        }
        catch (ExceptionInInitializerError eiie) { // EEIEIEE!
            throw new ServiceException(eiie);
        }

        // no longer doing a ping at startup, since this package could be run
        // before the users have been configured.

        // vcsadmin.checkConnection();

        VCSLog.log(VCSLog.INFO, VCSLog.VCS_PKG_ENABLED);
    }

    protected static IData getClientAsIData(String name)
    {
        Clients clients = Clients.getInstance();

        String      type     = clients.getType(name);
        String[]    packages = clients.getPackages(name);
        String[][]  params   = clients.getParameters(name);
        String[][]  parray   = new String[params.length][];
        IData       pdata    = IDataFactory.create();
        IDataCursor pc       = pdata.getCursor();

        for (int pi = 0; params != null && pi < params.length; ++pi) {
            IDataUtil.put(pc, "name",  params[pi][0]);
            IDataUtil.put(pc, "value", params[pi][1]);
        }

        IData cdata = IDataFactory.create(new Object[][] {
            { "name",       name     },
            { "type",       type     },
            { "packages",   packages },
            { "parameters", pdata    }
        });

        return cdata;
    }

	public static final String IMPL_SERVICES = "wm.server.vcsimpl";

	public static final String ADMIN_SERVICES = "wm.server.vcsadmin";

	protected static final void setACLs(String adminServices, String developerServices)
	{
        ACLManager.setAclGroup(developerServices, "Developers");
		ACLManager.setAclGroup(adminServices,     "Administrators");
	}

    protected static boolean match(String[] ary, String s)
    {
        for (int ai = 0; ai < ary.length; ++ai) {
            if (ary[ai].equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

	// --- <<B2B-END-SHARED>> ---

}

