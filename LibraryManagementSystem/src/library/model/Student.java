package library.model;

public class Student extends Member {
    private static final int MAX_BOOKS = 3;
    private static final int BORROW_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0;

    public Student(String id, String name, String email) {
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
        return "STUDENT";
    }
}
