package dao;

import entity.Account;
import org.apache.openjpa.persistence.OptimisticLockException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public final class AccountDAO {
    private final EntityManagerFactory emf;

    public AccountDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void create(Account account) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        close(em);
    }

    public Account find(long accountNumber) {
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

    @SuppressWarnings("unchecked")
    public List<Account> findAll() {
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

    @SuppressWarnings("unchecked")
    public List<Account> findAll(String owner) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT o FROM Account o WHERE o.owner = :owner");
            query.setParameter("owner", owner);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        close(em);
        return new ArrayList<>();
    }

    public void update(Account account) throws OptimisticLockException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(account);
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            throw new OptimisticLockException(ole.getMessage(), ole.getNestedThrowables(), ole.getFailedObject(), false);
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        close(em);
    }

    public void delete(long accountNumber) {
        EntityManager em = emf.createEntityManager();
        try {
            Account account = find(accountNumber);
            em.getTransaction().begin();
            em.remove(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
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
