package common.utils;

import common.db.entity.FileTransfer;
import common.db.entity.UserAccount;
import common.db.entity.UserSession;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.swing.ImageIcon;
import server.main.R;

public class Utils implements Conventions {

    private static int SCREEN_WIDTH = -1;
    private static int SCREEN_HEIGHT = -1;
    private final static Logger LOGGER = Logger.getLogger(Utils.class
            .getName());

    public static boolean isNewerVersion(String newestVersion, String JCHAT_VERSION) {
        // regex \\. for dot, simple . matches any character
        String newestFields[] = newestVersion.split("\\.");
        String currentFields[] = JCHAT_VERSION.split("\\.");

        int newestMajorRelease = Integer.parseInt(newestFields[0]);
        int currentMajorRelease = Integer.parseInt(currentFields[0]);

        if (newestMajorRelease > currentMajorRelease) {
            return true;
        }

        int newestMinorRelease = Integer.parseInt(newestFields[1]);
        int currentMinorRelease = Integer.parseInt(currentFields[1]);

        if (newestMinorRelease > currentMinorRelease) {
            return true;
        }

        int newestMinorUpdate = Integer.parseInt(newestFields[2]);
        int currentMinorUpdate = Integer.parseInt(currentFields[2]);

        if (newestMinorUpdate > currentMinorUpdate) {
            return true;
        }

        return false;
    }

    public synchronized static void print(String caller, String msg) {
        Date d = new Date();
        LOGGER.log(Level.INFO, d + " " + caller + " " + msg);

    }

    public static boolean isValidPassword(String input) {
        char[] password = input.toCharArray();

        if (password.length < MIN_PASSWORD_LEN) {
            return false;
        }

        boolean hasNumber = false, hasLetter = false;
        for (int i = 0; i < password.length; i++) {
            if (Character.isLetter(password[i])) {
                hasLetter = true;
            } else if (Character.isDigit(password[i])) {
                hasNumber = true;
            }

            if (hasLetter && hasNumber) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidFileTransfer(FileTransfer ftl) {
        return true;
    }

    public static void printSession(UserSession u) {
        String s = "\n\n -- User Session Entry --\n";
        s += "\nid: " + u.getId();
        s += "\nuserId: " + u.getUserId();
        s += "\nstart: " + u.getStart();
        s += "\nend: " + u.getEnd();
        s += "\nlast session id: " + u.getLastSessionId();
        s += "\ntoken: " + u.getToken();
        s += "\n\n";
        LOGGER.log(Level.INFO, s);
    }

    public static boolean isValidEmailAddress(String string) {
        int len = string.trim().length();
        // \b = word boundary
        // [A-Z0-9._%+-] = any of these characters 
        // + = one or more times
        // @ = @ once
        // [A-Z0-9.-] = any of these characters 
        // + = one or more times
        // . = . once
        // [A-Z]{2,4} = 2-4 characters from the [A-Z] set
        String emailRegex = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
        Pattern p = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);

        if (len > 5 && len < 255) {
            if (p.matcher(string).matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidUsername(String string) {
        int len = string.trim().length();
        if (len < MIN_USERNAME_LEN) {
            return false;
        }

        int lenwsp = string.length();
        if (lenwsp > MAX_USERNAME_LEN) {
            return false;
        }

        return true;
    }

    public static boolean isValidIP(String ip) {
        R.log("Checking IP: " + ip);
        if (ip.equals("localhost")) {
            return true;
        }

        String[] chunks = ip.split("\\."); // regex \\. for dot, simple . matches any character
        if (chunks.length != 4 && chunks.length != 6) {
            R.log("Invalid IP Chunks length = " + chunks.length);
            return false;
        }

        for (String chunk : chunks) {
            int num = Integer.valueOf(chunk);
            if (num < 0 || num > 255) {
                R.log("Invalid IP Chunk < 0 || > 255 = " + chunk);
                return false;
            }
        }

        R.log("Valid IP input");
        return true;
    }

    public static void printSocketInfo(SSLSocket so) {
        String s = "Socket class: " + so.getClass();
        s += "\n   Remote address = "
                + so.getInetAddress().toString();
        s += "\n   Remote port = " + so.getPort();
        s += "\n   Local socket address = "
                + so.getLocalSocketAddress().toString();
        s += "\n   Local address = "
                + so.getLocalAddress().toString();
        s += "\n   Local port = " + so.getLocalPort();
        s += "\n   Need client authentication = "
                + so.getNeedClientAuth();
        SSLSession ss = so.getSession();
        s += "\n   Cipher suite = " + ss.getCipherSuite();
        s += "\n   Protocol = " + ss.getProtocol();
        LOGGER.log(Level.INFO, s);
    }

    /**
     * Print Info about the Server Socket
     */
    public static void printServerSocketInfo(SSLServerSocket so) {
        String s = "Server socket class: " + so.getClass();
        s += "\n   Socker address = "
                + so.getInetAddress().toString();
        s += "\n   Socker port = "
                + so.getLocalPort();
        s += "\n   Need client authentication = "
                + so.getNeedClientAuth();
        s += "\n   Want client authentication = "
                + so.getWantClientAuth();
        s += "\n   Use client mode = "
                + so.getUseClientMode();
        s += "\n\n\n";

        LOGGER.log(Level.INFO, s);
    }

    public static boolean isValidChatMessage(String chatMessage) {
        // TODO Auto-generated method stub
        // Exclude forbidden characters (delimiters)
        // Check size < MAX_CHAT_MSG_LEN and > 0
        if (chatMessage.length() <= 0 || chatMessage.length() > MAX_CHAT_MSG_LEN) {
            return false;
        }

        return true;
    }

    public static URL getIcon(String relativePath) {
        return Utils.class.getResource("/resources/icons/" + relativePath);
    }

    public static ImageIcon getIIcon(String relativePath) {
        return new ImageIcon(Utils.class.getResource("/resources/icons/" + relativePath));
    }

    public static void getScreenSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        SCREEN_WIDTH = (int) screenSize.getWidth();
        SCREEN_HEIGHT = (int) screenSize.getHeight();
    }

    public static int getScreenWidth() {
        if (SCREEN_WIDTH == -1) {
            getScreenSize();
        }
        return SCREEN_WIDTH;
    }

    public static int getScreenHeight() {
        if (SCREEN_WIDTH == -1) {
            getScreenSize();
        }
        return SCREEN_HEIGHT;
    }

    public static void printInfo(UserAccount u) {
        String s = "\n\n -- User Entry --\n";

        s += "\n\n -- User Entry --\n";
        s += "\nid: " + u.getId();
        s += "\nusername: " + u.getUsername();
        s += "\npassword: " + u.getPassword();
        s += "\nemail: " + u.getEmail();
        s += "\nreg date: " + u.getRegDate();
        s += "\nstatus: " + u.getStatus();
        s += "\n\n";
        LOGGER.log(Level.INFO, s);
    }

    public static int birthdateToAge(Date birthDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(birthDate);
        int age = Calendar.getInstance().get(Calendar.YEAR) - c.get(Calendar.YEAR);
        return age;
    }

    public static File getCurrentJar() {
        String currentDir = null;
        try {
            currentDir = Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException ex) {
            R.log("The path to the jar file is corrupt.");
        }

        File f = new File(currentDir);
        return f;
    }

    public static String getAppDir() {
        return getCurrentJar().getParent();
    }

    public static String getUserFilesDir() {
        return getAppDir() + "/JCHAT_SERVER_STORAGE/USER_FILES/";
    }

    public static String getLatestVersionDir() {
        return getAppDir() + "/CLIENT_RELEASES/LATEST/";
    }

    public static void printMessageProtocol() {
        for (MessageType mt : MessageType.values()) {
            R.log(mt.ordinal() + "  " + mt.toString() + "\n");
        }
    }

    public static void printThreads() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Thread[] list = new Thread[20];
        int n = group.enumerate(list);
        if (list == null) {
            R.log("List is null");
            return;
        } else {
            R.log("List contains " + n + " threads");
        }
        int counter = 1;
        String s = "- - ACTIVE THREADS - -\n";
        for (Thread t : list) {
            if (t != null) {
                s += "\n" + (counter++) + "  " + t.getName();

                if (t.isAlive()) {
                    s += " Alive";
                }
            }
        }
        R.log(s + "\n\n");
    }

}
