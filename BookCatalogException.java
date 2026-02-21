/**
 * This class is for handling errors related to the book catalog
 * it is thrown when a catalog operation fails
 */
public class BookCatalogException extends Exception {

    /**
     * creates a new exception with a message explaining the error
     * 
     * @param message the error message
     */
    public BookCatalogException(String message) {
        super(message);
    }
}
