package gateway;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import server.BingoGameImpl; // Ensure this import matches your package structure
import server.IBingoGame; // Ensure this import matches your package structure;

public class GateWayServer {

    public static void main(String[] args) {
        try {
            int rmiPort = 1099; // Default port for RMI registry
            int gatewayPort = 12346; // Port for the Gateway Server

            // Instantiate the implementation of the Bingo game
            IBingoGame bingoGame = new BingoGameImpl(); 

            // Set up the RMI registry on a separate port
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            // Bind the Bingo game implementation to the registry
            registry.rebind("BingoGame", (Remote) bingoGame); 
            System.out.println("RMI Registry is running on port " + rmiPort);

            // Now create and start the gateway
            BingoGateway gateway = new BingoGateway(bingoGame);
            gateway.start(gatewayPort); // Start accepting client connections

            System.out.println("Gateway Server is running on port " + gatewayPort);
        } catch (RemoteException e) {
            System.err.println("RemoteException occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}