import dao.AccountDAO;
import dao.DAOFactory;
import entity.Account;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class Client {
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory("PersistenceUnit", System.getProperties());
            AccountDAO accountDAO = (AccountDAO) DAOFactory.createDAO(Account.class, emf);
            if (accountDAO != null) {
                accountDAO.create(new Account(1, 100.0, "Daniel"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        if (emf != null && emf.isOpen()) emf.close();
    }
}
