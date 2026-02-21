/**
 * Thrown when the number of arguments provided is not enough
 */
public class InsufficientArgumentsException extends BookCatalogException {

    /**
     * Creates a new exception with a message explaining the missing arguments
     *
     * @param message the error message
     */
    public InsufficientArgumentsException(String message) {
        super(message);
    }
}
