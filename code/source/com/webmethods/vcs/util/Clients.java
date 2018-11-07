package com.webmethods.vcs.util;

import com.webmethods.vcs.VCSLog;
import com.wm.app.b2b.server.Server;
import com.wm.util.Values;
import com.wm.util.coder.XMLCoder;
import java.io.*;
import java.util.*;


/**
 * Reads a configuration file of clients and passwords.
 */
public class Clients
{
    private Map clients = new HashMap();

    private static Clients instance = null;

    public static final String VCS_PASSWORD_KEY = "vcsPassword";
    
    public static final String VCS_NAME_KEY = "vcsName";
    
    public static Clients getInstance()
    {
        if (instance == null) {
            instance = new Clients();
        }
        return instance;
    }

    public boolean hasClient(String name)
    {
        return getEntry(name) != null;
    }

    public Collection getNames()
    {
        return clients.keySet();
    }

    public String getType(String name)
    {
        return (String)getEntryValue(name, 0);
    }

    public String[] getPackages(String name)
    {
        return (String[])getEntryValue(name, 1);
    }

    public String[][] getParameters(String name)
    {
        return (String[][])getEntryValue(name, 2);
    }

    public void setClient(String name, String type, String[] packages, String[][] params)
    {
        clients.put(name, new Object[] { type, packages, params });
    }

    public void removeClient(String name)
    {
        clients.remove(name);
    }
    
    public File getFile()
    {
        File cnf = new File(Server.getResources().getPackageConfigDir("WmVCS"), "clients.cnf");
        return cnf;
    }
    
    public void readFile()
    {
        File cnf = getFile();
		try {
			if (cnf.exists()) {
                // VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_LOADING_CLIENTS_FILE, cnf);

                XMLCoder coder = new XMLCoder();
                Values values = coder.readFromFile(cnf);

                Enumeration ken = values.keys();
                while (ken.hasMoreElements()) {
                    String     name     = (String)ken.nextElement();
                    Values     client   = (Values)values.get(name);
                    String     type     = (String)client.get("type");
                    String[]   packages = (String[])client.get("packages");
                    String[][] params   = (String[][])client.get("params");
                    
                    setClient(name, type, packages, params);
                }
			}
            else {
                // VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_NO_CLIENTS_FILE);
            }
		}
		catch (Exception e) {
            // VCSLog.log(VCSLog.WARN, VCSLog.VCS_PKG_ERROR_LOADING_CLIENTS_FILE, e.getMessage());
		}
    }

    public void writeFile() throws IOException
    {
        File cnf = getFile();
        
        try {
            // VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_SAVING_CLIENTS_FILE, cnf);

            Values values = new Values();
            Iterator it = clients.keySet().iterator();
            while (it.hasNext()) {
                String   name    = (String)it.next();
                Object[] entry   = getEntry(name);
                Values   client  = new Values();
                
                client.put("type",     entry[0]);
                client.put("packages", entry[1]);
                client.put("params",   entry[2]);
                values.put(name, client);
            }

            XMLCoder coder = new XMLCoder();
            coder.writeToFile(cnf, values);
		}
		catch (IOException io) {
            // VCSLog.log(VCSLog.WARN, VCSLog.VCS_PKG_ERROR_SAVING_CLIENTS_FILE, io.getMessage());
            throw io;
		}
    }

    protected Object getEntryValue(String name, int index)
    {
        Object[] entry = getEntry(name);
        if (entry == null || index >= entry.length || entry[index] == null) {
            return null;
        }
        else {
            return entry[index];
        }
    }

    protected Object[] getEntry(String name)
    {
        Object[] entry = (Object[])clients.get(name);
        return entry;
    }

    protected Clients()
    {
        readFile();
    }

}
