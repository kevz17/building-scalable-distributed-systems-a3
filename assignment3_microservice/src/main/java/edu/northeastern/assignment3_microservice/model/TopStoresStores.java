package edu.northeastern.assignment3_microservice.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

public class TopStoresStores {
  @SerializedName("storeID")
  private Integer storeID = null;

  @SerializedName("numberOfItems")
  private Integer numberOfItems = null;

  public TopStoresStores storeID(Integer storeID) {
    this.storeID = storeID;
    return this;
  }

  /**
   * Get storeID
   * @return storeID
   **/
  @Schema(description = "")
  public Integer getStoreID() {
    return storeID;
  }

  public void setStoreID(Integer storeID) {
    this.storeID = storeID;
  }

  public TopStoresStores numberOfItems(Integer numberOfItems) {
    this.numberOfItems = numberOfItems;
    return this;
  }

  /**
   * Get numberOfItems
   * @return numberOfItems
   **/
  @Schema(description = "")
  public Integer getNumberOfItems() {
    return numberOfItems;
  }

  public void setNumberOfItems(Integer numberOfItems) {
    this.numberOfItems = numberOfItems;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TopStoresStores topStoresStores = (TopStoresStores) o;
    return Objects.equals(this.storeID, topStoresStores.storeID) &&
        Objects.equals(this.numberOfItems, topStoresStores.numberOfItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(storeID, numberOfItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TopStoresStores {\n");

    sb.append("    storeID: ").append(toIndentedString(storeID)).append("\n");
    sb.append("    numberOfItems: ").append(toIndentedString(numberOfItems)).append("\n");
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
