package controller;

import model.User;
import model.UserDAO;
import view.AuthFrame;
import view.InscriFrame;
import view.MainView;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class UserController {
    private final UserDAO userDAO;
    private final InscriFrame inscriptionFrame;
    private final AuthFrame loginFrame;
    private final MainView mainView;

    public UserController(MainView mainView, UserDAO userDAO) {
        this.mainView = mainView;
        this.userDAO = userDAO;
        this.inscriptionFrame = mainView.getInscriFrame();
        this.loginFrame = mainView.getAuthFrame();
        setupListeners();
    }

    private void setupListeners() {
        inscriptionFrame.addInscriptionListener(e -> {
            String username = inscriptionFrame.getUsername();
            String email = inscriptionFrame.getEmail();
            String password = inscriptionFrame.getPassword();
            String confirmPass = inscriptionFrame.getConfirmPassword();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                inscriptionFrame.showMessage("fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPass)) {
                inscriptionFrame.showMessage("Password incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (userDAO.userExists(username, email)) {
                    inscriptionFrame.showMessage("username | email exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                User newUser = new User(username, password, email);
                if (userDAO.inscrire(newUser)) {
                    inscriptionFrame.showMessage("account created", "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainView.cardLayout.show(mainView.cards, "auth");
                } else {
                    inscriptionFrame.showMessage("failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                inscriptionFrame.showMessage("db error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

    
        loginFrame.addGoToInscriListener(e -> mainView.cardLayout.show(mainView.cards, "register"));


        loginFrame.addQuitListener(e -> {
            if (JOptionPane.showConfirmDialog(mainView, "sure wanna quit ? ", 
                    "Confirm Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    public class InscriptionListener implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
        }
    }
}