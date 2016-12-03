package common.db.entity;

import java.io.Serializable;
import java.util.Date;

public class AddContactRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer clientGenId;
    private Integer serverGenId;
    private String body;
    private Integer questerUserId;
    private Integer recipientUserId;
    private String questerName;
    private String recipientName;
    private Date timeSent;
    private Date timeByServer;
    private Date timeDelivered;
    private Date timeReplied;
    private Status status;
    private Boolean reply;

    public enum Status {

        BY_SOURCE, BY_SERVER, DELIVERED, REPLIED, REPLY_REPORTED
    }

    public AddContactRequest() {
    }

    public Integer getClientGenId() {
        return clientGenId;
    }

    public void setClientGenId(Integer clientGenId) {
        this.clientGenId = clientGenId;
    }

    public Integer getServerGenId() {
        return serverGenId;
    }

    public void setServerGenId(Integer serverGenId) {
        this.serverGenId = serverGenId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getQuesterUserId() {
        return questerUserId;
    }

    public void setQuesterUserId(Integer questerUserId) {
        this.questerUserId = questerUserId;
    }

    public Integer getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(Integer receiverUserId) {
        this.recipientUserId = receiverUserId;
    }

    public String getQuesterName() {
        return questerName;
    }

    public void setQuesterName(String questerName) {
        this.questerName = questerName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
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

    public Date getTimeDelivered() {
        return timeDelivered;
    }

    public void setTimeDelivered(Date timeDelivered) {
        this.timeDelivered = timeDelivered;
    }

    public Date getTimeReplied() {
        return timeReplied;
    }

    public void setTimeReplied(Date timeReplied) {
        this.timeReplied = timeReplied;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getReply() {
        return reply;
    }

    public void setReply(Boolean reply) {
        this.reply = reply;
    }

}
