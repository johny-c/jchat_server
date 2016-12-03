package server.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import common.utils.Conventions;

public class Server implements Conventions {

    private final String keystoreName = "jchatKeyStore.jks";
    private final char keystorePassword[] = "123456".toCharArray();
    private final char keyPassword[] = "123456".toCharArray();
    private KeyStore keystore;
    private KeyManagerFactory kmf;
    private SSLContext sslContext;
    private SSLServerSocketFactory sslServerSocketFactory;
    private static SSLServerSocket sslServerSocket;
    private static SSLServerSocket fileUploadServerSocket;
    private static SSLServerSocket fileDownloadServerSocket;
    private static SSLServerSocket latestVersionServerSocket;
    //private ExecutorService pool;
    private final static int poolSize = 50;
    private final static int corePoolSize = 2;
    private final static int maxPoolSize = 100;
    private final static long keepAliveTime = 60;
    private final static TimeUnit unit = TimeUnit.SECONDS;
    ThreadFactory threadFactory;
    static ThreadPoolExecutor executorPool;
    JChatRejectedExecutionHandler rejectionHandler;
    //private ConcurrentHashMap<Long, CommandHandler> threadUserMap;
    //private InterMessageQueue interMessages;
    //static ServerGUI gui;
    private final static Logger LOGGER = Logger.getLogger(Server.class
            .getName());

    public Server() {

        setupSSL();

        //RejectedExecutionHandler implementation
        //rejectionHandler = new JChatRejectedExecutionHandler();
        //Get the ThreadFactory implementation to use
        //threadFactory = Executors.defaultThreadFactory();
        //creating the ThreadPoolExecutor
        //executorPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
        //       unit, new ArrayBlockingQueue<Runnable>(3), threadFactory, rejectionHandler);
        startServerThreads();
    }

    private void setupSSL() {
        try {
            keystore = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.toString());
        }
        try {
            keystore.load(new FileInputStream(keystoreName), keystorePassword);
        } catch (NoSuchAlgorithmException | CertificateException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.toString());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.toString());
        }
        try {
            kmf = KeyManagerFactory.getInstance("SunX509");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.toString());
        }
        try {
            kmf.init(keystore, keyPassword);
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.toString());
        }
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.toString());
        }
        try {
            sslContext.init(kmf.getKeyManagers(), null, null);
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.toString());
        }
        sslServerSocketFactory = sslContext.getServerSocketFactory();
        try {
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(SERVER_PORT);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Utils.printServerSocketInfo(sslserversocket);
        try {
            fileUploadServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(SERVER_FILE_UPLOAD_PORT);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Utils.printServerSocketInfo(fileuploadserversocket);

        try {
            fileDownloadServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(SERVER_FILE_DOWNLOAD_PORT);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            latestVersionServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(SERVER_LATEST_VERSION_PORT);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Utils.printServerSocketInfo(filedownloadserversocket);

        //System.out.println("Server started:");
    }

    private void startServerThreads() {
        //submit work to the thread pool
        CommandServer cmdServer = new CommandServer(sslServerSocket);
        Thread cmdServerThread = new Thread(cmdServer);
        cmdServerThread.start();

        FileUploadServer fileUploadServer = new FileUploadServer(fileUploadServerSocket);
        Thread fileUploadServerThread = new Thread(fileUploadServer);
        fileUploadServerThread.start();

        FileDownloadServer fileDownloadServer = new FileDownloadServer(fileDownloadServerSocket);
        Thread fileDownloadServerThread = new Thread(fileDownloadServer);
        fileDownloadServerThread.start();

        LatestVersionServer latestVersionServer = new LatestVersionServer(latestVersionServerSocket);
        Thread latestVersionServerThread = new Thread(latestVersionServer);
        latestVersionServerThread.start();
    }
}
