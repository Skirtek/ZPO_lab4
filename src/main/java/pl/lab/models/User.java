package pl.lab.models;

public class User {
    private long id;
    private String name;
    private String password;
    private int age;

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private void makeNoise(){
        print("'Witaj przybyszu'");
    }

    private void print(String value){
        System.out.println(value);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
