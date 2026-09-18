package library.util;

import library.model.*;

import java.io.*;
import java.util.*;

// all the read/write to disk lives here so the rest of the app doesn't
// need to care about file paths or BufferedReader/Writer boilerplate
public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String BOOKS_FILE = DATA_DIR + "/books.txt";
    private static final String MEMBERS_FILE = DATA_DIR + "/members.txt";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.txt";

    public FileManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveBooks(Map<String, Book> books) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books.values()) {
                bw.write(b.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    public Map<String, Book> loadBooks() {
        Map<String, Book> books = new HashMap<>();
        File f = new File(BOOKS_FILE);
        if (!f.exists()) return books;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Book b = Book.fromFileString(line);
                books.put(b.getId(), b);
            }
        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    public void saveMembers(Map<String, Member> members) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : members.values()) {
                bw.write(m.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving members: " + e.getMessage());
        }
    }

    public Map<String, Member> loadMembers() {
        Map<String, Member> members = new HashMap<>();
        File f = new File(MEMBERS_FILE);
        if (!f.exists()) return members;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                Member m;
                if (p[3].equals("FACULTY")) {
                    m = new Faculty(p[0], p[1], p[2]);
                } else {
                    m = new Student(p[0], p[1], p[2]);
                }
                members.put(m.getId(), m);
            }
        } catch (IOException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
        return members;
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction t : transactions) {
                bw.write(t.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File f = new File(TRANSACTIONS_FILE);
        if (!f.exists()) return transactions;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                transactions.add(Transaction.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }
}
