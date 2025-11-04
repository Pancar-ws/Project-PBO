// public class admin extends user {
//     public admin(String name, String id) {
//         super(name, id);
//     }
    
//     public void manageRooms() {
//         System.out.println("admin: Mengelola kamar...");
//     }
// }

import javax.swing.JOptionPane;

public class admin extends user {
    public admin(String name, String id) {
        super(name, id);
    }

    @Override
    public void displayInfo() {
        System.out.println("admin: " + getName() + " (ID: " + getId() + ")");
    }

    public void manageRooms() {
    String[] options = {"Tambah Kamar", "Edit Harga", "Hapus Kamar", "Kembali"};
    int choice = JOptionPane.showOptionDialog(
        null, "Pilih Aksi Admin:", "Kelola Kamar",
        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
        null, options, options[0]
    );

        switch (choice) {
            case 0: JOptionPane.showMessageDialog(null, "Fitur tambah kamar (akan dikembangkan)"); break;
            case 1: JOptionPane.showMessageDialog(null, "Fitur edit harga"); break;
            case 2: JOptionPane.showMessageDialog(null, "Fitur hapus kamar"); break;
        }
    }
}