package library;

import library.exceptions.*;
import library.model.*;
import library.service.Library;
import library.service.OverdueChecker;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Library library = new Library();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // start the overdue-checker on its own thread so it doesn't
        // freeze up the menu while it's "sleeping" between checks
        OverdueChecker checker = new OverdueChecker(library, 60);
        Thread checkerThread = new Thread(checker);
        checkerThread.setDaemon(true); // dies automatically when main() ends
        checkerThread.start();

        System.out.println("=========================================");
        System.out.println(" Library Management System");
        System.out.println("=========================================");

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: addBook(); break;
                case 2: removeBook(); break;
                case 3: searchBook(); break;
                case 4: registerMember(); break;
                case 5: issueBook(); break;
                case 6: returnBook(); break;
                case 7: viewOverdue(); break;
                case 8: viewAllBooks(); break;
                case 9: viewAllMembers(); break;
                case 0:
                    exit = true;
                    System.out.println("Data saved. Bye!");
                    break;
                default:
                    System.out.println("That's not a valid option, try again.");
            }
        }
        checker.stopChecking();
    }

    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Add Book");
        System.out.println("2. Remove Book");
        System.out.println("3. Search Book");
        System.out.println("4. Register Member");
        System.out.println("5. Issue Book");
        System.out.println("6. Return Book");
        System.out.println("7. View Overdue Books");
        System.out.println("8. View All Books");
        System.out.println("9. View All Members");
        System.out.println("0. Exit");
    }

    private static void addBook() {
        try {
            System.out.print("Book ID: ");
            String id = sc.nextLine().trim();
            if (id.isEmpty()) throw new InvalidInputException("Book ID can't be empty");
            System.out.print("Title: ");
            String title = sc.nextLine().trim();
            System.out.print("Author: ");
            String author = sc.nextLine().trim();
            System.out.print("Genre: ");
            String genre = sc.nextLine().trim();
            int copies = readInt("Number of copies: ");

            Book book = new Book(id, title, author, genre, copies);
            library.addBook(book);
            System.out.println("Book added successfully.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeBook() {
        System.out.print("Enter Book ID to remove: ");
        String id = sc.nextLine().trim();
        try {
            library.removeBook(id);
            System.out.println("Book removed.");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchBook() {
        System.out.println("Search by: 1. Title  2. Author");
        int choice = readInt("Choice: ");
        System.out.print("Enter keyword: ");
        String keyword = sc.nextLine().trim();

        List<Book> results = (choice == 2) ? library.searchByAuthor(keyword) : library.searchByTitle(keyword);
        if (results.isEmpty()) {
            System.out.println("No books matched \"" + keyword + "\".");
        } else {
            for (Book b : results) {
                System.out.println(b.getDetails());
            }
        }
    }

    private static void registerMember() {
        try {
            System.out.print("Member ID: ");
            String id = sc.nextLine().trim();
            if (id.isEmpty()) throw new InvalidInputException("Member ID can't be empty");
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Email: ");
            String email = sc.nextLine().trim();
            System.out.print("Type (1-Student / 2-Faculty): ");
            String type = sc.nextLine().trim();

            Member member;
            if (type.equals("2")) {
                member = new Faculty(id, name, email);
            } else {
                member = new Student(id, name, email);
            }
            library.registerMember(member);
            System.out.println("Member registered as " + member.getMemberType() + ".");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void issueBook() {
        System.out.print("Book ID: ");
        String bookId = sc.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = sc.nextLine().trim();
        try {
            library.issueBook(bookId, memberId);
            System.out.println("Book issued. Due date is "
                    + library.getMember(memberId).getBorrowDurationDays() + " days from today.");
        } catch (BookNotFoundException | MemberNotFoundException | BookNotAvailableException
                | BorrowLimitExceededException e) {
            System.out.println("Could not issue book: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.print("Book ID: ");
        String bookId = sc.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = sc.nextLine().trim();
        try {
            double fine = library.returnBook(bookId, memberId);
            if (fine > 0) {
                System.out.printf("Book returned. Late fine: Rs. %.2f%n", fine);
            } else {
                System.out.println("Book returned on time, no fine.");
            }
        } catch (BookNotFoundException | MemberNotFoundException e) {
            System.out.println("Could not return book: " + e.getMessage());
        }
    }

    private static void viewOverdue() {
        var overdue = library.getOverdueTransactions();
        if (overdue.isEmpty()) {
            System.out.println("Nothing overdue right now.");
            return;
        }
        System.out.println("Overdue books:");
        for (var t : overdue) {
            Book b = library.getBook(t.getBookId());
            Member m = library.getMember(t.getMemberId());
            String title = (b != null) ? b.getTitle() : t.getBookId();
            String name = (m != null) ? m.getName() : t.getMemberId();
            System.out.println("- \"" + title + "\" with " + name + " (due " + t.getDueDate() + ")");
        }
    }

    private static void viewAllBooks() {
        Collection<Book> books = library.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the catalog yet.");
        }
        for (Book b : books) {
            System.out.println(b.getDetails());
        }
    }

    private static void viewAllMembers() {
        Collection<Member> members = library.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members registered yet.");
        }
        for (Member m : members) {
            System.out.println("[" + m.getId() + "] " + m.getName() + " (" + m.getMemberType()
                    + ") - books held: " + m.getIssuedBookIds().size() + "/" + m.getMaxBooksAllowed());
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
