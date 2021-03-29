import java.util.Arrays;
import java.util.List;

/**
 * This LatencyCalculator utility class handles latency/response time calculations for multi-thread
 * tasks
 */
public class LatencyCalculator {

  private static final int NUM_LATENCY_STATS = 4;
  private static final double PERCENTILE_99 = 0.99;

  /**
   * Given List of List type input, return latency statistics results as long[] type
   *
   * @param recordLines - lines of records as List of List to calculate
   * @return the calculated latency statistics results including mean, median. 99th percentile, and
   * maximum latency as long[] array
   * <p>
   * Returned value long[] mapping: latencyStats[0] -> total mean latency latencyStats[1] -> total
   * median Latency latencyStats[2] -> total 99th percentile latency latencyStats[3] -> total max
   * latency
   */
  public static long[] calculateTotalLatencyStats(List<List<String>> recordLines) {

    long[] latencies = new long[recordLines.size()];
    long[] latencyStats = new long[NUM_LATENCY_STATS];
    long sumLatency = 0;
    int recordSize = latencies.length;

    for (int i = 0; i < recordSize; i++) {
      latencies[i] = Long.parseLong(recordLines.get(i).get(2));
      sumLatency += latencies[i];
    }

    Arrays.sort(latencies);
    long totalMeanLatency = sumLatency / recordSize;
    long totalMedianLatency = (recordSize % 2 == 0)
        ? (latencies[recordSize / 2] + latencies[recordSize / 2 + 1]) : latencies[recordSize / 2];
    long totalP99Latency = latencies[(int) Math.floor(recordSize * PERCENTILE_99)];
    long totalMaxLatency = latencies[recordSize - 1];
    latencyStats[0] = totalMeanLatency;
    latencyStats[1] = totalMedianLatency;
    latencyStats[2] = totalP99Latency;
    latencyStats[3] = totalMaxLatency;

    return latencyStats;
  }
}
