package library.exceptions;

// thrown when a member has already reached their max allowed books
// (students can hold 3, faculty can hold 5 - see Member subclasses)
public class BorrowLimitExceededException extends Exception {
    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
