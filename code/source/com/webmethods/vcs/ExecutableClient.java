package com.webmethods.vcs;

import com.webmethods.vcs.*;
import com.wm.app.b2b.server.*;
import java.io.*;
import java.util.*;


/**
 * VCS support for a generic executable.
 */
public class ExecutableClient /* extends AbstractClient */
{
//     public final static String VCS_EXEC_CHECKIN = "watt.vcs.exec.checkin"; // =/home/jpace/bin/perforceps --checkin --description={0}
//     public final static String VCS_EXEC_CHECKOUT = "watt.vcs.exec.checkout"; // =/home/jpace/bin/perforceps --checkout
//     public final static String VCS_EXEC_DELETE = "watt.vcs.exec.delete"; // =/home/jpace/bin/perforceps --delete --description={0}
//     public final static String VCS_EXEC_INFO = "watt.vcs.exec.info"; // =/home/jpace/bin/perforceps --info
//     public final static String VCS_EXEC_LOAD = "watt.vcs.exec.load"; // =/home/jpace/bin/perforceps --load
//     public final static String VCS_EXEC_LOAD_BY_DATE = "watt.vcs.exec.loadByDate"; // =/home/jpace/bin/perforceps --load-by-date --date={0}
//     public final static String VCS_EXEC_LOAD_BY_LABEL = "watt.vcs.exec.loadByLabel"; // =/home/jpace/bin/perforceps --load-by-label --label={0}
//     public final static String VCS_EXEC_LOAD_BY_REVISION = "watt.vcs.exec.loadByRevision"; // =/home/jpace/bin/perforceps --load-by-revision --revision={0}
//     public final static String VCS_EXEC_LOG = "watt.vcs.exec.log"; // =/home/jpace/bin/perforceps --log
//     public final static String VCS_EXEC_REVERT = "watt.vcs.exec.revert"; // =/home/jpace/bin/perforceps --revert
//     public final static String VCS_EXEC_VALID = "watt.vcs.exec.valid"; // =/home/jpace/bin/perforceps --valid
//     public final static String VCS_EXEC_IS_CHECKED_OUT = "watt.vcs.exec.checkedout"; // =/home/jpace/bin/perforceps --checkedout

//     /**
//      * Returns info.
//      */
//     public List getInfo() throws VCSException
//     {
//         return runExecutable(VCS_EXEC_INFO, null, null);
//     }

//     /**
//      * Returns whether the given file is checked out.
//      */
//     public boolean isCheckedOut(String fileName) throws VCSException
//     {
//         if (isValidEntry(fileName)) {
//             Executable ex = makeExecutable(VCS_EXEC_IS_CHECKED_OUT, null, toList(fileName));
//             return ex.execute() == 0;
//         }
//         else {
//             return false;
//         }
//     }

//     /**
//      * Checks out the file for editing.
//      */
//     public void checkout(List fileNames) throws VCSException
//     {
//         fileNames = toUniqueList(fileNames);
//         List entries = getValidEntries(fileNames);

//         runExecutable(VCS_EXEC_CHECKOUT, null, fileNames);
//     }

//     /**
//      * Returns the log for the file.
//      */
//     public List getLog(String fileName) throws VCSException
//     {
//         if (isValidEntry(fileName)) {
//             return runExecutable(VCS_EXEC_LOG, null, toList(fileName));
//         }
//         else {
//             return null;
//         }
//     }

//     /**
//      * Checks the given files in, with a description.
//      */
//     public void checkin(List fileNames, String description) throws VCSException
//     {
//         submit(fileNames, description);
//     }

//     /**
//      * Returns whether the given entry name is valid to submit to the VCS. 
//      * Directories and generated files (such as .class) are not valid. The file
//      * does <em>not</em> need to exist to be considered valid.
//      */
//     protected boolean isValidEntry(String name)
//     {
//         String validator = Config.getInstance().getProperty(VCS_EXEC_VALID);
//         if (validator == null) {
//             // this is OK. We'll use the default:
//             return super.isValidEntry(name);
//         }
//         else {
//             List cmd = new ArrayList();
//             StringTokenizer st = new StringTokenizer(validator);
//             while (st.hasMoreTokens()) {
//                 cmd.add(st.nextToken());
//             }

//             cmd.add(name);

//             try {
//                 Executable ex = new Executable(cmd);
//                 return ex.execute() == 0;
//             }
//             catch (VCSException vcse) {
//                 // default to more conservative -- don't chance losing files
//                 return true;
//             }
//         }
//     }

//     /**
//      *
//      */
//     protected static List toList(String arg)
//     {
//         return Arrays.asList(new String[] { arg });
//     }

//     /**
//      * Submits a set of files.
//      */
//     protected void submit(List fileNames, String description) throws VCSException
//     {
//         fileNames = toUniqueList(fileNames);
//         runExecutable(VCS_EXEC_CHECKIN, description, fileNames);
//     }

//     /**
//      * Loads the directories by date.
//      */
//     public void load(List dirNames, Date date) throws VCSException
//     {
//         Calendar cal = Calendar.getInstance();
//         cal.setTime(date);

//         String dateStr = "" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH);

//         runExecutable(VCS_EXEC_LOAD_BY_DATE, dateStr, dirNames);
//     }

//     /**
//      * Loads the directories by revision (an integer).
//      */
//     public void load(List dirNames, String revision, boolean isInterface) throws VCSException
//     {
//         runExecutable(VCS_EXEC_LOAD_BY_REVISION, revision, dirNames);
//     }

//     /**
//      * Loads the directories by label (a string).
//      */
//     public void loadByLabel(List dirNames, String label) throws VCSException
//     {
//         runExecutable(VCS_EXEC_LOAD_BY_LABEL, label, dirNames);
//     }

//     /**
//      * Loads the current version of the given directories.
//      */
//     public void load(List dirNames) throws VCSException
//     {
//         runExecutable(VCS_EXEC_LOAD, null, dirNames);
//     }

//     /**
//      * Deletes the given files and submits the change.
//      */
//     public void delete(boolean isPredelete, List fileNames, String comment) throws VCSException
//     {
//         fileNames = toUniqueList(fileNames);

//         Iterator it = fileNames.iterator();
//         while (it.hasNext()) {
//             String fileName = (String)it.next();
//             delete(fileName, comment, false);
//         }

//         submit(fileNames, comment);
//     }

//     /**
//      * Deletes the given file and submits the change.
//      */
//     public void delete(String fileName, String comment) throws VCSException
//     {
//         delete(fileName, comment, true);
//     }

//     /**
//      * Returns whether this client must delete existing files before loading an
//      * entire directory.
//      */
//     public boolean mustDeleteBeforeLoad() throws VCSException
//     {
//         return false;
//     }

//     /**
//      * Deletes the given file.
//      */
//     protected void delete(String fileName, String comment, boolean doSubmit) throws VCSException
//     {
//         runExecutable(VCS_EXEC_DELETE, null, toList(fileName));

//         if (doSubmit) {
//             submit(toList(fileName), comment);
//         }
//     }

//     /**
//      * Reverts the given file.
//      */
//     public void revert(List fileNames) throws VCSException
//     {
//         fileNames = toUniqueList(fileNames);
//         runExecutable(VCS_EXEC_REVERT, null, fileNames);
//     }

//     /**
//      * Moves the given file.
//      */
//     public void move(String oldName, String newName) throws VCSException
//     {
//     }

//     /**
//      * Copies the given file.
//      */
//     public void copy(String source, String dest) throws VCSException
//     {
//     }

//     /**
//      * Runs the executable specified by the given property name. Throws a
//      * <code>VCSException</code> if no such property exists.
//      */
//     protected List runExecutable(String propName, String arg, List files) throws VCSException
//     {
//         return makeExecutable(propName, arg, files).exec();
//  	}

//     /**
//      * Creates, but does not run, the executable specified by the given property
//      * name. Throws a <code>VCSException</code> if no such property exists.
//      */
//     protected Executable makeExecutable(String propName, String arg, List files) throws VCSException
//     {
//         String propValue = Config.getInstance().getProperty(propName);
//         if (propValue == null) {
//             throw new VCSException(VCSLog.EXC_PROPERTY_NOT_SET, new Object[] { propName });
//         }
//         else {
//             VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_EXEC_COMMAND, propName, propValue);
            
//             List cmd = new ArrayList();
//             StringTokenizer st = new StringTokenizer(propValue);
//             while (st.hasMoreTokens()) {
//                 cmd.add(st.nextToken());
//             }
            
//             InvokeState state  = InvokeState.getCurrentState();
//             String      user   = state.getUser().getName();
//             String      server = ServerAPI.getServerName();

//             cmd.add("--user");
//             cmd.add(user);
            
//             cmd.add("--server");
//             cmd.add(server);
            
//             if (arg != null) {
//                 cmd.add(arg);
//             }

//             if (files != null && files.size() > 0) {
//                 cmd.addAll(files);
//             }

//             Executable ex = new Executable(cmd);
//             return ex;
//         }
//  	}

}
