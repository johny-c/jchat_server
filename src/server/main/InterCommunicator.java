/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.main;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author johny
 */
public class InterCommunicator implements Runnable {

    private final BlockingQueue<InterMessage> interMessages;
    private final ConcurrentHashMap<Integer, ArrayBlockingQueue> threadUserMap;
    private boolean commandServerIsRunning;
    private volatile Thread thisThread;

    InterCommunicator(BlockingQueue interMessages, ConcurrentHashMap threadUserMap) {
        this.interMessages = interMessages;
        this.threadUserMap = threadUserMap;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("INTER-COMMUNICATOR THREAD");
        thisThread = Thread.currentThread();
        InterMessage im;
        ArrayBlockingQueue clientQueue;

        commandServerIsRunning = true;
        while (commandServerIsRunning) {
            try {
                im = interMessages.take();

                R.log("Target user id is " + im.getTargetUserId());
                clientQueue = threadUserMap.get(im.getTargetUserId());
                if (clientQueue == null) {
                    R.log("Recipient is offline");
                } else {

                    try {
                        clientQueue.put(im.getMessage());
                        R.log("Sent inter message to online user");
                    } catch (InterruptedException ex) {
                        R.log("Online user's queue was full, could not send intermessage to userId=" + im.getTargetUserId());
                    }
                }
            } catch (InterruptedException ex) {
                R.log(ex.toString());
                commandServerIsRunning = false;
            }
        }
    }

    void stop() {
        thisThread.interrupt();
    }

}
