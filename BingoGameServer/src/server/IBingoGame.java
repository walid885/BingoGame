package server;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;



public interface IBingoGame extends Serializable {
    boolean makeGuess(int playerId, int number) throws RemoteException;
    int startNewGame() throws RemoteException;
    int getHighestScore() throws RemoteException;

    String getGameStatus(int playerId) throws RemoteException;
    
    // Remove the enum or replace with constants
    class GameStatus {
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String COMPLETED = "COMPLETED";
        public static final String NOT_STARTED = "NOT_STARTED";

}
}
