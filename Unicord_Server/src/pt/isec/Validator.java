package pt.isec;

import java.sql.SQLException;

public class Validator {

    private Validator() {
    }

    public static boolean checkUsernameAvailability(String name, Database db) throws SQLException {
        return db.User.getByUsername(name) == null;
    }

    public boolean checkChannelAvailability(String name, Database db) throws SQLException {
        return db.Channel.getByName(name) == null;
    }

    public boolean checkPasswordMatchUsername(User user,Database db) throws SQLException {
        return db.User.doesPasswordMatchUsername(user.username,user.password);
    }

}
