package edu.northeastern.assignment3_store_microservice.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopStores {
  @SerializedName("stores")
  private List<TopStoresStores> stores = null;

  public TopStores stores(List<TopStoresStores> stores) {
    this.stores = stores;
    return this;
  }

  public TopStores addStoresItem(TopStoresStores storesItem) {
    if (this.stores == null) {
      this.stores = new ArrayList<TopStoresStores>();
    }
    this.stores.add(storesItem);
    return this;
  }

  /**
   * Get stores
   * @return stores
   **/
  @Schema(description = "")
  public List<TopStoresStores> getStores() {
    return stores;
  }

  public void setStores(List<TopStoresStores> stores) {
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
    TopStores topStores = (TopStores) o;
    return Objects.equals(this.stores, topStores.stores);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stores);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TopStores {\n");

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
