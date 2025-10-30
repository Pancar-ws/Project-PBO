// import java.util.Scanner;

// public class main {
//     private static Scanner scanner = new Scanner(System.in);
//     private static user currentUser = null;
    
//     public static void main(String[] args) {
//         showPage1();  // Mulai dari Page 1
//     }
    
//     // PAGE 1: HOME
//     public static void showPage1() {
//         System.out.println("\n=== SIMPLE HOTEL BOOKING ===\n");
//         System.out.println("1. Login\n0. Exit");
//         int choice = scanner.nextInt();
//         if (choice == 1) showPage2();
//         else System.exit(0);
//     }
    
//     // PAGE 2: LOGIN (Fitur 1)
//     public static void showPage2() {
//         System.out.println("\n=== LOGIN ===\nID: admin001\nPass: 123");
//         System.out.print("ID: "); String id = scanner.next();
//         System.out.print("Pass: "); String pass = scanner.next();
//         if (id.equals("admin001") && pass.equals("123")) {
//             currentUser = new admin("Admin Boss", id);  // Inheritance!
//             System.out.println("Login Sukses!");
//             showPage3();
//         } else {
//             System.out.println("Gagal! Kembali...");
//             showPage1();
//         }
//     }
    
//     // PAGE 3: DASHBOARD (Fitur 2-5 + Interface)
//     public static void showPage3() {
//         System.out.println("\n=== DASHBOARD " + currentUser.getName() + " ===\n");
//         System.out.println("1. View Rooms\n2. Book Room\n3. Cancel\n4. Manage (Admin)\n5. Logout\n0. Back");
//         int choice = scanner.nextInt();
//         switch (choice) {
//             case 1: System.out.println("Fitur: Lihat Kamar Tersedia"); break;
//             case 2: System.out.println("Fitur: Booking!"); /* bookRoom() */ break;  // Interface
//             case 3: System.out.println("Fitur: Cancel Booking"); break;
//             case 4: ((admin)currentUser).manageRooms(); break;  // Polymorphism
//             case 5: System.out.println("Logout"); showPage1(); break;
//             default: showPage1();
//         }
//         showPage3();  // Loop di page ini
//     }
// }

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HotelApp extends JFrame implements bookable {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private user currentUser;

    // Constructor
    public HotelApp() {
        setTitle("Simple Hotel GUI - PBO Project");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Tambah 3 Halaman
        mainPanel.add(createHomePanel(), "HOME");
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createDashboardPanel(), "DASHBOARD");

        add(mainPanel);
        cardLayout.show(mainPanel, "HOME"); // Mulai dari Home
        setVisible(true);
    }

    // PAGE 1: HOME
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(70, 130, 180));

        JLabel title = new JLabel("SELAMAT DATANG DI HOTEL JAVA", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.CENTER);

        JButton loginBtn = new JButton("Login sebagai Admin");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        panel.add(loginBtn, BorderLayout.SOUTH);

        return panel;
    }

    // PAGE 2: LOGIN
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Kembali");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(idLabel, gbc);
        gbc.gridx = 1; panel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(passLabel, gbc);
        gbc.gridx = 1; panel.add(passField, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(loginBtn, gbc);
        gbc.gridx = 0; panel.add(backBtn, gbc);

        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pass = new String(passField.getPassword());
            if (id.equals("admin001") && pass.equals("123")) {
                currentUser = new admin("Boss Hotel", id); // Inheritance!
                JOptionPane.showMessageDialog(this, "Login Sukses!");
                cardLayout.show(mainPanel, "DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(this, "ID/Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        return panel;
    }

    // PAGE 3: DASHBOARD
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel welcome = new JLabel("DASHBOARD - " + (currentUser != null ? currentUser.getName() : ""), SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcome, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton viewBtn = new JButton("1. Lihat Kamar Tersedia");
        JButton bookBtn = new JButton("2. Booking Kamar");
        JButton cancelBtn = new JButton("3. Cancel Booking");
        JButton manageBtn = new JButton("4. Kelola Kamar (Admin)");
        JButton logoutBtn = new JButton("5. Logout");

        buttonPanel.add(viewBtn);
        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(manageBtn);
        buttonPanel.add(logoutBtn);
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Action Listener
        viewBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Kamar 101, 102 tersedia."));
        bookBtn.addActionListener(e -> bookRoom()); // Interface!
        cancelBtn.addActionListener(e -> cancelBooking());
        manageBtn.addActionListener(e -> {
            if (currentUser instanceof admin) {
                ((admin) currentUser).manageRooms(); // Polymorphism!
                JOptionPane.showMessageDialog(this, "Admin Mode: Kamar berhasil diatur!");
            }
        });
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, "HOME");
        });

        return panel;
    }

    // Implementasi Interface Bookable
    @Override
    public void bookRoom() {
        JOptionPane.showMessageDialog(this, "Booking berhasil! Kamar 101 dipesan.");
    }

    @Override
    public void cancelBooking() {
        JOptionPane.showMessageDialog(this, "Booking dibatalkan.");
    }

    // MAIN
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelApp::new);
    }
}