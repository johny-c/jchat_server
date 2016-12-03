package common.db.entity;

import java.io.Serializable;
import java.util.Date;

public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer userId;
    private Long token;
    private Date start;
    private Date end;
    private Boolean valid;
    private Integer lastSessionId;


    public UserSession() {
    }


    public UserSession(int id) {
        this();
        this.id = id;
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


    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public Long getToken() {
        return token;
    }


    public void setToken(Long token) {
        this.token = token;
    }


    public Date getStart() {
        return start;
    }


    public void setStart(Date start) {
        this.start = start;
    }


    public Date getEnd() {
        return end;
    }


    public void setEnd(Date end) {
        this.end = end;
    }


    public Boolean isValid() {
        return valid;
    }


    public void setValid(Boolean valid) {
        this.valid = valid;
    }



    public Integer getLastSessionId() {
        return lastSessionId;
    }


    public void setLastSessionId(Integer lastSessionId) {
        this.lastSessionId = lastSessionId;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }


    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserSession)) {
            return false;
        }
        UserSession other = (UserSession) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "client_db.entity.UserSession[ id=" + id + " ]";
    }
}

