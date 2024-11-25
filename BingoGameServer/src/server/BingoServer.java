package server;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BingoServer {
    public static void main(String[] args) {
        try {
            IBingoGame bingoGame = new BingoGameImpl();
            int rmiPort = 1100; // Change this if necessary
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind("BingoGame", (Remote) bingoGame);
            System.out.println("BingoServer is running on port " + rmiPort);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}