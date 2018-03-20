package service;

import dao.AccountDAO;
import entity.Account;

import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import java.util.List;

public final class AccountService {
    private final AccountDAO accountDAO;

    public AccountService(EntityManagerFactory emf) {
        accountDAO = new AccountDAO(emf);
    }

    /**
     * Create a new account.
     *
     * @param account - The account to create.
     */
    public void create(Account account) {
        if (account != null) {
            accountDAO.create(account);
        }
    }

    /**
     * Create a new account.
     *
     * @param accountNumber - The unique account number.
     * @param balance       - The starting balance of the account.
     * @param owner         - The owner of the account.
     */
    public void create(long accountNumber, double balance, String owner) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setOwner(owner);
        accountDAO.create(account);
    }

    /**
     * Find an account.
     *
     * @param accountNumber - The unique account number to identify the account.
     * @return an Account or null if not found or invalid account number.
     */
    public Account find(long accountNumber) {
        if (accountNumber >= 0) {
            return accountDAO.find(accountNumber);
        }
        return null;
    }

    /**
     * Finds all accounts.
     *
     * @return a list of all accounts, or empty list if there are no accounts.
     */
    public List<Account> findAll() {
        return accountDAO.findAll();
    }

    /**
     * Finds all accounts for an owner.
     *
     * @param owner - the owner of the accounts.
     * @return a list of accounts that belong to the given owner.
     */
    public List<Account> findAll(String owner) {
        return accountDAO.findAll(owner);
    }

    /**
     * Change the ownership of an account.
     *
     * @param account - The account to change ownership of.
     * @param owner   - The new owner of the account.
     */
    public void changeOwner(Account account, String owner) {
        if (account != null) {
            account.setOwner(owner);
            accountDAO.update(account);
        }
    }

    /**
     * Change the ownership of an account.
     *
     * @param accountNumber - The account number to identify the account.
     * @param owner         - The new owner of the account.
     */
    public void changeOwner(long accountNumber, String owner) {
        Account account = accountDAO.find(accountNumber);
        if (account != null) {
            account.setOwner(owner);
            accountDAO.update(account);
        }
    }

    /**
     * Delete the account that have the given account number.
     *
     * @param accountNumber - The unique account number to identify the account.
     */
    public void delete(long accountNumber) {
        accountDAO.delete(accountNumber);
    }

    /**
     * Delete the given account.
     *
     * @param account - The account to delete.
     */
    public void delete(Account account) {
        if (account != null) {
            accountDAO.delete(account.getAccountNumber());
        }
    }

    /**
     * Check if the account have sufficient funds. Account cannot be null,
     * and the amount must be positive.
     *
     * @param account - The account to check.
     * @param amount  - The amount to check for.
     * @return true if the account have a balance that is greater or equal to the amount.
     */
    private boolean sufficientFunds(Account account, double amount) {
        return account.getBalance() >= amount;
    }

    /**
     * Deposit funds into an account.
     *
     * @param account - The account to deposit to.
     * @param amount  - The amount to deposit.
     */
    public void deposit(Account account, double amount) throws OptimisticLockException {
        if (account != null) {
            amount = Math.abs(amount);
            account.setBalance(account.getBalance() + amount);
            accountDAO.update(account);
        }
    }

    /**
     * Withdraw funds from an account.
     *
     * @param account - The account to withdraw from.
     * @param amount  - The amount to withdraw.
     */
    public void withdraw(Account account, double amount) throws OptimisticLockException {
        if (account != null) {
            amount = Math.abs(amount);
            if (sufficientFunds(account, amount)) {
                account.setBalance(account.getBalance() - amount);
                accountDAO.update(account);
            }
        }
    }

    /**
     * Transfer currency from sender to recipient if the sender have sufficient
     * amount of currency.
     *
     * @param sender    - The account to transfer from.
     * @param recipient - The account to transfer to.
     * @param amount    - The amount to transfer.
     */
    public void transfer(Account sender, Account recipient, double amount) throws OptimisticLockException {
        if (sender != null && recipient != null && !(sender.equals(recipient))) {
            amount = Math.abs(amount);
            if (sufficientFunds(sender, amount)) {
                sender.setBalance(sender.getBalance() - amount);
                recipient.setBalance(recipient.getBalance() + amount);
                accountDAO.update(sender);
                accountDAO.update(recipient);
            }
        }
    }
}
