package common.db.entity;

import java.io.Serializable;
import java.util.Date;

public class FileTransfer implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer sourceUserId;
    private Integer targetUserId;
    private Long fileSize;
    private Integer filesCount;
    private String sourceName;
    private String targetName;
    private Date timeSent;
    private Date timeByServer;
    private Date timeNotified;
    private Date timeDownloaded;
    private Status status;
    private Integer conversationId;

    public enum Status {

        BY_SOURCE, BY_SERVER, RECIPIENT_NOTIFIED, FILE_DOWNLOADED, DOWNLOAD_REPORTED
    }

    public FileTransfer() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(Integer sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public Integer getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Integer targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Integer getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(Integer filesCount) {
        this.filesCount = filesCount;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public Date getTimeByServer() {
        return timeByServer;
    }

    public void setTimeByServer(Date timeByServer) {
        this.timeByServer = timeByServer;
    }

    public Date getTimeNotified() {
        return timeNotified;
    }

    public void setTimeNotified(Date timeNotified) {
        this.timeNotified = timeNotified;
    }

    public Date getTimeDownloaded() {
        return timeDownloaded;
    }

    public void setTimeDownloaded(Date timeDownloaded) {
        this.timeDownloaded = timeDownloaded;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

}
