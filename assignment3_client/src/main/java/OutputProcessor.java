import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * This OutputProcessor utility class handles the output writing given input parameters
 */
public class OutputProcessor {

  private static final String CSV_SUFFIX = ".csv";

  /**
   * Write input parameters to a CSV file, located at the root of project
   *
   * @param recordLines - List type of record line, which has multiple fields as List type
   * @param headers     - user defined headers for output CSV file
   * @param fileName    - user defined file name as output CSV file
   * @throws IOException - exception thrown when file handling error occurs
   */
  public static void writeToCsv(List<List<String>> recordLines, List<String> headers,
      String fileName) throws IOException {
    FileWriter fileWriter = new FileWriter(fileName + CSV_SUFFIX, true);
    CSVPrinter csvHeaderPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
    CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

    // Write header
    csvHeaderPrinter.printRecord(headers);
    // Write lines of records
    recordLines.forEach(line -> {
      try {
        csvPrinter.printRecord(line);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }
}
