// public class admin extends user {
//     public admin(String name, String id) {
//         super(name, id);
//     }
    
//     public void manageRooms() {
//         System.out.println("admin: Mengelola kamar...");
//     }
// }

public class admin extends user {
    public admin(String name, String id) {
        super(name, id);
    }

    @Override
    public void displayInfo() {
        System.out.println("admin: " + getName() + " (ID: " + getId() + ")");
    }

    public void manageRooms() {
        System.out.println("Mengatur kamar...");
    }
}