package fr.yanis.ynov;

import fr.yanis.ynov.controller.CLIController;

public class Main {

    public static void main(String[] args) {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│                     Secure Vault                           │");
        System.out.println("│        Coffre-fort de rapports clients                     │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        System.out.println("Tapez 'help' pour voir les commandes disponibles.");

        CLIController cliController = new CLIController();
        cliController.run();
    }
}
