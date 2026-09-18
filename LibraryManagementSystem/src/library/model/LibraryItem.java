package library.model;

// base class for anything the library owns. Right now we only have Book,
// but keeping this abstract means we could add EBook/Magazine later without
// touching the rest of the code
public abstract class LibraryItem {
    protected String id;
    protected String title;
    protected String author;
    protected boolean issued;

    public LibraryItem(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.issued = false;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isIssued() {
        return issued;
    }

    public void setIssued(boolean issued) {
        this.issued = issued;
    }

    // every item type has to say how it wants to be printed
    public abstract String getDetails();
}
