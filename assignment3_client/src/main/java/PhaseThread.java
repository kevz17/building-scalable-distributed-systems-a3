import java.util.concurrent.CountDownLatch;

/**
 * This PhaseThread class represents a collection of stores that is in a particular phase, mimicking
 * stores located at different time zones across the U.S.
 */
public class PhaseThread implements Runnable {

  private final int phaseID;
  private final String[] params;
  private CountDownLatch[] countDownLatch;

  public PhaseThread(int phaseID, String[] params, CountDownLatch[] countDownLatch) {
    this.phaseID = phaseID;
    this.params = params;
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void run() {
    int maxStores = Integer.parseInt(params[0]);
    int numStoresPhase1 = maxStores / 4;
    int numStoresPhase2 = numStoresPhase1;
    int startStore = 0;
    int endStore = 0;

    // Decide collection of stores
    switch (this.phaseID) {
      case 1:
        startStore = 1;
        endStore = numStoresPhase1;
        break;
      case 2:
        startStore = numStoresPhase1 + 1;
        endStore = numStoresPhase1 + numStoresPhase2;
        break;
      case 3:
        startStore = numStoresPhase1 + numStoresPhase2 + 1;
        endStore = maxStores;
        break;
    }

    // Start each store as a thread
    for (int i = startStore; i <= endStore; i++) {
      StoreThread thread = new StoreThread(i, params, this.countDownLatch);
      Thread t = new Thread(thread);
      t.start();
    }
  }
}
