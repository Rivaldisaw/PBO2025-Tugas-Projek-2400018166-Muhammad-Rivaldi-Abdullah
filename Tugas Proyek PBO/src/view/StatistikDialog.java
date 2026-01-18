package view;

import manager.StatistikManager;
import view.components.BarChartPanel;
import view.components.PieChartPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Dialog Statistik dengan CHARTS! 📊
 */
public class StatistikDialog extends JDialog {
    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_ITEM = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);

    private StatistikManager statistikManager;

    public StatistikDialog(Frame parent, StatistikManager manager) {
        super(parent, "Statistik", true);
        this.statistikManager = manager;

        setupDialog();
        setupComponents();
    }

    private void setupDialog() {
        setSize(600, 900); // LEBIH TINGGI untuk chart
        setLocationRelativeTo(getParent());
        setResizable(true);
        setMinimumSize(new Dimension(550, 700));
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_CARD);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(new EmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("✕  STATISTIK & ANALISIS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
            }
        });
        header.add(titleLabel, BorderLayout.WEST);

        mainPanel.add(header, BorderLayout.NORTH);

        // Content with CHARTS
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        contentPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Quick Stats Cards
        JPanel statsCards = new JPanel(new GridLayout(1, 2, 15, 0));
        statsCards.setBackground(BG_CARD);
        statsCards.setMaximumSize(new Dimension(560, 100));
        statsCards.setAlignmentX(Component.LEFT_ALIGNMENT);

        double jamBelajar = statistikManager.hitungJamBelajarMingguIni();
        int produktivitas = statistikManager.analisisProduktivitas();

        statsCards.add(createStatCard(String.format("%.0f", jamBelajar), "Total Jam"));
        statsCards.add(createStatCard(produktivitas + "%", "Produktivitas"));

        contentPanel.add(statsCards);
        contentPanel.add(Box.createVerticalStrut(25));

        // 📊 BAR CHART - Jam Belajar Per Hari
        contentPanel.add(createSectionTitle("📊 Jam Belajar Per Hari (Minggu Ini)"));
        contentPanel.add(Box.createVerticalStrut(10));

        Map<String, Double> jamPerHari = statistikManager.hitungJamBelajarPerHari();
        BarChartPanel barChart = new BarChartPanel("Jam Belajar", jamPerHari, "h");
        barChart.setMaximumSize(new Dimension(560, 320));
        barChart.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(barChart);
        contentPanel.add(Box.createVerticalStrut(25));

        // 🥧 PIE CHART - Status Tugas
        contentPanel.add(createSectionTitle("🥧 Status Tugas"));
        contentPanel.add(Box.createVerticalStrut(10));

        Map<String, Integer> statusTugas = statistikManager.getStatusTugas();
        PieChartPanel pieChart = new PieChartPanel("Distribusi Status Tugas", statusTugas);
        pieChart.setMaximumSize(new Dimension(560, 370));
        pieChart.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(pieChart);
        contentPanel.add(Box.createVerticalStrut(25));

        // 📈 BAR CHART - Jam Belajar Per Mata Kuliah
        contentPanel.add(createSectionTitle("📈 Jam Belajar Per Mata Kuliah"));
        contentPanel.add(Box.createVerticalStrut(10));

        Map<String, Double> jamPerMK = statistikManager.getJamBelajarPerMataKuliah();
        if (!jamPerMK.isEmpty()) {
            // Limit to top 5
            Map<String, Double> top5 = jamPerMK.entrySet().stream()
                    .limit(5)
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            java.util.LinkedHashMap::new
                    ));

            BarChartPanel mkChart = new BarChartPanel("Mata Kuliah", top5, "h");
            mkChart.setMaximumSize(new Dimension(560, 320));
            mkChart.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(mkChart);
        } else {
            JLabel emptyLabel = new JLabel("Belum ada data mata kuliah");
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(emptyLabel);
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createStatCard(String value, String label) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_ITEM);
        card.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(TEXT_PRIMARY);

        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(TEXT_SECONDARY);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(labelText, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}