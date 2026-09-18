package library.model;

import java.util.ArrayList;
import java.util.List;

// Student and Faculty both extend this. Borrow limits, due dates and fine
// rate are different for each, so those are left abstract and overridden
// (this is the polymorphism part - Library class just calls member.calculateFine()
// without caring whether it's a Student or Faculty underneath)
public abstract class Member {
    protected String id;
    protected String name;
    protected String email;
    protected List<String> issuedBookIds;

    public Member(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.issuedBookIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getIssuedBookIds() {
        return issuedBookIds;
    }

    public abstract int getMaxBooksAllowed();

    public abstract int getBorrowDurationDays();

    public abstract double calculateFine(long daysLate);

    public abstract String getMemberType();

    public String toFileString() {
        return id + "|" + name + "|" + email + "|" + getMemberType();
    }
}
