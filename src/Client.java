import entity.Account;
import org.apache.openjpa.persistence.OptimisticLockException;
import service.AccountService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Scanner;

public final class Client {
    /* Client Settings */
    private static Account currentAccount;

    /* Menu Options */
    private static final int TRANSFER = 1;
    private static final int DEPOSIT = 2;
    private static final int WITHDRAW = 3;
    private static final int SHOW_ALL = 4;
    private static final int EXIT = 5;

    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory("persistenceUnit", System.getProperties());
            AccountService accountService = new AccountService(emf);

            Scanner scanner = new Scanner(System.in);
            int option = 0;

            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();
            do {
                List<Account> accounts = accountService.findAll(name);
                if (accounts.size() == 0) {
                    option = createAccount(accountService, scanner, name);
                } else if (accounts.size() == 1) {
                    currentAccount = accounts.get(0);
                } else {
                    System.out.print("You have multiple accounts, select which to use.");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < accounts.size(); i++) {
                        sb.append("\n");
                        sb.append(i);
                        sb.append(": ");
                        sb.append(accounts.get(i).toString());
                    }
                    System.out.println(sb.toString());
                    System.out.print("Account #: ");
                    String answer = scanner.nextLine().trim();
                    if (answer.matches("[0-9]+")) {
                        int index = Integer.parseInt(answer);
                        if (index < accounts.size()) {
                            currentAccount = accounts.get(index);
                        }
                    }
                }
            } while (currentAccount == null);

            while (option != EXIT) {
                option = displayMenu(accountService, scanner);
                switch (option) {
                    case TRANSFER:
                        displayTransfer(accountService, scanner);
                        break;
                    case DEPOSIT:
                        displayDeposit(accountService, scanner);
                        break;
                    case WITHDRAW:
                        displayWithdraw(accountService, scanner);
                        break;
                    case SHOW_ALL:
                        displayAllAccounts(accountService);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        if (emf != null && emf.isOpen()) emf.close();
    }

    /**
     * Create a new account.
     *
     * @param accountService - Service used to create a new account.
     * @param scanner        - Scanner used to read user input.
     * @param owner          - The owner of the new account.
     * @return an integer signal, EXIT if the client should exit otherwise 0.
     */
    private static int createAccount(AccountService accountService, Scanner scanner, String owner) {
        System.out.print("You do not already own any accounts. \nDo you wish to create one? ");
        String answer = scanner.nextLine().toLowerCase();
        if (answer.contains("y")) {
            long accountNumber = (long) Math.abs(Math.random() * Long.MAX_VALUE);
            accountService.create(accountNumber, 0.0, owner);
            currentAccount = accountService.find(accountNumber);
            return 0;
        }
        return EXIT;
    }

    /**
     * Displays an interactive user menu.
     *
     * @param scanner - A scanner used to read user input.
     * @return the option the user picked.
     */
    private static int displayMenu(AccountService accountService, Scanner scanner) {
        currentAccount = accountService.find(currentAccount.getAccountNumber());
        System.out.println("\nCurrent " + ((currentAccount != null) ? currentAccount.toString() : "ACCOUNT MISSING!"));
        System.out.println("\nMain Menu\n" +
                TRANSFER + ": Transfer funds to another account.\n" +
                DEPOSIT + ": Deposit funds into your account.\n" +
                WITHDRAW + ": Withdraw funds from your account.\n" +
                SHOW_ALL + ": Display all accounts.\n" +
                EXIT + ": Exit the application");
        System.out.print("Enter option: ");
        String option = scanner.nextLine();
        if (option.matches("[0-9]+")) {
            return Integer.parseInt(option);
        } else if (option.equalsIgnoreCase("e") || option.equalsIgnoreCase("exit")) {
            return EXIT;
        }
        return displayMenu(accountService, scanner);
    }

    /**
     * Displays the menu for transferring funds to another account.
     *
     * @param accountService - Service used to execute transaction.
     * @param scanner        - Scanner used to read user input.
     */
    private static void displayTransfer(AccountService accountService, Scanner scanner) {
        currentAccount = accountService.find(currentAccount.getAccountNumber());
        System.out.println("\nWhich account do you wish to transfer funds to?");
        System.out.print("Account number: ");
        String input = scanner.nextLine().trim();
        if (input.matches("[0-9]+")) {
            long accountNumber = Long.parseLong(input);
            Account recipient = accountService.find(accountNumber);
            System.out.println("Enter the amount you want to transfer, current balance: " + currentAccount.getBalance());
            System.out.print("Amount: ");
            input = scanner.nextLine().trim().replace(',', '.');
            if (input.matches("[0-9.]+")) {
                double amount = Double.parseDouble(input);
                try {
                    accountService.transfer(currentAccount, recipient, amount);
                } catch (OptimisticLockException ole) {
                    System.err.println("The state of your account or the recipient's account has been changed since you " +
                            "started the transfer, you will need to redo your changes.");
                    displayTransfer(accountService, scanner);
                }
            }
        }
    }

    /**
     * Displays the menu for depositing funds into your account.
     *
     * @param accountService - Service used to execute deposit.
     * @param scanner        - Scanner used to read user input.
     */
    private static void displayDeposit(AccountService accountService, Scanner scanner) {
        currentAccount = accountService.find(currentAccount.getAccountNumber());
        System.out.println("\nCurrent balance: " + ((currentAccount != null) ? currentAccount.getBalance() : "MISSING ACCOUNT!"));
        System.out.print("Enter the amount to deposit: ");
        String input = scanner.nextLine().trim().replace(',', '.');
        if (input.matches("[0-9.]+")) {
            double amount = Double.parseDouble(input);
            try {
                accountService.deposit(currentAccount, amount);
            } catch (OptimisticLockException ole) {
                System.err.println("The state of your account has been changed since you started the deposit, " +
                        "you will need to redo your changes.");
                displayDeposit(accountService, scanner);
            }
        }
    }

    /**
     * Displays the menu for withdrawing funds from your account.
     *
     * @param accountService - Service used to execute withdrawal.
     * @param scanner        - Scanner used to read user input.
     */
    private static void displayWithdraw(AccountService accountService, Scanner scanner) {
        currentAccount = accountService.find(currentAccount.getAccountNumber());
        System.out.println("\nCurrent balance: " + ((currentAccount != null) ? currentAccount.getBalance() : "MISSING ACCOUNT!"));
        System.out.print("Enter the amount to withdraw: ");
        String input = scanner.nextLine().trim().replace(',', '.');
        if (input.matches("[0-9.]+")) {
            double amount = Double.parseDouble(input);
            try {
                accountService.withdraw(currentAccount, amount);
            } catch (OptimisticLockException ole) {
                System.err.println("The state of your account has been changed since you started the withdraw, " +
                        "you will need to redo your changes.");
                displayWithdraw(accountService, scanner);
            }
        }
    }

    /**
     * Display all accounts.
     *
     * @param accountService - used to retrieve accounts.
     */
    private static void displayAllAccounts(AccountService accountService) {
        List<Account> accounts = accountService.findAll();
        StringBuilder sb = new StringBuilder("\nAccounts:");
        for (Account account : accounts) {
            sb.append("\n");
            sb.append(account.toString());
        }
        sb.append("\n");
        System.out.print(sb.toString());
    }
}
