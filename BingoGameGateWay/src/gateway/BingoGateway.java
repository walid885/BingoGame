package gateway;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import server.IBingoGame;

public class BingoGateway {
    private IBingoGame bingoGame;
    private ExecutorService executorService;

    public BingoGateway(IBingoGame bingoGame) {
        this.bingoGame = bingoGame;
        this.executorService = Executors.newFixedThreadPool(10); // Thread pool for handling clients
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true); // Allow reuse of address
            serverSocket.bind(new InetSocketAddress(port)); // Bind to specified port
            System.out.println("Bingo Gateway is running on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept new client connections
                ClientHandler clientHandler = new ClientHandler(clientSocket, bingoGame);
                executorService.execute(clientHandler); // Handle each client in a separate thread
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions
        }
    }
}