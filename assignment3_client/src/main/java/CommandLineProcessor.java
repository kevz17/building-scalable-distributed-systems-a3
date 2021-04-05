import java.util.Scanner;

/**
 * This CommandLineProcessor utility handles input validation and interaction with users through
 * command line interface
 */
public class CommandLineProcessor {

  private static final String BASE_URL = "http://34.204.185.63:8080";
  private static final int MAX_INT_DIGIT = 10;
  private static final int MIN_NUM_ITEMS_PER_PURCHASE = 1;
  private static final int MAX_NUM_ITEMS_PER_PURCHASE = 20;
  private static final String DEFAULT_MAX_CUST_ID = "1000";
  private static final String DEFAULT_MAX_ITEM_ID = "100000";
  private static final String DEFAULT_NUM_PURCHASES_PER_HOUR = "300";
  private static final String DEFAULT_NUM_ITEMS_PER_PURCHASE = "5";
  private static final String DEFAULT_DATE = "20210328";
  private static final String NUMERIC_PATTERN = "[0-9]+";
  private static final String INTRO_MESSAGE =
      ">> Please enter valid inputs in the following steps to configure the API client, press [Enter] to use default values";
  private static final String TRY_AGAIN = "\n[One or more invalid inputs, please try again]\n";
  private static final String[] COMMAND_LINE_QUESTIONS = new String[]{
      "Enter the maximum number of stores to simulate:",
      "Enter the number of customers per store (default 1,000):",
      "Enter the maximum itemID (default 100,000)",
      "Enter the number of purchases per hour (default 300)",
      "Enter the number of items for each purchase (range 1-20, default 5)",
      "Enter the date (in yyyyMMdd format, default 20210328)",
      "Enter the IP/port address of the server (press [Enter] to use default)"
  };

  /**
   * Process user inputs via command line interface, return user's inputs as String[] array
   *
   * @return user's inputs including maxStores, maxCustID, maxItemID, numPurchasesPerHour,
   * numItemsPerPurchase, date, ipAddress as String[] array
   */
  public static String[] process() {
    // Store processed command line inputs from user
    String[] apiCallParams = new String[COMMAND_LINE_QUESTIONS.length];
    boolean isInputValid = false;

    // Command line interaction with user
    while (!isInputValid) {
      System.out.println(INTRO_MESSAGE);
      for (int i = 0; i < apiCallParams.length; i++) {
        System.out.println((i + 1) + ". " + COMMAND_LINE_QUESTIONS[i]);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        // Check whether to use default
        switch (i) {
          case 1:
            if (input.isEmpty()) {
              input = DEFAULT_MAX_CUST_ID;
            }
            break;
          case 2:
            if (input.isEmpty()) {
              input = DEFAULT_MAX_ITEM_ID;
            }
            break;
          case 3:
            if (input.isEmpty()) {
              input = DEFAULT_NUM_PURCHASES_PER_HOUR;
            }
            break;
          case 4:
            if (input.isEmpty()) {
              input = DEFAULT_NUM_ITEMS_PER_PURCHASE;
            }
            break;
          case 5:
            if (input.isEmpty()) {
              input = DEFAULT_DATE;
            }
            break;
          case 6:
            if (input.isEmpty()) {
              input = BASE_URL;
            }
            break;
        }
        apiCallParams[i] = input;
      }

      // Validation for numeric parameters and server's IP address to call
      if (isStringNumericInt(apiCallParams[0]) &&
          isStringNumericInt(apiCallParams[1]) &&
          isStringNumericInt(apiCallParams[2]) &&
          isStringNumericInt(apiCallParams[3]) &&
          isStringNumericInt(apiCallParams[4]) &&
          Integer.parseInt(apiCallParams[4]) >= MIN_NUM_ITEMS_PER_PURCHASE &&
          Integer.parseInt(apiCallParams[4]) <= MAX_NUM_ITEMS_PER_PURCHASE
      ) {
        isInputValid = true;
      } else {
        System.out.println(TRY_AGAIN);
      }
    }

    return apiCallParams;
  }

  /**
   * Helper method to validate numeric string
   *
   * @param numString - input string parameter
   * @return true if the input parameter contains only digit numbers and ranges from 0 to
   * Integer.MAX_VALUE, otherwise return false
   */
  private static boolean isStringNumericInt(String numString) {
    if (!numString.matches(NUMERIC_PATTERN) || numString.length() > MAX_INT_DIGIT) {
      return false;
    }

    long num = Long.parseLong(numString);
    return num <= Integer.MAX_VALUE && num > 0;
  }
}
