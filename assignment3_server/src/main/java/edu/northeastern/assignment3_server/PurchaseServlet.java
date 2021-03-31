package edu.northeastern.assignment3_server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.client.model.ResponseMsg;
import java.io.BufferedReader;
import java.text.ParseException;
import java.util.List;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * This PurchaseServlet class serves as a servlet for HTTP POST request, the implementation of GET
 * request uses synthetic data to simulate the interaction between server and database
 */
public class PurchaseServlet extends HttpServlet {

  private static final int MAX_INT_DIGIT = 10;
  private static final String RESPONSE_400 = "Invalid inputs";
  private static final String RESPONSE_404 = "Data not found";
  private static final String RESPONSE_INVALID_INPUTS = "Invalid inputs";
  private static final String RESPONSE_DATA_NOT_FOUND = "Data not found";
  private static final String RESPONSE_MISSING_PARAMETERS = "Missing parameters";
  private static final String CONTENT_TYPE_JSON = "application/json";
  private static final String URL_SPLIT_PATTERN = "/";
  private static final String URL_KEYWORD_PURCHASE = "purchase";
  private static final String URL_KEYWORD_CUSTOMER = "customer";
  private static final String URL_KEYWORD_DATE = "date";
  private static final String NUMBERS_PATTERN = "[0-9]+";
  private static final String DATE_PATTERN = "yyyyMMdd";
  private static final String EMPTY_STRING = "";
  private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    response.setContentType(CONTENT_TYPE_JSON);
    String urlPath = request.getPathInfo();
    ResponseMsg message = new ResponseMsg();

    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      message.setMessage(RESPONSE_INVALID_INPUTS);
      String errorMessage = gson.toJson(message);
      response.getWriter().write(errorMessage);
      return;
    }

    String[] urlParts = urlPath.split(URL_SPLIT_PATTERN);

    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      message.setMessage(RESPONSE_DATA_NOT_FOUND);
      String errorMessage = gson.toJson(message);
      response.getWriter().write(errorMessage);
    } else {
      // Extract url params
      int storeID = Integer.parseInt(urlParts[1]);
      int custID = Integer.parseInt(urlParts[3]);
      String date = urlParts[5];

      // Process request body
      BufferedReader reqBuffer = request.getReader();
      StringBuilder reqStringBuilder = new StringBuilder();
      String line;
      while ((line = reqBuffer.readLine()) != null) {
        reqStringBuilder.append(line.trim());
      }
      String jsonString = reqStringBuilder.toString();

      // Convert JSON string to Purchase object
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = mapper.readTree(jsonString);
      JsonNode itemsNode = jsonNode.get("items");
      List<PurchaseItems> items = mapper
          .convertValue(itemsNode, new TypeReference<List<PurchaseItems>>() {
          });

      // Connect to DB and insert record
      PurchaseDao.createPurchase(new Purchase(items), storeID, custID, date);
      response.setStatus(HttpServletResponse.SC_CREATED);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
  }
  /**
   * Check each URL part, including names, numbers, and date. Validate the request url path
   * according to the API spec. This method handles two cases for the input: local deployed server
   * and AWS EC2 deployed server. It is because of the request.getPathInfo() method's returned
   * value
   * <p>
   * urlPath  = "/{storeID}/customer/{custID}/date/{date}" urlParts = [, {storeID}, customer,
   * {custID}, date, {date}]
   *
   * @param urlParts - URL parts as String[] array type
   * @return true if given URL parts are valid, otherwise return false
   */
  private boolean isUrlValid(String[] urlParts) {

    int numUrlParts = 6;
    if (urlParts[1].equals(URL_KEYWORD_PURCHASE)) {
      numUrlParts++;
    }

    if (urlParts.length != numUrlParts) {
      return false;
    }

    if (!urlParts[1].equals(URL_KEYWORD_PURCHASE)) {
      return urlParts[0].equals(EMPTY_STRING) &&
          isStringNumericInt(urlParts[1]) &&
          urlParts[2].equals(URL_KEYWORD_CUSTOMER) &&
          isStringNumericInt(urlParts[3]) &&
          urlParts[4].equals(URL_KEYWORD_DATE) &&
          isValidDate(urlParts[5]);
    }

    return urlParts[0].equals(EMPTY_STRING) &&
        isStringNumericInt(urlParts[2]) &&
        urlParts[3].equals(URL_KEYWORD_CUSTOMER) &&
        isStringNumericInt(urlParts[4]) &&
        urlParts[5].equals(URL_KEYWORD_DATE) &&
        isValidDate(urlParts[6]);
  }

  /**
   * Check if string is numeric and ranges from 1 to 2^31 - 1
   *
   * @param numString - number string to check
   * @return true if the input string contains only digits and is within the range from 1 to
   * Integer.MAX_VALUE, otherwise return false
   */
  private boolean isStringNumericInt(String numString) {
    if (!numString.matches(NUMBERS_PATTERN) || numString.length() > MAX_INT_DIGIT) {
      return false;
    }

    long num = Long.parseLong(numString);
    return num <= Integer.MAX_VALUE && num > 0;
  }

  /**
   * Check if string is formatted as yyyyMMdd
   *
   * @param date - date string to check
   * @return true if the input string is formatted as yyyyMMdd and also valid, otherwise return
   * false
   */
  private boolean isValidDate(String date) {
    // Credit: Elliott Frisch
    // https://stackoverflow.com/questions/21975567/how-to-validate-a-string-whether-it-is-in-yyyymmdd-format
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_PATTERN);
    try {
      java.util.Date ret = sdf.parse(date.trim());
      return sdf.format(ret).equals(date.trim());
    } catch (ParseException e) {
      e.printStackTrace();
      return false;
    }
  }
}
