package server;

import java.rmi.RemoteException;

public class ScoreManager  {
    private int highestScore;

    public synchronized void updateHighestScore(int newScore) {
        if (newScore > highestScore) {
            highestScore = newScore;
        }
    }

    public int getHighestScore() {
        return highestScore;
    }



}