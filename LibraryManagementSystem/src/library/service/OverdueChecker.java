package library.service;

import library.model.Transaction;
import java.util.List;

// Runs in the background the whole time the program is open and just
// keeps an eye on overdue books, printing a reminder every so often.
// This is the multithreading part of the project - it runs alongside
// the main menu thread without blocking it.
public class OverdueChecker implements Runnable {
    private Library library;
    private volatile boolean running = true;
    private final int intervalSeconds;

    public OverdueChecker(Library library, int intervalSeconds) {
        this.library = library;
        this.intervalSeconds = intervalSeconds;
    }

    public void stopChecking() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(intervalSeconds * 1000L);
                List<Transaction> overdue = library.getOverdueTransactions();
                if (!overdue.isEmpty()) {
                    System.out.println("\n[Background check] " + overdue.size()
                            + " book(s) are currently overdue. Choose option 7 from the menu to view details.");
                    System.out.print("Enter your choice: "); // reprint the prompt so it doesn't look broken
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}
