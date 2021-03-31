package edu.northeastern.assignment3_store_microservice;

import java.text.ParseException;

/**
 * Utility methods that help handle URL parsing and validating
 */
public class Utility {

  private static final int MAX_INT_DIGIT = 10;
  private static final String URL_KEYWORD_ITEMS = "items";
  private static final String URL_KEYWORD_STORE = "store";
  private static final String URL_KEYWORD_TOP10 = "top10";
  private static final String URL_KEYWORD_PURCHASE = "purchase";
  private static final String URL_KEYWORD_CUSTOMER = "customer";
  private static final String URL_KEYWORD_DATE = "date";
  private static final String NUMBERS_PATTERN = "[0-9]+";
  private static final String EMPTY_STRING = "";
  private static final String DATE_PATTERN = "yyyyMMdd";

  /**
   * Validate the request url path according to the API spec. This method handles two cases for the
   * input: local deployed server and AWS EC2 deployed server. It is because of the
   * request.getPathInfo() method's returned value. urlPath  = "/items/store/{storeID}" urlParts =
   * [, items, store, {storeID}]
   *
   * @param urlParts - URL parts as String[] array type
   * @return true if given URL parts are valid, otherwise return false
   */
  public static boolean isUrlForItems(String[] urlParts) {

    int numUrlParts = 3;
    if (urlParts[1].equals(URL_KEYWORD_ITEMS)) {
      numUrlParts++;
    }

    if (urlParts.length != numUrlParts) {
      return false;
    }

    if (!urlParts[1].equals(URL_KEYWORD_ITEMS)) {
      return urlParts[0].equals(EMPTY_STRING) &&
          urlParts[1].equals(URL_KEYWORD_STORE) &&
          isStringNumericInt(urlParts[2]);
    }

    return urlParts[0].equals(EMPTY_STRING) &&
        urlParts[2].equals(URL_KEYWORD_STORE) &&
        isStringNumericInt(urlParts[3]);
  }

  /**
   * Validate the request url path according to the API spec. This method handles two cases for the
   * input: local deployed server and AWS EC2 deployed server. It is because of the
   * request.getPathInfo() method's returned value. urlPath  = "/items/top10/{itemID}" urlParts = [,
   * items, top10, {itemID}]
   *
   * @param urlParts - URL parts as String[] array type
   * @return true if given URL parts are valid, otherwise return false
   */
  public static boolean isUrlForStores(String[] urlParts) {

    int numUrlParts = 3;
    if (urlParts[1].equals(URL_KEYWORD_ITEMS)) {
      numUrlParts++;
    }

    if (urlParts.length != numUrlParts) {
      return false;
    }

    if (!urlParts[1].equals(URL_KEYWORD_ITEMS)) {
      return urlParts[0].equals(EMPTY_STRING) &&
          urlParts[1].equals(URL_KEYWORD_TOP10) &&
          isStringNumericInt(urlParts[2]);
    }

    return urlParts[0].equals(EMPTY_STRING) &&
        urlParts[2].equals(URL_KEYWORD_TOP10) &&
        isStringNumericInt(urlParts[3]);
  }

  /**
   * Check if string is numeric and ranges from 1 to 2^31 - 1
   *
   * @param numString - number string to check
   * @return true if the input string contains only digits and is within the range from 1 to
   * Integer.MAX_VALUE, otherwise return false
   */
  public static boolean isStringNumericInt(String numString) {
    if (!numString.matches(NUMBERS_PATTERN) || numString.length() > MAX_INT_DIGIT) {
      return false;
    }

    long num = Long.parseLong(numString);
    return num <= Integer.MAX_VALUE && num > 0;
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
  public static boolean isUrlValid(String[] urlParts) {

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
   * Check if string is formatted as yyyyMMdd
   *
   * @param date - date string to check
   * @return true if the input string is formatted as yyyyMMdd and also valid, otherwise return
   * false
   */
  public static boolean isValidDate(String date) {
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
