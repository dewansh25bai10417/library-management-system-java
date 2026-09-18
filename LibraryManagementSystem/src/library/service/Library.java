package library.service;

import library.exceptions.*;
import library.model.*;
import library.util.FileManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Library implements Issuable {
    private Map<String, Book> bookCatalog;
    private Map<String, Member> memberRegistry;
    private List<Transaction> transactionHistory;
    private FileManager fileManager;
    private int transactionCounter;

    public Library() {
        fileManager = new FileManager();
        bookCatalog = fileManager.loadBooks();
        memberRegistry = fileManager.loadMembers();
        // synchronizedList because OverdueChecker (a background thread) reads
        // this list while the main thread might be adding new transactions
        transactionHistory = Collections.synchronizedList(fileManager.loadTransactions());
        transactionCounter = transactionHistory.size();

        // rebuild each member's issuedBookIds list from transaction history
        // since we only persist the flat transaction log to disk
        for (Transaction t : transactionHistory) {
            if (!t.isReturned()) {
                Member m = memberRegistry.get(t.getMemberId());
                if (m != null && !m.getIssuedBookIds().contains(t.getBookId())) {
                    m.getIssuedBookIds().add(t.getBookId());
                }
            }
        }
    }

    // ---------- Book management ----------

    public void addBook(Book book) {
        bookCatalog.put(book.getId(), book);
        saveAll();
    }

    public void removeBook(String bookId) throws BookNotFoundException {
        if (!bookCatalog.containsKey(bookId)) {
            throw new BookNotFoundException("No book found with id " + bookId);
        }
        bookCatalog.remove(bookId);
        saveAll();
    }

    public List<Book> searchByTitle(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : bookCatalog.values()) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    public List<Book> searchByAuthor(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : bookCatalog.values()) {
            if (b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    public Collection<Book> getAllBooks() {
        return bookCatalog.values();
    }

    // ---------- Member management ----------

    public void registerMember(Member member) {
        memberRegistry.put(member.getId(), member);
        saveAll();
    }

    public Collection<Member> getAllMembers() {
        return memberRegistry.values();
    }

    // ---------- Issue / return (Issuable) ----------

    @Override
    public synchronized void issueBook(String bookId, String memberId)
            throws BookNotFoundException, MemberNotFoundException, BookNotAvailableException, BorrowLimitExceededException {

        Book book = bookCatalog.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("No book found with id " + bookId);
        }
        Member member = memberRegistry.get(memberId);
        if (member == null) {
            throw new MemberNotFoundException("No member found with id " + memberId);
        }
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("\"" + book.getTitle() + "\" has no copies available right now");
        }
        if (member.getIssuedBookIds().size() >= member.getMaxBooksAllowed()) {
            throw new BorrowLimitExceededException(member.getName() + " already has the max "
                    + member.getMaxBooksAllowed() + " books issued");
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(member.getBorrowDurationDays());
        transactionCounter++;
        Transaction t = new Transaction("T" + transactionCounter, bookId, memberId, today, dueDate);
        transactionHistory.add(t);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        member.getIssuedBookIds().add(bookId);

        saveAll();
    }

    @Override
    public synchronized double returnBook(String bookId, String memberId)
            throws BookNotFoundException, MemberNotFoundException {

        Book book = bookCatalog.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("No book found with id " + bookId);
        }
        Member member = memberRegistry.get(memberId);
        if (member == null) {
            throw new MemberNotFoundException("No member found with id " + memberId);
        }

        // find the open transaction for this book+member combo
        Transaction openTransaction = null;
        for (Transaction t : transactionHistory) {
            if (t.getBookId().equals(bookId) && t.getMemberId().equals(memberId) && !t.isReturned()) {
                openTransaction = t;
                break;
            }
        }

        if (openTransaction == null) {
            System.out.println("No record of this member having borrowed that book.");
            return 0.0;
        }

        LocalDate today = LocalDate.now();
        openTransaction.setReturnDate(today);

        long daysLate = ChronoUnit.DAYS.between(openTransaction.getDueDate(), today);
        double fine = member.calculateFine(daysLate);
        openTransaction.setFineCollected(fine);

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        member.getIssuedBookIds().remove(bookId);

        saveAll();
        return fine;
    }

    // ---------- Reports ----------

    public List<Transaction> getOverdueTransactions() {
        List<Transaction> overdue = new ArrayList<>();
        LocalDate today = LocalDate.now();
        synchronized (transactionHistory) {
            for (Transaction t : transactionHistory) {
                if (!t.isReturned() && t.getDueDate().isBefore(today)) {
                    overdue.add(t);
                }
            }
        }
        return overdue;
    }

    public Book getBook(String bookId) {
        return bookCatalog.get(bookId);
    }

    public Member getMember(String memberId) {
        return memberRegistry.get(memberId);
    }

    private void saveAll() {
        fileManager.saveBooks(bookCatalog);
        fileManager.saveMembers(memberRegistry);
        fileManager.saveTransactions(transactionHistory);
    }
}
