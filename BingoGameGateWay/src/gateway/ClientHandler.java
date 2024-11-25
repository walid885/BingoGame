package gateway;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;

import server.IBingoGame.GameStatus;
import server.IBingoGame;
public class ClientHandler implements Runnable, Serializable {
    private server.IBingoGame bingoGame;

    public ClientHandler(Socket socket, IBingoGame bingoGame2) {
        this.clientSocket = socket;
        this.bingoGame = (server.IBingoGame) bingoGame2;
    }
    private static final long serialVersionUID = 1L;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int playerId; // Class attribute
    private int score;
    private Date playDate;

    // Constructeur par défaut
    public ClientHandler() {}

    // Constructeur avec paramètres

    // Getters et setters
    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getPlayDate() {
        return playDate;
    }

    public void setPlayDate(Date playDate) {
        this.playDate = playDate;
    }

    // Méthode pour mettre à jour le score et la date
    public void updateScore(int score) {
        this.score = score;
        this.playDate = new Date(); // Mettre à jour la date avec la date actuelle
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            while (true) {
                String command = (String) in.readObject();
                processCommand(command);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void processCommand(String command) throws IOException, ClassNotFoundException {
        switch (command) {
            case "START_GAME":
                playerId = bingoGame.startNewGame();
                out.writeObject(playerId);
                out.flush();
                break;

            case "MAKE_GUESS":
                int number = in.readInt();
                boolean result = bingoGame.makeGuess(playerId, number);
                out.writeObject(result);
                out.flush();
                break;

            case "GET_GAME_STATUS":
                GameStatus status = (GameStatus) bingoGame.getGameStatus(playerId);
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
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}