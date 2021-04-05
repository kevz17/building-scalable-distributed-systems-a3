import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Purchase;
import model.PurchaseItems;

public class PostPurchaseConsumer {

  private static final String EXCHANGE_NAME = "purchase_records";
  private static final String RABBITMQ_USERNAME = System.getProperty("RABBITMQ_USERNAME");
  private static final String RABBITMQ_PASSWORD = System.getProperty("RABBITMQ_PASSWORD");
  private static final String RABBITMQ_HOST_URL = System.getProperty("RABBITMQ_URL");
  private static final int RABBITMQ_PORT = 5672;
  private static final String DELIMITER = " ";
  private static final int THREAD_POOL_SIZE = 32;
  private static final String QUEUE_NAME = "purchase_queue";
  private static ObjectMapper objectMapper = new ObjectMapper();

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername(RABBITMQ_USERNAME);
    factory.setPassword(RABBITMQ_PASSWORD);
    factory.setHost(RABBITMQ_HOST_URL);
    factory.setPort(RABBITMQ_PORT);
    Connection connection = factory.newConnection();

    Runnable storeConsumer = () -> {
      try {
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

          try {
            storeToDatabase(message);
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

  private static void storeToDatabase(String message) throws JsonProcessingException {
    // Deserialize message
    String[] messageParts = message.split(DELIMITER);
    String jsonString = messageParts[0];
    int storeID = Integer.parseInt(messageParts[1]);
    int custID = Integer.parseInt(messageParts[2]);
    String date = messageParts[3];

    // Convert JSON string to Purchase object
    JsonNode jsonNode = objectMapper.readTree(jsonString);
    JsonNode itemsNode = jsonNode.get("items");
    List<PurchaseItems> items = objectMapper
        .convertValue(itemsNode, new TypeReference<>() {
        });

    // Connect to DB and insert record
    PurchaseDao.createPurchase(new Purchase(items), storeID, custID, date);
  }
}
