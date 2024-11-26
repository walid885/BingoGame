package gateway;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;
import server.IBingoGame;

public class ClientHandler implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private server.IBingoGame bingoGame;
    private int playerId;
    private int score;
    private Date playDate;

    public ClientHandler(Socket socket, IBingoGame bingoGame) {
        this.clientSocket = socket;
        this.bingoGame = bingoGame;
    }

    @Override
    public void run() {
        try {
            // CRITICAL: Create output stream first and flush
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            
            // Then create input stream
            in = new ObjectInputStream(clientSocket.getInputStream());

            while (!clientSocket.isClosed()) {
                try {
                    Object commandObj = in.readObject();
                    if (commandObj instanceof String) {
                        String command = (String) commandObj;
                        processCommand(command);
                    } else {
                        System.err.println("Unexpected object type: " + commandObj.getClass());
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Class not found: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void processCommand(String command) throws IOException {
        try {
            switch (command) {
                case "START_GAME":
                    playerId = bingoGame.startNewGame();
                    out.writeObject(playerId);
                    out.flush();
                    break;
                case "MAKE_GUESS":
                    // Read player ID and number separately
                    int currentPlayerId = in.readInt();
                    int number = in.readInt();
                    
                    // Ensure the player ID matches the current game
                    if (currentPlayerId != playerId) {
                        playerId = currentPlayerId;
                    }
                    
                    boolean result = bingoGame.makeGuess(playerId, number);
                    out.writeObject(result);
                    out.flush();
                    break;
                case "GET_GAME_STATUS":
                    int statusPlayerId = in.readInt();
                    String status = bingoGame.getGameStatus(statusPlayerId);
                    out.writeObject(status);
                    out.flush();
                    break;
                case "GET_HIGHEST_SCORE":
                    int highScore = bingoGame.getHighestScore();
                    out.writeObject(highScore);
                    out.flush();
                    break;
                case "QUIT":
                    closeConnection();
                    break;
                default:
                    out.writeObject("UNKNOWN_COMMAND");
                    out.flush();
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing command: " + e.getMessage());
            out.writeObject("ERROR");
            out.flush();
        }
    }

    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}