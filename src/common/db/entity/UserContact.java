package common.db.entity;

public class UserContact implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer userId;
    private Integer contactId;
    private Status status; 

    public enum Status {

        PENDING, ACTIVE, BROKEN
    }

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getUserId() {
        return userId;
    }


    public void setUserId(Integer userId1) {
        this.userId = userId1;
    }


    public Integer getContactId() {
        return contactId;
    }


    public void setContactId(Integer userId2) {
        this.contactId = userId2;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}

