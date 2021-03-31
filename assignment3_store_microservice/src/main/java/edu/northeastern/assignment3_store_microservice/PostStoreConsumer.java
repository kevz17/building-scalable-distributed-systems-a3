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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostStoreConsumer {

  private static final String EXCHANGE_NAME = "purchase_records";
  private static final String RABBITMQ_HOST_URL = "http://192.168.0.1";
  private static final String DELIMITER = "/?";
  private static final int THREAD_POOL_SIZE = 4;

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RABBITMQ_HOST_URL);
    Connection connection = factory.newConnection();

    Runnable storeConsumer = () -> {
      try {
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

          try {
            storeToInMemoryRepository(message);
          } finally {
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          }
        };
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    };

    ExecutorService consumerPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    consumerPool.execute(storeConsumer);
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
}