package library.exceptions;

// thrown when someone tries to issue a book that is already issued to another member
public class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
