/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.main;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author johny
 */
class LatestVersionServer implements Runnable {

    private final SSLServerSocket serverSocket;
    private SSLSocket clientSocket;
    private ThreadPoolExecutor executorPool;
    private LatestVersionHandler clientHandler;

    private final static Logger LOGGER = Logger.getLogger(LatestVersionServer.class
            .getName());
    JChatRejectedExecutionHandler rejectionHandler;
    ThreadFactory threadFactory;
    private final static int corePoolSize = 2;
    private final static int maxPoolSize = 100;
    private final static long keepAliveTime = 60;
    private final static TimeUnit unit = TimeUnit.SECONDS;

    LatestVersionServer(SSLServerSocket sslserversocket) {
        this.serverSocket = sslserversocket;

        //RejectedExecutionHandler implementation
        rejectionHandler = new JChatRejectedExecutionHandler();
        //Get the ThreadFactory implementation to use
        threadFactory = Executors.defaultThreadFactory();
        //creating the ThreadPoolExecutor
        executorPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                unit, new ArrayBlockingQueue<Runnable>(50), threadFactory, rejectionHandler);

        LOGGER.setLevel(Level.ALL);
        //LOGGER.log(Level.INFO, "FileDownloadServer created");
        R.log("LatestVersionServer created");
    }

    @Override
    public void run() {

        int clientCounter = 0;

        for (;;) {
            try {
                clientSocket = (SSLSocket) serverSocket.accept();
                clientHandler = new LatestVersionHandler(clientSocket);
                executorPool.execute(clientHandler);
                clientCounter++;
                LOGGER.log(Level.INFO, "New Client for latest version download, count: {0}", clientCounter);
            } catch (IOException ex) {
                //shut down the pool
                executorPool.shutdown();
                try {
                    executorPool.awaitTermination(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    LOGGER.log(Level.SEVERE, e.toString());
                }
            }
        }

    }

}
