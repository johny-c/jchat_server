package server.main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import common.utils.Utils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author john
 */
class LatestVersionHandler implements Runnable {

    Socket socket;
    OutputStream os;
    InputStream is;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    FileInputStream fis;
    private static final String LATEST_VERSION_PATH = Utils.getLatestVersionDir();
    private BufferedInputStream bis;
    private byte[] buffer;
    private static final int FILE_BUFFER_SIZE = 1024;
    private File file;
    private final static Logger LOGGER = Logger.getLogger(LatestVersionHandler.class
            .getName());

    LatestVersionHandler(Socket clientSocket) {
        socket = clientSocket;
    }

    private void closeStreams() {
        try {
            bis.close();
            fis.close();
            ois.close();
            oos.close();
            is.close();
            os.close();
            socket.close();
            LOGGER.log(Level.INFO, "Closed streams");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.toString());
        }
    }

    @Override
    public void run() {

        try {
            os = socket.getOutputStream();
            is = socket.getInputStream();

            oos = new ObjectOutputStream(os);
            oos.flush();
            ois = new ObjectInputStream(is);

            R.log("LATEST_VERSION_PATH: " + LATEST_VERSION_PATH);
            // Find the one zip in the directory
            file = new File(LATEST_VERSION_PATH);
            R.log("LATEST_VERSION_PATH: " + LATEST_VERSION_PATH);
            file = file.listFiles()[0];
            fis = new FileInputStream(file);

            LOGGER.log(Level.INFO, "Waiting for client to request file info");
            int sendFileInfo = ois.readInt();

            LOGGER.log(Level.INFO, "Client requested file info size");
            oos.writeLong(file.length());
            oos.flush();
            LOGGER.log(Level.INFO, "Sent file size ..");
            ois.readInt();
            LOGGER.log(Level.INFO, "Client requested file info name");
            oos.writeUTF(file.getName());
            oos.flush();
            LOGGER.log(Level.INFO, "Sent file info ..");
            // Buffered input from File input
            bis = new BufferedInputStream(fis);
            buffer = new byte[FILE_BUFFER_SIZE];

            LOGGER.log(Level.INFO, "Waiting for client to request file");
            int ready = ois.readInt();
            if (ready == 1) {

                int bytesWritten = 0;
                int totalBytesWritten = 0;
                int progressBytes = 0;
                LOGGER.log(Level.INFO, "Starting file sending . . .");
                while ((bytesWritten = bis.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, bytesWritten);
                    //outputStream.flush();
                    totalBytesWritten += bytesWritten;
                }
                LOGGER.log(Level.INFO, "Completed file sending . . .");

                os.flush();
                LOGGER.log(Level.INFO, "Flushed outputstream . . .");
            }
            closeStreams();

        } catch (IOException ex) {
            LOGGER.log(Level.INFO, ex.toString());
        }
    }

}
