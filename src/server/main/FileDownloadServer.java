package server.main;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import common.utils.Conventions;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class FileDownloadServer implements Runnable, Observer, Conventions {

    private final SSLServerSocket serverSocket;
    private final ThreadPoolExecutor executorPool;
    private FileDownloadHandler fileDownloadHandler;
    private SSLSocket clientSocket;
    //ServerGUI gui;
    private final static Logger LOGGER = Logger.getLogger(FileDownloadServer.class
            .getName());
    JChatRejectedExecutionHandler rejectionHandler;
    ThreadFactory threadFactory;
    private final static int corePoolSize = 2;
    private final static int maxPoolSize = 100;
    private final static long keepAliveTime = 60;
    private final static TimeUnit unit = TimeUnit.SECONDS;

    FileDownloadServer(SSLServerSocket sslserversocket) {
        this.serverSocket = sslserversocket;
        //RejectedExecutionHandler implementation
        rejectionHandler = new JChatRejectedExecutionHandler();
        //Get the ThreadFactory implementation to use
        threadFactory = Executors.defaultThreadFactory();
        //creating the ThreadPoolExecutor
        executorPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                unit, new ArrayBlockingQueue<Runnable>(50), threadFactory, rejectionHandler);

        //LOGGER.setLevel(Level.ALL);
        //LOGGER.log(Level.INFO, "FileDownloadServer created");
        R.log("FileDownloadServer created");
    }

    @Override
    public void run() {
        R.log("File Download Server started\n\n");

        int clientCounter = 0;
        try {
            for (;;) {
                clientSocket = (SSLSocket) serverSocket.accept();
                fileDownloadHandler = new FileDownloadHandler(clientSocket);
                executorPool.execute(fileDownloadHandler);
                clientCounter++;
                R.log("New file download " + clientCounter);
            }
        } catch (IOException ex) {
            //shut down the pool
            executorPool.shutdown();
            try {
                executorPool.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.toString());
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
