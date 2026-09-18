package library.model;

public class Faculty extends Member {
    private static final int MAX_BOOKS = 5;
    private static final int BORROW_DAYS = 30;
    private static final double FINE_PER_DAY = 2.0; // faculty get a lighter fine rate

    public Faculty(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public int getMaxBooksAllowed() {
        return MAX_BOOKS;
    }

    @Override
    public int getBorrowDurationDays() {
        return BORROW_DAYS;
    }

    @Override
    public double calculateFine(long daysLate) {
        if (daysLate <= 0) return 0.0;
        return daysLate * FINE_PER_DAY;
    }

    @Override
    public String getMemberType() {
        return "FACULTY";
    }
}
