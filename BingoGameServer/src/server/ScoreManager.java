package server;

public class ScoreManager {
    private int highestScore;

    public synchronized void updateHighestScore(int newScore) {
        if (newScore > highestScore) {
            highestScore = newScore;
        }
    }

    public int getHighestScore() {
        return highestScore;
    }

    public enum GameStatus {
        IN_PROGRESS,
        COMPLETED,
        NOT_STARTED
    }
}