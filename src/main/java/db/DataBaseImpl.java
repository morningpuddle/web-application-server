package db;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBaseImpl implements DataBase {
    private static final Logger log = LoggerFactory.getLogger(DataBaseImpl.class);
    private Map<String, User> users = Maps.newHashMap();

    public void addUser(User user) {
        log.debug("Added new user {} to database", user);
        users.put(user.getUserId(), user);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public Collection<User> findAll() {
        return users.values();
    }
}
