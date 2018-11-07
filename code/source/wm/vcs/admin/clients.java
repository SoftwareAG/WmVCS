package wm.vcs.admin;

// -----( B2B Java Code Template v1.2
// -----( CREATED: Thu Sep 03 12:00:00 GMT 1752
// -----( ON-HOST: hemlock.east.webmethods.com

// --- <<B2B-START-IMPORTS>> ---
import com.webmethods.vcs.*;
import com.webmethods.vcs.util.*;
import com.wm.app.b2b.server.ServiceException;
import com.wm.data.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
// --- <<B2B-END-IMPORTS>> ---

public final class clients
{
	// ---( internal utility methods )---

	final static clients _instance = new clients();

	static clients _newInstance()
	{
		return new clients();
	}

	static clients _cast(Object o)
	{
		return (clients)o;
	}

	// ---( server methods )---


	public static final void addClient(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(addClient)>> ---
		// @sigtype java 3.5
        // [i] field:0:required name
        // [i] field:0:required type { "p4", "vss", "none" }
        // [i] field:1:required packages
        // [i] record:1:required parameters
        // [i] - field:0:required name
        // [i] - field:0:required value

        IDataCursor cur = pipeline.getCursor();

        String     name       = IDataUtil.getString(cur, "name");
        String     type       = IDataUtil.getString(cur, "type");
        String[]   packages   = IDataUtil.getStringArray(cur, "packages");
        IData[]    parameters = IDataUtil.getIDataArray(cur, "parameters");
        
        String[][] params     = new String[parameters.length][];
        for (int pi = 0; pi < parameters.length; ++pi) {
            IData       param  = parameters[pi];
            IDataCursor pc     = param.getCursor();
            String      pname  = IDataUtil.getString(pc, "name");
            String      pvalue = IDataUtil.getString(pc, "value");
            params[pi] = new String[] { pname, pvalue };
        }

        Clients.getInstance().setClient(name, type, packages, params);
                                        
        try {
            Clients.getInstance().writeFile();
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
        
        cur.destroy();

		// --- <<B2B-END>> ---
    }   


	public static final void getClients(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(getClients)>> ---
		// @sigtype java 3.5
        // [o] record:1:required clients
        // [o] - field:0:required name
        // [o] - field:0:required type
        // [o] - field:1:required packages
        // [o] - record:1:required parameters
        // [o] -- field:0:required name
        // [o] -- field:0:required value

        IDataCursor cur = pipeline.getCursor();

        IData       cldata = IDataFactory.create();
        IDataCursor cdc    = cldata.getCursor();
        Collection  names  = Clients.getInstance().getNames();
        Iterator    nit    = names.iterator();
        while (nit.hasNext()) {
            String name  = (String)nit.next();
            IData  cdata = getClientAsIData(name);
            IDataUtil.put(cdc, name, cdata);
        }

        IDataUtil.put(cur, "clients", cldata);

        cur.destroy();

		// --- <<B2B-END>> ---
    }   


	public static final void getClient(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(getClient)>> ---
		// @sigtype java 3.5
        // [i] field:0:required name
        // [o] record:0:required client
        // [o] - field:0:required name
        // [o] - field:0:required type
        // [o] - field:1:required packages
        // [o] - record:1:required parameters
        // [o] -- field:0:required name
        // [o] -- field:0:required value

        IDataCursor cur     = pipeline.getCursor();
        String      name    = (String)IDataUtil.get(cur, "name");
        Clients     clients = Clients.getInstance();
        if (name != null && clients.hasClient(name)) {
            IData cdata = getClientAsIData(name);
            IDataUtil.put(cur, "client", cdata);
        }

        cur.destroy();

		// --- <<B2B-END>> ---
    }   


	public static final void removeClient(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(removeClient)>> ---
		// @sigtype java 3.5
        // [i] field:0:required name

        IDataCursor cur = pipeline.getCursor();

        String  name    = (String)IDataUtil.get(cur, "name");
        Clients clients = Clients.getInstance();
        if (name != null && clients.hasClient(name)) {
            clients.removeClient(name);
        }
        
        cur.destroy();

		// --- <<B2B-END>> ---
    }   

	// --- <<B2B-START-SHARED>> ---

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

	// --- <<B2B-END-SHARED>> ---

}

