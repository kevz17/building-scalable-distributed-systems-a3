import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.PurchaseApi;
import io.swagger.client.model.Purchase;
import io.swagger.client.model.PurchaseItems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This StoreThread class represents a store handles purchase requests from customers
 */
public class StoreThread implements Runnable {

  private static final int ITEM_AMOUNT = 1;
  private static final int PURCHASE_HOURS_9 = 9;
  private static final int MAX_RETRY_COUNT = 10;
  private static final String REQUEST_TYPE = "POST";
  private static final String RESPONSE_CODE_SUCCESS = "201";
  private int storeID;
  private final String[] params;
  private CountDownLatch[] countDownLatch;

  public StoreThread(int storeID, String[] params, CountDownLatch[] countDownLatch) {
    this.storeID = storeID;
    this.params = params;
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void run() {
    int maxCustID = Integer.parseInt(params[1]);
    int maxItemID = Integer.parseInt(params[2]);
    int numPurchasesPerHour = Integer.parseInt(params[3]);
    int numItemsPerPurchase = Integer.parseInt(params[4]);
    String date = params[5];
    String ipAddress = params[6];

    int totalNumPurchases = 0;
    int endPoint = numPurchasesPerHour * PURCHASE_HOURS_9;

    PurchaseApi apiInstance = new PurchaseApi();
    ApiClient apiClient = apiInstance.getApiClient();
    apiClient.setBasePath(ipAddress);

    // Make purchases (post requests to server)
    while (totalNumPurchases < endPoint) {

      Purchase purchaseBody = new Purchase();
      Integer custID = ThreadLocalRandom.current()
          .nextInt(this.storeID * 1000, this.storeID * 1000 + maxCustID);

      for (int i = 0; i < numItemsPerPurchase; i++) {
        PurchaseItems itemBody = new PurchaseItems();
        String itemID = String.valueOf(ThreadLocalRandom.current().nextInt(1, maxItemID + 1));
        itemBody.setItemID(itemID);
        itemBody.setNumberOfItems(ITEM_AMOUNT);
        purchaseBody.addItemsItem(itemBody);
      }

      String responseCode = "";
      long startTime = System.currentTimeMillis();

      // Limited number of retries
      int retryCount = -1;
      while (!responseCode.equals(RESPONSE_CODE_SUCCESS) && retryCount <= MAX_RETRY_COUNT) {
        try {
          retryCount++;
          ApiResponse<Void> res = apiInstance
              .newPurchaseWithHttpInfo(purchaseBody, this.storeID, custID, date);
          PurchaseApiStarter.incrementSuccessReq();
          responseCode = String.valueOf(res.getStatusCode());
        } catch (ApiException e) {
          PurchaseApiStarter.incrementUnsuccessReq();
          responseCode = String.valueOf(e.getCode());
          e.printStackTrace();
        }
      }

      long endTime = System.currentTimeMillis();
      totalNumPurchases++;

      for (CountDownLatch latch : this.countDownLatch) {
        latch.countDown();
      }

      // Send each thread's result to main
      List<String> recordLine = new ArrayList<>();
      String latency = String.valueOf(endTime - startTime);
      recordLine.add(String.valueOf(startTime));
      recordLine.add(REQUEST_TYPE);
      recordLine.add(latency);
      recordLine.add(responseCode);
      PurchaseApiStarter.addRecordLine(recordLine);
    }
  }
}
