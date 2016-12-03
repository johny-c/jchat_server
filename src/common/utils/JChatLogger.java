package common.utils;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JChatLogger implements Runnable {

    private BlockingQueue<String> logs;
    private final static Logger LOGGER = Logger.getLogger(JChatLogger.class
            .getName());

    private final String LOG_PATH = System.getProperty("user.dir") + "/JCHAT_SERVER_STORAGE/JChatServerLog.txt";


    public JChatLogger(ArrayBlockingQueue logs) {
        try {
            this.logs = logs;
            LOGGER.setLevel(Level.ALL);
            FileHandler fh;

            fh = new FileHandler(LOG_PATH);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(JChatLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void run() {
        String s;
        while (true) {
            try {
                s = logs.take();
                LOGGER.log(Level.INFO, s);
            } catch (InterruptedException ex) {
                Logger.getLogger(JChatLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

