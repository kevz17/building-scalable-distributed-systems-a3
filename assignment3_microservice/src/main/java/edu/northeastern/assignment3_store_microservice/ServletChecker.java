package edu.northeastern.assignment3_microservice;

public class ServletChecker {

  public static void main(String[] args) {
    System.out.println(InMemoryRepository.getInstance().getStoreIDMap());
    System.out.println(InMemoryRepository.getInstance().getItemIDMap());
  }
}
