package edu.northeastern.assignment3_server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PurchaseDao {

  public static void createPurchase(Purchase purchase, int storeID, int custID, String date) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO Purchase "
        + "(purchaseID, batchID, itemID, numberOfItems, storeID, custID, date) "
        + "VALUES (?,?,?,?,?,?,?)";
    int numOfBatch = purchase.getItems().size();

    try {
      conn = DataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      String uuid = UUID.randomUUID().toString();

      for (int i = 1; i <= numOfBatch; i++) {
        PurchaseItems purchaseItem = purchase.getItems().get(i - 1);
        preparedStatement.setString(1, uuid);
        preparedStatement.setInt(2, i);
        preparedStatement.setString(3, purchaseItem.getItemID());
        preparedStatement.setInt(4, purchaseItem.getNumberOfItems());
        preparedStatement.setInt(5, storeID);
        preparedStatement.setInt(6, custID);
        preparedStatement.setString(7, date);
        preparedStatement.addBatch();
      }
      // execute bulk insert SQL statement
      preparedStatement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }
}
