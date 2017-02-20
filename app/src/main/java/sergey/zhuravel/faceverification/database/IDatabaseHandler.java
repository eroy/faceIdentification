package sergey.zhuravel.faceverification.database;


import java.util.List;

public interface IDatabaseHandler {
    void addUser(User user);
    User getUser(int id);
    List<User> getAllUser();
    boolean deleteAllUser();


}
