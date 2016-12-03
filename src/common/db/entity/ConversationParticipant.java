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
public class ConversationParticipant implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer participantId;
    private Integer conversationId;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }



    public Integer getParticipantId() {
        return participantId;
    }


    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }


    public Integer getConversationId() {
        return conversationId;
    }


    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }



}

