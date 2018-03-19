import entity.Account;
import service.AccountService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class Client {
    private static boolean OPTIMISTIC = false;

    public static void main(String[] args) {
        processArgs(args);

        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory((OPTIMISTIC) ? "Optimistic" : "NoOptimistic", System.getProperties());
            AccountService accountService = new AccountService(emf);
            accountService.create(new Account(1, 100.0, "Daniel"));
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
}
