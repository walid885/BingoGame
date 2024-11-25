package client;

import java.io.IOException;
import java.util.Scanner;

import server.ScoreManager.GameStatus;

public class ClientUI {
    private BingoClient client;
    private Scanner scanner;
    private int currentPlayerId;
    private static final int MAX_ATTEMPTS = 10;
    private boolean isGameInProgress;

    public ClientUI(BingoClient client) {
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

    private void handleMenuChoice(int choice) {
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur de communication avec le serveur : " + e.getMessage());
        }
    }

    private void playBingo() throws IOException, ClassNotFoundException {
        if (isGameInProgress) {
            System.out.println("Une partie est déjà en cours!");
            return;
        }

        // Démarrer une nouvelle partie
        currentPlayerId = client.startGame();
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

            boolean isCorrect = client.makeGuess(guess);

            if (isCorrect) {
                System.out.println("Correct! Bien joué!");
                correctGuesses++;
            } else {
                System.out.println("Incorrect! Essayez encore!");
            }

            // Vérifier si la partie est terminée
            GameStatus status = client.getGameStatus();
            if (status == GameStatus.COMPLETED) {
                break;
            }
        }

        // Afficher le résultat final
        displayGameResult(correctGuesses);
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

    private void displayGameResult(int correctGuesses) {
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

    private void showHighScore() throws IOException, ClassNotFoundException {
        int highScore = client.getHighestScore();
        System.out.println("\n=== Meilleur Score ===");
        System.out.println("Le meilleur score est : " + highScore + "/" + MAX_ATTEMPTS);
    }

    private void quit() throws IOException {
        System.out.println("Merci d'avoir joué! Au revoir!");
        client.close();
        System.exit(0);
    }

    // Méthode utilitaire pour effacer l'écran (optionnel)
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
