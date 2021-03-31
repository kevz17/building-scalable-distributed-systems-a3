package edu.northeastern.assignment3_microservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.assignment3_microservice.model.PurchaseItems;
import edu.northeastern.assignment3_microservice.model.ResponseMsg;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This PurchaseServlet class serves as a microservice for HTTP POST request
 */
public class PurchaseServlet extends HttpServlet {
  private static final String RESPONSE_INVALID_INPUTS = "Invalid inputs";
  private static final String RESPONSE_DATA_NOT_FOUND = "Data not found";
  private static final String CONTENT_TYPE_JSON = "application/json";
  private static final String URL_SPLIT_PATTERN = "/";
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

    if (!Utility.isUrlValid(urlParts)) {
      message.setMessage(RESPONSE_DATA_NOT_FOUND);
      String errorMessage = objectMapper.writeValueAsString(message);
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write(errorMessage);
    } else {
      // Extract url params
      int storeID = Integer.parseInt(urlParts[1]);

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

      // Store in data storage
      InMemoryRepository.getInstance().addPurchaseToStoreIDMap(items, storeID);
      InMemoryRepository.getInstance().addPurchaseToItemIDMap(items, storeID);
      response.setStatus(HttpServletResponse.SC_CREATED);
    }
  }
}
