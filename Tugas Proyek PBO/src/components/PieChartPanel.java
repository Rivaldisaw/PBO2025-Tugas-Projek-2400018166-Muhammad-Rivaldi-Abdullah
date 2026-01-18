package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * Custom Pie Chart Component
 * Menampilkan pie chart dengan legend
 */
public class PieChartPanel extends JPanel {
    private Map<String, Integer> data;
    private String title;

    // Colors untuk setiap segment
    private static final Color[] COLORS = {
            new Color(34, 197, 94),   // Green - Selesai
            new Color(59, 130, 246),  // Blue - Sedang Dikerjakan
            new Color(148, 163, 184), // Gray - Belum Mulai
            new Color(239, 68, 68)    // Red - Terlambat
    };

    private static final Color BG_COLOR = new Color(51, 65, 85);
    private static final Color TEXT_COLOR = new Color(248, 250, 252);
    private static final Color LABEL_COLOR = new Color(203, 213, 225);

    private int hoveredSegment = -1;

    public PieChartPanel(String title, Map<String, Integer> data) {
        this.title = title;
        this.data = data;

        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(500, 350));

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                updateHoveredSegment(evt.getX(), evt.getY());
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hoveredSegment = -1;
                repaint();
            }
        });
    }

    private void updateHoveredSegment(int mouseX, int mouseY) {
        if (data == null || data.isEmpty()) return;

        int centerX = 150;
        int centerY = getHeight() / 2;
        int radius = 100;

        // Check if mouse is inside circle
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > radius) {
            hoveredSegment = -1;
            repaint();
            return;
        }

        // Calculate angle
        double angle = Math.atan2(dy, dx);
        if (angle < 0) angle += 2 * Math.PI;
        double degrees = Math.toDegrees(angle);
        if (degrees < 0) degrees += 360;

        // Adjust for start at top (270 degrees)
        degrees = (degrees + 90) % 360;

        // Find segment
        int total = data.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) {
            hoveredSegment = -1;
            repaint();
            return;
        }

        double currentAngle = 0;
        int index = 0;
        int oldHoveredSegment = hoveredSegment;
        hoveredSegment = -1;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            double segmentAngle = 360.0 * entry.getValue() / total;

            if (degrees >= currentAngle && degrees < currentAngle + segmentAngle) {
                hoveredSegment = index;
                break;
            }

            currentAngle += segmentAngle;
            index++;
        }

        if (oldHoveredSegment != hoveredSegment) {
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

        // Calculate total
        int total = data.values().stream().mapToInt(Integer::intValue).sum();

        if (total == 0) {
            drawEmptyState(g);
            return;
        }

        // Pie chart dimensions
        int centerX = 150;
        int centerY = getHeight() / 2;
        int diameter = 200;
        int radius = diameter / 2;

        // Draw pie segments
        double currentAngle = 270; // Start from top
        int index = 0;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String label = entry.getKey();
            int value = entry.getValue();

            if (value == 0) {
                index++;
                continue;
            }

            double arcAngle = 360.0 * value / total;

            // Explode effect for hovered segment
            int offsetX = 0;
            int offsetY = 0;
            if (index == hoveredSegment) {
                double midAngle = Math.toRadians(currentAngle + arcAngle / 2);
                offsetX = (int) (10 * Math.cos(midAngle));
                offsetY = (int) (10 * Math.sin(midAngle));
            }

            // Draw arc
            Arc2D arc = new Arc2D.Double(
                    centerX - radius + offsetX,
                    centerY - radius + offsetY,
                    diameter,
                    diameter,
                    currentAngle,
                    arcAngle,
                    Arc2D.PIE
            );

            Color segmentColor = COLORS[index % COLORS.length];
            g2.setColor(segmentColor);
            g2.fill(arc);

            // Border
            g2.setColor(BG_COLOR);
            g2.setStroke(new BasicStroke(2));
            g2.draw(arc);

            // Percentage label in segment (if big enough)
            if (arcAngle > 20) {
                double midAngle = Math.toRadians(currentAngle + arcAngle / 2);
                int labelX = (int) (centerX + offsetX + (radius * 0.6) * Math.cos(midAngle));
                int labelY = (int) (centerY + offsetY + (radius * 0.6) * Math.sin(midAngle));

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String percentage = String.format("%.0f%%", (value * 100.0 / total));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(percentage);
                g2.drawString(percentage, labelX - textWidth / 2, labelY + fm.getAscent() / 2);
            }

            currentAngle += arcAngle;
            index++;
        }

        // Draw legend
        drawLegend(g2, total);

        // Draw center circle (donut effect - optional)
        int holeRadius = 40;
        g2.setColor(BG_COLOR);
        g2.fillOval(centerX - holeRadius, centerY - holeRadius, holeRadius * 2, holeRadius * 2);

        // Total in center
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        String totalText = String.valueOf(total);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(totalText);
        g2.drawString(totalText, centerX - textWidth / 2, centerY + fm.getAscent() / 2);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(LABEL_COLOR);
        String totalLabel = "Total";
        textWidth = g2.getFontMetrics().stringWidth(totalLabel);
        g2.drawString(totalLabel, centerX - textWidth / 2, centerY + 20);
    }

    private void drawLegend(Graphics2D g2, int total) {
        int legendX = 320;
        int legendY = 60;
        int legendItemHeight = 30;

        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String label = entry.getKey();
            int value = entry.getValue();

            // Color box
            g2.setColor(COLORS[index % COLORS.length]);
            g2.fillRect(legendX, legendY + index * legendItemHeight, 15, 15);

            // Border
            g2.setColor(LABEL_COLOR);
            g2.drawRect(legendX, legendY + index * legendItemHeight, 15, 15);

            // Label
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString(label, legendX + 22, legendY + index * legendItemHeight + 12);

            // Value
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(TEXT_COLOR);
            String valueText = String.format("%d (%.0f%%)", value, (value * 100.0 / total));
            g2.drawString(valueText, legendX + 22, legendY + index * legendItemHeight + 26);

            g2.setColor(LABEL_COLOR);
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
    public void updateData(Map<String, Integer> newData) {
        this.data = newData;
        repaint();
    }
}