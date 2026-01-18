package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

/**
 * Custom Bar Chart Component
 * Menampilkan bar chart dengan style modern
 */
public class BarChartPanel extends JPanel {
    private Map<String, Double> data;
    private String title;
    private String unit;

    // Colors - Navy Theme
    private static final Color BG_COLOR = new Color(51, 65, 85);      // Navy 700
    private static final Color BAR_COLOR = new Color(59, 130, 246);   // Blue 500
    private static final Color BAR_HOVER = new Color(96, 165, 250);   // Blue 400
    private static final Color TEXT_COLOR = new Color(248, 250, 252); // White
    private static final Color LABEL_COLOR = new Color(203, 213, 225); // Gray
    private static final Color GRID_COLOR = new Color(71, 85, 105);   // Navy 600

    private int hoveredBarIndex = -1;

    public BarChartPanel(String title, Map<String, Double> data, String unit) {
        this.title = title;
        this.data = data;
        this.unit = unit;

        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(500, 300));

        // Mouse hover effect
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                updateHoveredBar(evt.getX(), evt.getY());
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hoveredBarIndex = -1;
                repaint();
            }
        });
    }

    private void updateHoveredBar(int mouseX, int mouseY) {
        if (data == null || data.isEmpty()) return;

        int padding = 50;
        int chartWidth = getWidth() - 2 * padding;
        int chartHeight = getHeight() - 2 * padding - 30;

        int barCount = data.size();
        int barWidth = Math.max(30, (chartWidth - (barCount - 1) * 20) / barCount);
        int spacing = 20;

        int oldHoveredIndex = hoveredBarIndex;
        hoveredBarIndex = -1;

        int index = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            int barX = padding + index * (barWidth + spacing);

            if (mouseX >= barX && mouseX <= barX + barWidth) {
                hoveredBarIndex = index;
                break;
            }
            index++;
        }

        if (oldHoveredIndex != hoveredBarIndex) {
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data == null || data.isEmpty()) {
            drawEmptyState(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(TEXT_COLOR);
        g2.drawString(title, 20, 25);

        // Chart area
        int padding = 50;
        int chartWidth = getWidth() - 2 * padding;
        int chartHeight = getHeight() - 2 * padding - 30;
        int chartTop = padding + 30;

        // Find max value
        double maxValue = data.values().stream().max(Double::compare).orElse(1.0);
        if (maxValue == 0) maxValue = 1.0;

        // Draw grid lines
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0));
        for (int i = 0; i <= 5; i++) {
            int y = chartTop + (chartHeight * i / 5);
            g2.drawLine(padding, y, padding + chartWidth, y);

            // Value labels
            double value = maxValue * (5 - i) / 5.0;
            g2.setColor(LABEL_COLOR);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(String.format("%.1f%s", value, unit), padding - 45, y + 4);
        }

        // Draw bars
        int barCount = data.size();
        int barWidth = Math.max(30, (chartWidth - (barCount - 1) * 20) / barCount);
        int spacing = 20;

        int index = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String label = entry.getKey();
            double value = entry.getValue();

            // Calculate bar height
            int barHeight = (int) ((value / maxValue) * chartHeight);
            int barX = padding + index * (barWidth + spacing);
            int barY = chartTop + chartHeight - barHeight;

            // Draw bar with rounded top
            Color barColor = (index == hoveredBarIndex) ? BAR_HOVER : BAR_COLOR;
            g2.setColor(barColor);

            RoundRectangle2D bar = new RoundRectangle2D.Double(
                    barX, barY, barWidth, barHeight, 8, 8
            );
            g2.fill(bar);

            // Bar border (subtle)
            g2.setColor(barColor.darker());
            g2.setStroke(new BasicStroke(1));
            g2.draw(bar);

            // Value on top of bar (if hovered)
            if (index == hoveredBarIndex) {
                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String valueText = String.format("%.1f%s", value, unit);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(valueText);
                g2.drawString(valueText, barX + (barWidth - textWidth) / 2, barY - 8);
            }

            // Label below bar
            g2.setColor(LABEL_COLOR);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();

            // Rotate label jika terlalu panjang
            if (label.length() > 8) {
                Graphics2D g2d = (Graphics2D) g2.create();
                g2d.translate(barX + barWidth / 2, chartTop + chartHeight + 15);
                g2d.rotate(-Math.PI / 4);
                g2d.drawString(label, 0, 0);
                g2d.dispose();
            } else {
                int textWidth = fm.stringWidth(label);
                g2.drawString(label, barX + (barWidth - textWidth) / 2, chartTop + chartHeight + 20);
            }

            index++;
        }
    }

    private void drawEmptyState(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(LABEL_COLOR);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String msg = "Belum ada data untuk ditampilkan";
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(msg);
        g2.drawString(msg, (getWidth() - textWidth) / 2, getHeight() / 2);
    }

    /**
     * Update data chart
     */
    public void updateData(Map<String, Double> newData) {
        this.data = newData;
        repaint();
    }
}