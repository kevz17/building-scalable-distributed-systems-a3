package edu.northeastern.assignment3_microservice.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

public class TopItemsStores {
  @SerializedName("itemID")
  private Integer itemID = null;

  @SerializedName("numberOfItems")
  private Integer numberOfItems = null;

  public TopItemsStores itemID(Integer itemID) {
    this.itemID = itemID;
    return this;
  }

  /**
   * Get itemID
   * @return itemID
   **/
  @Schema(description = "")
  public Integer getItemID() {
    return itemID;
  }

  public void setItemID(Integer itemID) {
    this.itemID = itemID;
  }

  public TopItemsStores numberOfItems(Integer numberOfItems) {
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
    TopItemsStores topItemsStores = (TopItemsStores) o;
    return Objects.equals(this.itemID, topItemsStores.itemID) &&
        Objects.equals(this.numberOfItems, topItemsStores.numberOfItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemID, numberOfItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TopItemsStores {\n");

    sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
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
