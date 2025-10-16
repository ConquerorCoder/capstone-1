import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                else if (choice.equals("L")) showLedgerMenu(in);
                else if (choice.equals("X")) {
                    System.out.println("Bon Voyage!");
                    running = false;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        }
    }

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
        if (!isDeposit) {
            amount = amount * -1;
        }

        String record = dateTime + "|" + description + "|" + vendor + "|" + amount;

        File file = new File(filename);
        boolean isEmpty = file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (isEmpty) {
                String header = "date|time|description|vendor|amount";
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

    //  Ledger menu
    public static void showLedgerMenu(Scanner in) {
        boolean inLedger = true;
        while (inLedger) {
            System.out.println("\n---- Ledger Menu ----");
            System.out.println("A) All entries");
            System.out.println("D) Deposits only");
            System.out.println("P) Payments only");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Enter choice: ");
            String choice = in.nextLine().trim().toUpperCase();

            if (choice.equals("A")) displayLedger("All");
            else if (choice.equals("D")) displayLedger("Deposits");
            else if (choice.equals("P")) displayLedger("Payments");
            else if (choice.equals("R")) showReportsMenu(in);
            else if (choice.equals("H")) inLedger = false;
            else System.out.println("Invalid choice.");
        }
    }
    // NEW: Reports menu
    public static void showReportsMenu(Scanner in) {
        boolean inReports = true;
        while (inReports) {
            System.out.println("\n---- Reports Menu ----");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back to Ledger");
            System.out.print("Enter choice: ");
            String choice = in.nextLine().trim();

            if (choice.equals("1")) displayMonthToDate();
            else if (choice.equals("2"))displayPreviousMonth();
            else if (choice.equals("3")) displayYearToDate();
            else if (choice.equals("4")) displayPreviousYear();
            else if (choice.equals("5")) searchByVendor(in);
            else if (choice.equals("0")) inReports = false;
            else System.out.println("Invalid choice.");
        }
    }
    // Month To Date report
    public static void displayMonthToDate() {
        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();// Returns the month as a number (1-12)
        // January = 1, February = 2, ... December = 12
        int currentYear = now.getYear();  // Returns the year as a number (e.g., 2024, 2025)

        displayDateFilteredTransactions("Month To Date", currentYear, currentMonth, currentYear, currentMonth);
    }

    // Previous Month report
    public static void displayPreviousMonth() {
        LocalDateTime now = LocalDateTime.now().minusMonths(1);
        int prevMonth = now.getMonthValue();
        int prevYear = now.getYear();

        displayDateFilteredTransactions("Previous Month", prevYear, prevMonth, prevYear, prevMonth);
    }

    // Year To Date report
    public static void displayYearToDate() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        displayDateFilteredTransactions("Year To Date", currentYear, 1, currentYear, 12);
    }

    // Previous Year report
    public static void displayPreviousYear() {
        LocalDateTime now = LocalDateTime.now();
        int prevYear = now.getYear() - 1;

        displayDateFilteredTransactions("Previous Year", prevYear, 1, prevYear, 12);
    }

    // Helper method to display transactions within a date range
    public static void displayDateFilteredTransactions(String reportName, int startYear, int startMonth, int endYear, int endMonth) {
        List<Transaction> transactions = readTransactions();
        List<Transaction> matches = new ArrayList<>();

        for (Transaction t : transactions) {
            try {
                // Parse the date (format: yyyy-MM-dd)
                String[] dateParts = t.date.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);

                // Check if transaction falls within the date range
                boolean inRange = false;
                if (year > startYear && year < endYear) {
                    inRange = true;
                } else if (year == startYear && year == endYear) {
                    inRange = (month >= startMonth && month <= endMonth);
                } else if (year == startYear) {
                    inRange = (month >= startMonth);
                } else if (year == endYear) {
                    inRange = (month <= endMonth);
                }

                if (inRange) {
                    matches.add(t);
                }
            } catch (Exception e) {
                // Skip transactions with invalid date format
            }
        }

        if (matches.isEmpty()) {
            System.out.println("\nNo transactions found for " + reportName);
        } else {
            System.out.println("\n---- " + reportName + " ----");
            System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                    "DATE", "TIME", "DESCRIPTION", "VENDOR", "AMOUNT");
            System.out.println("--------------------------------------------------------------------------------");

            for (Transaction t : matches) {
                System.out.printf("%-12s %-10s %-25s %-20s %10.2f%n",
                        t.date, t.time, t.description, t.vendor, t.amount);
            }
        }
    }
    public static void searchByVendor(Scanner in) {
        System.out.print("Enter vendor name: ");
        String searchVendor = in.nextLine().trim();

        List<Transaction> transactions = readTransactions();
        List<Transaction> matches = new ArrayList<>();

        for (Transaction t : transactions) {
            if (t.vendor.equalsIgnoreCase(searchVendor)) {
                matches.add(t);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("No transactions found for vendor: " + searchVendor);
        } else {
            System.out.println("\n---- Transactions for " + searchVendor + " ----");
            System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                    "date","time","description","vendor", "amount");
            System.out.println("--------------------------------------------------------------------------------");

            for (Transaction t : matches) {
                System.out.printf("%-12s %-10s %-25s %-20s %10.2f%n",
                        t.date, t.time, t.description, t.vendor, t.amount);
            }
        }
    }

    //  Display all ledger entries (newest first)
    public static void displayLedger(String filterType) {
        List<Transaction> transactions = readTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
            return;
        }

        System.out.println("\n---- " + filterType + " ----");
        System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                "date", "time", "description", "vendor", "amount");
        System.out.println("--------------------------------------------------------------------------------");

        for (Transaction t : transactions) {
            boolean shouldDisplay = false;

            if (filterType.equals("All")) {
                shouldDisplay = true;
            } else if (filterType.equals("Deposits") && t.amount > 0) {
                shouldDisplay = true;
            } else if (filterType.equals("Payments") && t.amount < 0) {
                shouldDisplay = true;
            }

            if (shouldDisplay) {
                System.out.printf("%-12s %-10s %-25s %-20s $%10.2f%n",
                        t.date, t.time, t.description, t.vendor, t.amount);
            }
        }
    }


    //  Read transactions from file and return as list (newest first)
    public static List<Transaction> readTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File f = new File(filename);

        if (!f.exists() || f.length() == 0) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    Transaction t = new Transaction(
                            parts[0].trim(), // date
                            parts[1].trim(), // time
                            parts[2].trim(), // description
                            parts[3].trim(), // vendor
                            Double.parseDouble(parts[4].trim()) // amount
                    );
                    transactions.add(t);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing transaction amount: " + e.getMessage());
        }

        // Sort newest first by reversing the list
        Collections.reverse(transactions);

        return transactions;
    }

    // Transaction class to hold transaction data
    static class Transaction {
        String date;
        String time;
        String description;
        String vendor;
        double amount;

        Transaction(String date, String time, String description, String vendor, double amount) {
            this.date = date;
            this.time = time;
            this.description = description;
            this.vendor = vendor;
            this.amount = amount;
        }
    }
}