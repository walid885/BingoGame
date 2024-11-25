package client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
    	BingoClient client = new BingoClient("localhost", 12346); 
    	ClientUI ui = new ClientUI(client);
        ui.start();
        System.out.println(" client is running baaabyyyyyyyyyy!");

    }

}