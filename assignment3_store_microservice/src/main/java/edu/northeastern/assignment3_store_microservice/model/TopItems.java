package edu.northeastern.assignment3_store_microservice.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopItems {
  @SerializedName("stores")
  private List<TopItemsStores> stores = null;

  public TopItems stores(List<TopItemsStores> stores) {
    this.stores = stores;
    return this;
  }

  public TopItems addStoresItem(TopItemsStores storesItem) {
    if (this.stores == null) {
      this.stores = new ArrayList<TopItemsStores>();
    }
    this.stores.add(storesItem);
    return this;
  }

  /**
   * Get stores
   * @return stores
   **/
  @Schema(description = "")
  public List<TopItemsStores> getStores() {
    return stores;
  }

  public void setStores(List<TopItemsStores> stores) {
    this.stores = stores;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TopItems topItems = (TopItems) o;
    return Objects.equals(this.stores, topItems.stores);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stores);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TopItems {\n");

    sb.append("    stores: ").append(toIndentedString(stores)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
