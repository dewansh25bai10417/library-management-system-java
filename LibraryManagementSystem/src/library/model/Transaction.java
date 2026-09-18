package library.model;

import java.time.LocalDate;

public class Transaction {
    private String transactionId;
    private String bookId;
    private String memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null while the book is still out
    private double fineCollected;

    public Transaction(String transactionId, String bookId, String memberId,
                        LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fineCollected = 0.0;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineCollected() {
        return fineCollected;
    }

    public void setFineCollected(double fineCollected) {
        this.fineCollected = fineCollected;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public String toFileString() {
        return transactionId + "|" + bookId + "|" + memberId + "|" + issueDate + "|"
                + dueDate + "|" + (returnDate == null ? "NULL" : returnDate) + "|" + fineCollected;
    }

    public static Transaction fromFileString(String line) {
        String[] p = line.split("\\|");
        Transaction t = new Transaction(p[0], p[1], p[2], LocalDate.parse(p[3]), LocalDate.parse(p[4]));
        if (!p[5].equals("NULL")) {
            t.setReturnDate(LocalDate.parse(p[5]));
        }
        t.setFineCollected(Double.parseDouble(p[6]));
        return t;
    }
}
