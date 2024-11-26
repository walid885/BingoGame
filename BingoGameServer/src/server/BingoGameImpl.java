package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class BingoGameImpl extends UnicastRemoteObject implements IBingoGame {
    private static final long serialVersionUID = 1L;
    private Map<Integer, Urn> playerUrns;
    private Map<Integer, Integer> playerScores;
    private ScoreManager scoreManager;
    private AtomicInteger playerIdCounter;

    public BingoGameImpl() throws RemoteException {
        playerUrns = new ConcurrentHashMap<>();
        playerScores = new ConcurrentHashMap<>();
        scoreManager = new ScoreManager();
        playerIdCounter = new AtomicInteger(0);
    }

    @Override
    public int startNewGame() throws RemoteException {
        int playerId = playerIdCounter.incrementAndGet();
        playerUrns.put(playerId, new Urn());
        playerScores.put(playerId, 0);
        return playerId;
    }

    @Override
    public boolean makeGuess(int playerId, int number) throws RemoteException {
        Urn urn = playerUrns.get(playerId);
        if (urn == null) {
            throw new RemoteException("Invalid player ID");
        }

        int drawnNumber = urn.drawBall();
        if (drawnNumber == number) {
            int currentScore = playerScores.get(playerId);
            playerScores.put(playerId, currentScore + 1);
            scoreManager.updateHighestScore(currentScore + 1); // Mise à jour du meilleur score
            return true;
        }
        return false;
    }

    @Override
    public int getHighestScore() throws RemoteException {
        return scoreManager.getHighestScore();
    }

    @Override
 // In your server-side implementation
    public String getGameStatus(int playerId) throws RemoteException {
    	// Return one of the status strings
    	if (gameIsNotStarted()) {
    	return IBingoGame.GameStatus.NOT_STARTED;
    	} else if (gameIsCompleted()) {
    	return IBingoGame.GameStatus.COMPLETED;
    	} else {
    	return IBingoGame.GameStatus.IN_PROGRESS;
    	}
    	}

    	private boolean gameIsCompleted() {
    	// TODO Auto-generated method stub
    	return false;
    	}

    	private boolean gameIsNotStarted() {
    	// TODO Auto-generated method stub
    	return false;
    	}

    
}