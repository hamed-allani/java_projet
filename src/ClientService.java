import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService {

    private static final String URL = "jdbc:mysql://localhost:3306/banque";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public void createAccount(String nom, String adresse, String telephone, String motDePasse) {
        String sql = "INSERT INTO clients (nom, adresse, telephone, motDePasse, solde) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, adresse);
            pstmt.setString(3, telephone);
            pstmt.setString(4, motDePasse);
            pstmt.setDouble(5, 0.0);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Compte créé avec succès. ID du client: " + id);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateClientInfo(int id, String nom, String adresse, String telephone) {
        String sql = "UPDATE clients SET nom = ?, adresse = ?, telephone = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, adresse);
            pstmt.setString(3, telephone);
            pstmt.setInt(4, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Informations du client mises à jour avec succès.");
            } else {
                System.out.println("Client introuvable.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void changePassword(int id, String newPassword) {
        String sql = "UPDATE clients SET motDePasse = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Mot de passe mis à jour avec succès.");
            } else {
                System.out.println("Client introuvable.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<String> getTransactionHistory(int clientId) {
        List<String> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE clientId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String details = rs.getString("details");
                Date date = rs.getDate("date");
                double amount = rs.getDouble("amount");
                String type = rs.getString("type");
                transactions.add(new Transaction(id, clientId, details, date, amount, type).toString());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transactions;
    }

    public void makeDeposit(int clientId, double amount) {
        if (amount <= 0) {
            System.out.println("Le montant du dépôt doit être strictement positif.");
            return;
        }

        String sql = "UPDATE clients SET solde = solde + ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, clientId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Dépôt effectué avec succès.");
                addTransaction(new Transaction(0, clientId, "Dépôt de " + amount, new java.util.Date(), amount, "Dépôt"));
            } else {
                System.out.println("Client introuvable.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeTransfer(int fromClientId, int toClientId, double amount) {
        if (amount <= 0) {
            System.out.println("Le montant du virement doit être strictement positif.");
            return;
        }

        double fromClientSolde = getClientSolde(fromClientId);
        if (amount > fromClientSolde) {
            System.out.println("Le montant du virement dépasse le solde du client source.");
            return;
        }

        String withdrawSql = "UPDATE clients SET solde = solde - ? WHERE id = ?";
        String depositSql = "UPDATE clients SET solde = solde + ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            try (PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSql);
                 PreparedStatement depositStmt = conn.prepareStatement(depositSql)) {

                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setInt(2, fromClientId);

                depositStmt.setDouble(1, amount);
                depositStmt.setInt(2, toClientId);

                int withdrawRows = withdrawStmt.executeUpdate();
                int depositRows = depositStmt.executeUpdate();

                if (withdrawRows > 0 && depositRows > 0) {
                    conn.commit();
                    System.out.println("Virement effectué avec succès.");
                    addTransaction(new Transaction(0, fromClientId, "Virement de " + amount + " vers le compte " + toClientId, new java.util.Date(), amount, "Virement"));
                    addTransaction(new Transaction(0, toClientId, "Virement reçu de " + amount + " du compte " + fromClientId, new java.util.Date(), amount, "Virement"));
                } else {
                    conn.rollback();
                    System.out.println("Virement échoué. Un des comptes est introuvable.");
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println(e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private double getClientSolde(int clientId) {
        String sql = "SELECT solde FROM clients WHERE id = ?";
        double solde = 0.0;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                solde = rs.getDouble("solde");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return solde;
    }

    private void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (clientId, details, date, amount, type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, transaction.getClientId());
            pstmt.setString(2, transaction.getDetails());
            pstmt.setDate(3, new java.sql.Date(transaction.getDate().getTime())); // Correction here
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getType());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Transaction ajoutée avec succès. ID de la transaction: " + id);
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
