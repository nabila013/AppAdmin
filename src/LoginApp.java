import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginApp extends JFrame {
    private JTextField usernameField, passwordField;

    public LoginApp() {
        setTitle("Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set latar belakang warna biru
        getContentPane().setBackground(Color.BLUE);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Set font Times New Roman
        Font timesNewRomanFont = new Font("Times New Roman", Font.PLAIN, 16);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(timesNewRomanFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(timesNewRomanFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(timesNewRomanFont);
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        getContentPane().add(loginPanel, BorderLayout.CENTER);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String (password = passwordField.getText());


        if (username.equals("admin123") && password.equals("admin123")) {
            // Login berhasil, buka aplikasi inventory
            new InventoryApp().setVisible(true);
            dispose(); // Tutup jendela login
        } else {
            JOptionPane.showMessageDialog(this, "Login gagal. Coba lagi.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Mengatur tampilan aplikasi dengan tampilan sistem yang lebih baik
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new LoginApp().setVisible(true);
            }
        });
    }
}
