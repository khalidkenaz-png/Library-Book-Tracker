import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * A simple tool to manage a library book catalog stored in a text file
 * Supports searching by title or ISBN, and adding new books
 */
public class LibraryBookTracker {

    private static int validRecordsProcessed = 0;
    private static int searchResults = 0;
    private static int booksAdded = 0;
    private static int errorsEncountered = 0;
    // Column widths for formatted console output
    private static final int COL_TITLE = 30;
    private static final int COL_AUTHOR = 20;
    private static final int COL_ISBN = 15;
    private static final int COL_COPIES = 5;
    // Timestamp format used when writing to the error log
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Entry point of the program. Reads the catalog file and performs
     * the requested operation based on the given arguments
     *
     * @param args expects at least 2 arguments: catalog file path and operation
     */
    public static void main(String[] args) {

        try {
            if (args.length < 2) {
                throw new InsufficientArgumentsException(
                        "At least 2 arguments required: <catalogFile.txt> <operation>");
            }

            String catalogPath = args[0];
            String operation = args[1];

            if (!catalogPath.endsWith(".txt")) {
                throw new InvalidFileNameException(
                        "Catalog file must end with .txt, got: " + catalogPath);
            }

            ensureFileExists(catalogPath);
            String errorLogPath = deriveErrorLogPath(catalogPath);
            List<Book> catalog = readCatalog(catalogPath, errorLogPath);
            if (isIsbn(operation)) {

                searchByIsbn(catalog, operation, errorLogPath);

            } else if (isNewBookRecord(operation)) {

                addBook(catalog, operation, catalogPath, errorLogPath);

            } else {

                searchByTitle(catalog, operation);
            }

        } catch (InsufficientArgumentsException | InvalidFileNameException e) {

            System.out.println("Error: " + e.getMessage());
            errorsEncountered++;

        } catch (IOException e) {

            System.out.println("I/O Error: " + e.getMessage());
            errorsEncountered++;

        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            errorsEncountered++;

        } finally {

            System.out.println("\n--- Statistics ---");
            System.out.println("Valid records processed : " + validRecordsProcessed);
            System.out.println("Search results          : " + searchResults);
            System.out.println("Books added             : " + booksAdded);
            System.out.println("Errors encountered      : " + errorsEncountered);
            System.out.println("\nThank you for using the Library Book Tracker.");
        }
    }

    /**
     * Creates the catalog file if it does not already exist
     * including any missing parent directories.
     *
     * @param catalogPath path to the catalog file
     * @throws IOException if the file could not be created
     */

    private static void ensureFileExists(String catalogPath) throws IOException {
        File file = new File(catalogPath);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
            System.out.println("Info: Created new catalog file: " + catalogPath);
        }
    }

    /**
     * Builds the error log file path based on the catalog file location
     * 
     * @param catalogPath path to the catalog file
     * @return path to the error log file
     */
    private static String deriveErrorLogPath(String catalogPath) {
        File catalog = new File(catalogPath);
        File parent = catalog.getParentFile();
        if (parent == null) {
            return "errors.log";
        }
        return new File(parent, "errors.log").getPath();
    }

    /**
     * Reads all valid book records from the catalog file
     * invalid lines are logged to the error log and skipped
     * 
     * @param catalogPath  path to the catalog file
     * @param errorLogPath path to the error log file
     * @return list of valid books read from the file
     * @throws IOException if the file could not be read
     */

    private static List<Book> readCatalog(String catalogPath, String errorLogPath)
            throws IOException {

        List<Book> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(catalogPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                try {
                    Book book = parseLine(line);
                    books.add(book);
                    validRecordsProcessed++;
                } catch (BookCatalogException e) {
                    errorsEncountered++;
                    logError(errorLogPath, line, e);
                }
            }
        }
        return books;
    }

    /**
     * Parses a single line from the catalog into a Book object
     * Expected format: Title:Author:ISBN:Copies
     *
     * @param line the line to parse
     * @return a Book object with the parsed data
     * @throws MalformedBookEntryException if the line format is wrong or fields are
     *                                     missing
     * @throws InvalidISBNException        if the ISBN is not exactly 13 digits
     */
    private static Book parseLine(String line)
            throws MalformedBookEntryException, InvalidISBNException {

        String[] parts = line.split(":", 4);

        if (parts.length < 4) {
            throw new MalformedBookEntryException(
                    "Expected 4 fields (Title:Author:ISBN:Copies), found: " + parts.length);
        }

        String title = parts[0].trim();
        String author = parts[1].trim();
        String isbn = parts[2].trim();
        String copiesS = parts[3].trim();

        if (title.isEmpty()) {
            throw new MalformedBookEntryException("Title field is empty");
        }
        if (author.isEmpty()) {
            throw new MalformedBookEntryException("Author field is empty");
        }

        validateIsbn(isbn);

        int copies;
        try {
            copies = Integer.parseInt(copiesS);
        } catch (NumberFormatException ex) {
            throw new MalformedBookEntryException(
                    "Copies field is not a valid integer: \"" + copiesS + "\"");
        }
        if (copies <= 0) {
            throw new MalformedBookEntryException(
                    "Copies must be a positive integer, got: " + copies);
        }

        return new Book(title, author, isbn, copies);
    }

    /**
     * Searches the catalog for books whose title contains the given keyword
     * The search is case-insensitive
     * 
     * @param catalog list of books to search through
     * @param keyword the keyword to look for in book titles
     */

    private static void searchByTitle(List<Book> catalog, String keyword) {
        System.out.println("\n Title Search: \"" + keyword + "\"");
        printHeader();

        String lowerKey = keyword.toLowerCase();
        int found = 0;

        for (Book book : catalog) {
            if (book.getTitle().toLowerCase().contains(lowerKey)) {
                printBook(book);
                found++;
            }
        }

        searchResults = found;
        System.out.println("Found " + found + " result.");
    }

    /**
     * Searches the catalog for a book with the given ISBN
     * Logs an error if more than one book has the same ISBN
     *
     * @param catalog      list of books to search through
     * @param isbn         the ISBN to search for
     * @param errorLogPath path to the error log file
     */
    private static void searchByIsbn(List<Book> catalog, String isbn,
            String errorLogPath) {
        System.out.println("\n ISBN Search: " + isbn + ")");

        List<Book> matches = new ArrayList<>();
        for (Book book : catalog) {
            if (book.getIsbn().equals(isbn)) {
                matches.add(book);
            }
        }

        try {
            if (matches.size() > 1) {
                throw new DuplicateISBNException(
                        "Found " + matches.size() + " books with ISBN " + isbn);
            }

            printHeader();
            if (matches.isEmpty()) {
                System.out.println("No book found with ISBN: " + isbn);
                searchResults = 0;
            } else {
                printBook(matches.get(0));
                searchResults = 1;
            }

        } catch (DuplicateISBNException e) {
            errorsEncountered++;
            logError(errorLogPath, isbn, e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Parses the given record and adds the book to the catalog file
     * The catalog is sorted alphabetically by title after adding
     *
     * @param catalog      current list of books
     * @param record       the new book record in Title:Author:ISBN:Copies format
     * @param catalogPath  path to the catalog file
     * @param errorLogPath path to the error log file
     * @throws IOException if the file could not be written
     */
    private static void addBook(List<Book> catalog, String record,
            String catalogPath, String errorLogPath)
            throws IOException {

        System.out.println("\n Add Book ");

        try {
            Book newBook = parseLine(record);

            catalog.add(newBook);
            catalog.sort(Comparator.comparing(b -> b.getTitle().toLowerCase()));

            try (PrintWriter pw = new PrintWriter(new FileWriter(catalogPath, false))) {
                for (Book b : catalog) {
                    pw.println(b.toCatalogLine());
                }
            }

            printHeader();
            printBook(newBook);
            booksAdded = 1;
            System.out.println("Book added successfully to the catalog.");

        } catch (BookCatalogException e) {

            errorsEncountered++;
            logError(errorLogPath, record, e);
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    /**
     * prints the table header for the book list output
     */
    private static void printHeader() {
        System.out.printf(
                "%-" + COL_TITLE + "s %-" + COL_AUTHOR + "s %-"
                        + COL_ISBN + "s %" + COL_COPIES + "s%n",
                "Title", "Author", "ISBN", "Copies");
        System.out.println("-".repeat(COL_TITLE + COL_AUTHOR + COL_ISBN + COL_COPIES + 3));
    }

    /**
     * prints a single book's details in a formatted table row
     * 
     * @param book the book to print
     */
    private static void printBook(Book book) {
        System.out.printf(
                "%-" + COL_TITLE + "s %-" + COL_AUTHOR + "s %-"
                        + COL_ISBN + "s %" + COL_COPIES + "d%n",
                book.getTitle(), book.getAuthor(), book.getIsbn(), book.getCopies());
    }

    /**
     * Checks if the given string is a valid 13-digit ISBN
     *
     * @param s the string to check
     * @return true if it matches 13 digits, false otherwise
     */
    private static boolean isIsbn(String s) {
        return s.matches("\\d{13}");
    }

    /**
     * Checks if the given string looks like a new book record
     * by verifying it has exactly 4 colon-separated fields
     *
     * @param s the string to check
     * @return true if it has 4 fields, false otherwise
     */
    private static boolean isNewBookRecord(String s) {
        return s.split(":", -1).length == 4;
    }

    /**
     * Validates that the ISBN is exactly 13 digits
     *
     * @param isbn the ISBN string to validate
     * @throws InvalidISBNException if the ISBN format is wrong
     */
    private static void validateIsbn(String isbn) throws InvalidISBNException {
        if (!isbn.matches("\\d{13}")) {
            throw new InvalidISBNException(
                    "ISBN must be exactly 13 digits, got: \"" + isbn + "\"");
        }
    }

    /**
     * Logs an error to the error log file with a timestamp
     *
     * @param errorLogPath path to the error log file
     * @param offending    the line or value that caused the error
     * @param e            the exception that was thrown
     */
    private static void logError(String errorLogPath, String offending,
            BookCatalogException e) {
        String timestamp = LocalDateTime.now().format(DT_FMT);
        String entry = "[" + timestamp + "] INVALID LINE: \""
                + offending + "\" - "
                + e.getClass().getSimpleName() + ": " + e.getMessage();

        try (PrintWriter pw = new PrintWriter(new FileWriter(errorLogPath, true))) {
            pw.println(entry);
        } catch (IOException ioEx) {
            System.out.println("Warning: Could not write to error log: "
                    + ioEx.getMessage());
        }
    }
}
