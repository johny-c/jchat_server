package common.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String username;
    private String email;
    private Date regDate;
    private Date birthDate;
    private String password;
    private Status status;

    private Set<Conversation> conversations = new HashSet<>();
    private Set<UserAccount> contacts = new HashSet<>();

    private transient Preferences prefs;

    public enum Status {

        OFFLINE, ONLINE, AWAY, BUSY, APPEAR_OFFLINE
    };

    public UserAccount() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(Set<Conversation> conversations) {
        this.conversations = conversations;
    }

    public Set<UserAccount> getContacts() {
        return contacts;
    }

    public void setContacts(Set<UserAccount> contacts) {
        this.contacts = contacts;
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public void setPrefs(Preferences prefs) {
        this.prefs = prefs;
    }

}
