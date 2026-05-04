package com.tonin.animaltrack.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.tonin.animaltrack.model.dto.UsuarioLoginDTO;
import com.tonin.animaltrack.service.AuthService;
import com.tonin.animaltrack.service.impl.AuthServiceImpl;

public class LoginWindow {

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
        frame.setBounds(100, 100, 430, 230);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(formPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        emailTF = new JTextField(24);
        gbc.gridx = 1;
        formPanel.add(emailTF, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Contraseña:"), gbc);

        passwordPF = new JPasswordField(24);
        gbc.gridx = 1;
        formPanel.add(passwordPF, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton loginButton = new JButton("Entrar");
        loginButton.setIcon(new ImageIcon(LoginWindow.class.getResource("/nuvola/16x16/1710_ok_yes_accept_green_ok_green_accept_yes.png")));
        loginButton.addActionListener(e -> doLogin());
        buttonPanel.add(loginButton);

        JButton exitButton = new JButton("Salir");
        exitButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(exitButton);

        frame.getRootPane().setDefaultButton(loginButton);
    }

    private void doLogin() {
        String email = emailTF.getText();
        String password = new String(passwordPF.getPassword());
        UsuarioLoginDTO user = authService.login(email, password);
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
