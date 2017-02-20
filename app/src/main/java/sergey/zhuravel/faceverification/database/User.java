package sergey.zhuravel.faceverification.database;

import java.util.List;


public class User {
    private int id;
    private List<Float> feuter;
    private String name;
    private String basePath;

    public User() {
    }

    public User(List<Float> feuter) {

        this.feuter = feuter;
    }

    public User(int id, List<Float> feuter, String name) {
        this.id = id;
        this.feuter = feuter;
        this.name = name;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Float> getFeuter() {
        return feuter;
    }

    public void setFeuter(List<Float> feuter) {
        this.feuter = feuter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
