package proyek_akhir;

public class user {
    private String name;
    private String id;

    public user(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() { return name; }
    public String getId() { return id; }

    public void displayInfo() {
        System.out.println("User: " + name);
    }
}