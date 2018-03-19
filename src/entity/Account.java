package entity;

import javax.persistence.Id;
import javax.persistence.Version;
import java.io.Serializable;

@javax.persistence.Entity

public class Account implements Entity, Serializable {
    @Id
    private long accountNumber;
    private double balance;
    private String owner;
    @Version
    private int locked;

    public Account() { }

    public Account(long accountNumber, double balance, String owner) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.owner = owner;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getOwner() {
        return owner;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean withdraw(double amount) {
        if ((balance - amount) >= 0) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Account: number: '" + accountNumber +
                "', balance: '" + balance +
                "', owner: " + owner;
    }
}
