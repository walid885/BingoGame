package server;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IBingoGame extends Serializable {
    boolean makeGuess(int playerId, int number) throws RemoteException;
    int startNewGame() throws RemoteException;
    int getHighestScore() throws RemoteException;
    GameStatus getGameStatus(int playerId) throws RemoteException;

    // Utilisez GameStatus du serveur
    enum GameStatus {
        IN_PROGRESS,
        COMPLETED,
        NOT_STARTED
    }
}
