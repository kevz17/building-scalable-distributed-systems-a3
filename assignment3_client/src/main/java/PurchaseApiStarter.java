import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * The entry point of the program that performs command line interactions with users and then mimics
 * the purchase requests from various time zones by generating and sending synthetic item purchases
 * to a server in the cloud
 */
public class PurchaseApiStarter {

  private static final int PURCHASE_HOURS_3 = 3;
  private static final int PURCHASE_HOURS_5 = 5;
  private static final int PURCHASE_HOURS_9 = 9;
  private static final double SEC_MILLISEC_CONVERSION = 1000.0;
  private static final String[] HEADERS = {
      "Start Time (millisec)", "Request Type", "Latency (millisec)", "Response Code"};
  private static final String START_RUNNING = "Start running ...\n";
  private static final String END_RUNNING = "Running completed, showing result ...\n";
  private static final String RESULT_WRITING = "\nRunning result has been written to ";
  private static final String ERROR_LOG = "error_log.txt";
  private static final String OUTPUT_FILE_NAME = "assignment2_client_thread";
  private static int numSuccessReq = 0;
  private static int numUnsuccessReq = 0;
  private static List<List<String>> recordLines = new ArrayList<>();

  synchronized public static void incrementSuccessReq() {
    numSuccessReq++;
  }

  synchronized public static void incrementUnsuccessReq() {
    numUnsuccessReq++;
  }

  synchronized public static void addRecordLine(List<String> line) {
    recordLines.add(line);
  }

  /**
   * Process command line inputs, mimic purchasing, then present statistics results
   * String[] params mapping:
   * params[0] -> maxStores
   * params[1] -> maxCustID
   * params[2] -> maxItemID
   * params[3] -> numPurchasesPerHour
   * params[4] -> numItemsPerPurchase
   * params[5] -> date
   * params[6] -> ipAddress
   */
  public static void main(String[] args) throws InterruptedException, IOException {

    // Process and validate command line inputs from user
    String[] params = CommandLineProcessor.process();
    // Extract these two parameters to calculate countdown latch counts
    int maxStores = Integer.parseInt(params[0]);
    int numPurchasesPerHour = Integer.parseInt(params[3]);

    // Control initiating Phase 2
    int countDownPhase1 = numPurchasesPerHour * PURCHASE_HOURS_3 * (maxStores / 4);
    // Control initiating Phase 3
    int countDownPhase2 = numPurchasesPerHour * PURCHASE_HOURS_5 * (maxStores / 2);
    // Control waiting for all threads to complete, cleanly terminate
    int countDownPhase3 = numPurchasesPerHour * PURCHASE_HOURS_9 * maxStores;

    CountDownLatch countDownLatch1 = new CountDownLatch(countDownPhase1);
    CountDownLatch countDownLatch2 = new CountDownLatch(countDownPhase2);
    CountDownLatch countDownLatch3 = new CountDownLatch(countDownPhase3);
    CountDownLatch[] countDownLatch = new CountDownLatch[]{
        countDownLatch1, countDownLatch2, countDownLatch3};

    // Redirect stderr to be written into a txt file
    PrintStream errOut = null;
    try {
      errOut = new PrintStream(new FileOutputStream(ERROR_LOG));
      System.setErr(errOut);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    System.out.println(START_RUNNING);
    // Record the start of wall time
    long startTime = System.currentTimeMillis();

    // Start running
    for (int i = 1; i <= 3; i++) {
      PhaseThread phase = new PhaseThread(i, params, countDownLatch);
      Thread phaseThread = new Thread(phase);
      phaseThread.start();
      countDownLatch[i - 1].await();
    }

    // Record the end of wall time
    long endTime = System.currentTimeMillis();

    // Calculate results
    long totalRunTime = Math.round((endTime - startTime) / SEC_MILLISEC_CONVERSION);
    long throughPut = Math.round(numSuccessReq / ((endTime - startTime) / SEC_MILLISEC_CONVERSION));
    long[] latencyStats = LatencyCalculator.calculateTotalLatencyStats(recordLines);

    System.out.println(END_RUNNING);
    System.out.println("Number of thread:     [ " + maxStores + " ]");
    System.out.println("Total wall time:      [ " + totalRunTime + " sec ]");
    System.out.println("Throughput:           [ " + throughPut + " req/sec ]");
    System.out.println("Success/Fail request: [ " + numSuccessReq + " / " + numUnsuccessReq + " ]");
    System.out.println("Mean latency:         [ " + latencyStats[0] + " millisec ]");
    System.out.println("Median latency:       [ " + latencyStats[1] + " millisec ]");
    System.out.println("Percentile99 latency: [ " + latencyStats[2] + " millisec ]");
    System.out.println("Max latency:          [ " + latencyStats[3] + " millisec ]");

    // Write results to CSV file
    String fileName = OUTPUT_FILE_NAME + maxStores;
    OutputProcessor.writeToCsv(recordLines, Arrays.asList(HEADERS), fileName);
    System.out.println(RESULT_WRITING + fileName);
  }
}