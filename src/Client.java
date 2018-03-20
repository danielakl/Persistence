import entity.Account;
import service.AccountService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Scanner;

public final class Client {
    /* Client Settings */
    private static boolean OPTIMISTIC = false;

    private static Account currentAccount;

    /* Menu Options */
    private static final int TRANSFER = 1;
    private static final int WITHDRAW = 2;
    private static final int SHOW_ALL = 3;
    private static final int EXIT = 4;

    public static void main(String[] args) {
        processArgs(args);

        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory(OPTIMISTIC ? "Optimistic" : "NoOptimistic", System.getProperties());
            AccountService accountService = new AccountService(emf);

            System.out.println((OPTIMISTIC ? "Using" : "Not using") + " optimistic mode.");
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
            } while(currentAccount == null);

            do {
                option = displayMenu(scanner);
                if (option != EXIT) {
                    switch (option) {
                        case TRANSFER:
                            displayTransfer(accountService, scanner);
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
            } while(option != EXIT);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        if (emf != null && emf.isOpen()) emf.close();
    }

    /**
     * Process command line arguments, setting initial settings.
     * @param args - The given arguments.
     */
    private static void processArgs(String[] args) {
        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "optimistic":
                    OPTIMISTIC = true;
                    break;
                default:
                    break;
            }
        }
    }

    private static int createAccount(AccountService accountService, Scanner scanner, String owner) {
        System.out.print("You do not already own any accounts. \nDo you wish to create one? ");
        String answer = scanner.nextLine().toLowerCase();
        if (answer.contains("y")) {
            long accountNumber = (long) Math.abs(Math.random() * Long.MAX_VALUE);
            accountService.create(accountNumber, 100.0, owner);
            currentAccount = accountService.find(accountNumber);
            return 0;
        }
        return EXIT;
    }

    /**
     * Displays an interactive user menu.
     * @param scanner - A scanner used to read user input.
     * @return the option the user picked.
     */
    private static int displayMenu(Scanner scanner) {
        System.out.println("\n" + currentAccount.toString());
        System.out.println("\nMain Menu\n" +
                TRANSFER + ": Transfer funds to another account.\n" +
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
        return displayMenu(scanner);
    }

    /**
     * Displays the menu for transferring funds to another account.
     * @param accountService    - Service used to execute transaction.
     * @param scanner           - Scanner used to read user input.
     */
    private static void displayTransfer(AccountService accountService, Scanner scanner) {
        System.out.println("\nWhich account do you wish to transfer funds to?");
        System.out.print("Account number: ");
        String input = scanner.nextLine().trim();
        if (input.matches("[0-9]+")) {
            long accountNumber = Long.parseLong(input);
            Account recipient = accountService.find(accountNumber);
            System.out.println("Enter the amount you want to transfer.");
            System.out.print("Amount: ");
            input = scanner.nextLine().trim().replace(',', '.');
            if (input.matches("[0-9.]+")) {
                double amount = Double.parseDouble(input);
                accountService.transfer(currentAccount, recipient, amount);
            }
        }
    }

    /**
     * Displays the menu for withdrawing funds from your account.
     * @param accountService    - Service used to execute withdrawal.
     * @param scanner           - Scanner used to read user input.
     */
    private static void displayWithdraw(AccountService accountService, Scanner scanner) {
        System.out.println("\nCurrent balance: " + currentAccount.getBalance());
        System.out.print("Enter the amount to withdraw: ");
        String input = scanner.nextLine().trim().replace(',', '.');
        if (input.matches("[0-9.]+")) {
            double amount = Double.parseDouble(input);
            accountService.withdraw(currentAccount, amount);
        }
    }

    /**
     * Display all accounts.
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
