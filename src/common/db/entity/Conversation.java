package common.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer clientGenId;
    private Integer serverGenId;
    private Integer starterUserId;
    private Date startTime;
    private Date endTime;
    private Status status;
    private Set<UserAccount> participants = new HashSet<>();

    public enum Status {

        PENDING, ACTIVE, INACTIVE
    }

    public Conversation() {
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

    public Integer getStarterUserId() {
        return starterUserId;
    }

    public void setStarterUserId(Integer starterUserId) {
        this.starterUserId = starterUserId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<UserAccount> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<UserAccount> participants) {
        this.participants = participants;
    }

}
