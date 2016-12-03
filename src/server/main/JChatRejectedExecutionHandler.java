package server.main;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.JTextArea;

public class JChatRejectedExecutionHandler implements RejectedExecutionHandler {

    private String caller = this.getClass().getSimpleName();
    JTextArea logArea;

    public JChatRejectedExecutionHandler() {
        // TODO Auto-generated constructor stub
    }

    JChatRejectedExecutionHandler(JTextArea logArea) {
        this.logArea = logArea;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logArea.append(caller + ": Rejected execution to a thread\n");
    }
}
