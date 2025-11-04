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
import java.util.HashMap;

public class HotelApp extends JFrame implements bookable {
    private CardLayout cards;
    private JPanel mainPanel;
    private user currentUser;

    // Simulasi database kamar
    private static HashMap<String, String> roomStatus = new HashMap<>(); // <No, Status>
    private static String bookedRoom = null; // kamar yang dibooking user

    static {
        // Inisialisasi kamar
        roomStatus.put("101", "Tersedia");
        roomStatus.put("102", "Tersedia");
        roomStatus.put("103", "Tersedia");
    }

    public HotelApp() {
        setTitle("Hotel Sederhana - Tugas PBO (Booking & Cancel)");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cards = new CardLayout();
        mainPanel = new JPanel(cards);

        mainPanel.add(createHomePanel(), "HOME");
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createDashboardPanel(), "DASH");
        mainPanel.add(createRoomListPanel(), "ROOMS");

        add(mainPanel);
        cards.show(mainPanel, "HOME");
        setVisible(true);
    }

    // PAGE 1: HOME
    private JPanel createHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(70, 130, 180));
        JLabel title = new JLabel("HOTEL JAVA", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.CENTER);

        JButton loginBtn = new JButton("Masuk ke Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.addActionListener(e -> cards.show(mainPanel, "LOGIN"));
        p.add(loginBtn, BorderLayout.SOUTH);
        return p;
    }

    // PAGE 2: LOGIN
    private JPanel createLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JTextField id = new JTextField(15);
        JPasswordField pass = new JPasswordField(15);
        JButton login = new JButton("Login");
        JButton back = new JButton("Kembali");

        gbc.gridx = 1; gbc.gridy = 0; p.add(new JLabel("ID:"), gbc);
        gbc.gridy = 1; p.add(new JLabel("Pass:"), gbc);
        gbc.gridx = 2; gbc.gridy = 0; p.add(id, gbc);
        gbc.gridy = 1; p.add(pass, gbc);
        gbc.gridy = 2; p.add(login, gbc);
        gbc.gridx = 0; p.add(back, gbc);

        login.addActionListener(e -> {
            if (id.getText().equals("admin") && new String(pass.getPassword()).equals("123")) {
                currentUser = new admin("Boss", "admin");
                JOptionPane.showMessageDialog(this, "Login Sukses!");
                cards.show(mainPanel, "DASH");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        back.addActionListener(e -> cards.show(mainPanel, "HOME"));
        return p;
    }

    // PAGE 3: DASHBOARD
    private JPanel createDashboardPanel() {
        JPanel p = new JPanel(new GridLayout(6, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        p.add(new JButton("1. Lihat Daftar Kamar") {{
            addActionListener(e -> {
                updateRoomTable(); // Refresh tabel
                cards.show(mainPanel, "ROOMS");
            });
        }});
        p.add(new JButton("2. Booking Kamar") {{
            addActionListener(e -> showBookingDialog());
        }});
        p.add(new JButton("3. Cancel Booking") {{
            addActionListener(e -> showCancelDialog());
        }});
        p.add(new JButton("4. Kelola Kamar (Admin)") {{
            addActionListener(e -> ((admin)currentUser).manageRooms());
        }});
        p.add(new JButton("5. Logout") {{
            addActionListener(e -> { currentUser = null; bookedRoom = null; cards.show(mainPanel, "HOME"); });
        }});

        return p;
    }

    // PAGE 4: LIHAT KAMAR (Dengan JTable + Update Real-time)
    private JPanel createRoomListPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JLabel title = new JLabel("DAFTAR KAMAR TERSEDIA", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        // Tabel akan di-update via updateRoomTable()
        String[] cols = {"No", "Tipe", "Harga", "Status"};
        Object[][] data = new Object[0][0];
        JTable table = new JTable(data, cols);
        table.setEnabled(false);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton back = new JButton("Kembali ke Dashboard");
        back.addActionListener(e -> cards.show(mainPanel, "DASH"));
        p.add(back, BorderLayout.SOUTH);

        // Simpan tabel untuk di-update
        this.roomTable = table;
        return p;
    }

    private JTable roomTable; // Untuk update tabel

    // Update tabel kamar
    private void updateRoomTable() {
        String[][] data = {
            {"101", "Single", "Rp 150.000", roomStatus.get("101")},
            {"102", "Double", "Rp 250.000", roomStatus.get("102")},
            {"103", "Suite",  "Rp 500.000", roomStatus.get("103")}
        };
        String[] cols = {"No", "Tipe", "Harga", "Status"};
        roomTable.setModel(new javax.swing.table.DefaultTableModel(data, cols));
    }

    // DIALOG BOOKING
    private void showBookingDialog() {
        String[] options = {"101 - Single (Rp 150.000)", "102 - Double (Rp 250.000)", "103 - Suite (Rp 500.000)"};
        String choice = (String) JOptionPane.showInputDialog(
            this, "Pilih kamar untuk booking:", "Booking Kamar",
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]
        );

        if (choice != null) {
            String roomNo = choice.substring(0, 3);
            if (roomStatus.get(roomNo).equals("Tersedia")) {
                roomStatus.put(roomNo, "Dipesan");
                bookedRoom = roomNo;
                JOptionPane.showMessageDialog(this, "Booking kamar " + roomNo + " berhasil!");
            } else {
                JOptionPane.showMessageDialog(this, "Kamar " + roomNo + " sudah dipesan!", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // DIALOG CANCEL
    private void showCancelDialog() {
        if (bookedRoom == null) {
            JOptionPane.showMessageDialog(this, "Anda belum booking kamar apapun.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this, "Batalkan booking kamar " + bookedRoom + "?", "Cancel Booking",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            roomStatus.put(bookedRoom, "Tersedia");
            JOptionPane.showMessageDialog(this, "Booking kamar " + bookedRoom + " dibatalkan.");
            bookedRoom = null;
        }
    }

    // Interface
    @Override public void bookRoom() { showBookingDialog(); }
    @Override public void cancelBooking() { showCancelDialog(); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelApp::new);
    }
}