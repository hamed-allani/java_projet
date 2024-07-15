import java.util.Date;

public class Transaction {
    private int id;
    private int clientId;
    private String details;
    private Date date;
    private double amount;
    private String type;

    public Transaction(int id, int clientId, String details, Date date, double amount, String type) {
        this.id = id;
        this.clientId = clientId;
        this.details = details;
        this.date = date;
        this.amount = amount;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public int getClientId() {
        return clientId;
    }

    public String getDetails() {
        return details;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", details='" + details + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                '}';
    }
}
