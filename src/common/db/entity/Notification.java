package common.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Exists only in Client, it is a virtual wrapper entity
 *
 * For online ACRs, For offline ACRs, CMs, Files
 *
 * @author johny
 */
public final class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Type type;
    private Status status;
    private Integer eventId;
    private String relatedUsername;
    private Date timeStamp;

    public enum Type {

        ACR_RECEIVED,
        ACR_DECISION_REPORT,
        MISSED_CHAT,
        MISSED_CALL,
        FILE_RECEIVED,
        FILE_DOWNLOAD_REPORT
    }

    // READ IF CLOSE BUTTON CLICKED
    public enum Status {

        UNREAD, READ, HANDLED, DELETED
    }

    public Notification() {
    }

    public Notification(Type type, AddContactRequest acrIn) {

        this.setType(type);
        this.setEventId(acrIn.getServerGenId());
        this.setStatus(Notification.Status.UNREAD);
        this.setTimeStamp(acrIn.getTimeByServer());

        if (type == Type.ACR_RECEIVED) {
            this.setRelatedUsername(acrIn.getQuesterName());
        } else if (type == Type.ACR_DECISION_REPORT) {
            this.setRelatedUsername(acrIn.getRecipientName());
        }
    }

    public Notification(Type type, FileTransfer fileIn) {

        this.setType(type);
        this.setEventId(fileIn.getId());
        this.setStatus(Notification.Status.UNREAD);

        if (type == Type.FILE_RECEIVED) {
            this.setRelatedUsername(fileIn.getSourceName());
            this.setTimeStamp(fileIn.getTimeByServer());
        } else if (type == Type.FILE_DOWNLOAD_REPORT) {
            this.setRelatedUsername(fileIn.getTargetName());
            this.setTimeStamp(fileIn.getTimeDownloaded());
        }

    }

    public Notification(Type type, List<ChatMessage> cmsIn) {

        this.setType(type);
        this.setEventId(cmsIn.get(0).getConversationId());
        this.setStatus(Notification.Status.UNREAD);
        this.setTimeStamp(cmsIn.get(0).getTimeByServer());
        this.setRelatedUsername(cmsIn.get(0).getSourceName());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getRelatedUsername() {
        return relatedUsername;
    }

    public void setRelatedUsername(String relatedUsername) {
        this.relatedUsername = relatedUsername;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
