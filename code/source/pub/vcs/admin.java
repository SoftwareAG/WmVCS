package pub.vcs;

// -----( B2B Java Code Template v1.2
// -----( CREATED: Thu Sep 03 12:00:00 GMT 1752
// -----( ON-HOST: hemlock.east.webmethods.com

// --- <<B2B-START-IMPORTS>> ---
import com.webmethods.vcs.*;
import com.webmethods.vcs.util.*;
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.UserManager;
import com.wm.data.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
// --- <<B2B-END-IMPORTS>> ---

public final class admin
{
	// ---( internal utility methods )---

	final static admin _instance = new admin();

	static admin _newInstance()
	{
		return new admin();
	}

	static admin _cast(Object o)
	{
		return (admin)o;
	}

	// ---( server methods )---


	public static final void setCurrentUser(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(setCurrentUser)>> ---
		// @sigtype java 3.5
        // [i] field:0:required vcsName
        // [i] field:0:optional vcsPassword hints[{field_usereditable,true},{field_largerEditor,false},{field_password,true}]

        IDataCursor cur         = pipeline.getCursor();
        InvokeState state       = InvokeState.getCurrentState();
        String      devName     = state.getUser().getName();
        String      vcsName     = IDataUtil.getString(cur, "vcsName");
        String      vcsPassword = IDataUtil.getString(cur, "vcsPassword");

        checkVCSParameters(devName, vcsName, vcsPassword);

        Users.getInstance().setUser(devName, vcsName, vcsPassword);

        try {
            Users.getInstance().writeFile();
        }
        catch (IOException ioe) {
            throw new ServiceException(ioe);
        }

        IDataUtil.put(cur, "vcsPassword", "********");

        cur.destroy();

		// --- <<B2B-END>> ---
    }


	public static final void removeCurrentUser(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(removeCurrentUser)>> ---
		// @sigtype java 3.5

        IDataCursor cur     = pipeline.getCursor();
        InvokeState state   = InvokeState.getCurrentState();
        String      devName = state.getUser().getName();

        Users users = Users.getInstance();
        checkDeveloperUser(devName);
        if (users.hasUser(devName)) {
            users.removeUser(devName);
            try {
                users.writeFile();
            }
            catch (IOException ioe) {
                throw new ServiceException(ioe);
            }
        }
        else {
            throw new ServiceException(new VCSException(VCSLog.EXC_NO_SUCH_USER, new Object[] { devName }));
        }

        cur.destroy();

		// --- <<B2B-END>> ---
    }


	public static final void getUsers(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(getUsers)>> ---
		// @sigtype java 3.5
        // [o] record:1:required users
        // [o] - field:0:required devName
        // [o] - field:0:required vcsName

        IDataCursor pc = pipeline.getCursor();

        String[][] users    = Users.getInstance().getUsers();
        IData[]    userData = new IData[users.length];

        for (int ui = 0; ui < users.length; ++ui) {
            String[] user = users[ui];
            userData[ui] = IDataFactory.create();
            IDataCursor udc = userData[ui].getCursor();
            IDataUtil.put(udc, "devName", user[0]);
            IDataUtil.put(udc, "vcsName", user[1]);

            udc.destroy();
        }

        IDataUtil.put(pc, "users", userData);

        pc.destroy();

		// --- <<B2B-END>> ---
    }


	public static final void setMultipleUsers(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(setMultipleUsers)>> ---
		// @sigtype java 3.5
        // [i] record:1:required users
        // [i] - field:0:required devName
        // [i] - field:0:required vcsName
        // [i] - field:0:optional vcsPassword hints[{field_usereditable,true},{field_largerEditor,false},{field_password,true}]

        IDataCursor cur   = pipeline.getCursor();
        IData[]     users = IDataUtil.getIDataArray(cur, "users");

        if (users == null || users.length == 0) {
            throw new ServiceException(new VCSException(VCSLog.EXC_NO_USERS_SPECIFIED));
        }
        else {
            for (int ui = 0; ui < users.length; ++ui) {
                IData       user        = users[ui];
                IDataCursor uc          = user.getCursor();
                String      devName     = IDataUtil.getString(uc, "devName");
                String      vcsName     = IDataUtil.getString(uc, "vcsName");
                String      vcsPassword = IDataUtil.getString(uc, "vcsPassword");

                checkVCSParameters(devName, vcsName, vcsPassword);
            }

            for (int ui = 0; ui < users.length; ++ui) {
                IData       user        = users[ui];
                IDataCursor uc          = user.getCursor();
                String      devName     = IDataUtil.getString(uc, "devName");
                String      vcsName     = IDataUtil.getString(uc, "vcsName");
                String      vcsPassword = IDataUtil.getString(uc, "vcsPassword");

                Users.getInstance().setUser(devName, vcsName, vcsPassword);

                IDataUtil.put(uc, "vcsPassword", "********");
            }

            try {
                Users.getInstance().writeFile();
            }
            catch (IOException ioe) {
                throw new ServiceException(ioe);
            }
            finally {
                cur.destroy();
            }
        }

		// --- <<B2B-END>> ---
    }


	public static final void removeMultipleUsers(IData pipeline) throws ServiceException
	{
		// --- <<B2B-START(removeMultipleUsers)>> ---
		// @sigtype java 3.5
        // [i] field:1:required devNames

        IDataCursor cur      = pipeline.getCursor();
        String[]    devNames = IDataUtil.getStringArray(cur, "devNames");

        if (devNames == null || devNames.length == 0) {
            throw new ServiceException(new VCSException(VCSLog.EXC_NO_USERS_SPECIFIED));
        }
        else {
            Users users = Users.getInstance();

            // do validation check before modification of Users.
            for (int di = 0; di < devNames.length; ++di) {
                String devName = devNames[di];
                checkDeveloperUser(devName);
                if (!users.hasUser(devName)) {
                    throw new ServiceException(new VCSException(VCSLog.EXC_NO_SUCH_USER, new Object[] { devName }));
                }
            }

            for (int di = 0; di < devNames.length; ++di) {
                String devName = devNames[di];
                users.removeUser(devName);
            }

            try {
                users.writeFile();
            }
            catch (IOException ioe) {
                throw new ServiceException(ioe);
            }
            finally {
                cur.destroy();
            }
        }

		// --- <<B2B-END>> ---
    }


	// --- <<B2B-START-SHARED>> ---

    public static void checkVCSParameters(String devName, String vcsName, String vcsPassword) throws ServiceException
    {
        checkDeveloperUser(devName);
        if (vcsName == null || vcsName.length() == 0) {
            throw new ServiceException(new VCSException(VCSLog.EXC_INVALID_VCS_USER_NAME, new Object[] { vcsName }));
        }
    }

    public static void checkDeveloperUser(String devName) throws ServiceException
    {
        if (devName == null || devName.length() == 0 || UserManager.getUser(devName) == null) {
            throw new ServiceException(new VCSException(VCSLog.EXC_INVALID_DEVELOPER_NAME, new Object[] { devName }));
        }
    }

	// --- <<B2B-END-SHARED>> ---

}

