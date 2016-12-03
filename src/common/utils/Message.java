package common.utils;

import java.io.Serializable;

public final class Message implements Serializable {

    private static final long serialVersionUID = 4L;
    private MessageType type;
    private Object content;

    public Message(MessageType type) {
        this.setType(type);
    }

    public Message(MessageType type, Object obj) {
        this.setType(type);
        this.setContent(obj);
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object messageContent) {
        this.content = messageContent;
    }

}
