package client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ClientUI {
    private IBingoGame client;
    private Scanner scanner;
    private int currentPlayerId;
    private static final int MAX_ATTEMPTS = 10;
    private boolean isGameInProgress;

    public ClientUI(IBingoGame client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.isGameInProgress = false;
    }

    public void start() {
        while (true) {
            try {
                displayMenu();
                int choice = scanner.nextInt();
                handleMenuChoice(choice);
            } catch (Exception e) {
                System.out.println("Une erreur est survenue : " + e.getMessage());
                scanner.nextLine(); // Clear scanner buffer
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== BINGO GAME ===");
        System.out.println("1. Jouer BINGO");
        System.out.println("2. Connaître le meilleur score");
        System.out.println("3. Quitter");
        System.out.print("Votre choix : ");
    }

    private void handleMenuChoice(int choice) throws RemoteException {
        try {
            switch (choice) {
                case 1:
                    playBingo();
                    break;
                case 2:
                    showHighScore();
                    break;
                case 3:
                    quit();
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        } catch (IOException e) {
            System.out.println("Erreur de communication avec le serveur : " + e.getMessage());
        }
    }

    private void playBingo() throws RemoteException {
        if (isGameInProgress) {
            System.out.println("Une partie est déjà en cours!");
            return;
        }

        // Démarrer une nouvelle partie
        currentPlayerId = client.startNewGame();
        isGameInProgress = true;
        int correctGuesses = 0;

        System.out.println("\nNouvelle partie de Bingo commencée!");
        System.out.println("Vous avez " + MAX_ATTEMPTS + " tentatives pour deviner les numéros.");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.println("\nTentative " + attempt + "/" + MAX_ATTEMPTS);
            System.out.print("Devinez un numéro (0-9) : ");
            
            int guess = getValidGuess();
            
            if (guess == -1) {
                System.out.println("Tentative invalide, veuillez entrer un nombre entre 0 et 9.");
                attempt--; // Ne pas compter cette tentative
                continue;
            }

            boolean isCorrect = client.makeGuess(currentPlayerId, guess);

            if (isCorrect) {
                correctGuesses++;
                System.out.println("Correct! Bien joué!");
                displayGameResult(correctGuesses);
                
                // Ask if the user wants to play again
                if (!askToPlayAgain()) {
                    break;
                }
                
                // Restart the game
                currentPlayerId = client.startNewGame();
                correctGuesses = 0;
                attempt = 0; // Reset attempts for new game
            } else {
                System.out.println("Incorrect! Essayez encore!");
            }

            // Vérifier si la partie est terminée
            String status = client.getGameStatus(currentPlayerId);
            if (IBingoGame.GameStatus.COMPLETED.equals(status)) {
                break;
            }
        }

        isGameInProgress = false;
    }

    private int getValidGuess() {
        while (true) {
            try {
                int guess = scanner.nextInt();
                if (guess >= 0 && guess <= 9) {
                    return guess;
                } else {
                    System.out.print("Veuillez entrer un nombre entre 0 et 9 : ");
                }
            } catch (Exception e) {
                System.out.print("Entrée invalide. Veuillez entrer un nombre entre 0 et 9 : ");
                scanner.nextLine(); // Clear scanner buffer
            }
        }
    }

    private void displayGameResult(int correctGuesses) throws RemoteException {
        System.out.println("\n=== Fin de la partie ===");
        System.out.println("Votre score : " + correctGuesses + "/" + MAX_ATTEMPTS);
        System.out.println("Pourcentage de réussite : " + 
            String.format("%.1f", (correctGuesses * 100.0 / MAX_ATTEMPTS)) + "%");
        
        try {
            int highScore = client.getHighestScore();
            if (correctGuesses >= highScore) {
                System.out.println("Félicitations! Vous avez battu le meilleur score!");
            }
        } catch (Exception e) {
            System.out.println("Impossible de récupérer le meilleur score.");
        }
    }

    private boolean askToPlayAgain() {
        System.out.print("\nVoulez-vous jouer à nouveau? (oui/non): ");
        String response = scanner.next().trim().toLowerCase();
        
        return response.equals("oui") || response.equals("o");
    }

    private void showHighScore() throws RemoteException {
        int highScore = client.getHighestScore();
        System.out.println("\n=== Meilleur Score ===");
        System.out.println("Le meilleur score est : " + highScore + "/" + MAX_ATTEMPTS);
    }

    private void quit() throws IOException {
        System.out.println("Merci d'avoir joué! Au revoir!");
        System.exit(0);
    }
}