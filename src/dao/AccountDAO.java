package dao;

import entity.Account;
import entity.Entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public final class AccountDAO implements DAO {
    private final EntityManagerFactory emf;

    public AccountDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Entity entity) {
        Account account = (Account) entity;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        close(em);
    }

    @Override
    public Entity find(Object identifier) {
        long accountNumber = (long) identifier;
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Account.class, accountNumber);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        close(em);
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Entity> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT o FROM Account o", Account.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        close(em);
        return new ArrayList<>();
    }

    @Override
    public void update(Entity entity) {
        Account account = (Account) entity;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        close(em);
    }

    @Override
    public void delete(Object identifier) {
        long accountNumber = (long) identifier;
        EntityManager em = emf.createEntityManager();
        try {
            Account account = (Account) find(accountNumber);
            em.getTransaction().begin();
            em.remove(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        close(em);
    }

    private void close(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
