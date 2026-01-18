package view;

import manager.JadwalManager;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dialog Edit Kegiatan
 */
public class EditKegiatanDialog extends JDialog {
    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color ACCENT_PRIMARY = new Color(59, 130, 246);
    private static final Color ACCENT_HOVER = new Color(96, 165, 250);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);

    private JadwalManager jadwalManager;
    private Kegiatan kegiatan;
    private JTextField judulField, mataKuliahField;
    private JTextField tanggalField, waktuMulaiField, waktuSelesaiField;
    private JTextField topikField, deadlineField, prioritasField, progressField;
    private JTextField ruanganField, materiField, jenisUjianField;
    private JComboBox<String> statusCombo;
    private JPanel dynamicPanel;

    public EditKegiatanDialog(Frame parent, JadwalManager manager, Kegiatan k) {
        super(parent, "Edit Kegiatan", true);
        this.jadwalManager = manager;
        this.kegiatan = k;
        setupDialog();
        setupComponents();
        loadData();
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

        JLabel titleLabel = new JLabel("Edit Kegiatan");
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

        // Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);
        formPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Judul
        formPanel.add(createLabel("Judul"));
        formPanel.add(Box.createVerticalStrut(8));
        judulField = createTextField("");
        formPanel.add(judulField);
        formPanel.add(Box.createVerticalStrut(20));

        // Mata Kuliah
        formPanel.add(createLabel("Mata Kuliah"));
        formPanel.add(Box.createVerticalStrut(8));
        mataKuliahField = createTextField("");
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
        tanggalField = createTextField("");
        tanggalPanel.add(tanggalField);

        JPanel mulaiPanel = new JPanel();
        mulaiPanel.setLayout(new BoxLayout(mulaiPanel, BoxLayout.Y_AXIS));
        mulaiPanel.setBackground(BG_CARD);
        mulaiPanel.add(createLabel("Mulai"));
        mulaiPanel.add(Box.createVerticalStrut(8));
        waktuMulaiField = createTextField("");
        mulaiPanel.add(waktuMulaiField);

        JPanel selesaiPanel = new JPanel();
        selesaiPanel.setLayout(new BoxLayout(selesaiPanel, BoxLayout.Y_AXIS));
        selesaiPanel.setBackground(BG_CARD);
        selesaiPanel.add(createLabel("Selesai"));
        selesaiPanel.add(Box.createVerticalStrut(8));
        waktuSelesaiField = createTextField("");
        selesaiPanel.add(waktuSelesaiField);

        dateTimePanel.add(tanggalPanel);
        dateTimePanel.add(mulaiPanel);
        dateTimePanel.add(selesaiPanel);

        formPanel.add(dateTimePanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Status
        formPanel.add(createLabel("Status"));
        formPanel.add(Box.createVerticalStrut(8));
        statusCombo = new JComboBox<>(new String[]{"Belum Mulai", "Sedang Berjalan", "Selesai"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusCombo.setBackground(BG_CARD);
        statusCombo.setForeground(TEXT_PRIMARY);
        statusCombo.setMaximumSize(new Dimension(502, 42));
        statusCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(statusCombo);
        formPanel.add(Box.createVerticalStrut(20));

        // Dynamic fields
        dynamicPanel = new JPanel();
        dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.Y_AXIS));
        dynamicPanel.setBackground(BG_CARD);
        dynamicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(dynamicPanel);

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
        simpanBtn.addActionListener(e -> simpanPerubahan());

        buttonPanel.add(batalBtn);
        buttonPanel.add(simpanBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadData() {
        judulField.setText(kegiatan.getJudul());
        tanggalField.setText(kegiatan.getTanggal().toString());
        waktuMulaiField.setText(kegiatan.getWaktuMulai().toString());
        waktuSelesaiField.setText(kegiatan.getWaktuSelesai().toString());
        statusCombo.setSelectedItem(kegiatan.getStatus());

        if (kegiatan instanceof KegiatanBelajar) {
            KegiatanBelajar kb = (KegiatanBelajar) kegiatan;
            mataKuliahField.setText(kb.getMataKuliah());

            dynamicPanel.add(createLabel("Topik"));
            dynamicPanel.add(Box.createVerticalStrut(8));
            topikField = createTextField(kb.getTopik());
            dynamicPanel.add(topikField);
        } else if (kegiatan instanceof KegiatanTugas) {
            KegiatanTugas kt = (KegiatanTugas) kegiatan;
            mataKuliahField.setText(kt.getMataKuliah());

            dynamicPanel.add(createLabel("Deadline"));
            dynamicPanel.add(Box.createVerticalStrut(8));
            deadlineField = createTextField(kt.getDeadline().toString());
            dynamicPanel.add(deadlineField);
            dynamicPanel.add(Box.createVerticalStrut(20));

            dynamicPanel.add(createLabel("Prioritas"));
            dynamicPanel.add(Box.createVerticalStrut(8));
            prioritasField = createTextField(kt.getPrioritas());
            dynamicPanel.add(prioritasField);
            dynamicPanel.add(Box.createVerticalStrut(20));

            dynamicPanel.add(createLabel("Progress (0-100)"));
            dynamicPanel.add(Box.createVerticalStrut(8));
            progressField = createTextField(String.valueOf(kt.getProgress()));
            dynamicPanel.add(progressField);
        } else if (kegiatan instanceof KegiatanUjian) {
            KegiatanUjian ku = (KegiatanUjian) kegiatan;
            mataKuliahField.setText(ku.getMataKuliah());

            dynamicPanel.add(createLabel("Ruangan"));
            dynamicPanel.add(Box.createVerticalStrut(8));
            ruanganField = createTextField(ku.getRuangan());
            dynamicPanel.add(ruanganField);
            dynamicPanel.add(Box.createVerticalStrut(20));

            dynamicPanel.add(createLabel("Jenis Ujian"));
            dynamicPanel.add(Box.createVerticalStrut(8));
            jenisUjianField = createTextField(ku.getJenisUjian());
            dynamicPanel.add(jenisUjianField);
            dynamicPanel.add(Box.createVerticalStrut(20));

            dynamicPanel.add(createLabel("Materi"));
            dynamicPanel.add(Box.createVerticalStrut(8));
            materiField = createTextField(ku.getMateriUjian());
            dynamicPanel.add(materiField);
        }
    }

    private void simpanPerubahan() {
        try {
            String judul = judulField.getText().trim();
            String mataKuliah = mataKuliahField.getText().trim();
            LocalDate tanggal = LocalDate.parse(tanggalField.getText().trim());
            LocalTime waktuMulai = LocalTime.parse(waktuMulaiField.getText().trim());
            LocalTime waktuSelesai = LocalTime.parse(waktuSelesaiField.getText().trim());
            String status = (String) statusCombo.getSelectedItem();

            Kegiatan kegiatanBaru = null;

            if (kegiatan instanceof KegiatanBelajar) {
                String topik = topikField.getText().trim();
                kegiatanBaru = new KegiatanBelajar(kegiatan.getId(), judul, tanggal, waktuMulai, waktuSelesai, mataKuliah, topik);
            } else if (kegiatan instanceof KegiatanTugas) {
                LocalDate deadline = LocalDate.parse(deadlineField.getText().trim());
                String prioritas = prioritasField.getText().trim();
                int progress = Integer.parseInt(progressField.getText().trim());
                KegiatanTugas kt = new KegiatanTugas(kegiatan.getId(), judul, tanggal, waktuMulai, waktuSelesai, mataKuliah, deadline, prioritas);
                kt.updateProgress(progress);
                kegiatanBaru = kt;
            } else if (kegiatan instanceof KegiatanUjian) {
                String ruangan = ruanganField.getText().trim();
                String jenisUjian = jenisUjianField.getText().trim();
                String materi = materiField.getText().trim();
                kegiatanBaru = new KegiatanUjian(kegiatan.getId(), judul, tanggal, waktuMulai, waktuSelesai, mataKuliah, ruangan, jenisUjian, materi);
            }

            if (kegiatanBaru != null) {
                kegiatanBaru.ubahStatus(status);
                jadwalManager.editKegiatan(kegiatan.getId(), kegiatanBaru);
                JOptionPane.showMessageDialog(this, "✓ Kegiatan berhasil diupdate!");
                dispose();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
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

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(BG_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 24, 10, 24)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
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
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        return btn;
    }
}