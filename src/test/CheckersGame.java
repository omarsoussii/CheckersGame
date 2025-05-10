package test;

import javax.swing.SwingUtilities;

import controller.MainController;
import view.MainView;

public class CheckersGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            new MainController(view);
            view.setVisible(true);
        });
    }
}