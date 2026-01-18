import view.MainFrame;
import javax.swing.*;

/**
 * MainApp - Entry Point untuk Aplikasi GUI
 * Jalankan file ini untuk membuka aplikasi
 */
public class MainApp {
    public static void main(String[] args) {
        // Set Look and Feel (opsional - untuk tampilan lebih modern)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run aplikasi di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}