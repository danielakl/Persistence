import dao.BookDAO;
import dao.DAOFactory;
import entity.Book;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class Client {
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory("PersistenceUnit");
            BookDAO bookDAO = (BookDAO) DAOFactory.createDAO(Book.class, emf);
            if (bookDAO != null) {
                bookDAO.create(new Book());
                System.out.println("Created Book");
            } else {
                System.out.println("BookDAO is null");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        if (emf != null) emf.close();
    }
}
