package library.exceptions;

// thrown when a book id/isbn doesn't exist in the catalog
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}
