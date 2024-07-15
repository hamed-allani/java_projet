import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        ClientService clientService = new ClientService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Créer un compte");
            System.out.println("2. Mettre à jour les informations du client");
            System.out.println("3. Modifier le mot de passe");
            System.out.println("4. Consulter l'historique des transactions");
            System.out.println("5. Effectuer une transaction");
            System.out.println("6. Quitter");
            System.out.print("Choisissez une option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createAccount(clientService, scanner);
                    break;
                case 2:
                    updateClientInfo(clientService, scanner);
                    break;
                case 3:
                    changePassword(clientService, scanner);
                    break;
                case 4:
                    viewTransactionHistory(clientService, scanner);
                    break;
                case 5:
                    addTransaction(clientService, scanner);
                    break;
                case 6:
                    System.out.println("Au revoir!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Option invalide.");
            }
        }
    }

    private static void createAccount(ClientService clientService, Scanner scanner) {
        System.out.print("Nom: ");
        String nom = scanner.nextLine();

        System.out.print("Adresse email: ");
        String adresse = scanner.nextLine();

        System.out.print("Numéro de téléphone: ");
        String telephone = scanner.nextLine();

        System.out.print("Mot de passe: ");
        String motDePasse = scanner.nextLine();

        clientService.createAccount(nom, adresse, telephone, motDePasse);
    }

    private static void updateClientInfo(ClientService clientService, Scanner scanner) {
        System.out.print("ID du client: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nouveau nom: ");
        String nom = scanner.nextLine();

        System.out.print("Nouvelle adresse email: ");
        String adresse = scanner.nextLine();

        System.out.print("Nouveau numéro de téléphone: ");
        String telephone = scanner.nextLine();

        clientService.updateClientInfo(id, nom, adresse, telephone);
    }

    private static void changePassword(ClientService clientService, Scanner scanner) {
        System.out.print("ID du client: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nouveau mot de passe: ");
        String newPassword = scanner.nextLine();

        clientService.changePassword(id, newPassword);
    }

    private static void viewTransactionHistory(ClientService clientService, Scanner scanner) {
        System.out.print("ID du client: ");
        int clientId = scanner.nextInt();
        scanner.nextLine();

        List<String> transactions = clientService.getTransactionHistory(clientId);
        if (transactions.isEmpty()) {
            System.out.println("Aucune transaction trouvée pour ce client.");
        } else {
            System.out.println("Historique des transactions:");
            transactions.forEach(System.out::println);
        }
    }

    private static void addTransaction(ClientService clientService, Scanner scanner) {
        System.out.println("\nType de transaction:");
        System.out.println("1. Versement sur le compte");
        System.out.println("2. Virement entre comptes");
        System.out.print("Choisissez une option: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        if (typeChoice == 1) {
            makeDeposit(clientService, scanner);
        } else if (typeChoice == 2) {
            makeTransfer(clientService, scanner);
        } else {
            System.out.println("Option de transaction invalide.");
        }
    }

    private static void makeDeposit(ClientService clientService, Scanner scanner) {
        System.out.print("ID du client: ");
        int clientId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Montant du dépôt: ");
        String amountString = scanner.nextLine().replaceAll("[^0-9.]", "");
        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            System.out.println("Montant invalide.");
            return;
        }

        clientService.makeDeposit(clientId, amount);
    }

    private static void makeTransfer(ClientService clientService, Scanner scanner) {
        System.out.print("ID du client source: ");
        int fromClientId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("ID du client destination: ");
        int toClientId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Montant du virement: ");
        String amountString = scanner.nextLine().replaceAll("[^0-9.]", "");
        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            System.out.println("Montant invalide.");
            return;
        }

        clientService.makeTransfer(fromClientId, toClientId, amount);
    }
}
