package dao;

import javax.persistence.EntityManagerFactory;

public final class DAOFactory {
    public static DAO createDAO(Class<?> type, EntityManagerFactory emf) {
        switch (type.getName()) {
            case "entity.Account":
                return new AccountDAO(emf);
            default:
                break;
        }
        return null;
    }
}
