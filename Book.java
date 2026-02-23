/**
 * Represents a single book in the catalog.
 * Each book has a title, author, ISBN, and number of copies.
 */
public class Book {

    private String title;
    private String author;
    private String isbn;
    private int copies;

    /**
     * Creates a new Book object with the given details
     * 
     * @param title  the title of the book
     * @param author the author of the book
     * @param isbn   the 13-digit ISBN of the book
     * @param copies the number of available copies must be positive
     * @param isbn
     * @param copies
     */
    public Book(String title, String author, String isbn, int copies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.copies = copies;
    }

    /**
     * 
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * 
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * 
     * @return the number of copies available
     */
    public int getCopies() {
        return copies;
    }

    /**
     * converts the book into a catalog line format
     * 
     * @return a string in the format title:author:isbn:copies
     */
    public String toCatalogLine() {
        return title + ":" + author + ":" + isbn + ":" + copies;
    }
}
