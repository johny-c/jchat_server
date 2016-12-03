/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.main;

import common.utils.Message;
import common.utils.MessageType;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author john
 */
class MessageSender implements Runnable {

    private final SSLSocket socket;
    private final BlockingQueue queue;
    private volatile boolean needed;
    private volatile Thread thisThread;

    MessageSender(SSLSocket socket, BlockingQueue queue) {
        this.socket = socket;
        this.queue = queue;

    }

    @Override
    public void run() {

        thisThread = Thread.currentThread();

        OutputStream os = null;
        try {
            ObjectOutputStream oos = null;
            Thread.currentThread().setName("MESSAGE SENDER THREAD");
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);
            Message msg;
            while (true) {

                try {
                    msg = (Message) queue.take();
                    R.log("sending " + msg.getType().toString());
                    oos.writeObject(msg);
                    oos.flush();
                    oos.reset();
                    if (msg.getType() == MessageType.LOGOUT_RESPONSE) {
                        break;
                    }
                } catch (InterruptedException | IOException ex) {
                    R.log(ex.toString());
                    oos.close();
                    os.close();
                }

            }
        } catch (IOException ex) {
            R.log(ex.toString());
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                R.log(ex.toString());
            }
        }

    }

    public void stop() {
        needed = false;
        thisThread.interrupt();
    }

}
