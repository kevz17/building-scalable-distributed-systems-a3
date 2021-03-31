package edu.northeastern.assignment3_store_microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.assignment3_store_microservice.model.ResponseMsg;
import edu.northeastern.assignment3_store_microservice.model.TopItems;
import edu.northeastern.assignment3_store_microservice.model.TopItemsStores;
import edu.northeastern.assignment3_store_microservice.model.TopStores;
import edu.northeastern.assignment3_store_microservice.model.TopStoresStores;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * This StoreServlet class serves as a microservice for HTTP GET request
 */
public class StoreServlet extends HttpServlet {

  private static final String RESPONSE_INVALID_INPUTS = "Invalid inputs";
  private static final String RESPONSE_DATA_NOT_FOUND = "Data not found";
  private static final String CONTENT_TYPE_JSON = "application/json";
  private static final String URL_SPLIT_PATTERN = "/";
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
