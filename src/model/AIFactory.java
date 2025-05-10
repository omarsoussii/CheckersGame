package model;

public class AIFactory {
    public static AI createAI(AIDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> new EasyAI();
            case MEDIUM -> new MediumAI();
            case HARD -> new HardAI();
        };
    }
}