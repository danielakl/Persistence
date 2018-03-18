package dao;

import javax.persistence.EntityManagerFactory;

public final class DAOFactory {
    public static DAO createDAO(Class<?> type, EntityManagerFactory emf) {
        for (Class<?> c : type.getClasses()) {
            switch (c.getName().toLowerCase()) {
                case "book":
                    return new BookDAO(emf);
                default:
                    break;
            }
        }
        return null;
    }
}
