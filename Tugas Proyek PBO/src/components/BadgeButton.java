package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Custom Button dengan Badge Notifikasi
 * Badge muncul di pojok kanan atas dengan angka
 */
public class BadgeButton extends JButton {
    private int badgeCount = 0;
    private boolean showBadge = false;

    // Colors
    private static final Color BADGE_BG = new Color(239, 68, 68); // Red
    private static final Color BADGE_TEXT = Color.WHITE;

    public BadgeButton(String text) {
        super(text);
    }

    /**
     * Set badge count dan tampilkan badge
     */
    public void setBadgeCount(int count) {
        this.badgeCount = count;
        this.showBadge = count > 0;
        repaint();
    }

    /**
     * Get badge count
     */
    public int getBadgeCount() {
        return badgeCount;
    }

    /**
     * Hide badge
     */
    public void hideBadge() {
        this.showBadge = false;
        repaint();
    }

    /**
     * Show badge
     */
    public void showBadge() {
        this.showBadge = badgeCount > 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (showBadge && badgeCount > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculate badge size
            String badgeText = badgeCount > 99 ? "99+" : String.valueOf(badgeCount);
            Font badgeFont = new Font("Segoe UI", Font.BOLD, 10);
            g2.setFont(badgeFont);

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(badgeText);
            int badgeWidth = Math.max(18, textWidth + 8);
            int badgeHeight = 18;

            // Position badge at top-right corner
            int badgeX = getWidth() - badgeWidth - 2;
            int badgeY = 2;

            // Draw badge background (circle/pill shape)
            g2.setColor(BADGE_BG);
            if (badgeWidth > badgeHeight) {
                // Pill shape for numbers > 9
                g2.fillRoundRect(badgeX, badgeY, badgeWidth, badgeHeight, badgeHeight, badgeHeight);
            } else {
                // Circle for single digit
                g2.fill(new Ellipse2D.Double(badgeX, badgeY, badgeWidth, badgeHeight));
            }

            // Draw badge border (optional, for better contrast)
            g2.setColor(new Color(220, 38, 38)); // Darker red
            g2.setStroke(new BasicStroke(1.5f));
            if (badgeWidth > badgeHeight) {
                g2.drawRoundRect(badgeX, badgeY, badgeWidth, badgeHeight, badgeHeight, badgeHeight);
            } else {
                g2.draw(new Ellipse2D.Double(badgeX, badgeY, badgeWidth, badgeHeight));
            }

            // Draw badge text
            g2.setColor(BADGE_TEXT);
            int textX = badgeX + (badgeWidth - textWidth) / 2;
            int textY = badgeY + ((badgeHeight - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(badgeText, textX, textY);

            g2.dispose();
        }
    }

    /**
     * Tambahkan pulse animation saat badge muncul (optional)
     */
    public void pulse() {
        if (!showBadge) return;

        Timer timer = new Timer(50, null);
        final int[] scale = {0};

        timer.addActionListener(e -> {
            scale[0]++;
            if (scale[0] > 6) {
                timer.stop();
            }
            repaint();
        });

        timer.start();
    }
}