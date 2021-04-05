package edu.northeastern.assignment3_store_microservice;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestartDataServlet extends HttpServlet {

  private static final String CONTENT_TYPE_JSON = "application/json";

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType(CONTENT_TYPE_JSON);
    String responseMsg = "Before: " + InMemoryRepository.getInstance().getItemIDMap().size() + " "
        + InMemoryRepository
        .getInstance().getStoreIDMap().size();
    InMemoryRepository.getInstance().setItemIDMap(new ConcurrentHashMap<>());
    InMemoryRepository.getInstance().setStoreIDMap(new ConcurrentHashMap<>());
    responseMsg +=
        "  After: " + InMemoryRepository.getInstance().getItemIDMap().size() + " "
            + InMemoryRepository
            .getInstance().getStoreIDMap().size();
    response.getWriter().write(responseMsg);
  }

}
