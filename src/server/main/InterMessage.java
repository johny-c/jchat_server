package server.main;

import common.utils.Message;

public class InterMessage {

    private Integer sourceUserId;
    private Integer targetUserId;
    private Message message;

    Integer getSourceUserId() {
        return sourceUserId;
    }

    void setSourceUserId(Integer senderId) {
        this.sourceUserId = senderId;
    }

    Integer getTargetUserId() {
        return targetUserId;
    }

    void setTargetUserId(Integer recipientId) {
        this.targetUserId = recipientId;
    }

    Message getMessage() {
        return message;
    }

    void setMessage(Message message) {
        this.message = message;
    }
}
