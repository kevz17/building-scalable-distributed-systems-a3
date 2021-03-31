package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseItems {

  private String itemID;

  private Integer numberOfItems;

  public PurchaseItems() {
  }

  public PurchaseItems(String itemID, Integer numberOfItems) {
    this.itemID = itemID;
    this.numberOfItems = numberOfItems;
  }

  public String getItemID() {
    return this.itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public Integer getNumberOfItems() {
    return this.numberOfItems;
  }

  public void setNumberOfItems(Integer numberOfItems) {
    this.numberOfItems = numberOfItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PurchaseItems purchaseItems = (PurchaseItems) o;
    return Objects.equals(this.itemID, purchaseItems.itemID) &&
        Objects.equals(this.numberOfItems, purchaseItems.numberOfItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemID, numberOfItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PurchaseItems {\n");
    sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
    sb.append("    numberOfItems: ").append(toIndentedString(numberOfItems)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
