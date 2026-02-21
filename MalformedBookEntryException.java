/**
 * Thrown when a book entry in the catalog has a wrong or missing format
 */
public class MalformedBookEntryException extends BookCatalogException {

    /**
     * Creates a new exception with a message explaining the malformed entry
     *
     * @param message the error message
     */
    public MalformedBookEntryException(String message) {
        super(message);
    }
}
