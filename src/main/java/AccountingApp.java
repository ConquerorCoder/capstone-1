import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AccountingApp {
    static String filename = "src/main/resources/transactions.csv";

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
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd|hh:mm a");
        String dateTime = now.format(formatter);

        System.out.print("Description: ");
        String description = in.nextLine();

        System.out.print("Vendor ");
        String vendor = in.nextLine();

        System.out.print("Enter the amount: ");
        double amount = Double.parseDouble(in.nextLine());

        // make payments negative no matter the input
        if(!isDeposit){
            amount = amount* -1;
        }

        String type = isDeposit ? "DEPOSIT" : "PAYMENT";

        String record = dateTime + "|" + type + "|" + description + "|" + vendor + "|" + amount;

        File file = new File(filename);
        boolean isEmpty = file.length() == 0;


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (isEmpty) {
                String header ="date|time|description|vendor|amount";
                writer.write(header);
                writer.newLine();

            }
            writer.write(record);
            writer.newLine(); // moves to the next line after each record
            System.out.println("Transaction recorded successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void showLedger() {

    }
}