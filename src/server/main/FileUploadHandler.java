package server.main;

import common.db.entity.FileTransfer;
import common.utils.Conventions;
import common.utils.Utils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import server.db.util.Database;

class FileUploadHandler implements Runnable, Conventions {

    private final SSLSocket sslsocket;
    private InputStream is;
    private OutputStream os;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private byte[] buffer;
    private FileTransfer fileTransfer;
    private File destinationFile;
    private final Database db;
    private static final String DRAW_FOLDER = Utils.getUserFilesDir();

    FileUploadHandler(SSLSocket socket) {
        sslsocket = socket;
        db = new Database();
    }

    @Override
    public void run() {

        try {
            os = sslsocket.getOutputStream();
            oos = new ObjectOutputStream(os);
            oos.flush();

            is = sslsocket.getInputStream();
            ois = new ObjectInputStream(is);

            R.log("Requesting filetransfer id");
            oos.write(1);   // requesting filetransfer id
            oos.flush();

            int id = ois.readInt();
            fileTransfer = (FileTransfer) db.get(id, FileTransfer.class);
            R.log("Got filetransfer id= " + id);
            for (int i = 0; i < fileTransfer.getFilesCount(); i++) {
                try {
                    oos.write(2);
                    oos.flush();    // requesting file name
                    String fileName = ois.readUTF();
                    R.log("Got filename= " + fileName);
                    destinationFile = new File(DRAW_FOLDER + fileTransfer.getId() + "/" + fileName);
                    R.log(destinationFile.getAbsolutePath());
                    destinationFile.getParentFile().mkdirs();
                    destinationFile.createNewFile();

                    fos = new FileOutputStream(destinationFile);
                    bos = new BufferedOutputStream(fos);
                    buffer = new byte[FILE_BUFFER_SIZE];

                    int bytesRead;
                    //int totalBytesRead = 0;

                    oos.write(3);
                    oos.flush();    // requesting file upload
                    R.log("Sent signal server is ready to receive file upload");

                    while ((bytesRead = is.read(buffer, 0, buffer.length)) > 0) {
                        bos.write(buffer, 0, bytesRead);
                        bos.flush();
                        //totalBytesRead += bytesRead;
                        R.log("Receiving bytes, bytesRead = " + bytesRead);
                    }
                    R.log("End of file, exited reading loop");
                    oos.flush();
                } catch (IOException ex) {
                    Logger.getLogger(FileUploadHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            os.flush();
            closeStreams();
            R.log("Closed all streams");
        } catch (IOException ex) {
            Logger.getLogger(FileUploadHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeStreams() throws IOException {

        bos.close();
        fos.close();
        ois.close();
        oos.close();
        is.close();
        os.close();
        sslsocket.close();

    }
}
