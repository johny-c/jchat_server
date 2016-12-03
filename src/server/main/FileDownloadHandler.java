package server.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import common.db.entity.FileTransfer;
import common.utils.Conventions;
import common.utils.Utils;
import server.db.util.Database;

class FileDownloadHandler implements Runnable, Conventions {

    private final SSLSocket socket;
    private InputStream is;
    private OutputStream os;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private byte[] buffer;
    private FileTransfer ft;
    private File[] files;
    private final Database db;
    private static final String DRAW_FOLDER = Utils.getUserFilesDir();

    FileDownloadHandler(SSLSocket clientSocket) {
        socket = clientSocket;
        db = new Database();
    }

    @Override
    public void run() {

        try {
            os = socket.getOutputStream();
            is = socket.getInputStream();

            oos = new ObjectOutputStream(os);
            oos.flush();

            ois = new ObjectInputStream(is);

            int id = ois.readInt();
            ft = (FileTransfer) db.get(id, FileTransfer.class);

            File drawFolder = new File(DRAW_FOLDER + ft.getId() + "");
            files = drawFolder.listFiles();

            int bytesWritten;
            //int totalBytesWritten;
            for (File file : files) {
                try {

                    ois.read();      // waiting for client to request file name
                    oos.writeUTF(file.getName()); // sending file name
                    oos.flush();

                    ois.read();      // waiting for client to request file size
                    oos.writeLong(file.length());
                    oos.flush();

                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    buffer = new byte[FILE_BUFFER_SIZE];

                    bytesWritten = 0;
                    //totalBytesWritten = 0;
                    R.log("Starting file sending . . ." + file.getAbsolutePath());

                    ois.read();     // waiting for client to indicate ready to receive file
                    while ((bytesWritten = bis.read(buffer, 0, buffer.length)) > 0) {
                        os.write(buffer, 0, bytesWritten);
                        //os.flush();
                        //totalBytesWritten += bytesWritten;
                    }
                    R.log("Completed file sending . . ." + file.getAbsolutePath());

                    os.flush();
                    R.log("Flushed outputstream . . .");
                } catch (IOException ex) {
                    R.log(ex.toString());
                }

            }

        } catch (IOException ex) {
            R.log(ex.toString());
        } finally {
            closeStreams();
            R.log("Closed all streams");
        }

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
        } catch (IOException ex) {
            R.log(ex.toString());
        }

    }
}
