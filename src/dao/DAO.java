package dao;

import entity.Entity;

import java.util.List;

public interface DAO {
    void create(Entity entity);
    Entity find(Object identifier);
    List<Entity> findAll();
    void update(Entity entity);
    void delete(Object identifier);
}
