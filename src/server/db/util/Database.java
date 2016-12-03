package server.db.util;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import common.db.entity.AddContactRequest;
import common.db.entity.ConversationParticipant;
import common.db.entity.FileTransfer;
import common.db.entity.UserAccount;
import common.db.entity.UserContact;
import common.db.entity.UserIcon;
import common.db.entity.UserSession;
import java.util.Date;
import org.jasypt.util.password.StrongPasswordEncryptor;
import server.db.entity.ChatMessageDelivery;
import server.main.R;

public class Database {

    public Database() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.close();
    }

    public Integer insert(Object obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer objectID = null;
        try {
            tx = session.beginTransaction();

            objectID = (Integer) session.save(obj);
            session.flush();
            session.clear();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }
        return objectID;
    }

    /* Method to  READ all the employees */
    public List<Object> select(Class className) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<Object> objects = null;
        try {
            tx = session.beginTransaction();
            objects = session.createQuery("FROM " + className.getSimpleName()).list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return objects;
    }

    public void delete(Object obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(obj);
            session.flush();
            session.clear();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }
    }

    public void update(Object obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(obj);
            session.flush();
            session.clear();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }
    }

    public Object get(Integer id, Class className) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Object object = null;
        try {
            tx = session.beginTransaction();
            object = session.get(className, id);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return object;
    }

    public UserIcon getUserIcon(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        UserIcon icon = null;
        try {
            tx = session.beginTransaction();
            String hql = "from "
                    + UserIcon.class.getSimpleName()
                    + " where uacId = " + userId;
            icon = (UserIcon) session.createQuery(hql).uniqueResult();

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return icon;
    }

    public List<Integer> select(Class className, String string) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<Integer> integers = null;
        try {
            tx = session.beginTransaction();
            integers = session.createQuery("FROM " + className.getSimpleName() + " " + string).list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return integers;
    }

    public void delete(Class<UserContact> aClass, String string) {
    }

    public UserAccount findUserByCredentials(UserAccount userIn) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        UserAccount r = null;
        try {
            tx = session.beginTransaction();
            String hql = "FROM " + UserAccount.class.getSimpleName()
                    + " WHERE username = " + userIn.getUsername()
                    + " and u.password = " + userIn.getPassword();
            r = (UserAccount) session.createQuery(hql).uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return r;
    }

    public UserSession getCurrentUserSession(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        UserSession r = null;
        try {
            tx = session.beginTransaction();
            String hql = "from " + UserSession.class.getSimpleName()
                    + " where userId = " + userId
                    + " and valid = " + Boolean.TRUE;
            Query q = session.createQuery(hql);
            R.log("Query: " + q.getQueryString());
            r = (UserSession) q.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return r;
    }

    public UserSession findUserSession(UserSession usIn) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        UserSession r = null;
        try {
            tx = session.beginTransaction();
            String hql = "FROM " + UserSession.class.getSimpleName() + " u WHERE u.token = "
                    + usIn.getToken();

            r = (UserSession) session.createQuery(hql).uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return r;
    }

    // Find users with username or email similar to the user search input
    public List<UserAccount> findUsers(String userInput) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<UserAccount> resultSet = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("from "
                    + UserAccount.class.getSimpleName()
                    + " where username like :input or email like :input");
            query.setParameter("input", "%" + userInput + "%");
            resultSet = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return resultSet;
    }

    public void updateUCStatus(UserContact uc) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query hql = session.createQuery("update UserContact set status = :status where id = :id");
            hql.setParameter("status", uc.getId());
            hql.setParameter("id", uc.getStatus());
            hql.executeUpdate();
            session.flush();
            session.clear();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }
    }

    public void updateFTStatus(FileTransfer ft) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query hql = session.createQuery("update FileTransfer set status = :status where id = :id");
            hql.setParameter("status", ft.getId());
            hql.setParameter("id", ft.getStatus());
            hql.executeUpdate();
            session.flush();
            session.clear();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }
    }

    public List<Integer> getParticipants(Integer convId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<Integer> resultSet = null;
        try {
            tx = session.beginTransaction();
            String sql = "SELECT P_ID FROM PARTICIPANT_CONVERSATION WHERE C_ID = " + convId + ";";
            Query query = session.createSQLQuery(sql);
            resultSet = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return resultSet;
    }

    public ConversationParticipant getConversationParticipant(Integer convId, Integer partId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ConversationParticipant cp = null;
        try {
            tx = session.beginTransaction();
            String hql = "from ConversationParticipant where conversationId = :convId and participantId = :partId";
            Query query = session.createQuery(hql);
            query.setParameter("convId", convId);
            query.setParameter("partId", partId);
            cp = (ConversationParticipant) query.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return cp;
    }

    public List<ConversationParticipant> getConversationParticipants(Integer convId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<ConversationParticipant> cps = null;
        try {
            tx = session.beginTransaction();
            String hql = "from ConversationParticipant where conversationId = :convId";
            Query query = session.createQuery(hql);
            query.setParameter("convId", convId);
            cps = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());
        } finally {
            session.close();
        }

        return cps;
    }

    public List<AddContactRequest> getUndeliveredACRs(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<AddContactRequest> cps = null;
        try {
            tx = session.beginTransaction();
            String hql = "from " + AddContactRequest.class.getSimpleName() + " where recipientUserId = :userId and status = :undelivered";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            query.setParameter("undelivered", AddContactRequest.Status.BY_SERVER);
            cps = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }

        return cps;
    }

    public List<AddContactRequest> getUndeliveredACRDecisions(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<AddContactRequest> cps = null;
        try {
            tx = session.beginTransaction();
            String hql = "from " + AddContactRequest.class.getSimpleName()
                    + " where questerUserId = :userId and status = :replied";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            query.setParameter("replied", AddContactRequest.Status.REPLIED);
            cps = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }

        return cps;
    }

    public List<FileTransfer> getUndeliveredFiles(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<FileTransfer> results = null;
        try {
            tx = session.beginTransaction();
            String hql = "from " + FileTransfer.class.getSimpleName()
                    + " where targetUserId = :userId and status = :undelivered";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            query.setParameter("undelivered", FileTransfer.Status.BY_SERVER);
            results = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }

        return results;
    }

    public List<UserContact> getUserContacts(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<UserContact> results = null;
        try {
            tx = session.beginTransaction();
            String hql = "from UserContact where userId = :userId and status = :active";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            query.setParameter("active", UserContact.Status.ACTIVE);
            results = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }

        return results;
    }

    public UserContact getUserContact(Integer userId, Integer ctId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        UserContact result = null;
        try {
            tx = session.beginTransaction();
            String hql = "from UserContact where userId = :userId and contactId = :ctId";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            query.setParameter("ctId", ctId);
            result = (UserContact) query.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }

        return result;
    }

    public ChatMessageDelivery getChatMessageDelivery(Integer cmId, Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ChatMessageDelivery result = null;
        try {
            tx = session.beginTransaction();
            String hql = "from ChatMessageDelivery where chatMessageId = :cmId and targetUserId = :userId";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            query.setParameter("cmId", cmId);
            result = (ChatMessageDelivery) query.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }

        return result;
    }

    public UserAccount registerUser(UserAccount userIn) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        UserAccount result = null;
        try {
            tx = session.beginTransaction();
            String hql = "from " + UserAccount.class.getSimpleName() + " where username = :name";
            Query query = session.createQuery(hql);
            query.setParameter("name", userIn.getUsername());
            result = (UserAccount) query.uniqueResult();
            if (result == null) {
                StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
                userIn.setRegDate(new Date());
                userIn.setStatus(UserAccount.Status.OFFLINE);
                String encryptedPassword = spe.encryptPassword(userIn.getPassword());
                R.log("\nUser's password: " + userIn.getPassword());
                userIn.setPassword(encryptedPassword);
                R.log("\nUser's encrypted password: " + userIn.getPassword());
                Integer userId = insert(userIn);
                result = userIn;
                result.setId(userId);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }

        return result;
    }

    public UserAccount findUserByUsername(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        UserAccount result = null;
        try {
            tx = session.beginTransaction();
            String hql = "from " + UserAccount.class.getSimpleName() + " where username = :username";
            Query query = session.createQuery(hql);
            query.setParameter("username", username);
            result = (UserAccount) query.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }
        return result;
    }

    public UserAccount getUsername(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        UserAccount result = null;
        try {
            tx = session.beginTransaction();
            String hql = "from " + UserAccount.class.getSimpleName() + " where id = :userId";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            result = (UserAccount) query.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            R.log(e.toString());

        } finally {
            session.close();
        }
        return result;
    }

}
