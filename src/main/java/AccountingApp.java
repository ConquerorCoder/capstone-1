import java.util.Scanner;

public class AccountingApp {
    static String file = "src/main/resources/transactions.csv";

    public static void main(String[] args) {
        System.out.println("Bonjure! This Is My Accounting App");

        try (Scanner in = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("\nChoose an option:");
                System.out.println("D) Add Deposit");
                System.out.println("P) Make Payment");
                System.out.println("L) View Ledger");
                System.out.println("X) Exit");
                System.out.print("Enter choice: ");
                String choice = in.nextLine().trim().toUpperCase();

                if (choice.equals("D")) addTransaction(in, true);
                else if (choice.equals("P")) addTransaction(in, false);
                else if (choice.equals("L")) showLedger();
                else if (choice.equals("X")) {
                    System.out.println("Bon Voyage!");
                    running = false;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        }
    }

    // (compile check)
    public static void addTransaction(Scanner in, boolean isDeposit) {
        System.out.println(" addTransaction called");
    }

    public static void showLedger() {
        System.out.println(" showLedger called");
    }
}