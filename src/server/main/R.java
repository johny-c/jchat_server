package server.main;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jasypt.util.password.StrongPasswordEncryptor;
import common.db.entity.UserAccount;
import common.db.entity.UserSession;
import common.utils.Conventions;
import common.utils.JChatLogger;
import common.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import server.db.util.Database;

public class R implements Conventions {

    private static Database db;
    private static Random random;
    private static ArrayBlockingQueue logs;
    private static final Logger LOGGER = Logger.getLogger("JChatServerLogger");

    public R() {
        startLogger();

        random = new Random();

        Server server = new Server();
    }

    public static Database getDb() {
        return db;
    }

    public static void setDb(Database db) {
        R.db = db;
    }

    public static synchronized void log(String s) {
        LOGGER.log(Level.INFO, "{0}  {1}\n", new Object[]{Thread.currentThread().getName(), s});
    }

    public static synchronized Random getRandom() {
        return random;
    }

    public static void main(String[] args) {

        new R();
        db = new Database();

        //testSignup();
    }

    private void startLogger() {
        try {
            LOGGER.setLevel(Level.ALL);
            FileHandler fh = new FileHandler(Utils.getAppDir() + File.separator + "log.txt");
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            R.log("Logging start\n\n");
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(JChatLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void testSignup() {
        UserAccount userIn;
        UserSession usNew;
        StrongPasswordEncryptor spe = new StrongPasswordEncryptor();

        userIn = new UserAccount();
        userIn.setUsername("Mitsos");
        userIn.setPassword("password123");
        // Check username availability
        userIn.setRegDate(new Date());
        userIn.setStatus(UserAccount.Status.OFFLINE);
        String encryptedPassword = spe.encryptPassword(userIn.getPassword());
        R.log("User's password: " + userIn.getPassword());
        Utils.printInfo(userIn);
        userIn.setPassword(encryptedPassword);
        R.log("User's encrypted password: " + userIn.getPassword());
        Integer userId = db.insert(userIn);
        userIn.setId(userId);
        Utils.printInfo(userIn);

        usNew = new UserSession();
        usNew.setUserId(userId);
        usNew.setToken(random.nextLong());
        usNew.setValid(Boolean.FALSE);
        Integer usId = db.insert(usNew);
        usNew.setId(usId);
        Utils.printSession(usNew);
    }

}
