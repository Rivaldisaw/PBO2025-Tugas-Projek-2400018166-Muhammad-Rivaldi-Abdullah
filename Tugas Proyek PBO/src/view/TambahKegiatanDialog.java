package view;

import manager.JadwalManager;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dialog Tambah Kegiatan - Modern Minimalist Design
 */
public class TambahKegiatanDialog extends JDialog {
    // Modern Color Palette - Navy Theme
    private static final Color BG_PRIMARY = new Color(15, 23, 42);      // Navy 900
    private static final Color BG_CARD = new Color(30, 41, 59);         // Navy 800
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252); // White-ish
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225); // Gray 300
    private static final Color ACCENT_PRIMARY = new Color(59, 130, 246); // Blue 500
    private static final Color ACCENT_HOVER = new Color(96, 165, 250);  // Blue 400
    private static final Color BORDER_COLOR = new Color(51, 65, 85);    // Navy 700

    private JadwalManager jadwalManager;
    private ButtonGroup jenisGroup;
    private JRadioButton belajarRadio, tugasRadio, ujianRadio;
    private JTextField judulField, mataKuliahField, topikField;
    private JTextField tanggalField, waktuMulaiField, waktuSelesaiField;
    private JTextField ruanganField, materiField, jenisUjianField;
    private JTextField deadlineField, prioritasField;
    private JPanel dynamicPanel;

    public TambahKegiatanDialog(Frame parent, JadwalManager manager) {
        super(parent, "Tambah Kegiatan", true);
        this.jadwalManager = manager;
        setupDialog();
        setupComponents();
    }

    private void setupDialog() {
        setSize(550, 700);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_CARD);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(24, 24, 20, 24)
        ));

        JLabel titleLabel = new JLabel("Tambah Kegiatan Baru");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel closeBtn = new JLabel("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        closeBtn.setForeground(TEXT_SECONDARY);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
            }
        });

        header.add(titleLabel, BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);
        formPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Jenis Kegiatan
        formPanel.add(createLabel("Jenis Kegiatan"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createJenisPanel());
        formPanel.add(Box.createVerticalStrut(20));

        // Judul
        formPanel.add(createLabel("Judul"));
        formPanel.add(Box.createVerticalStrut(8));
        judulField = createTextField("Masukkan judul kegiatan");
        formPanel.add(judulField);
        formPanel.add(Box.createVerticalStrut(20));

        // Mata Kuliah
        formPanel.add(createLabel("Mata Kuliah"));
        formPanel.add(Box.createVerticalStrut(8));
        mataKuliahField = createTextField("Contoh: Pemrograman Berorientasi Objek");
        formPanel.add(mataKuliahField);
        formPanel.add(Box.createVerticalStrut(20));

        // Tanggal & Waktu
        JPanel dateTimePanel = new JPanel(new GridLayout(1, 3, 12, 0));
        dateTimePanel.setBackground(BG_CARD);
        dateTimePanel.setMaximumSize(new Dimension(502, 80));
        dateTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tanggalPanel = new JPanel();
        tanggalPanel.setLayout(new BoxLayout(tanggalPanel, BoxLayout.Y_AXIS));
        tanggalPanel.setBackground(BG_CARD);
        tanggalPanel.add(createLabel("Tanggal"));
        tanggalPanel.add(Box.createVerticalStrut(8));
        tanggalField = createTextField("2026-01-17");
        tanggalField.setText(LocalDate.now().toString());
        tanggalPanel.add(tanggalField);

        JPanel mulaiPanel = new JPanel();
        mulaiPanel.setLayout(new BoxLayout(mulaiPanel, BoxLayout.Y_AXIS));
        mulaiPanel.setBackground(BG_CARD);
        mulaiPanel.add(createLabel("Mulai"));
        mulaiPanel.add(Box.createVerticalStrut(8));
        waktuMulaiField = createTextField("08:00");
        waktuMulaiField.setText("08:00");
        mulaiPanel.add(waktuMulaiField);

        JPanel selesaiPanel = new JPanel();
        selesaiPanel.setLayout(new BoxLayout(selesaiPanel, BoxLayout.Y_AXIS));
        selesaiPanel.setBackground(BG_CARD);
        selesaiPanel.add(createLabel("Selesai"));
        selesaiPanel.add(Box.createVerticalStrut(8));
        waktuSelesaiField = createTextField("10:00");
        waktuSelesaiField.setText("10:00");
        selesaiPanel.add(waktuSelesaiField);

        dateTimePanel.add(tanggalPanel);
        dateTimePanel.add(mulaiPanel);
        dateTimePanel.add(selesaiPanel);

        formPanel.add(dateTimePanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Dynamic Panel
        dynamicPanel = new JPanel();
        dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.Y_AXIS));
        dynamicPanel.setBackground(BG_CARD);
        dynamicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(dynamicPanel);

        updateDynamicFields("Belajar");

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(BG_CARD);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JButton batalBtn = createOutlineButton("Batal");
        JButton simpanBtn = createPrimaryButton("Simpan");

        batalBtn.addActionListener(e -> dispose());
        simpanBtn.addActionListener(e -> simpanKegiatan());

        buttonPanel.add(batalBtn);
        buttonPanel.add(simpanBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createJenisPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(BG_CARD);
        panel.setMaximumSize(new Dimension(502, 40));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        jenisGroup = new ButtonGroup();
        belajarRadio = createRadioButton("📖 Belajar");
        tugasRadio = createRadioButton("📝 Tugas");
        ujianRadio = createRadioButton("📚 Ujian");

        belajarRadio.setSelected(true);
        jenisGroup.add(belajarRadio);
        jenisGroup.add(tugasRadio);
        jenisGroup.add(ujianRadio);

        belajarRadio.addActionListener(e -> updateDynamicFields("Belajar"));
        tugasRadio.addActionListener(e -> updateDynamicFields("Tugas"));
        ujianRadio.addActionListener(e -> updateDynamicFields("Ujian"));

        panel.add(belajarRadio);
        panel.add(tugasRadio);
        panel.add(ujianRadio);

        return panel;
    }

    private void updateDynamicFields(String jenis) {
        dynamicPanel.removeAll();

        switch (jenis) {
            case "Belajar":
                dynamicPanel.add(createLabel("Topik Pembelajaran"));
                dynamicPanel.add(Box.createVerticalStrut(8));
                topikField = createTextField("Contoh: Polimorfisme & Abstract Class");
                dynamicPanel.add(topikField);
                break;

            case "Tugas":
                dynamicPanel.add(createLabel("Deadline"));
                dynamicPanel.add(Box.createVerticalStrut(8));
                deadlineField = createTextField("2026-01-20");
                deadlineField.setText(LocalDate.now().plusDays(7).toString());
                dynamicPanel.add(deadlineField);
                dynamicPanel.add(Box.createVerticalStrut(20));

                dynamicPanel.add(createLabel("Prioritas"));
                dynamicPanel.add(Box.createVerticalStrut(8));
                prioritasField = createTextField("Tinggi / Sedang / Rendah");
                prioritasField.setText("Sedang");
                dynamicPanel.add(prioritasField);
                break;

            case "Ujian":
                dynamicPanel.add(createLabel("Ruangan"));
                dynamicPanel.add(Box.createVerticalStrut(8));
                ruanganField = createTextField("Contoh: Lab 301");
                dynamicPanel.add(ruanganField);
                dynamicPanel.add(Box.createVerticalStrut(20));

                dynamicPanel.add(createLabel("Jenis Ujian"));
                dynamicPanel.add(Box.createVerticalStrut(8));
                jenisUjianField = createTextField("UTS / UAS / Quiz");
                dynamicPanel.add(jenisUjianField);
                dynamicPanel.add(Box.createVerticalStrut(20));

                dynamicPanel.add(createLabel("Materi Ujian"));
                dynamicPanel.add(Box.createVerticalStrut(8));
                materiField = createTextField("Contoh: Polimorfisme, Inheritance");
                dynamicPanel.add(materiField);
                break;
        }

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }

    private void simpanKegiatan() {
        try {
            String judul = judulField.getText().trim();
            String mataKuliah = mataKuliahField.getText().trim();
            LocalDate tanggal = LocalDate.parse(tanggalField.getText().trim());
            LocalTime waktuMulai = LocalTime.parse(waktuMulaiField.getText().trim());
            LocalTime waktuSelesai = LocalTime.parse(waktuSelesaiField.getText().trim());

            Kegiatan kegiatan = null;

            if (belajarRadio.isSelected()) {
                String topik = topikField.getText().trim();
                kegiatan = new KegiatanBelajar(0, judul, tanggal, waktuMulai, waktuSelesai, mataKuliah, topik);
            } else if (tugasRadio.isSelected()) {
                LocalDate deadline = LocalDate.parse(deadlineField.getText().trim());
                String prioritas = prioritasField.getText().trim();
                kegiatan = new KegiatanTugas(0, judul, tanggal, waktuMulai, waktuSelesai, mataKuliah, deadline, prioritas);
            } else if (ujianRadio.isSelected()) {
                String ruangan = ruanganField.getText().trim();
                String jenisUjian = jenisUjianField.getText().trim();
                String materi = materiField.getText().trim();
                kegiatan = new KegiatanUjian(0, judul, tanggal, waktuMulai, waktuSelesai, mataKuliah, ruangan, jenisUjian, materi);
            }

            if (kegiatan != null && jadwalManager.tambahKegiatan(kegiatan)) {
                JOptionPane.showMessageDialog(this,
                        "✓ Kegiatan berhasil ditambahkan!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Format tidak valid!\n\nContoh:\nTanggal: 2026-01-17\nWaktu: 08:00",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_CARD);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        field.setMaximumSize(new Dimension(502, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        radio.setForeground(TEXT_PRIMARY);
        radio.setBackground(BG_CARD);
        radio.setFocusPainted(false);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return radio;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_SECONDARY); // WARNA TEKS ABU-ABU (bukan putih!)
        btn.setBackground(BG_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 24, 10, 24)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true); // TAMBAH INI
        btn.setContentAreaFilled(true); // TAMBAH INI

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(51, 65, 85)); // Navy 700
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_CARD);
            }
        });

        return btn;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_PRIMARY);
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true); // TAMBAH INI
        btn.setContentAreaFilled(true); // TAMBAH INI

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_PRIMARY);
            }
        });

        return btn;
    }
}