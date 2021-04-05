package edu.northeastern.assignment3_server;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import com.rabbitmq.client.Channel;


public class ChannelFactory extends BasePooledObjectFactory<Channel> {

  private static final String RABBITMQ_USERNAME = System.getProperty("RABBITMQ_USERNAME");
  private static final String RABBITMQ_PASSWORD = System.getProperty("RABBITMQ_PASSWORD");
  private static final String RABBITMQ_HOST_URL = System.getProperty("RABBITMQ_HOST_URL");
  private static final int RABBITMQ_PORT = 5672;
  private static Connection connection;

  static {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername(RABBITMQ_USERNAME);
    factory.setPassword(RABBITMQ_PASSWORD);
    factory.setHost(RABBITMQ_HOST_URL);
    factory.setPort(RABBITMQ_PORT);
    try {
      connection = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Channel create() throws IOException {
    return connection.createChannel();
  }

  /**
   * Use the default PooledObject implementation.
   */
  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<>(channel);
  }
}