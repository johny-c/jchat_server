package common.utils;

import java.awt.Color;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public interface Conventions {

    public static final String JCHAT_VERSION = "0.0.1";
    // -- Network --
    // Server details
    //static final String SERVER_NAME = "localhost";
    static final int SERVER_PORT = 9999;
    static final int SERVER_FILE_UPLOAD_PORT = 9998;
    static final int SERVER_FILE_DOWNLOAD_PORT = 9997;
    static final int SERVER_VIDEO_UPLOAD_PORT = 9996;
    static final int SERVER_VIDEO_DOWNLOAD_PORT = 9995;
    static final int SERVER_LATEST_VERSION_PORT = 9990;

    // Messages meta
    public static final int CLIENT_MSG_QUEUE_OUT_CAPACITY = 10;
    public static final int CLIENT_MSG_QUEUE_IN_CAPACITY = 10;
    // Password meta
    public static int HASH_LENGTH_IN_BYTES = 64;
    public static int SALT_LENGTH_IN_BYTES = 64;
    public static final int MIN_PASSWORD_LEN = 4;
    public static final int PASSWORD_ECHO_LENGTH = 16;
    public static final String PASSWORD_ECHO_CHAR = "*";
    // Username meta
    static final int MIN_USERNAME_LEN = 4;
    static final int MAX_USERNAME_LEN = 50;
    // Chat messages meta
    static final int MAX_CHAT_MSG_LEN = 1000;
    // Files meta
    public static final int FILE_BUFFER_SIZE = 1024;
    public static final int MAX_FILE_TRANSFER_SIZE = 100 * 1024 * 1024; // in B
    // -- Message Codes --

    // Network Connection Messages
    public static final int UNKNOWN_HOST_EXCEPTION = -2000;
    public static final int IO_EXCEPTION = -2001;
    public static final String BROADCAST = "Broadcast";

    // UI
    public static final Icon JCHAT_LOGO = new ImageIcon(Utils.getIcon("jchat_logo/jchat_logo_20.png"));
    public static final String LOGIN_TAB_TITLE = "JChat - Login";
    public static final String LOGIN_TAB_TIP = "JChat Login tab";
    public static final String SIGNUP_TAB_TITLE = "JChat - Signup";
    public static final String SIGNUP_TAB_TIP = "JChat Signup tab";
    public static final String SIGNUP_SUCCESS_TAB_TITLE = "JChat - Signup Success";
    public static final String SIGNUP_SUCCESS_TAB_TIP = "JChat Signup Success tab";
    public static final String WELCOME_TAB_TIP = "JChat Welcome tab";
    public static final String CONTACTS_TAB_TITLE = "JChat - Contacts tab";
    public static final String CONTACTS_TAB_TIP = "JChat Contacts tab";
    public static final String ADD_CONTACT_TAB_TITLE = "JChat - Add contact";
    public static final String ADD_CONTACT_TAB_TIP = "Adding a contact";
    public static final String LOGOUT_TAB_TITLE = "JChat - Log out";
    public static final String LOGOUT_TAB_TIP = "Logging out";
    public static final String ACTIVE_TAB_TITLE = "JChat - Active";
    public static final String ACTIVE_TAB_TIP = "Active chatting";
    public static final String MAIN_TAB_TITLE = "JChat Main";
    public static final String MAIN_TAB_TIP = "JChat - Main Tab";
    public static final Color COLOR_CRIMSON_RED = new Color(220, 20, 60);
    public static final Color COLOR_LIGHT_BLACK = new Color(60, 59, 55);
    public static final Color COLOR_PAPAYA = new Color(255, 228, 196);
    public static final Color COLOR_PASTEL_GREEN = new Color(65, 179, 129);
    public static final Color COLOR_PASTEL_BLUE = new Color(78, 110, 187);
    public static final Font FONT_UBUNTU_PLAIN_14 = new Font("Ubuntu", Font.PLAIN, 14);
    public static final Font FONT_UBUNTU_BOLD_15 = new Font("Ubuntu", Font.BOLD, 15);
    public static final Font FONT_UBUNTU_BOLD_16 = new Font("Ubuntu", Font.BOLD, 16);
    public static final Font FONT_FUNKY = new Font("Purisa", Font.PLAIN, 20);
    public static final String WELCOME_LABEL = "Welcome to JChat !";

    // Settings key-names
    public static final String SERVER_IP = "serverIp";  // "138.246.2.70";
    public static final String DEFAULT_SERVER_IP = "192.168.2.16";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TEXT_COLOR = "textColor";
    public static final String DOWNLOAD_FOLDER = "downloadFolder";
    public static final String DEFAULT_DOWNLOAD_FOLDER = System.getProperty("user.dir");
    public static final String ASK_DOWNLOAD_FOLDER = "askDownloadFolder";
    public static final boolean DEFAULT_ASK_DOWNLOAD_FOLDER = true;
    public static final String REMEMBER_CREDENTIALS = "rememberCredentials";
    public static final boolean DEFAULT_REMEMBER_CREDENTIALS = false;
}
