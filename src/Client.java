import java.util.ArrayList;
import java.util.List;

public class Client {
    private int id;
    private String nom;
    private String adresse;
    private String telephone;
    private String motDePasse;
    private double solde;
    private List<Transaction> transactions;

    public Client(String nom, String adresse, String telephone, String motDePasse) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.solde = 0.0;
        this.transactions = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
