package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import server.ScoreManager.GameStatus;

public class BingoClient {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int playerId;

    public BingoClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        // Important : créer d'abord l'OutputStream puis l'InputStream pour éviter le deadlock
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush(); // Important pour éviter les problèmes de buffer
        in = new ObjectInputStream(socket.getInputStream());
    }

    public int startGame() throws IOException, ClassNotFoundException {
        out.writeObject("START_GAME");
        out.flush();
        playerId = (Integer) in.readObject();
        return playerId;
    }

    public boolean makeGuess(int number) throws IOException, ClassNotFoundException {
        out.writeObject("MAKE_GUESS");
        out.writeInt(playerId);
        out.writeInt(number);
        out.flush();
        return (Boolean) in.readObject();
    }

    public GameStatus getGameStatus() throws IOException, ClassNotFoundException {
        out.writeObject("GET_GAME_STATUS");
        out.writeInt(playerId);
        out.flush();
        return (GameStatus) in.readObject();
    }

    public int getHighestScore() throws IOException, ClassNotFoundException {
        out.writeObject("GET_HIGHEST_SCORE");
        out.flush();
        return (Integer) in.readObject();
    }

    public void close() throws IOException {
        try {
            if (out != null) {
                out.writeObject("QUIT");
                out.flush();
            }
        } finally {
            try {
                if (in != null) in.close();
            } finally {
                try {
                    if (out != null) out.close();
                } finally {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                }
            }
        }
    }

    // Méthode utilitaire pour vérifier la connexion
    private void checkConnection() throws IOException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("La connexion au serveur est fermée");
        }
    }

    // Méthode pour envoyer une commande générique
    private void sendCommand(String command) throws IOException {
        checkConnection();
        out.writeObject(command);
        out.flush();
    }
}