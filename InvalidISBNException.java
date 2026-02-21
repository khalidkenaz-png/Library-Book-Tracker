/**
 * Thrown when the ISBN format is not valid
 * This happens when the ISBN is not exactly 13 digits
 * or contains non-numeric characters.
 */
public class InvalidISBNException extends BookCatalogException {

    /**
     * Creates a new exception with a message explaining the invalid ISBN
     * 
     * @param message the error message
     */
    public InvalidISBNException(String message) {
        super(message);
    }
}
