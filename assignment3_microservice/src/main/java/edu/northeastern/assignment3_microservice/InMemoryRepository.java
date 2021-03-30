package edu.northeastern.assignment3_microservice;

import edu.northeastern.assignment3_microservice.model.PurchaseItems;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository {

  private static InMemoryRepository instance;
  private ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Long>> storeIDMap;
  private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Long>> itemIDMap;

  private InMemoryRepository() {
    this.storeIDMap = new ConcurrentHashMap<>();
    this.itemIDMap  = new ConcurrentHashMap<>();
  }

  public static InMemoryRepository getInstance() {
    if (instance == null) {
      instance = new InMemoryRepository();
    }
    return instance;
  }

  public ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Long>> getStoreIDMap() {
    return this.storeIDMap;
  }

  public ConcurrentHashMap<Long, ConcurrentHashMap<Long, Long>> getItemIDMap() {
    return this.itemIDMap;
  }

  public void addPurchaseToStoreIDMap(List<PurchaseItems> items, int storeID) {
    for (PurchaseItems purchaseItems : items) {
      long itemID = Long.parseLong(purchaseItems.getItemID());
      long numOfItems = purchaseItems.getNumberOfItems();
      ConcurrentHashMap<Long, Long> itemIDCountMap = storeIDMap.getOrDefault(storeID, new ConcurrentHashMap<>());

      itemIDCountMap.put(itemID, itemIDCountMap.getOrDefault(itemID, 0L) + numOfItems);
      storeIDMap.put(storeID, itemIDCountMap);
    }
  }

  public void addPurchaseToItemIDMap(List<PurchaseItems> items, int storeID) {
    for (PurchaseItems purchaseItems : items) {
      long itemID = Long.parseLong(purchaseItems.getItemID());
      long numOfItems = purchaseItems.getNumberOfItems();
      ConcurrentHashMap<Long, Long> storeIDCountMap = itemIDMap.getOrDefault(itemID, new ConcurrentHashMap<>());

      storeIDCountMap
          .put((long) storeID, storeIDCountMap.getOrDefault((long) storeID, 0L) + numOfItems);
      itemIDMap.put(itemID, storeIDCountMap);
    }
  }
}
