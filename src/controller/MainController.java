package controller;

import model.*;
import view.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class MainController {
    private final MainView view;
    private final UserController userController;
    private final GameController gameController;
    private final UserDAO userDAO = new UserDAO();

    public MainController(MainView view) {
        this.view = view;
        this.userController = new UserController(view, userDAO);
        this.gameController = new GameController(view);
        setupListeners();
    }

    private void setupListeners() {
        // Set up window closing behavior to prompt for saving game state
        view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (gameController.getGameState() != null && !gameController.getGameState().isGameOver()) {
                    int choice = JOptionPane.showConfirmDialog(view, "Do you want to save the game before exiting?", "Save Game", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        gameController.saveGameState();
                        System.exit(0);
                    } else if (choice == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });

        // Login listener
        view.getAuthFrame().addLoginListener(e -> {
            String email = view.getAuthFrame().getEmail();
            String password = view.getAuthFrame().getPassword();

            if (email.isEmpty() || password.isEmpty()) {
                view.showMessage("fill the fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = userDAO.login(email, password);
            if (user != null) {
                view.setCurrentUser(user);
                gameController.setCurrentUser(user);
                view.setWelcomeMessage("Welcome, " + user.getUsername() + "!");
                view.showMainMenu();
            } else {
                view.showMessage("incorrect email | pass", "Error", JOptionPane.ERROR_MESSAGE);
            }
            view.getAuthFrame().clearFields();
        });

        // New game listener with save/load functionality
        view.addNewGameListener(e -> {
            if (view.getCurrentUser() == null) {
                view.showMessage("login first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AIDifficulty difficulty = view.getSelectedDifficulty();
            if (difficulty == null) {
                view.showMessage("select a difficulty level!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (gameController.hasSavedGame()) {
                int choice = JOptionPane.showConfirmDialog(view, "A saved game exists. Do you want to load it?", "Load Game", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    if (gameController.loadGameState()) {
                        GameView gameView = new GameView();
                        gameView.setController(gameController);
                        view.showGamePanel(gameView);
                        return;
                    } else {
                        view.showMessage("Failed to load saved game. Starting new game.", "Error", JOptionPane.ERROR_MESSAGE);
                        gameController.deleteSaveFile(); 
                    }
                } else {
                    gameController.deleteSaveFile(); 
                }
            }

            GameView gameView = new GameView();
            gameController.setDifficulty(difficulty);
            gameView.setController(gameController);
            view.showGamePanel(gameView);
        });

        // Stats listener
        view.addStatsListener(e -> {
            User user = view.getCurrentUser();
            if (user == null) {
                view.showMessage("login first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User updatedUser = userDAO.getUserStats(user.getUsername());
            if (updatedUser != null) {
                view.setCurrentUser(updatedUser);
                view.showStats(updatedUser);
            }
        });
    }
}