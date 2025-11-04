import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HotelApp extends JFrame implements bookable {
    private CardLayout cards;
    private JPanel mainPanel;
    private user currentUser;

    // Simulasi "database kamar": <No, Status>
    private static HashMap<String, String> roomStatus = new HashMap<>();

    // Menyimpan semua kamar yang dibooking oleh (sesi) user saat ini
    private Set<String> bookedRooms = new HashSet<>();

    private JTable roomTable; // Untuk update tabel

    static {
        // Inisialisasi status kamar dalam Bahasa Indonesia konsisten
        roomStatus.put("101", "Tersedia");
        roomStatus.put("102", "Tersedia");
        roomStatus.put("103", "Tersedia");
    }

    public HotelApp() {
        setTitle("Hotel Management System - Object Oriented Programming");
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

        JLabel title = new JLabel("Hotel Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.CENTER);

        JButton loginBtn = new JButton("Login");
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
        gbc.gridy = 1; p.add(new JLabel("Password:"), gbc);
        gbc.gridx = 2; gbc.gridy = 0; p.add(id, gbc);
        gbc.gridy = 1; p.add(pass, gbc);
        gbc.gridy = 2; p.add(login, gbc);
        gbc.gridx = 0; p.add(back, gbc);

        login.addActionListener(e -> {
            if (id.getText().equals("admin") && new String(pass.getPassword()).equals("123")) {
                currentUser = new admin("Boss", "admin");
                bookedRooms.clear(); // reset booking session saat login
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                cards.show(mainPanel, "DASH");
            } else {
                JOptionPane.showMessageDialog(this, "Login gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        back.addActionListener(e -> cards.show(mainPanel, "HOME"));
        return p;
    }

    // PAGE 3: DASHBOARD
    private JPanel createDashboardPanel() {
        JPanel p = new JPanel(new GridLayout(6, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // 1. Lihat Daftar Kamar
        p.add(new JButton("1. Lihat Daftar Kamar") {{
            addActionListener(e -> {
                updateRoomTable();
                cards.show(mainPanel, "ROOMS");
            });
        }});

        // 2. Booking Kamar
        p.add(new JButton("2. Booking Kamar") {{
            addActionListener(e -> showBookingDialog());
        }});

        // 3. Cancel Booking
        p.add(new JButton("3. Batalkan Booking") {{
            addActionListener(e -> showCancelDialog());
        }});

        // 4. Kelola Kamar (Admin)
        p.add(new JButton("4. Kelola Kamar (Admin)") {{
            addActionListener(e -> {
                if (currentUser != null && currentUser instanceof admin) {
                    ((admin) currentUser).manageRooms();
                } else {
                    JOptionPane.showMessageDialog(null, "Fitur hanya untuk admin.", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
                }
            });
        }});

        // 5. Logout
        p.add(new JButton("5. Logout") {{
            addActionListener(e -> {
                currentUser = null;
                bookedRooms.clear();
                cards.show(mainPanel, "HOME");
            });
        }});

        return p;
    }

    // PAGE 4: LIHAT KAMAR
    private JPanel createRoomListPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JLabel title = new JLabel("DAFTAR KAMAR TERSEDIA", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        String[] cols = {"No", "Tipe", "Harga", "Status"};
        Object[][] data = new Object[0][0];
        JTable table = new JTable(data, cols);
        table.setEnabled(false);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton back = new JButton("Kembali ke Dashboard");
        back.addActionListener(e -> cards.show(mainPanel, "DASH"));
        p.add(back, BorderLayout.SOUTH);

        this.roomTable = table;
        return p;
    }

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

    // DIALOG BOOKING (mendukung multi-booking)
    private void showBookingDialog() {
        String[] options = {"101 - Single (Rp 150.000)", "102 - Double (Rp 250.000)", "103 - Suite (Rp 500.000)"};
        String choice = (String) JOptionPane.showInputDialog(
            this, "Pilih kamar untuk booking:", "Booking Kamar",
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]
        );

        if (choice != null) {
            String roomNo = choice.substring(0, 3);
            String status = roomStatus.get(roomNo);

            if (status == null) {
                JOptionPane.showMessageDialog(this, "Nomor kamar tidak valid.", "Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (status.equals("Tersedia")) {
                roomStatus.put(roomNo, "Dipesan");
                bookedRooms.add(roomNo);
                JOptionPane.showMessageDialog(this, "Booking kamar " + roomNo + " berhasil!");
                updateRoomTable();
            } else {
                JOptionPane.showMessageDialog(this, "Kamar " + roomNo + " sudah dipesan!", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // DIALOG CANCEL (mendukung cancel satu atau semua kamar yang dibooking)
    private void showCancelDialog() {
        if (bookedRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Anda belum booking kamar apapun.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Siapkan opsi pembatalan: daftar kamar yang dibooking + opsi "Batalkan Semua"
        String[] bookedArray = bookedRooms.toArray(new String[0]);
        String[] options = new String[bookedArray.length + 1];
        for (int i = 0; i < bookedArray.length; i++) {
            options[i] = bookedArray[i] + " - " + getRoomTypeLabel(bookedArray[i]);
        }
        options[bookedArray.length] = "Batalkan Semua";

        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Pilih booking yang ingin dibatalkan:",
            "Batalkan Booking",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == null) return; // user batal memilih

        if (choice.equals("Batalkan Semua")) {
            // Batalkan semua
            for (String rn : bookedRooms) {
                roomStatus.put(rn, "Tersedia");
            }
            bookedRooms.clear();
            JOptionPane.showMessageDialog(this, "Semua booking dibatalkan.");
            updateRoomTable();
            return;
        }

        // Jika memilih salah satu, ambil nomor kamar (format "101 - ...")
        String roomNo = choice.substring(0, 3);
        if (bookedRooms.contains(roomNo)) {
            roomStatus.put(roomNo, "Tersedia");
            bookedRooms.remove(roomNo);
            JOptionPane.showMessageDialog(this, "Booking kamar " + roomNo + " dibatalkan.");
            updateRoomTable();
        } else {
            // Kondisi ini seharusnya tidak terjadi karena opsi berasal dari bookedRooms,
            // tetapi sediakan pesan aman jika terjadi inkonsistensi.
            JOptionPane.showMessageDialog(this, "Gagal membatalkan: data booking tidak konsisten.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper: berikan label tipe kamar untuk tampilan cancel (opsional)
    private String getRoomTypeLabel(String roomNo) {
        switch (roomNo) {
            case "101": return "Single (Rp 150.000)";
            case "102": return "Double (Rp 250.000)";
            case "103": return "Suite (Rp 500.000)";
            default: return "Unknown";
        }
    }

    // Implementasi interface bookable
    @Override
    public void bookRoom() {
        showBookingDialog();
    }

    @Override
    public void cancelBooking() {
        showCancelDialog();
    }

    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelApp::new);
    }
}