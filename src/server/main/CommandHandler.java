package server.main;

import common.db.entity.AddContactRequest;
import common.db.entity.ChatMessage;
import common.db.entity.Conversation;
import common.db.entity.ConversationParticipant;
import common.db.entity.FileTransfer;
import common.db.entity.UserAccount;
import common.db.entity.UserContact;
import common.db.entity.UserIcon;
import common.db.entity.UserSession;
import common.utils.Conventions;
import common.utils.Message;
import common.utils.MessageType;
import common.utils.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.SSLSocket;
import org.jasypt.util.password.StrongPasswordEncryptor;
import server.db.entity.ChatMessageDelivery;
import server.db.util.Database;

public class CommandHandler implements Runnable, Conventions {

    private final SSLSocket sslsocket;
    private InputStream inputStream;
    private ObjectInputStream mis;
    private ObjectOutputStream mout;
    /**
     * Used also by other threads - clients that are simultaneously online they
     * send to the current thread - client through this thread-safe queue
     *
     * @param outgoingMessages
     */
    private final ArrayBlockingQueue outgoingMessages;
    private UserAccount USER;
    private UserIcon USER_ICON;
    private final BlockingQueue<InterMessage> interMessages;
    private final ConcurrentHashMap<Integer, ArrayBlockingQueue> threadsLoggedIn;
    private final Database db;
    private final String caller = this.getClass().getSimpleName();
    private MessageSender sender;
    private StrongPasswordEncryptor spe;

    //private ServerGUI g;
    CommandHandler(SSLSocket socket, ConcurrentHashMap<Integer, ArrayBlockingQueue> threadsMap, BlockingQueue<InterMessage> queue) {
        //g = gui;
        sslsocket = socket;
        outgoingMessages = new ArrayBlockingQueue(100);
        USER = new UserAccount();
        threadsLoggedIn = threadsMap;
        interMessages = queue;
        db = new Database();
        Utils.printSocketInfo(sslsocket);
    }


    /*
     * Runs a thread of communication with one client (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        try {
            sender = new MessageSender(sslsocket, outgoingMessages);
            new Thread(sender).start();

            inputStream = sslsocket.getInputStream();
            mis = new ObjectInputStream(inputStream);

            Object obj;
            Message m;

            boolean needed = true;
            while (((obj = mis.readObject()) != null) && needed) {
                try {
                    m = (Message) obj;
                    R.log("Receiving " + m.getType().toString());
                    needed = handleRequest(m);
                } catch (SQLException ex) {
                    R.log(ex.toString());
                }
            }

        } catch (IOException | ClassNotFoundException ex) {
            R.log(ex.toString());
        } finally {
            try {
                mis.close();
                sslsocket.close();
            } catch (IOException ex) {
                R.log(ex.toString());
            }
        }

    }


    /*
     * Handle the incoming requests appropriately
     */
    private boolean handleRequest(Message m) throws SQLException {
        Message response;
        InterMessage im;
        boolean needed = true;

        ChatMessage cmIn;
        UserAccount userIn, userOut, quester, recipient;
        UserIcon iconIn;
        UserContact uc, ucIn, ucOut;
        List<UserContact> contacts;
        FileTransfer fIn;
        Conversation convIn;
        Message msgOut;
        AddContactRequest acrIn, acrOut;
        UserSession usIn, usOut, usNew;
        ConversationParticipant lcp, ncp;
        List<ConversationParticipant> cps;
        ChatMessageDelivery cmd;
        Integer usId, fId, ctId, convId, cmId;

        R.log("Server: Handling request " + m.getType().toString());

        switch (m.getType()) {
            case SIGNUP_REQUEST:
                userIn = (UserAccount) m.getContent();
                // Check username availability
                userOut = db.findUserByUsername(userIn.getUsername());
                if (userOut == null) {
                    // Username is available, register user account
                    spe = new StrongPasswordEncryptor();
                    userIn.setRegDate(new Date());
                    userIn.setStatus(UserAccount.Status.OFFLINE);
                    String encryptedPassword = spe.encryptPassword(userIn.getPassword());
                    userIn.setPassword(encryptedPassword);
                    Integer userId = db.insert(userIn);

                    response = new Message(MessageType.ACCOUNT_ID, userId);
                    outgoingMessages.offer(response);

                    // Create a new user session
                    usNew = new UserSession();
                    usNew.setUserId(userId);
                    usNew.setToken(R.getRandom().nextLong());
                    usNew.setValid(Boolean.FALSE);
                    usId = db.insert(usNew);
                    usNew.setId(usId);
                    response = new Message(MessageType.NEW_USER_SESSION, usNew);
                    outgoingMessages.offer(response);
                } else {
                    // Username is reserved
                    response = new Message(MessageType.USERNAME_UNAVAILABLE);
                    outgoingMessages.offer(response);
                }
                break;

            // old way of logging in
            // or if credentials are not stored locally, db is corrupt
            case LOGIN_REQUEST:
                userIn = (UserAccount) m.getContent();
                String encryptedPW = spe.encryptPassword(userIn.getPassword());
                userIn.setPassword(encryptedPW);
                UserAccount match = db.findUserByCredentials(userIn);
                if (match == null) {
                    response = new Message(MessageType.LOGIN_FAIL);
                    outgoingMessages.offer(response);
                } else {
                    USER = match;
                    threadsLoggedIn.put(USER.getId(), outgoingMessages);
                    R.log("Put This In User Thread Map with id " + USER.getId());
                    R.log(displayUserThreadMap(threadsLoggedIn));
                    usNew = new UserSession();
                    usNew.setUserId(match.getId());
                    usNew.setToken(R.getRandom().nextLong());
                    usId = db.insert(usNew);
                    usNew.setId(usId);
                    response = new Message(MessageType.NEW_USER_SESSION, usNew);
                    outgoingMessages.offer(response);
                    response = new Message(MessageType.LOGIN_SUCCESS);
                    outgoingMessages.offer(response);
                    sendEventsWhileOffline();
                }
                break;

            // New way of logging in
            case NEW_USER_SESSION_REQUEST:
                usIn = (UserSession) m.getContent();
                usOut = (UserSession) db.get(usIn.getId(), UserSession.class);
                if (usOut == null) {
                    response = new Message(MessageType.LOGIN_FAIL);
                    outgoingMessages.offer(response);
                } else {
                    USER = (UserAccount) db.get(usOut.getUserId(), UserAccount.class);
                    threadsLoggedIn.put(USER.getId(), outgoingMessages);
                    R.log("Put This In User Thread Map with id " + USER.getId());
                    R.log(displayUserThreadMap(threadsLoggedIn));

                    // Create new Session
                    usNew = new UserSession();
                    usNew.setUserId(usOut.getUserId());
                    usNew.setToken(R.getRandom().nextLong());
                    usNew.setLastSessionId(usOut.getId());
                    usNew.setValid(Boolean.FALSE);
                    usId = db.insert(usNew);
                    usNew.setId(usId);

                    // Invalidate old session
                    usOut.setValid(Boolean.FALSE);
                    db.update(usOut);

                    // Send new session
                    response = new Message(MessageType.NEW_USER_SESSION, usNew);
                    outgoingMessages.offer(response);
                    response = new Message(MessageType.LOGIN_SUCCESS);
                    outgoingMessages.offer(response);
                    sendEventsWhileOffline();
                }
                break;

            case NEW_USER_SESSION_ACK:
                usId = (Integer) m.getContent();
                usIn = (UserSession) db.get(usId, UserSession.class);

                // Delete old session
                if (usIn.getLastSessionId() != null) {
                    // This is not the first session
                    usOut = (UserSession) db.get(usIn.getLastSessionId(), UserSession.class);
                    db.delete(usOut);
                }
                usIn.setStart(new Date());
                usIn.setValid(Boolean.TRUE);
                db.update(usIn);

                USER.setStatus(UserAccount.Status.ONLINE);
                db.update(USER);
                sendUserUpdateToContacts(USER);
                break;

            case SEARCH_FOR_USER_REQUEST:
                String userInput = (String) m.getContent();
                List<UserAccount> usersFound = db.findUsers(userInput);
                response = new Message(MessageType.SEARCH_FOR_USER_RESPONSE, usersFound);
                outgoingMessages.offer(response);
                break;

            case ADD_CONTACT_REQUEST:
                acrIn = (AddContactRequest) m.getContent();
                acrIn.setQuesterUserId(USER.getId());

                uc = db.getUserContact(acrIn.getQuesterUserId(), acrIn.getRecipientUserId());
                if (uc != null) { // if connection has been made
                    if (uc.getStatus() != UserContact.Status.BROKEN) {
                        // and it's not broken, ignore the ACR
                        break;
                    }
                }

                acrIn.setStatus(AddContactRequest.Status.BY_SERVER);
                acrIn.setQuesterName(USER.getUsername());
                acrIn.setTimeByServer(new Date());
                Integer acrId = db.insert(acrIn);
                acrIn.setServerGenId(acrId);
                acrIn.setBody(""); // set all fields null except ids, convId
                response = new Message(MessageType.ADD_CONTACT_REQ_ACK, acrIn);
                outgoingMessages.offer(response);

                msgOut = new Message(MessageType.ACR_DELIVERY, acrIn);
                im = new InterMessage();
                im.setSourceUserId(USER.getId());
                im.setTargetUserId(acrIn.getRecipientUserId());
                im.setMessage(msgOut);
                interMessages.offer(im);
                break;

            case ACR_DELIVERY_ACK:  // done
                acrId = (Integer) m.getContent();
                acrOut = (AddContactRequest) db.get(acrId, AddContactRequest.class);
                acrOut.setStatus(AddContactRequest.Status.DELIVERED);
                acrOut.setTimeDelivered(new Date());
                db.update(acrOut);
                break;

            case ACR_DECISION:
                acrIn = (AddContactRequest) m.getContent();
                acrOut = (AddContactRequest) db.get(acrIn.getServerGenId(), AddContactRequest.class);
                acrOut.setStatus(AddContactRequest.Status.REPLIED);
                acrOut.setTimeReplied(new Date());
                acrOut.setReply(acrIn.getReply());
                recipient = (UserAccount) db.get(acrOut.getRecipientUserId(), UserAccount.class);
                acrOut.setRecipientName(recipient.getUsername());
                db.update(acrOut);

                // SEND ACR_DECISION_ACK
                msgOut = new Message(MessageType.ACR_DECISION_DELIVERY, acrOut);
                im = new InterMessage();
                im.setSourceUserId(USER.getId());
                im.setTargetUserId(acrOut.getQuesterUserId());
                im.setMessage(msgOut);
                interMessages.offer(im);

                // Handle the new contact relationship
                if (acrIn.getReply()) { // UserAccount accepted ACR   
                    quester = (UserAccount) db.get(acrOut.getQuesterUserId(), UserAccount.class);

                    UserContact uc1 = new UserContact();
                    uc1.setUserId(quester.getId());
                    uc1.setContactId(recipient.getId());
                    uc1.setStatus(UserContact.Status.PENDING);
                    db.insert(uc1);

                    UserContact uc2 = new UserContact();
                    uc2.setUserId(recipient.getId());
                    uc2.setContactId(quester.getId());
                    uc2.setStatus(UserContact.Status.PENDING);
                    db.insert(uc2);

                    response = new Message(MessageType.CONTACT_ADDITION, quester);
                    outgoingMessages.offer(response);

                    iconIn = db.getUserIcon(quester.getId());
                    if (iconIn != null) {
                        m = new Message(MessageType.CONTACT_ICON_UPDATE, iconIn);
                        outgoingMessages.offer(m);
                    }

                    msgOut = new Message(MessageType.CONTACT_ADDITION, recipient);
                    im = new InterMessage();
                    im.setTargetUserId(quester.getId());
                    im.setMessage(msgOut);
                    interMessages.offer(im);

                    iconIn = db.getUserIcon(recipient.getId());
                    if (iconIn != null) {
                        m = new Message(MessageType.CONTACT_ICON_UPDATE, iconIn);
                        im = new InterMessage();
                        im.setTargetUserId(quester.getId());
                        im.setMessage(m);
                        interMessages.offer(im);
                    }

                }
                break;

            case ACR_DECISION_DELIVERY_ACK:
                acrId = (Integer) m.getContent();
                acrIn = (AddContactRequest) db.get(acrId, AddContactRequest.class);
                acrIn.setStatus(AddContactRequest.Status.REPLY_REPORTED);
                db.update(acrIn);
                break;

            case CONTACT_ADDED_ACK:
                // Contact has been added
                usId = (Integer) m.getContent();
                uc = (UserContact) db.getUserContact(USER.getId(), usId);
                uc.setStatus(UserContact.Status.ACTIVE);
                db.update(uc);
                break;

            case NEW_CONVERSATION_REQ:
                convIn = (Conversation) m.getContent();
                convIn.setStartTime(new Date());
                convIn.setStarterUserId(USER.getId());
                convIn.setServerGenId(db.insert(convIn));

                // BROADCAST
                for (UserAccount p : convIn.getParticipants()) {
                    ncp = new ConversationParticipant();
                    ncp.setConversationId(convIn.getServerGenId());
                    ncp.setParticipantId(p.getId());
                    db.insert(ncp);
                    msgOut = new Message(MessageType.NEW_CONVERSATION_DELIVERY, convIn);
                    im = new InterMessage();
                    im.setTargetUserId(p.getId());
                    im.setMessage(msgOut);
                    interMessages.offer(im);
                }
                break;

            case USER_JOINED_CONVERSATION:
                // update db - participants counter
                // update db - if(participants counter == 10) conv_max_participants
                convId = (Integer) m.getContent();
                ncp = new ConversationParticipant();
                ncp.setConversationId(convId);
                ncp.setParticipantId(USER.getId());
                cps = db.getConversationParticipants(convId);
                for (ConversationParticipant cp : cps) {
                    msgOut = new Message(MessageType.PARTICIPANT_JOINED_CONVERSATION, ncp);
                    im = new InterMessage();
                    im.setTargetUserId(cp.getParticipantId());
                    im.setMessage(msgOut);
                    interMessages.offer(im);
                }
                db.insert(ncp);
                break;

            case USER_LEFT_CONVERSATION:
                // update db - participants counter
                // update db - if(participants counter == 1) conv_end
                convId = (Integer) m.getContent();
                lcp = db.getConversationParticipant(convId, USER.getId());
                db.delete(lcp);
                cps = db.getConversationParticipants(convId);
                for (ConversationParticipant cp : cps) {
                    msgOut = new Message(MessageType.PARTICIPANT_LEFT_CONVERSATION, lcp);
                    im = new InterMessage();
                    im.setTargetUserId(cp.getParticipantId());
                    im.setMessage(msgOut);
                    interMessages.offer(im);
                }
                if (cps.size() == 1) {
                    convIn = (Conversation) db.get(convId, Conversation.class);
                    convIn.setEndTime(new Date());
                    db.update(convIn);
                    msgOut = new Message(MessageType.CONVERSATION_END, convId);
                    im = new InterMessage();
                    im.setTargetUserId(cps.get(0).getParticipantId());
                    im.setMessage(msgOut);
                    interMessages.offer(im);
                }
                break;

            case FILE_SEND_REQ:
                fIn = (FileTransfer) m.getContent();
                if (Utils.isValidFileTransfer(fIn)) {
                    fIn.setSourceName(USER.getUsername());
                    fIn.setId(db.insert(fIn));
                    response = new Message(MessageType.FILE_SEND_ACK, fIn);
                } else {
                    response = new Message(MessageType.FILE_SEND_SERVER_REJECTION, fIn);
                }
                outgoingMessages.offer(response);
                break;

            case FILE_UPLOAD_COMPLETED:
                fId = (Integer) m.getContent();
                fIn = (FileTransfer) db.get(fId, FileTransfer.class);
                fIn.setStatus(FileTransfer.Status.BY_SERVER);
                fIn.setTimeByServer(new Date());
                db.update(fIn);

                msgOut = new Message(MessageType.FILE_NOTIFICATION, fIn);
                im = new InterMessage();
                im.setSourceUserId(USER.getId());
                im.setTargetUserId(fIn.getTargetUserId());
                im.setMessage(msgOut);
                interMessages.offer(im);
                break;

            case FILE_NOTIFICATION_ACK:
                fId = (Integer) m.getContent();
                fIn = (FileTransfer) db.get(fId, FileTransfer.class);
                fIn.setStatus(FileTransfer.Status.RECIPIENT_NOTIFIED);
                fIn.setTimeNotified(new Date());
                //db.updateFTStatus(fIn);
                break;

            case FILE_DOWNLOAD_COMPLETED:
                fId = (Integer) m.getContent();
                fIn = (FileTransfer) db.get(fId, FileTransfer.class);
                fIn.setStatus(FileTransfer.Status.FILE_DOWNLOADED);
                fIn.setTimeDownloaded(new Date());
                db.update(fIn);

                msgOut = new Message(MessageType.FILE_DOWNLOAD_REPORT, fIn);
                im = new InterMessage();
                im.setSourceUserId(fIn.getTargetUserId());
                im.setTargetUserId(fIn.getSourceUserId());
                im.setMessage(msgOut);
                interMessages.offer(im);
                break;

            case FILE_DOWNLOAD_REPORT_ACK:
                fId = (Integer) m.getContent();
                fIn = (FileTransfer) db.get(fId, FileTransfer.class);
                fIn.setStatus(FileTransfer.Status.DOWNLOAD_REPORTED);
                db.update(fIn);
                break;

            case LOGOUT_REQUEST:
                usOut = (UserSession) db.getCurrentUserSession(USER.getId());

                if (usOut != null) {
                    usOut.setEnd(new Date());
                    db.update(usOut);
                    USER.setStatus(UserAccount.Status.OFFLINE);
                    db.update(USER);
                    sendUserUpdateToContacts(USER);
                }

                //Thread.currentThread().interrupt();
                needed = false;
                break;

            case CHAT_MSG_SEND_REQ:
                ChatMessage cm = (ChatMessage) m.getContent();
                cm.setStatus(ChatMessage.Status.BY_SERVER);
                cm.setTimeByServer(new Date());
                cm.setSourceName(USER.getUsername());

                cps = db.getConversationParticipants(cm.getConversationId());
                cm.setTargetsCount(cps.size());
                cm.setDeliveredCount(0);
                cm.setServerGenId(db.insert(cm));

                msgOut = new Message(MessageType.CHAT_MSG_DELIVERY, cm);
                for (ConversationParticipant part : cps) {
                    cmd = new ChatMessageDelivery();
                    cmd.setChatMessageId(cm.getServerGenId());
                    cmd.setStatus(ChatMessageDelivery.Status.BY_SERVER);
                    cmd.setTargetUserId(part.getParticipantId());
                    cmd.setId(db.insert(cmd));

                    im = new InterMessage();
                    im.setSourceUserId(USER.getId());
                    im.setTargetUserId(part.getParticipantId());
                    im.setMessage(msgOut);
                    interMessages.offer(im);
                }
                break;

            case CHAT_MSG_DELIVERY_ACK:
                cmId = (Integer) m.getContent();

                cmd = db.getChatMessageDelivery(cmId, USER.getId());
                cmd.setStatus(ChatMessageDelivery.Status.DELIVERED);
                cmd.setTimeDelivered(new Date());

                cmIn = (ChatMessage) db.get(cmId, ChatMessage.class);
                cmIn.setDeliveredCount(cmIn.getDeliveredCount() + 1);
                if (Objects.equals(cmIn.getDeliveredCount(), cmIn.getTargetsCount())) {
                    cmIn.setTimeDelivered(new Date());
                    db.update(cmIn);

                    cmIn.setBody("");
                    msgOut = new Message(MessageType.CHAT_MSG_DELIVERY_REPORT, cmIn);
                    im = new InterMessage();
                    im.setTargetUserId(cmIn.getSourceUserId());
                    im.setMessage(msgOut);
                    interMessages.offer(im);
                } else {
                    db.update(cmIn);
                }
                break;

            case CHAT_MSG_DELIVERY_REPORT_ACK:
                cmId = (Integer) m.getContent();
                cmIn = (ChatMessage) db.get(cmId, ChatMessage.class);
                cmIn.setStatus(ChatMessage.Status.DELIVERY_REPORTED);
                cmIn.setTimeReported(new Date());
                db.update(cmIn);
                break;

            case USER_UPDATE:
                userIn = (UserAccount) m.getContent();
                USER = (UserAccount) db.get(USER.getId(), UserAccount.class);
                handleUserUpdate(userIn);
                break;

            case USER_ICON_UPDATE:
                iconIn = (UserIcon) m.getContent();
                USER = (UserAccount) db.get(USER.getId(), UserAccount.class);
                USER_ICON = (UserIcon) db.getUserIcon(USER.getId());
                if (USER_ICON == null) {
                    USER_ICON = new UserIcon();
                    USER_ICON.setUacId(USER.getId());
                    USER_ICON.setIconData(iconIn.getIconData());
                    db.insert(USER_ICON);
                } else {
                    USER_ICON.setIconData(iconIn.getIconData());
                    db.update(USER_ICON);
                }

                contacts = db.getUserContacts(USER.getId());
                msgOut = new Message(MessageType.CONTACT_ICON_UPDATE, USER_ICON);
                for (UserContact contact : contacts) {
                    im = new InterMessage();
                    im.setSourceUserId(USER.getId());
                    im.setTargetUserId(contact.getContactId());
                    im.setMessage(msgOut);
                    interMessages.offer(im);
                }
                break;

            case UPDATE_PROFILE_REQUEST:
                userIn = (UserAccount) m.getContent();
                USER = (UserAccount) db.get(USER.getId(), UserAccount.class);

                // If user wants to change username
                if (!userIn.getUsername().equals(USER.getUsername())) {
                    // Check username availability
                    userOut = db.findUserByUsername(userIn.getUsername());
                    if (userOut != null) {
                        // Username is reserved
                        response = new Message(MessageType.USERNAME_UNAVAILABLE);
                        outgoingMessages.offer(response);
                        return true;
                    }
                }

                // User keeps his username or new username is available
                threadsLoggedIn.remove(USER.getId());
                userIn.setStatus(UserAccount.Status.OFFLINE);
                spe = new StrongPasswordEncryptor();
                String encryptedPassword = spe.encryptPassword(userIn.getPassword());
                userIn.setPassword(encryptedPassword);
                userIn.setId(USER.getId());
                db.update(userIn);

                response = new Message(MessageType.UPDATE_PROFILE_SUCCESS);
                outgoingMessages.offer(response);

                // Create a new user session
                usNew = new UserSession();
                usNew.setUserId(USER.getId());
                usNew.setToken(R.getRandom().nextLong());
                usNew.setValid(Boolean.FALSE);
                usId = db.insert(usNew);
                usNew.setId(usId);
                response = new Message(MessageType.NEW_USER_SESSION, usNew);
                outgoingMessages.offer(response);

                sendUserUpdateToContacts(USER);
                break;

            case DELETE_ACCOUNT_REQUEST:
                db.delete(USER);
                response = new Message(MessageType.DELETE_ACCOUNT_SUCCESS);
                outgoingMessages.offer(response);

                //informContactsAboutDeletion(USER);
                break;

            case CONTACT_DELETION:
                ctId = (Integer) m.getContent();
                ucIn = db.getUserContact(USER.getId(), ctId);
                // delete the connection for the user that deletes
                db.delete(ucIn);

                // make the connection broken for the other user
                ucOut = db.getUserContact(ctId, USER.getId());
                if (ucOut != null) {
                    ucOut.setStatus(UserContact.Status.BROKEN);
                    db.update(ucOut);

                    // Inform contact
                    msgOut = new Message(MessageType.CONTACT_BROKEN, USER.getId());
                    im = new InterMessage();
                    im.setSourceUserId(USER.getId());
                    im.setTargetUserId(ctId);
                    im.setMessage(msgOut);
                    interMessages.offer(im);
                }
                break;

            case CONTACT_BROKEN_ACK:
                ctId = (Integer) m.getContent();
                ucIn = db.getUserContact(USER.getId(), ctId);
                db.delete(ucIn);
                break;

            case NEWEST_VERSION_REQUEST:
                R.log("In the NVR case");
                response = new Message(MessageType.NEWEST_VERSION_RESPONSE, "0.0.1");
                outgoingMessages.offer(response);
                break;

            default:
                R.log("In the default case");
                response = new Message(MessageType.COMMUNICATION_TERMINATION_RESPONSE, "WTF???");
                outgoingMessages.offer(response);

        }

        return needed;
    }

    // AT LOGIN
    private void sendEventsWhileOffline() {
        Message m;
        UserIcon icon;

        List<UserContact> ucs = db.getUserContacts(USER.getId());
        for (UserContact uc : ucs) {
            UserAccount contact;
            switch (uc.getStatus()) {
                case PENDING: // UNCONFIRMED CONTACT ADDITIONS
                    contact = (UserAccount) db.get(uc.getContactId(), UserAccount.class);
                    m = new Message(MessageType.CONTACT_ADDITION, contact);
                    outgoingMessages.offer(m);

                    icon = db.getUserIcon(contact.getId());
                    if (icon != null) {
                        m = new Message(MessageType.CONTACT_ICON_UPDATE, icon);
                        outgoingMessages.offer(m);
                    }
                    break;

                case BROKEN: // UNCONFIRMED CONTACT DELETIONS
                    m = new Message(MessageType.CONTACT_BROKEN, uc.getContactId());
                    outgoingMessages.offer(m);
                    break;

                case ACTIVE: // CURRENT STATE OF CONTACTS
                    contact = (UserAccount) db.get(uc.getContactId(), UserAccount.class);
                    m = new Message(MessageType.CONTACT_UPDATE, contact);
                    outgoingMessages.offer(m);

                    icon = db.getUserIcon(contact.getId());
                    if (icon != null) {
                        m = new Message(MessageType.CONTACT_ICON_UPDATE, icon);
                        outgoingMessages.offer(m);
                    }
                    break;
            }
        }

        //MISSED_ACRS 
        List<AddContactRequest> acrs = db.getUndeliveredACRs(USER.getId());
        R.log("Sending missed acrs...");
        for (AddContactRequest acr : acrs) {
            R.log("ACR " + acr.getServerGenId() + " from " + acr.getQuesterUserId() + " to " + acr.getRecipientUserId());
        }
        m = new Message(MessageType.MISSED_ACRS, acrs);
        outgoingMessages.offer(m);

        //MISSED_ACR_DECISIONS
        List<AddContactRequest> acras = db.getUndeliveredACRDecisions(USER.getId());
        m = new Message(MessageType.MISSED_ACR_DECISIONS, acras);
        outgoingMessages.offer(m);

        //MISSED_FILES
        List<FileTransfer> fts = db.getUndeliveredFiles(USER.getId());
        m = new Message(MessageType.MISSED_FILES, fts);
        outgoingMessages.offer(m);

        //MISSED_CALLS 
        //MISSED_CHATS
    }

    private void handleUserUpdate(UserAccount userIn) {

        if (userIn.getUsername() != null) {
            USER.setUsername(userIn.getUsername());
        }

        if (userIn.getEmail() != null) {
            USER.setEmail(userIn.getEmail());
        }
        if (userIn.getStatus() != null) {
            USER.setStatus(userIn.getStatus());
        }

        if (userIn.getBirthDate() != null) {
            USER.setBirthDate(userIn.getBirthDate());
        }

        if (userIn.getPassword() != null) {
            if (!userIn.getPassword().isEmpty()) {
                spe = new StrongPasswordEncryptor();
                String encryptedPassword = spe.encryptPassword(userIn.getPassword());
                R.log("\nUser's password: " + userIn.getPassword());
                USER.setPassword(encryptedPassword);
                R.log("\nUser's encrypted password: " + userIn.getPassword());
            }
        }

        db.update(USER);
        sendUserUpdateToContacts(USER);
    }

    private String displayUserThreadMap(ConcurrentHashMap<Integer, ArrayBlockingQueue> threadsLoggedIn) {
        String s = "User accounts logged in\n\n";
        for (Integer i : threadsLoggedIn.keySet()) {
            s += i + ",\n";
        }
        return s;
    }

    private void sendUserUpdateToContacts(UserAccount USER) {
        List<UserContact> contacts = db.getUserContacts(USER.getId());
        Message msgOut = new Message(MessageType.CONTACT_UPDATE, USER);
        InterMessage im;
        for (UserContact contact : contacts) {
            im = new InterMessage();
            im.setSourceUserId(USER.getId());
            im.setTargetUserId(contact.getContactId());
            im.setMessage(msgOut);
            interMessages.offer(im);
        }
    }

}
