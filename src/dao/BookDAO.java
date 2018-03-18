package dao;

import entity.Entity;

import javax.persistence.*;
import java.util.List;

public final class BookDAO implements DAO {
    private final EntityManagerFactory emf;

    public BookDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Entity entity) {

    }

    @Override
    public Entity find(Object identifier) {
        return null;
    }

    @Override
    public List<Entity> findAll() {
        return null;
    }

    @Override
    public void update(Entity entity) {

    }

    @Override
    public void delete(Object identifier) {

    }
}
