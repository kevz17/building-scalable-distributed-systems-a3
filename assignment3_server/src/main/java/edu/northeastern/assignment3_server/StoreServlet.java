package edu.northeastern.assignment3_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.assignment3_server.model.ResponseMsg;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StoreServlet extends HttpServlet {

  private static final String RESPONSE_INVALID_INPUTS = "Invalid inputs";
  private static final String RESPONSE_DATA_NOT_FOUND = "Data not found";
  private static final String CONTENT_TYPE_JSON = "application/json";
  private static final String URL_SPLIT_PATTERN = "/";
  private static final String STORE_MICROSERVICE_URL = System.getProperty("STORE_MICROSERVICE_URL");
  private static final String TOP_ITEMS_URL = "/items/store/";
  private static final String TOP_STORES_URL = "/items/top10/";
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    response.setContentType(CONTENT_TYPE_JSON);
    String urlPath = request.getPathInfo();
    ResponseMsg message = new ResponseMsg();

    if (urlPath == null || urlPath.isEmpty()) {
      message.setMessage(RESPONSE_INVALID_INPUTS);
      String errorMessage = objectMapper.writeValueAsString(message);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write(errorMessage);
      return;
    }

    String[] urlParts = urlPath.split(URL_SPLIT_PATTERN);

    if (!Utility.isUrlForItems(urlParts) && !Utility.isUrlForStores(urlParts)) {
      message.setMessage(RESPONSE_DATA_NOT_FOUND);
      String errorMessage = objectMapper.writeValueAsString(message);
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write(errorMessage);
    } else {
      // Extract url params
      boolean isGetTopItems = Utility.isUrlForItems(urlParts);
      long id = Long.parseLong(urlParts[2]);

      // Redirect request to corresponding microservice
      if (isGetTopItems) {
        response.sendRedirect(STORE_MICROSERVICE_URL + TOP_ITEMS_URL + id);
      } else {
        response.sendRedirect(STORE_MICROSERVICE_URL + TOP_STORES_URL + id);
      }
    }
  }
}
