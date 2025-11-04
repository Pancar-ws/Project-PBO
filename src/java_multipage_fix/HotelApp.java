import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class HotelApp extends JFrame implements Bookable {
    private CardLayout cards;
    private JPanel mainPanel;
    private User currentUser;

    private static List<Room> rooms = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();

    private JTable roomTable;
    private JTable bookingTable;

    static {
        rooms.add(new Room("101", "Single", 150000, true));
        rooms.add(new Room("102", "Double", 250000, true));
        rooms.add(new Room("103", "Suite", 500000, false));
    }

    public HotelApp() {
        setTitle("Hotel Management System - PBO");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cards = new CardLayout();
        mainPanel = new JPanel(cards);

        mainPanel.add(createWelcomePanel(), "WELCOME");
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createHomePanel(), "HOME");
        mainPanel.add(createDashboardPanel(), "DASHBOARD");
        mainPanel.add(createRoomManagementPanel(), "ROOM_MGMT");
        mainPanel.add(createBookingManagementPanel(), "BOOKING_MGMT");

        add(mainPanel);
        cards.show(mainPanel, "WELCOME");
        setVisible(true);
    }

    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 60, 120));

        JLabel title = new JLabel("HOTEL JAVA", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.CENTER);

        JLabel desc = new JLabel("<html><center>Sistem Manajemen Hotel<br>Modern • Aman • Efisien</center></html>", SwingConstants.CENTER);
        desc.setFont(new Font("Arial", Font.PLAIN, 18));
        desc.setForeground(Color.LIGHT_GRAY);
        p.add(desc, BorderLayout.SOUTH);

        JButton loginBtn = new JButton("Masuk ke Sistem");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setBackground(new Color(0, 120, 215));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(e -> cards.show(mainPanel, "LOGIN"));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(30, 60, 120));
        btnPanel.add(loginBtn);
        p.add(btnPanel, BorderLayout.NORTH);

        return p;
    }

    private JPanel createLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JTextField id = new JTextField(15);
        JPasswordField pass = new JPasswordField(15);
        JButton login = new JButton("Login");

        gbc.gridx = 1; gbc.gridy = 0; p.add(new JLabel("ID:"), gbc);
        gbc.gridy = 1; p.add(new JLabel("Pass:"), gbc);
        gbc.gridx = 2; gbc.gridy = 0; p.add(id, gbc);
        gbc.gridy = 1; p.add(pass, gbc);
        gbc.gridy = 2; p.add(login, gbc);

        login.addActionListener(e -> {
            if (id.getText().equals("admin") && new String(pass.getPassword()).equals("admin")) {
                currentUser = new Admin("Manager", "admin");
                JOptionPane.showMessageDialog(this, "Login Sukses!");
                cards.show(mainPanel, "HOME");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private JPanel createHomePanel() {
        JPanel p = new JPanel(new GridLayout(4, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        p.add(createButton("Dashboard", e -> cards.show(mainPanel, "DASHBOARD")));
        p.add(createButton("Room Management", e -> {
            refreshRoomTable();
            cards.show(mainPanel, "ROOM_MGMT");
        }));
        p.add(createButton("Booking Management", e -> {
            refreshBookingTable();
            cards.show(mainPanel, "BOOKING_MGMT");
        }));
        p.add(createButton("Logout", e -> {
            currentUser = null;
            cards.show(mainPanel, "WELCOME");
        }));

        return p;
    }

    private JPanel createDashboardPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,15,15,15);
        gbc.anchor = GridBagConstraints.WEST;

        int available = (int) rooms.stream().filter(Room::isAvailable).count();
        int activeBookings = (int) bookings.stream().filter(Booking::isActive).count();

        JLabel stat1 = new JLabel("Jumlah Kamar Tersedia: " + available);
        JLabel stat2 = new JLabel("Jumlah Booking Aktif: " + activeBookings);

        gbc.gridx = 0; gbc.gridy = 0; p.add(stat1, gbc);
        gbc.gridy = 1; p.add(stat2, gbc);
        gbc.gridy = 2; p.add(createButton("Kembali", e -> cards.show(mainPanel, "HOME")), gbc);

        return p;
    }

    private JPanel createRoomManagementPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("ROOM MANAGEMENT", SwingConstants.CENTER), BorderLayout.NORTH);

        String[] cols = {"No", "Tipe", "Harga", "Tersedia"};
        roomTable = new JTable(new Object[0][0], cols);
        p.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        btns.add(createButton("Tambah", e -> addRoom()));
        btns.add(createButton("Edit", e -> editRoom()));
        btns.add(createButton("Hapus", e -> deleteRoom()));
        btns.add(createButton("Kembali", e -> cards.show(mainPanel, "HOME")));
        p.add(btns, BorderLayout.SOUTH);

        return p;
    }

    private void refreshRoomTable() {
        Object[][] data = rooms.stream().map(r -> new Object[]{
            r.getNo(), r.getType(), r.getPrice(), r.isAvailable() ? "Ya" : "Tidak"
        }).toArray(Object[][]::new);
        roomTable.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"No", "Tipe", "Harga", "Tersedia"}));
    }

    private void addRoom() {
        JTextField no = new JTextField(), type = new JTextField(), price = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("No:")); panel.add(no);
        panel.add(new JLabel("Tipe:")); panel.add(type);
        panel.add(new JLabel("Harga:")); panel.add(price);

        if (JOptionPane.showConfirmDialog(this, panel, "Tambah Kamar", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                rooms.add(new Room(no.getText(), type.getText(), Double.parseDouble(price.getText()), true));
                refreshRoomTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus angka!");
            }
        }
    }

    private void editRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih kamar!"); return; }
        Room r = rooms.get(row);
        JTextField type = new JTextField(r.getType()), price = new JTextField(String.valueOf(r.getPrice()));
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Tipe:")); panel.add(type);
        panel.add(new JLabel("Harga:")); panel.add(price);

        if (JOptionPane.showConfirmDialog(this, panel, "Edit Kamar", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                r.setType(type.getText());
                r.setPrice(Double.parseDouble(price.getText()));
                refreshRoomTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus angka!");
            }
        }
    }

    private void deleteRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih kamar!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Hapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            rooms.remove(row);
            refreshRoomTable();
        }
    }

    private JPanel createBookingManagementPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("BOOKING MANAGEMENT", SwingConstants.CENTER), BorderLayout.NORTH);

        String[] cols = {"No Kamar", "Tamu", "Status"};
        bookingTable = new JTable(new Object[0][0], cols);
        p.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        btns.add(createButton("Buat Booking", e -> bookRoom()));
        btns.add(createButton("Batalkan", e -> cancelBooking()));
        btns.add(createButton("Kembali", e -> cards.show(mainPanel, "HOME")));
        p.add(btns, BorderLayout.SOUTH);

        return p;
    }

    private void refreshBookingTable() {
        Object[][] data = bookings.stream().map(b -> new Object[]{
            b.getRoomNo(), b.getGuestName(), b.isActive() ? "Aktif" : "Dibatalkan"
        }).toArray(Object[][]::new);
        bookingTable.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"No Kamar", "Tamu", "Status"}));
    }

    @Override
    public void bookRoom() {
        String roomNo = JOptionPane.showInputDialog("No Kamar:");
        String guest = JOptionPane.showInputDialog("Nama Tamu:");
        if (roomNo != null && guest != null && !roomNo.isEmpty() && !guest.isEmpty()) {
            Room r = rooms.stream().filter(room -> room.getNo().equals(roomNo) && room.isAvailable()).findFirst().orElse(null);
            if (r != null) {
                r.setAvailable(false);
                bookings.add(new Booking(roomNo, guest, true));
                refreshBookingTable();
                JOptionPane.showMessageDialog(this, "Booking berhasil!");
            } else {
                JOptionPane.showMessageDialog(this, "Kamar tidak tersedia!");
            }
        }
    }

    @Override
    public void cancelBooking() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih booking!"); return; }
        Booking b = bookings.get(row);
        if (JOptionPane.showConfirmDialog(this, "Batalkan?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            b.setActive(false);
            Room r = rooms.stream().filter(room -> room.getNo().equals(b.getRoomNo())).findFirst().orElse(null);
            if (r != null) r.setAvailable(true);
            refreshBookingTable();
        }
    }

    private JButton createButton(String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.addActionListener(al);
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelApp::new);
    }
}