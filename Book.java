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
     * Creates a new Book with its details
     * 
     * @param title       book title
     * @param authorparam book author
     * @param isbn        13-digit ISBN
     * @param copies      number of copies must be positive
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
     * @return the book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * 
     * @return the ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * 
     * @return the number of copies
     */
    public int getCopies() {
        return copies;
    }

    /**
     * converts this book into one line for the catalog file
     * 
     * @return string in format: title:author:isbn:copies
     */
    public String toCatalogLine() {
        return title + ":" + author + ":" + isbn + ":" + copies;
    }
}
