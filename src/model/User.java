package model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private final String username;
    private String password;
    private String email;
    private int totalGames;
    private int wins;
    private int losses;
    private final Map<AIDifficulty, Integer> gamesByDifficulty = new HashMap<>();

    public User(String username, String password, String email) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Username, password, and email cannot be null or empty");
        }
        this.username = username;
        this.password = password;
        this.email = email;
        for (AIDifficulty difficulty : AIDifficulty.values()) {
            gamesByDifficulty.put(difficulty, 0);
        }
    }

    public void addGameResult(boolean won, AIDifficulty difficulty) {
        if (difficulty == null) return;
        totalGames++;
        if (won) wins++;
        else losses++;
        gamesByDifficulty.merge(difficulty, 1, Integer::sum);
    }

    public double getWinPercentage() {
        return totalGames == 0 ? 0 : (double) wins / totalGames * 100;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public int getTotalGames() { return totalGames; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public Map<AIDifficulty, Integer> getGamesByDifficulty() { return gamesByDifficulty; }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = email;
    }

    public void setTotalGames(int totalGames) { this.totalGames = Math.max(0, totalGames); }
    public void setWins(int wins) { this.wins = Math.max(0, wins); }
    public void setLosses(int losses) { this.losses = Math.max(0, losses); }
    public void setGamesByDifficulty(AIDifficulty difficulty, int count) {
        gamesByDifficulty.put(difficulty, Math.max(0, count));
    }
}