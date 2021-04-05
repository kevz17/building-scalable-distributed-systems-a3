package edu.northeastern.assignment3_store_microservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import edu.northeastern.assignment3_store_microservice.model.PurchaseItems;
import edu.northeastern.assignment3_store_microservice.model.TopItems;
import edu.northeastern.assignment3_store_microservice.model.TopItemsStores;
import edu.northeastern.assignment3_store_microservice.model.TopStores;
import edu.northeastern.assignment3_store_microservice.model.TopStoresStores;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * This StoreServlet class serves as a microservice for HTTP GET request
 */
public class StoreServlet extends HttpServlet {

  private static final String EXCHANGE_NAME = "purchase_records";
  private static final String RABBITMQ_USERNAME = "admin";
  private static final String RABBITMQ_PASSWORD = "admin";
  private static final String RABBITMQ_HOST_URL = "100.26.182.92";
  private static final int RABBITMQ_PORT = 5672;
  private static final String DELIMITER = " ";
  private static final int THREAD_POOL_SIZE = 32;
  private static final String CONTENT_TYPE_JSON = "application/json";
  private static final String URL_SPLIT_PATTERN = "/";
  private static final String QUEUE_NAME = "store_queue";
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void init() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername(RABBITMQ_USERNAME);
    factory.setPassword(RABBITMQ_PASSWORD);
    factory.setHost(RABBITMQ_HOST_URL);
    factory.setPort(RABBITMQ_PORT);
    Connection connection = null;
    try {
      connection = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }

    Connection finalConnection = connection;
    Runnable storeConsumer = () -> {
      try {
        Channel channel = finalConnection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

          try {
            storeToInMemoryRepository(message);
          } finally {
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          }
        };

        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    };

    ExecutorService consumerPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      consumerPool.execute(storeConsumer);
    }
  }

  private static void storeToInMemoryRepository(String message) throws JsonProcessingException {
    // Deserialize message
    String[] messageParts = message.split(DELIMITER);
    String jsonString = messageParts[0];
    int storeID = Integer.parseInt(messageParts[1]);

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
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    // URL checking is ignored since already processed by server before forwarding
    response.setContentType(CONTENT_TYPE_JSON);
    String urlPath = request.getPathInfo();
    String[] urlParts = urlPath.split(URL_SPLIT_PATTERN);

    // Extract url params
    boolean isGetTopItems = Utility.isUrlForItems(urlParts);
    long id = Long.parseLong(urlParts[2]);

    // Process request
    response.setStatus(HttpServletResponse.SC_OK);
    if (isGetTopItems) {
      TopItems topItems = new TopItems();
      List<TopItemsStores> data = getTop10Items(id);
      topItems.setStores(data);
      String responseTopItems = objectMapper.writeValueAsString(topItems);
      response.getWriter().write(responseTopItems);
    } else {
      TopStores topStores = new TopStores();
      List<TopStoresStores> data = getTop5Stores(id);
      topStores.setStores(data);
      String responseTopStores = objectMapper.writeValueAsString(topStores);
      response.getWriter().write(responseTopStores);
    }

  }

  private List<TopItemsStores> getTop10Items(long storeID) {
    List<TopItemsStores> result = new ArrayList<>();
    List<List<Long>> storeIDList = new ArrayList<>();
    ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Long>> storeIDMap = InMemoryRepository
        .getInstance().getStoreIDMap();

    for (Long itemID : storeIDMap.get((int) storeID).keySet()) {
      storeIDList
          .add(new ArrayList<>(Arrays.asList(itemID, storeIDMap.get((int) storeID).get(itemID))));
    }

    int numberOfTopItems = 10;
    storeIDList.sort((a, b) -> (int) (b.get(1) - a.get(1)));

    for (int i = 0; i < numberOfTopItems; i++) {
      TopItemsStores topItemsStores = new TopItemsStores();
      topItemsStores.setItemID(Math.toIntExact(storeIDList.get(i).get(0)));
      topItemsStores.setNumberOfItems(Math.toIntExact(storeIDList.get(i).get(1)));
      result.add(topItemsStores);
    }
    return result;
  }

  private List<TopStoresStores> getTop5Stores(long itemID) {
    List<TopStoresStores> result = new ArrayList<>();
    List<List<Long>> itemIDList = new ArrayList<>();
    ConcurrentHashMap<Long, ConcurrentHashMap<Long, Long>> itemIDMap = InMemoryRepository
        .getInstance().getItemIDMap();

    for (Long storeID : itemIDMap.get(itemID).keySet()) {
      itemIDList.add(new ArrayList<>(Arrays.asList(storeID, itemIDMap.get(itemID).get(storeID))));
    }

    int numberOfTopStores = 5;
    itemIDList.sort((a, b) -> (int) (b.get(1) - a.get(1)));

    for (int i = 0; i < numberOfTopStores; i++) {
      TopStoresStores topStoresStores = new TopStoresStores();
      topStoresStores.setStoreID(Math.toIntExact(itemIDList.get(i).get(0)));
      topStoresStores.setNumberOfItems(Math.toIntExact(itemIDList.get(i).get(1)));
      result.add(topStoresStores);
    }
    return result;
  }
}
