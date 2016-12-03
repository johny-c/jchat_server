package server.db.entity;

import common.utils.Conventions;
import java.io.Serializable;
import java.util.Date;

public class ChatMessageDelivery implements Serializable, Conventions {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer targetUserId;
    private Date timeDelivered;
    private Date timeReported;
    private Date timeFailed;
    private Status status;
    private Integer chatMessageId;

    public enum Status {

        BY_SERVER, DELIVERED, REPORTED, FAILED
    }

    public ChatMessageDelivery() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Integer targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Date getTimeReported() {
        return timeReported;
    }

    public void setTimeReported(Date timeReported) {
        this.timeReported = timeReported;
    }

    public Date getTimeDelivered() {
        return timeDelivered;
    }

    public void setTimeDelivered(Date timeDelivered) {
        this.timeDelivered = timeDelivered;
    }

    public Date getTimeFailed() {
        return timeFailed;
    }

    public void setTimeFailed(Date timeFailed) {
        this.timeFailed = timeFailed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(Integer chatMessageId) {
        this.chatMessageId = chatMessageId;
    }

}
