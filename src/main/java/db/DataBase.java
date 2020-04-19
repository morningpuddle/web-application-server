package db;

import model.User;

import java.util.Collection;

public interface DataBase {
    void addUser(User user);
    User findUserById(String userId);
    Collection<User> findAll();
}
