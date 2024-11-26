package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

public class BingoClient implements IBingoGame {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int playerId;

    public BingoClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        // Important: create the OutputStream first to avoid deadlock
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush(); // Important to avoid buffering issues
        in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public int startNewGame() throws RemoteException {
        try {
            sendCommand("START_GAME");
            playerId = (Integer) in.readObject();
            return playerId;
        } catch (IOException | ClassNotFoundException e) {
            throw new RemoteException("Failed to start new game", e);
        }
    }

    @Override
    public boolean makeGuess(int playerId, int number) throws RemoteException {
        try {
            sendCommand("MAKE_GUESS");
            out.writeInt(playerId);
            out.writeInt(number);
            out.flush();

            Object response = in.readObject();
            if (response instanceof Boolean) {
                return (Boolean) response;
            } else {
                throw new RemoteException("Unexpected response from server");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RemoteException("Failed to make guess", e);
        }
    }

    @Override
    public String getGameStatus(int playerId) throws RemoteException {
        try {
            sendCommand("GET_GAME_STATUS");
            out.writeInt(playerId);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                return (String) response;
            } else {
                throw new RemoteException("Unexpected response from server");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RemoteException("Failed to get game status", e);
        }
    }

    @Override
    public int getHighestScore() throws RemoteException {
        try {
            sendCommand("GET_HIGHEST_SCORE");
            return (Integer) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RemoteException("Failed to get highest score", e);
        }
    }

    public void close() throws IOException {
        try {
            if (out != null) {
                sendCommand("QUIT");
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

    // Utility method to check the connection
    private void checkConnection() throws IOException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("Connection to server is closed");
        }
    }

    // Method to send a generic command
    private void sendCommand(String command) throws IOException {
        checkConnection();
        out.writeObject(command);
        out.flush();
    }
}