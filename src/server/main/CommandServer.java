package server.main;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import common.utils.Conventions;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class CommandServer implements Runnable, Conventions {

    private ConcurrentHashMap<Integer, ArrayBlockingQueue> threadUserMap;
    private BlockingQueue<InterMessage> interMessages;
    private static final int INTER_MESSAGE_QUEUE_CAPACITY = 1024;
    private final SSLServerSocket serverSocket;
    private final ThreadPoolExecutor executorPool;
    private CommandHandler clientHandler;
    private SSLSocket clientSocket;
    private InterCommunicator interCommunicator;
    //ServerGUI gui;
    private final static Logger LOGGER = Logger.getLogger(CommandServer.class
            .getName());

    JChatRejectedExecutionHandler rejectionHandler;
    ThreadFactory threadFactory;
    private final static int corePoolSize = 2;
    private final static int maxPoolSize = 100;
    private final static long keepAliveTime = 60;
    private final static TimeUnit unit = TimeUnit.SECONDS;

    CommandServer(SSLServerSocket sslserversocket) {
        this.serverSocket = sslserversocket;

        //RejectedExecutionHandler implementation
        rejectionHandler = new JChatRejectedExecutionHandler();
        //Get the ThreadFactory implementation to use
        threadFactory = Executors.defaultThreadFactory();
        //creating the ThreadPoolExecutor
        executorPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                unit, new ArrayBlockingQueue<Runnable>(50), threadFactory, rejectionHandler);

        //this.gui = gui;
        R.log("CommandServer created");
        //LOGGER.log(Level.INFO, "Created");
    }

    @Override
    public void run() {
        R.log("Command Server started\n\n");
        threadUserMap = new ConcurrentHashMap<>();
        interMessages = new ArrayBlockingQueue(INTER_MESSAGE_QUEUE_CAPACITY);
        interCommunicator = new InterCommunicator(interMessages, threadUserMap);
        new Thread(interCommunicator).start();

        int clientCounter = 0;
        try {
            for (;;) {
                clientSocket = (SSLSocket) serverSocket.accept();
                clientHandler = new CommandHandler(clientSocket, threadUserMap, interMessages);
                executorPool.execute(clientHandler);
                clientCounter++;
                R.log("New client " + clientCounter);
            }
        } catch (IOException ex) {
            //shut down the pool
            executorPool.shutdown();
            try {
                executorPool.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                R.log(e.toString());
            }
        } finally {
            interCommunicator.stop();
        }
    }

}
