package proyek_akhir;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

public class HotelApp extends JFrame implements bookable {
    private CardLayout cards;
    private JPanel mainPanel;
    private user currentUser;

    // Database simulasi
    private static HashMap<String, String> roomStatus = new HashMap<>();
    private static HashMap<String, Integer> roomPrices = new HashMap<>();
    private static HashMap<String, Integer> bookedNights = new HashMap<>(); // BARU: simpan jumlah malam

    private long totalRevenue = 0; // HANYA bertambah saat check-out
    private Set<String> bookedRooms = new HashSet<>();
    private JTable roomTable;

    // Variabel sementara untuk booking
    private String selectedRoomNo;
    private int selectedNights;

    static {
        roomStatus.put("101", "Tersedia");
        roomStatus.put("102", "Tersedia");
        roomStatus.put("103", "Tersedia");

        roomPrices.put("101", 150000);
        roomPrices.put("102", 250000);
        roomPrices.put("103", 500000);
    }

    public HotelApp() {
        setTitle("Hotel Management System - Object Oriented Programming");
        setSize(700, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cards = new CardLayout();
        mainPanel = new JPanel(cards);

        mainPanel.add(createHomePanel(), "HOME");
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createDashboardPanel(), "DASH");
        mainPanel.add(createRoomListPanel(), "ROOMS");
        mainPanel.add(createBookingPage(), "BOOKING_PAGE");

        add(mainPanel);
        cards.show(mainPanel, "HOME");
        setVisible(true);
    }

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
                bookedRooms.clear();
                bookedNights.clear();
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                cards.show(mainPanel, "DASH");
            } else {
                JOptionPane.showMessageDialog(this, "Login gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        back.addActionListener(e -> cards.show(mainPanel, "HOME"));
        return p;
    }

    private JPanel createDashboardPanel() {
        JPanel p = new JPanel(new GridLayout(6, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        p.add(new JButton("1. Lihat Daftar Kamar") {{
            addActionListener(e -> {
                updateRoomTable();
                cards.show(mainPanel, "ROOMS");
            });
        }});

        p.add(new JButton("2. Booking Kamar") {{
            addActionListener(e -> cards.show(mainPanel, "BOOKING_PAGE"));
        }});

        p.add(new JButton("3. Batalkan Booking") {{
            addActionListener(e -> showCancelDialog());
        }});

        p.add(new JButton("4. Billing & Check-out") {{
            addActionListener(e -> showBillingMenu());
        }});

        p.add(new JButton("5. Laporan Pendapatan") {{
            addActionListener(e -> showRevenueReport());
        }});

        p.add(new JButton("6. Logout") {{
            addActionListener(e -> {
                currentUser = null;
                bookedRooms.clear();
                bookedNights.clear();
                cards.show(mainPanel, "HOME");
            });
        }});

        return p;
    }

    private JPanel createRoomListPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JLabel title = new JLabel("DAFTAR KAMAR HOTEL", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        String[] cols = {"No", "Tipe", "Harga", "Status"};
        JTable table = new JTable(new Object[0][0], cols);
        table.setEnabled(false);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton back = new JButton("Kembali ke Dashboard");
        back.addActionListener(e -> cards.show(mainPanel, "DASH"));
        p.add(back, BorderLayout.SOUTH);

        this.roomTable = table;
        return p;
    }

    // HALAMAN BOOKING
    private JPanel createBookingPage() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblRoom = new JLabel("Pilih Tipe Kamar:");
        JComboBox<String> roomCombo = new JComboBox<>(new String[]{
                "101 - Single (Rp 150.000)", "102 - Double (Rp 250.000)", "103 - Suite (Rp 500.000)"
        });

        JLabel lblNights = new JLabel("Jumlah Malam:");
        JTextField nightsField = new JTextField("1", 5);

        JLabel lblTotal = new JLabel("Total: Rp 0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(Color.BLUE);

        JButton confirmBtn = new JButton("Konfirmasi Booking");
        JButton cancelBtn = new JButton("Batal");

        Runnable updateTotal = () -> {
            try {
                String selected = (String) roomCombo.getSelectedItem();
                String roomNo = selected.substring(0, 3);
                int price = roomPrices.get(roomNo);
                int nights = Integer.parseInt(nightsField.getText());
                if (nights > 0) {
                    lblTotal.setText(String.format("Total: Rp %,d", (long) price * nights));
                }
            } catch (Exception ex) {
                lblTotal.setText("Total: Rp 0");
            }
        };

        nightsField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotal.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotal.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotal.run(); }
        });
        roomCombo.addActionListener(e -> updateTotal.run());

        confirmBtn.addActionListener(e -> {
            String selected = (String) roomCombo.getSelectedItem();
            selectedRoomNo = selected.substring(0, 3);
            try {
                selectedNights = Integer.parseInt(nightsField.getText());
                if (selectedNights <= 0) throw new Exception();
                if (roomStatus.get(selectedRoomNo).equals("Tersedia")) {
                    roomStatus.put(selectedRoomNo, "Dipesan");
                    bookedRooms.add(selectedRoomNo);
                    bookedNights.put(selectedRoomNo, selectedNights); // SIMPAN JUMLAH MALAM
                    updateRoomTable();
                    JOptionPane.showMessageDialog(this,
                            "Booking Berhasil!\nKamar: " + selectedRoomNo +
                                    "\nMalam: " + selectedNights +
                                    "\nTotal: Rp " + String.format("%,d", (long) roomPrices.get(selectedRoomNo) * selectedNights));
                    cards.show(mainPanel, "DASH");
                } else {
                    JOptionPane.showMessageDialog(this, "Kamar sudah dipesan!", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Masukkan jumlah malam yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> cards.show(mainPanel, "DASH"));

        gbc.gridx = 0; gbc.gridy = 0; p.add(lblRoom, gbc);
        gbc.gridx = 1; p.add(roomCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(lblNights, gbc);
        gbc.gridx = 1; p.add(nightsField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; p.add(lblTotal, gbc);
        gbc.gridwidth = 1; gbc.gridy = 3; p.add(confirmBtn, gbc);
        gbc.gridx = 1; p.add(cancelBtn, gbc);

        return p;
    }

    private void updateRoomTable() {
        String[][] data = {
                {"101", "Single", String.format("Rp %,d", roomPrices.get("101")), roomStatus.get("101")},
                {"102", "Double", String.format("Rp %,d", roomPrices.get("102")), roomStatus.get("102")},
                {"103", "Suite",  String.format("Rp %,d", roomPrices.get("103")), roomStatus.get("103")}
        };
        String[] cols = {"No", "Tipe", "Harga", "Status"};
        roomTable.setModel(new DefaultTableModel(data, cols));
    }

    private void showBillingMenu() {
        String[] options = {"Check-in (Dipesan → Terisi)", "Check-out (Bayar & Kosongkan)", "Kembali"};
        int choice = JOptionPane.showOptionDialog(this, "Pilih opsi:", "Billing",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) showCheckinDialog();
        else if (choice == 1) showCheckoutDialog();
    }

    private void showCheckinDialog() {
        String roomNo = JOptionPane.showInputDialog(this, "Masukkan No. Kamar untuk Check-in:");
        if (roomNo != null && roomStatus.get(roomNo) != null && roomStatus.get(roomNo).equals("Dipesan")) {
            roomStatus.put(roomNo, "Terisi");
            updateRoomTable();
            JOptionPane.showMessageDialog(this, "Check-in berhasil! Kamar " + roomNo + " sekarang Terisi.");
        } else {
            JOptionPane.showMessageDialog(this, "Kamar tidak valid atau bukan 'Dipesan'!", "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CHECK-OUT: TOTAL = HARGA × JUMLAH MALAM
    private void showCheckoutDialog() {
        String roomNo = JOptionPane.showInputDialog(this, "Masukkan No. Kamar untuk Check-out:");
        if (roomNo == null || !roomStatus.containsKey(roomNo) || !roomStatus.get(roomNo).equals("Terisi")) {
            JOptionPane.showMessageDialog(this, "Kamar tidak valid atau bukan 'Terisi'!", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer nights = bookedNights.get(roomNo);
        if (nights == null) nights = 1; // fallback

        long pricePerNight = roomPrices.get(roomNo);
        long totalBill = pricePerNight * nights;

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Check-out Kamar %s\nMalam: %d\nHarga per malam: Rp %,d\nTOTAL TAGIHAN: Rp %,d\n\nKonfirmasi pembayaran?",
                        roomNo, nights, pricePerNight, totalBill),
                "Konfirmasi Check-out", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            totalRevenue += totalBill; // PENDAPATAN MASUK SAAT CHECK-OUT
            roomStatus.put(roomNo, "Tersedia");
            bookedRooms.remove(roomNo);
            bookedNights.remove(roomNo);
            updateRoomTable();
            JOptionPane.showMessageDialog(this, "Check-out berhasil!\nPendapatan +Rp " + String.format("%,d", totalBill));
        }
    }

    private void showRevenueReport() {
        JOptionPane.showMessageDialog(this,
                "LAPORAN PENDAPATAN HOTEL\n\nTotal Pendapatan (dari Check-out): Rp " + String.format("%,d", totalRevenue),
                "Laporan Pendapatan", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCancelDialog() {
        if (bookedRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada booking aktif.");
            return;
        }
        String[] options = bookedRooms.toArray(new String[0]);
        String choice = (String) JOptionPane.showInputDialog(this, "Pilih booking untuk dibatalkan:", "Cancel",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice != null && roomStatus.get(choice).equals("Dipesan")) {
            roomStatus.put(choice, "Tersedia");
            bookedRooms.remove(choice);
            bookedNights.remove(choice); // HAPUS DATA MALAM
            updateRoomTable();
            JOptionPane.showMessageDialog(this, "Booking " + choice + " dibatalkan.");
        }
    }

    @Override
    public void bookRoom() {
        cards.show(mainPanel, "BOOKING_PAGE");
    }

    @Override
    public void cancelBooking() {
        showCancelDialog();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelApp::new);
    }
}