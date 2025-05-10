package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InscriFrame extends JPanel {
    private final JTextField usernameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;
    private final JButton registerButton;
    private Image backgroundImage;

    public InscriFrame() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;


            backgroundImage = new ImageIcon(getClass().getResource("/images/bg.png")).getImage();
      
        JLabel titleLabel = new JLabel("Register", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 255, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);


        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Verdana", Font.PLAIN, 16));
        usernameField.setBackground(new Color(50, 50, 50));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2));
        gbc.gridx = 1;
        add(usernameField, gbc);

    
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Verdana", Font.PLAIN, 16));
        emailField.setBackground(new Color(50, 50, 50));
        emailField.setForeground(Color.WHITE);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2));
        gbc.gridx = 1;
        add(emailField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Verdana", Font.PLAIN, 16));
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(Color.WHITE);
        confirmLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        add(confirmLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Verdana", Font.PLAIN, 16));
        confirmPasswordField.setBackground(new Color(50, 50, 50));
        confirmPasswordField.setForeground(Color.WHITE);
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2));
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);


        registerButton = createStyledButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(registerButton, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 18));
        button.setBackground(new Color(0, 150, 150));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2));
        button.setPreferredSize(new Dimension(200, 50));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 200, 200));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 150, 150));
            }
        });
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return String.valueOf(confirmPasswordField.getPassword());
    }

    public void addInscriptionListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}