package view;

import manager.*;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import view.components.BadgeButton;

/**
 * MainFrame - Dashboard Utama
 * + SEARCH BOX REAL-TIME
 * + AUTO SAVE/LOAD
 */
public class MainFrame extends JFrame {
    // Colors
    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_SIDEBAR = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color TEXT_TERTIARY = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);
    private static final Color ACCENT_PRIMARY = new Color(59, 130, 246);
    private static final Color ACCENT_HOVER = new Color(96, 165, 250);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color DANGER = new Color(239, 68, 68);

    private JadwalManager jadwalManager;
    private NotifikasiManager notifikasiManager;
    private StatistikManager statistikManager;
    private JPanel kegiatanListPanel;
    private JPanel statsPanel;
    private JLabel dateHeaderLabel;
    private JTextField searchField; // 🔍 SEARCH BOX

    private JButton[] tabButtons;
    private String currentFilter = "Semua";
    private String currentSearchKeyword = ""; // 🔍 SEARCH KEYWORD

    private BadgeButton notifBadgeButton;

    public MainFrame() {
        jadwalManager = new JadwalManager(); // AUTO-LOAD data
        notifikasiManager = new NotifikasiManager(jadwalManager);
        statistikManager = new StatistikManager(jadwalManager);

        setupFrame();
        setupComponents();

        // Load sample data HANYA jika belum ada data
        if (jadwalManager.isEmpty()) {
            loadSampleData();
        }

        refreshKegiatanList();
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Jadwal Belajar");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1000, 600));
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_PRIMARY);

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel contentContainer = new JPanel(new BorderLayout(20, 0));
        contentContainer.setBackground(BG_PRIMARY);
        contentContainer.setBorder(new EmptyBorder(24, 24, 24, 24));

        // LEFT
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BG_PRIMARY);

        // 🔍 SEARCH BOX - TAMBAH DI SINI
        leftPanel.add(createSearchBox());
        leftPanel.add(Box.createVerticalStrut(16));

        // TAB NAVIGATION
        leftPanel.add(createTabNavigation());
        leftPanel.add(Box.createVerticalStrut(20));

        // Date header
        dateHeaderLabel = new JLabel(getDateHeaderText());
        dateHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dateHeaderLabel.setForeground(TEXT_SECONDARY);
        dateHeaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(dateHeaderLabel);
        leftPanel.add(Box.createVerticalStrut(16));

        // Kegiatan List
        kegiatanListPanel = new JPanel();
        kegiatanListPanel.setLayout(new BoxLayout(kegiatanListPanel, BoxLayout.Y_AXIS));
        kegiatanListPanel.setBackground(BG_PRIMARY);
        kegiatanListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(kegiatanListPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_PRIMARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(scrollPane);

        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(createAddButton());

        // RIGHT - Stats
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(BG_PRIMARY);
        statsPanel.setPreferredSize(new Dimension(300, 0));

        contentContainer.add(leftPanel, BorderLayout.CENTER);
        contentContainer.add(statsPanel, BorderLayout.EAST);

        mainPanel.add(contentContainer, BorderLayout.CENTER);
        add(mainPanel);
    }

    // 🔍 CREATE SEARCH BOX
    private JPanel createSearchBox() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BG_CARD);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        searchPanel.setMaximumSize(new Dimension(9999, 48));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBackground(BG_CARD);
        searchField.setBorder(null);
        searchField.setCaretColor(TEXT_PRIMARY);

        // Placeholder effect
        searchField.setText("Cari kegiatan...");
        searchField.setForeground(TEXT_TERTIARY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Cari kegiatan...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Cari kegiatan...");
                    searchField.setForeground(TEXT_TERTIARY);
                }
            }
        });

        // 🔥 REAL-TIME SEARCH - setiap ketik langsung filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void insertUpdate(DocumentEvent e) { search(); }

            private void search() {
                String text = searchField.getText();
                if (!text.equals("Cari kegiatan...")) {
                    currentSearchKeyword = text.toLowerCase();
                    refreshKegiatanList();
                }
            }
        });

        // Clear button
        JLabel clearBtn = new JLabel("✕");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        clearBtn.setForeground(TEXT_TERTIARY);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchField.setText("");
                currentSearchKeyword = "";
                searchField.requestFocus();
                refreshKegiatanList();
            }
        });

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(clearBtn, BorderLayout.EAST);

        return searchPanel;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel titleLabel = new JLabel("📚 Jadwal Belajar");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(BG_CARD);

        JButton exportBtn = createHeaderButton("💾 Export");
        JButton statistikBtn = createHeaderButton("📊 Statistik");

        // 🔔 BADGE BUTTON - GANTI JADI BadgeButton
        notifBadgeButton = new BadgeButton("🔔 Notifikasi");
        styleHeaderButton(notifBadgeButton); // Apply styling

        exportBtn.addActionListener(e -> exportData());
        statistikBtn.addActionListener(e -> openStatistik());
        notifBadgeButton.addActionListener(e -> openNotifikasi());

        buttonPanel.add(exportBtn);
        buttonPanel.add(statistikBtn);
        buttonPanel.add(notifBadgeButton);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);

        // 🔥 UPDATE BADGE COUNT
        updateNotificationBadge();

        return header;
    }

    private void styleHeaderButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(51, 65, 85)); // Navy 700
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(71, 85, 105));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_PRIMARY, 2),
                        new EmptyBorder(7, 15, 7, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(51, 65, 85));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                        new EmptyBorder(8, 16, 8, 16)
                ));
            }
        });
    }

    private void updateNotificationBadge() {
        if (notifBadgeButton != null) {
            notifikasiManager.cekDeadline();
            int jumlahNotif = notifikasiManager.getJumlahNotifikasi();

            if (jumlahNotif > 0) {
                notifBadgeButton.setBadgeCount(jumlahNotif);
                notifBadgeButton.pulse(); // Animasi pulse (optional)
            } else {
                notifBadgeButton.setBadgeCount(0);
            }
        }
    }

    private JPanel createTabNavigation() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(BG_PRIMARY);
        panel.setMaximumSize(new Dimension(9999, 40));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] tabs = {"Semua", "Hari Ini", "Minggu Ini", "Belajar", "Tugas", "Ujian"};
        tabButtons = new JButton[tabs.length];

        for (int i = 0; i < tabs.length; i++) {
            final String tab = tabs[i];
            tabButtons[i] = createTabButton(tab);
            tabButtons[i].addActionListener(e -> {
                currentFilter = tab;
                updateTabActiveState();
                updateDateHeader();
                refreshKegiatanList();
            });
            panel.add(tabButtons[i]);
        }

        updateTabActiveState();
        return panel;
    }

    private JButton createTabButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(BG_CARD);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        return btn;
    }

    private void updateTabActiveState() {
        for (JButton btn : tabButtons) {
            if (btn.getText().equals(currentFilter)) {
                btn.setBackground(ACCENT_PRIMARY);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(BG_CARD);
                btn.setForeground(TEXT_SECONDARY);
            }
        }
    }

    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(BG_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_CARD);
            }
        });

        return btn;
    }

    private JButton createAddButton() {
        JButton btn = new JButton("+ Tambah Kegiatan");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_PRIMARY);
        btn.setBorder(new EmptyBorder(14, 24, 14, 24));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(9999, 48));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_PRIMARY);
            }
        });

        btn.addActionListener(e -> openTambahKegiatan());

        return btn;
    }

    // 🔥 REFRESH WITH FILTER + SEARCH
    private void refreshKegiatanList() {
        kegiatanListPanel.removeAll();

        List<Kegiatan> kegiatanList = getFilteredKegiatan();

        if (!currentSearchKeyword.isEmpty()) {
            kegiatanList = kegiatanList.stream()
                    .filter(k -> k.getJudul().toLowerCase().contains(currentSearchKeyword) ||
                            getMataKuliah(k).toLowerCase().contains(currentSearchKeyword))
                    .collect(Collectors.toList());
        }

        if (kegiatanList.isEmpty()) {
            JPanel emptyPanel = createEmptyState();
            kegiatanListPanel.add(emptyPanel);
        } else {
            for (Kegiatan k : kegiatanList) {
                kegiatanListPanel.add(createKegiatanCard(k));
                kegiatanListPanel.add(Box.createVerticalStrut(12));
            }
        }

        kegiatanListPanel.revalidate();
        kegiatanListPanel.repaint();
        updateStats();

        // 🔥 UPDATE BADGE SETIAP KALI REFRESH
        updateNotificationBadge();
    }

    private String getMataKuliah(Kegiatan k) {
        if (k instanceof KegiatanBelajar) return ((KegiatanBelajar) k).getMataKuliah();
        if (k instanceof KegiatanTugas) return ((KegiatanTugas) k).getMataKuliah();
        if (k instanceof KegiatanUjian) return ((KegiatanUjian) k).getMataKuliah();
        return "";
    }

    private List<Kegiatan> getFilteredKegiatan() {
        switch (currentFilter) {
            case "Hari Ini":
                return jadwalManager.getKegiatanHariIni();
            case "Minggu Ini":
                return jadwalManager.getKegiatanMingguIni();
            case "Belajar":
                return (List) jadwalManager.getKegiatanBelajar();
            case "Tugas":
                return (List) jadwalManager.getKegiatanTugas();
            case "Ujian":
                return (List) jadwalManager.getKegiatanUjian();
            default:
                return jadwalManager.getDaftarKegiatan();
        }
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(60, 40, 60, 40));
        panel.setMaximumSize(new Dimension(9999, 200));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel(currentSearchKeyword.isEmpty() ? "📅" : "🔍");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel text = new JLabel(currentSearchKeyword.isEmpty() ?
                "Tidak ada kegiatan" : "Tidak ditemukan");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setForeground(TEXT_SECONDARY);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createVerticalStrut(12));
        panel.add(text);

        return panel;
    }

    private JPanel createKegiatanCard(Kegiatan k) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(9999, 120));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BG_CARD);
        leftPanel.setPreferredSize(new Dimension(80, 70));

        JLabel timeLabel = new JLabel(k.getWaktuMulai().toString());
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        timeLabel.setForeground(TEXT_PRIMARY);

        JPanel statusDot = new JPanel();
        statusDot.setPreferredSize(new Dimension(8, 8));
        statusDot.setMaximumSize(new Dimension(8, 8));
        if (k.getStatus().equals("Selesai")) {
            statusDot.setBackground(SUCCESS);
        } else if (k.getStatus().contains("Sedang")) {
            statusDot.setBackground(WARNING);
        } else {
            statusDot.setBackground(TEXT_TERTIARY);
        }

        leftPanel.add(timeLabel);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(statusDot);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);

        JLabel titleLabel = new JLabel(k.getJudul());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(6));

        String detail = getKegiatanDetail(k);
        JLabel detailLabel = new JLabel(detail);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailLabel.setForeground(TEXT_SECONDARY);
        contentPanel.add(detailLabel);

        if (k instanceof KegiatanTugas) {
            contentPanel.add(Box.createVerticalStrut(10));
            contentPanel.add(createProgressBar(((KegiatanTugas) k).getProgress()));
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(BG_CARD);
        rightPanel.setPreferredSize(new Dimension(90, 70));

        JButton editBtn = createActionButton("✏️", ACCENT_PRIMARY);
        JButton deleteBtn = createActionButton("🗑️", DANGER);

        editBtn.addActionListener(e -> editKegiatan(k));
        deleteBtn.addActionListener(e -> hapusKegiatan(k));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.add(editBtn);
        btnRow.add(deleteBtn);

        rightPanel.add(btnRow);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JButton createActionButton(String icon, Color color) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(color);
        btn.setBackground(BG_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        final Color hoverColor = color.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(hoverColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverColor, 1),
                        new EmptyBorder(6, 10, 6, 10)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(color);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 1),
                        new EmptyBorder(6, 10, 6, 10)
                ));
            }
        });

        return btn;
    }

    private void editKegiatan(Kegiatan k) {
        EditKegiatanDialog dialog = new EditKegiatanDialog(this, jadwalManager, k);
        dialog.setVisible(true);
        refreshKegiatanList();
    }

    private void hapusKegiatan(Kegiatan k) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hapus kegiatan \"" + k.getJudul() + "\"?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            jadwalManager.hapusKegiatan(k.getId());
            refreshKegiatanList();
            JOptionPane.showMessageDialog(this, "✔ Kegiatan berhasil dihapus!");
        }
    }

    private JPanel createProgressBar(int progress) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(BG_CARD);

        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_SIDEBAR);
                g2.fillRoundRect(0, 0, 240, 8, 8, 8);
                g2.setColor(ACCENT_PRIMARY);
                int width = (int)(240 * progress / 100.0);
                g2.fillRoundRect(0, 0, width, 8, 8, 8);
            }
        };
        bar.setPreferredSize(new Dimension(240, 8));
        bar.setBackground(BG_CARD);

        JLabel percentLabel = new JLabel("  " + progress + "%");
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        percentLabel.setForeground(TEXT_SECONDARY);

        panel.add(bar);
        panel.add(percentLabel);

        return panel;
    }

    private String getKegiatanDetail(Kegiatan k) {
        if (k instanceof KegiatanBelajar) {
            KegiatanBelajar kb = (KegiatanBelajar) k;
            return kb.getMataKuliah() + " • " + kb.getTopik();
        } else if (k instanceof KegiatanTugas) {
            KegiatanTugas kt = (KegiatanTugas) k;
            long sisaHari = kt.hitungSisaWaktu();
            return kt.getMataKuliah() + " • Deadline: " + (sisaHari > 0 ? sisaHari + " hari lagi" : "Hari ini!");
        } else if (k instanceof KegiatanUjian) {
            KegiatanUjian ku = (KegiatanUjian) k;
            return ku.getMataKuliah() + " • " + ku.getRuangan();
        }
        return "";
    }

    private String getCurrentDateString() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        return today.format(formatter);
    }

    private String getDateHeaderText() {
        switch (currentFilter) {
            case "Hari Ini":
                return getCurrentDateString();
            case "Minggu Ini":
                return "Minggu Ini";
            case "Belajar":
                return "Kegiatan Belajar";
            case "Tugas":
                return "Daftar Tugas";
            case "Ujian":
                return "Jadwal Ujian";
            default:
                return "Semua Kegiatan";
        }
    }

    private void updateDateHeader() {
        dateHeaderLabel.setText(getDateHeaderText());
    }

    private void updateStats() {
        statsPanel.removeAll();

        JLabel statsTitle = new JLabel("Statistik");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statsTitle.setForeground(TEXT_PRIMARY);
        statsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(statsTitle);
        statsPanel.add(Box.createVerticalStrut(20));

        double jamBelajar = statistikManager.hitungJamBelajarMingguIni();
        int produktivitas = statistikManager.analisisProduktivitas();
        var statusTugas = statistikManager.getStatusTugas();
        int totalTugas = statusTugas.values().stream().mapToInt(Integer::intValue).sum();
        int selesai = statusTugas.get("Selesai");

        statsPanel.add(createStatCard("⏰", "Total Jam Belajar", String.format("%.1f jam", jamBelajar), "Minggu ini"));
        statsPanel.add(Box.createVerticalStrut(12));
        statsPanel.add(createStatCard("🎯", "Produktivitas", produktivitas + "%", getLabelProduktivitas(produktivitas)));
        statsPanel.add(Box.createVerticalStrut(12));
        statsPanel.add(createStatCard("📝", "Tugas Selesai", selesai + "/" + totalTugas, totalTugas > 0 ? (selesai * 100 / totalTugas) + "% complete" : "Belum ada tugas"));

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private String getLabelProduktivitas(int skor) {
        if (skor >= 90) return "Sangat baik";
        else if (skor >= 75) return "Baik";
        else if (skor >= 60) return "Cukup";
        else return "Perlu ditingkatkan";
    }

    private JPanel createStatCard(String icon, String label, String value, String subtitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(300, 120));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(BG_CARD);
        headerPanel.setMaximumSize(new Dimension(300, 30));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        JLabel labelText = new JLabel("  " + label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(TEXT_SECONDARY);

        headerPanel.add(iconLabel);
        headerPanel.add(labelText);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueText.setForeground(TEXT_PRIMARY);
        valueText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleText = new JLabel(subtitle);
        subtitleText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleText.setForeground(TEXT_TERTIARY);
        subtitleText.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueText);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleText);

        return card;
    }

    private void openTambahKegiatan() {
        TambahKegiatanDialog dialog = new TambahKegiatanDialog(this, jadwalManager);
        dialog.setVisible(true);
        refreshKegiatanList();
    }

    private void openStatistik() {
        StatistikDialog dialog = new StatistikDialog(this, statistikManager);
        dialog.setVisible(true);
    }

    private void openNotifikasi() {
        notifikasiManager.cekDeadline();
        NotifikasiDialog dialog = new NotifikasiDialog(this, notifikasiManager);
        dialog.setVisible(true);

        // 🔥 RESET BADGE setelah user buka notifikasi
        updateNotificationBadge();
    }

    // 💾 EXPORT DATA
    private void exportData() {
        String[] options = {"CSV", "Backup TXT", "Batal"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Pilih format export:",
                "Export Data",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) { // CSV
            String fileName = "jadwal_export_" + LocalDate.now() + ".csv";
            if (jadwalManager.exportToCSV(fileName)) {
                JOptionPane.showMessageDialog(this,
                        "✔ Data berhasil di-export ke:\n" + fileName,
                        "Export Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (choice == 1) { // Backup
            String fileName = "jadwal_backup_" + LocalDate.now() + ".txt";
            if (jadwalManager.backupData(fileName)) {
                JOptionPane.showMessageDialog(this,
                        "✔ Backup berhasil dibuat:\n" + fileName,
                        "Backup Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void loadSampleData() {
        KegiatanBelajar b1 = new KegiatanBelajar(0, "Belajar Polimorfisme",
                LocalDate.now(), java.time.LocalTime.of(8, 0), java.time.LocalTime.of(10, 0),
                "Pemrograman Berorientasi Objek", "Polimorfisme & Abstract Class");
        b1.ubahStatus("Selesai");
        jadwalManager.tambahKegiatan(b1);

        KegiatanTugas t1 = new KegiatanTugas(0, "Tugas Basis Data",
                LocalDate.now(), java.time.LocalTime.of(13, 0), java.time.LocalTime.of(15, 0),
                "Basis Data", LocalDate.now().plusDays(3), "Tinggi");
        t1.updateProgress(65);
        jadwalManager.tambahKegiatan(t1);

        KegiatanBelajar b2 = new KegiatanBelajar(0, "Belajar Normalisasi",
                LocalDate.now(), java.time.LocalTime.of(19, 0), java.time.LocalTime.of(21, 0),
                "Basis Data", "Normalisasi Database");
        jadwalManager.tambahKegiatan(b2);

        updateStats();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}