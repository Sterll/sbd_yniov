package fr.yanis.ynov.controller;

import fr.yanis.ynov.model.RapportModel;
import fr.yanis.ynov.repository.FileRapportRepository;
import fr.yanis.ynov.repository.RapportRepository;

import java.util.Scanner;

public class CLIController {

    private final RapportRepository rapportRepository;
    private final Scanner scanner;

    public CLIController() {
        this.rapportRepository = new FileRapportRepository();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.print("\n> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = parseLine(line);
            String command = parts[0].toLowerCase();

            switch (command) {
                case "save" -> handleSave(parts);
                case "read" -> handleRead(parts);
                case "exit", "quit" -> running = false;
                case "help" -> printHelp();
                default -> System.out.println("Commande inconnue. Tapez 'help' pour l'aide.");
            }
        }
    }

    private String[] parseLine(String line) {
        if (line.contains("\"")) {
            int firstQuote = line.indexOf('"');
            int lastQuote = line.lastIndexOf('"');
            if (firstQuote != lastQuote) {
                String before = line.substring(0, firstQuote).trim();
                String quoted = line.substring(firstQuote + 1, lastQuote);
                String[] beforeParts = before.split("\\s+");
                String[] result = new String[beforeParts.length + 1];
                System.arraycopy(beforeParts, 0, result, 0, beforeParts.length);
                result[beforeParts.length] = quoted;
                return result;
            }
        }
        return line.split("\\s+");
    }

    private void handleSave(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: save <clientId> \"contenu du rapport\"");
            return;
        }
        try {
            int clientId = Integer.parseInt(parts[1]);
            String content = parts[2];
            RapportModel rapport = new RapportModel(clientId, content);
            rapportRepository.saveRapport(rapport);
            System.out.println("Rapport sauvegardé pour le client " + clientId);
        } catch (NumberFormatException e) {
            System.out.println("Erreur: clientId doit être un nombre.");
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    private void handleRead(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: read <clientId>");
            return;
        }
        try {
            int clientId = Integer.parseInt(parts[1]);
            RapportModel rapport = rapportRepository.getRapportByClientId(clientId);
            if (rapport != null) {
                System.out.println("Rapport du client " + clientId + ":");
                System.out.println(rapport.content());
            } else {
                System.out.println("Aucun rapport trouvé pour le client " + clientId);
            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur: clientId doit être un nombre.");
        } catch (Exception e) {
            System.out.println("Erreur lors de la lecture: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Commandes disponibles:");
        System.out.println("  save <clientId> \"contenu\"  - Sauvegarde un rapport");
        System.out.println("  read <clientId>            - Lit un rapport");
        System.out.println("  exit                       - Quitte l'application");
    }
}
