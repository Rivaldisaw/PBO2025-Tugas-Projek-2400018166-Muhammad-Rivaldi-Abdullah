package view;

import manager.NotifikasiManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Dialog Notifikasi - Navy Theme
 */
public class NotifikasiDialog extends JDialog {
    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_ITEM = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);

    private NotifikasiManager notifikasiManager;

    public NotifikasiDialog(Frame parent, NotifikasiManager manager) {
        super(parent, "Notifikasi", true);
        this.notifikasiManager = manager;

        setupDialog();
        setupComponents();
    }

    private void setupDialog() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_CARD);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(new EmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("✕  NOTIFIKASI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
            }
        });
        header.add(titleLabel, BorderLayout.WEST);

        mainPanel.add(header, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        contentPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Summary
        String summary = notifikasiManager.getSummaryNotifikasi();
        if (!summary.equals("✅ Tidak ada notifikasi")) {
            JLabel summaryLabel = new JLabel(summary);
            summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            summaryLabel.setForeground(TEXT_PRIMARY);
            summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(summaryLabel);
            contentPanel.add(Box.createVerticalStrut(15));
        }

        // Notifikasi List
        List<String> notifikasi = notifikasiManager.getNotifikasiAktif();

        if (notifikasi.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setBackground(BG_ITEM);
            emptyPanel.setBorder(new EmptyBorder(40, 20, 40, 20));
            emptyPanel.setMaximumSize(new Dimension(460, 120));
            emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel emptyLabel = new JLabel("✓ Tidak ada notifikasi");
            emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subLabel = new JLabel("Semua jadwal terkendali");
            subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subLabel.setForeground(TEXT_SECONDARY);
            subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyLabel);
            emptyPanel.add(Box.createVerticalStrut(5));
            emptyPanel.add(subLabel);

            contentPanel.add(emptyPanel);
        } else {
            for (String notif : notifikasi) {
                contentPanel.add(createNotifCard(notif));
                contentPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createNotifCard(String notif) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_ITEM);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        card.setMaximumSize(new Dimension(460, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icon & Priority
        String icon;
        Color iconColor;

        if (notif.contains("TERLAMBAT")) {
            icon = "⚠️";
            iconColor = new Color(239, 68, 68);
        } else if (notif.contains("HARI INI")) {
            icon = "🔴";
            iconColor = new Color(239, 68, 68);
        } else if (notif.contains("BESOK")) {
            icon = "🟡";
            iconColor = new Color(251, 191, 36);
        } else {
            icon = "🟢";
            iconColor = new Color(34, 197, 94);
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Clean text (remove emoji from text)
        String cleanText = notif.replaceAll("[🔴🟡🟢⚠️📝📚📖]", "").trim();

        JTextArea textArea = new JTextArea(cleanText);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBackground(BG_ITEM);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textArea, BorderLayout.CENTER);

        return card;
    }
}