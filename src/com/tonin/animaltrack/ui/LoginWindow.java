package com.tonin.animaltrack.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tonin.animaltrack.model.dto.UsuarioLoginDTO;
import com.tonin.animaltrack.service.AuthService;
import com.tonin.animaltrack.service.impl.AuthServiceImpl;

public class LoginWindow {

    private static Logger logger = LogManager.getLogger(LoginWindow.class.getName());

    private JFrame frame;
    private JTextField emailTF;
    private JPasswordField passwordPF;
    private AuthService authService;

    public LoginWindow() {
        this.authService = new AuthServiceImpl();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("AnimalTrack - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 620, 520);
        frame.setMinimumSize(new Dimension(560, 480));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(245, 247, 248));
        frame.getContentPane().setLayout(new GridBagLayout());

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 226, 229)),
                BorderFactory.createEmptyBorder(28, 34, 26, 34)));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.insets = new Insets(24, 24, 24, 24);
        frame.getContentPane().add(cardPanel, cardGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel logoLabel = new JLabel(new ImageIcon(new ImageIcon(LoginWindow.class.getResource("/animaltrack/logo-256.png"))
                .getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 18, 8);
        cardPanel.add(logoLabel, gbc);

        JLabel titleLabel = new JLabel("Inicio de sesión");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setForeground(new Color(0, 83, 95));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 8, 18, 8);
        cardPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 8, 8, 8);
        cardPanel.add(new JLabel("Email:"), gbc);

        emailTF = new JTextField(24);
        emailTF.setPreferredSize(new Dimension(280, 28));
        gbc.gridx = 1;
        gbc.weightx = 1;
        cardPanel.add(emailTF, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        cardPanel.add(new JLabel("Contraseña:"), gbc);

        passwordPF = new JPasswordField(24);
        passwordPF.setPreferredSize(new Dimension(280, 28));
        gbc.gridx = 1;
        gbc.weightx = 1;
        cardPanel.add(passwordPF, gbc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.insets = new Insets(0, 4, 0, 4);
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;

        JButton loginButton = new JButton("Entrar");
        loginButton.setPreferredSize(new Dimension(118, 32));
        loginButton.setIcon(new ImageIcon(LoginWindow.class.getResource("/animaltrack/icons/16/login.png")));
        loginButton.addActionListener(e -> doLogin());
        buttonGbc.gridx = 0;
        buttonPanel.add(loginButton, buttonGbc);

        JButton exitButton = new JButton("Salir");
        exitButton.setPreferredSize(new Dimension(118, 32));
        exitButton.addActionListener(e -> frame.dispose());
        buttonGbc.gridx = 1;
        buttonPanel.add(exitButton, buttonGbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 0, 8);
        cardPanel.add(buttonPanel, gbc);

        frame.getRootPane().setDefaultButton(loginButton);
    }

    private void doLogin() {
        String email = emailTF.getText();
        String password = new String(passwordPF.getPassword());
        UsuarioLoginDTO user = null;
        try {
            user = authService.login(email, password);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(frame, "No se pudo iniciar sesion.", "Login", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (user == null) {
            JOptionPane.showMessageDialog(frame, "Email o contraseña incorrectos.", "Login",
                    JOptionPane.WARNING_MESSAGE);
            passwordPF.setText("");
            passwordPF.requestFocusInWindow();
            return;
        }

        MainWindow mainWindow = MainWindow.getInstance();
        mainWindow.setCurrentUser(user);
        frame.dispose();
        SwingUtilities.invokeLater(mainWindow::showWindow);
    }

    public void showWindow() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
