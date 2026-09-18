package library.model;

public class Book extends LibraryItem {
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String id, String title, String author, String genre, int totalCopies) {
        super(id, title, author);
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getGenre() {
        return genre;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
        // if no copies left mark it issued so search results show it correctly
        this.issued = availableCopies <= 0;
    }

    @Override
    public String getDetails() {
        return String.format("[%s] \"%s\" by %s | Genre: %s | Available: %d/%d",
                id, title, author, genre, availableCopies, totalCopies);
    }

    // used when writing to books.txt, pipe separated so titles with commas don't break anything
    public String toFileString() {
        return id + "|" + title + "|" + author + "|" + genre + "|" + totalCopies + "|" + availableCopies;
    }

    public static Book fromFileString(String line) {
        String[] parts = line.split("\\|");
        Book b = new Book(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
        b.setAvailableCopies(Integer.parseInt(parts[5]));
        return b;
    }
}
