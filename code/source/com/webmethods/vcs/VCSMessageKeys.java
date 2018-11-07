package com.webmethods.vcs;


/**
 * Keys used by the journal logging interface. Generated automatically from
 * VCSMessageBundle.java.
 * by: /home/jpace/bin/msgbundletokeys.rb
 * at: Fri Mar 17 12:52:55 EST 2006
 */
public interface VCSMessageKeys
{
    // general
    public static final int VCS_GENERAL_LINE = 0;
    public static final int VCS_GENERAL_ERROR = 1;
    public static final int VCS_GENERAL_MESSAGE = 2;

    // pkg
    public static final int VCS_PKG_INITIALIZING = 50;
    public static final int VCS_PKG_SHUTTING_DOWN = 51;
    public static final int VCS_PKG_LOADING_CONFIG_FILE = 52;
    public static final int VCS_PKG_NO_CONFIG_FILE = 53;
    public static final int VCS_PKG_ERROR_LOADING_CONFIG_FILE = 54;
    public static final int VCS_PKG_ENABLED = 55;
    public static final int VCS_PKG_NOT_ENABLED = 56;
    public static final int VCS_PKG_LOADING_USERS_FILE = 57;
    public static final int VCS_PKG_NO_USERS_FILE = 58;
    public static final int VCS_PKG_ERROR_LOADING_USERS_FILE = 59;
    public static final int VCS_PKG_SAVING_USERS_FILE = 60;
    public static final int VCS_PKG_ERROR_SAVING_USERS_FILE = 61;
    public static final int VCS_PKG_GETTING_INFO = 62;
    public static final int VCS_PKG_NO_CONFIG_FILE_READ = 63;
    public static final int VCS_PKG_OVERWRITING_EXISTING_PROPERTY = 64;

    // file
    public static final int VCS_FILE_NOT_CHECKED_OUT = 100;
    public static final int VCS_FILE_CHECKED_OUT = 101;
    public static final int VCS_FILE_OUT_OF_DATE = 102;
    public static final int VCS_FILE_CHECKED_OUT_BY_OTHER_USER = 103;

    // client
    public static final int VCS_CLIENT_SETTING = 150;
    public static final int VCS_CLIENT_NONE = 151;
    public static final int VCS_CLIENT_INVALID = 152;
    public static final int VCS_CLIENT_TYPE_NOT_SET = 153;

    // load
    public static final int VCS_LOAD_STARTING = 200;
    public static final int VCS_LOAD_DONE = 201;
    public static final int VCS_LOAD_FILES_TO_DELETE = 202;

    // delete
    public static final int VCS_DELETE_STARTING = 250;
    public static final int VCS_DELETE_DONE = 251;
    public static final int VCS_DELETE_DEFAULT_COMMENT = 252;
    public static final int VCS_DELETE_DEFAULT_METACOMMENT = 253;
    public static final int VCS_DELETE_FAILED_REVERTING = 254;

    // checkout
    public static final int VCS_CHECKOUT_STARTING = 300;
    public static final int VCS_CHECKOUT_DONE = 301;
    public static final int VCS_CHECKOUT_IGNORING_BUILD_MODE = 302;
    public static final int VCS_CHECKOUT_IGNORING_DELETE_MODE = 303;
    public static final int VCS_CHECKOUT_FROM_VCS = 304;

    // checkin
    public static final int VCS_CHECKIN_STARTING = 350;
    public static final int VCS_CHECKIN_DONE = 351;
    public static final int VCS_CHECKIN_NO_NODE_DATA = 352;
    public static final int VCS_CHECKIN_DEFAULT_COMMENT = 353;
    public static final int VCS_CHECKIN_DEFAULT_METACOMMENT = 354;
    public static final int VCS_CHECKIN_TO_VCS = 355;

    // getlogs
    public static final int VCS_GETLOGS_STARTING = 400;
    public static final int VCS_GETLOGS_DONE = 401;

    // getfilestodelete
    public static final int VCS_GETFILESTODELETE_STARTING = 450;
    public static final int VCS_GETFILESTODELETE_DONE = 451;

    // predelete
    public static final int VCS_PREDELETE_STARTING = 500;
    public static final int VCS_PREDELETE_DONE = 501;
    public static final int VCS_PREDELETE_SKIPPING = 502;

    // getcommenttext
    public static final int VCS_GETCOMMENTTEXT_STARTING = 550;
    public static final int VCS_GETCOMMENTTEXT_DONE = 551;

    // ischeckedout
    public static final int VCS_ISCHECKEDOUT_STARTING = 600;
    public static final int VCS_ISCHECKEDOUT_DONE = 601;

    // submit
    public static final int VCS_SUBMIT = 650;
    public static final int VCS_SUBMIT_NO_ENTRIES = 651;

    // load
    public static final int VCS_LOAD_DIRECTORIES = 700;

    // exec
    public static final int VCS_EXEC_COMPLETED_NORMALLY = 750;
    public static final int VCS_EXEC_COMPLETED_TERMINATED = 751;

    // p4
    public static final int VCS_P4_CLIENT_NOT_FOUND = 800;
    public static final int VCS_P4_USER_NOT_FOUND = 801;

    // exec
    public static final int VCS_EXEC_COMMAND = 850;

    // file
    public static final int VCS_FILE_ADDING = 900;

    // parent
    public static final int VCS_PARENT_EXISTS = 950;
    public static final int VCS_PARENT_NOT_EXISTS = 951;

    // no
    public static final int VCS_NO_SUCH_NODE = 1000;
}
