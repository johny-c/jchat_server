/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common.db.entity;

import java.io.Serializable;

/**
 *
 * @author johny
 */
public class VideoFrame implements Serializable {

    private static final long serialVersionUID = 1L;
    private byte[] data;
    private Integer conversationId;

    public VideoFrame(byte[] b, Integer cId) {
        data = b;
        conversationId = cId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

}
