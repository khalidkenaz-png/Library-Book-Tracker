/**
 * Thrown when trying to add a book with an ISBN
 * that already exists in the catalog
 * 
 */
public class DuplicateISBNException extends BookCatalogException {

    /**
     * Creates a new exception with a message explaining the duplicate ISBN error
     * 
     * @param message the error message
     */

    public DuplicateISBNException(String message) {
        super(message);
    }
}
