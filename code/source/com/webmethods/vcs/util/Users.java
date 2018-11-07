package com.webmethods.vcs.util;

import com.webmethods.vcs.VCSLog;
import com.wm.app.b2b.server.OutboundPasswordManager;
import com.wm.app.b2b.server.Server;
import com.wm.passman.PasswordManagerException;
import com.wm.util.Values;
import com.wm.util.coder.XMLCoder;
import com.wm.util.security.WmSecureString;
import java.io.*;
import java.util.*;


/**
 * Reads a configuration file of users and passwords.
 */
public class Users
{
    private Map users = new HashMap();

    private static Users instance = null;

    private static final String PASS_HANDLE_PREFIX = OutboundPasswordManager.OPE_ADMIN_HANDLE_PREFIX + ".vcs.";

    public static final String VCS_PASSWORD_KEY = "vcsPassword";
    
    public static final String VCS_NAME_KEY = "vcsName";
    
    public static Users getInstance()
    {
        if (instance == null) {
            instance = new Users();
        }
        return instance;
    }

    public boolean hasUser(String devName)
    {
        return getEntry(devName) != null;
    }

    public String[][] getUsers()
    {
        List userList = new ArrayList();

        Iterator it = users.keySet().iterator();
        while (it.hasNext()) {
            String   devName = (String)it.next();
            Object[] entry   = getEntry(devName);
            String   vcsName = entry[0].toString();
            userList.add(new String[] { devName, vcsName });
        }

        return (String[][])userList.toArray(new String[0][]);
    }

    public String getUser(String devName)
    {
        return getEntryValue(devName, 0);
    }

    public String getPassword(String devName)
    {
        return getEntryValue(devName, 1);
    }

    public void setUser(String devName, String userName, String password)
    {
        WmSecureString ss = null;
        
        if (password != null) {
            String handle = PASS_HANDLE_PREFIX + userName;
            ss = new WmSecureString(password);
            try {
                OutboundPasswordManager.storePassword(handle, ss);
            }
            catch (PasswordManagerException pme) {
                pme.printStackTrace(System.out);
            }
        }
            
        users.put(devName, new Object[] { userName, ss });
    }

    public void removeUser(String devName)
    {
        users.remove(devName);
    }
    
    public File getFile()
    {
        File cnf = new File(Server.getResources().getPackageConfigDir("WmVCS"), "users.cnf");
        return cnf;
    }
    
    public void readFile()
    {
        File cnf = getFile();
		try {
			if (cnf.exists()) {
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_LOADING_USERS_FILE, cnf);

                XMLCoder coder  = new XMLCoder();
                Values   values = coder.readFromFile(cnf);
                
                Enumeration ken = values.keys();
                while (ken.hasMoreElements()) {
                    String devName = (String)ken.nextElement();
                    Values user    = (Values)values.get(devName);
                    String vcsName = (String)user.get(VCS_NAME_KEY);
                    String handle  = (String)user.get(VCS_PASSWORD_KEY);
                    
                    WmSecureString ss = OutboundPasswordManager.retrievePassword(handle);
                    
                    users.put(devName, new Object[] { vcsName, ss });
                }
			}
            else {
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_NO_USERS_FILE);
            }
		}
		catch (Exception e) {
            VCSLog.log(VCSLog.WARN, VCSLog.VCS_PKG_ERROR_LOADING_USERS_FILE, e.getMessage());
		}
    }

    public void writeFile() throws IOException
    {
        File cnf = getFile();
        
        try {
            VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_SAVING_USERS_FILE, cnf);

            Values values = new Values();
            Iterator it = users.keySet().iterator();
            while (it.hasNext()) {
                String   devName = (String)it.next();
                Object[] entry   = getEntry(devName);
                Values   user    = new Values();
                String   vcsName = entry[0].toString();
                user.put(VCS_NAME_KEY, vcsName);
                if (entry.length > 1 && entry[1] != null) {
                    String handle = PASS_HANDLE_PREFIX + vcsName;
                    user.put(VCS_PASSWORD_KEY, handle);
                }
                values.put(devName, user);
            }

            XMLCoder coder = new XMLCoder();
            coder.writeToFile(cnf, values);
		}
		catch (IOException io) {
            VCSLog.log(VCSLog.WARN, VCSLog.VCS_PKG_ERROR_SAVING_USERS_FILE, io.getMessage());
            throw io;
		}
    }

    protected Object[] getEntry(String devName)
    {
        Object[] entry = (Object[])users.get(devName);
        return entry;
    }

    protected String getEntryValue(String devName, int index)
    {
        Object[] entry = getEntry(devName);
        if (entry == null || index >= entry.length || entry[index] == null) {
            return null;
        }
        else {
            return entry[index].toString();
        }
    }

    protected Users()
    {
        readFile();
    }

}
