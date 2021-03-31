package edu.northeastern.assignment3_server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.assignment3_server.model.ResponseMsg;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * This PurchaseServlet class serves as a servlet for HTTP POST request, the implementation of GET
 * request uses synthetic data to simulate the interaction between server and database
 */
public class PurchaseServlet extends HttpServlet {

  private static final String RESPONSE_INVALID_INPUTS = "Invalid inputs";
  private static final String RESPONSE_DATA_NOT_FOUND = "Data not found";
  private static final String CONTENT_TYPE_JSON = "application/json";
  private static final String URL_SPLIT_PATTERN = "/";
  private static final String EXCHANGE_NAME = "purchase_records";
  private static final String DELIMITER = "?";
  private static final String RABBITMQ_HOST_URL = "http://192.168.0.1";
  private ObjectMapper objectMapper = new ObjectMapper();
  private Connection connection;

  @Override
  public void init() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RABBITMQ_HOST_URL);
    try {
      connection = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    response.setContentType(CONTENT_TYPE_JSON);
    String urlPath = request.getPathInfo();
    ResponseMsg responseMsg = new ResponseMsg();

    if (urlPath == null || urlPath.isEmpty()) {
      responseMsg.setMessage(RESPONSE_INVALID_INPUTS);
      String errorMessage = objectMapper.writeValueAsString(responseMsg);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write(errorMessage);
      return;
    }

    String[] urlParts = urlPath.split(URL_SPLIT_PATTERN);

    if (!Utility.isUrlValid(urlParts)) {
      responseMsg.setMessage(RESPONSE_DATA_NOT_FOUND);
      String errorMessage = objectMapper.writeValueAsString(responseMsg);
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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

      // Incorporate storeID, custID, and date into JSON string as message
      String message = jsonString + (DELIMITER + storeID + DELIMITER + custID + DELIMITER + date);

      try (Channel channel = connection.createChannel()) {
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
      } catch (TimeoutException e) {
        e.printStackTrace();
      }

      response.setStatus(HttpServletResponse.SC_CREATED);
    }
  }
}
