/**
 * Thrown when the given file name is not valid
 * this happens when the file does not end with .txt
 */
public class InvalidFileNameException extends BookCatalogException {

    /**
     * Creates a new exception with a message explaining the invalid file name
     *
     * @param message the error message
     */
    public InvalidFileNameException(String message) {
        super(message);
    }
}
