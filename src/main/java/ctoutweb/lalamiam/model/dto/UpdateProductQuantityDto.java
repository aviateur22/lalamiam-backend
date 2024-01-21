package ctoutweb.lalamiam.model.dto;

import java.math.BigInteger;
import java.util.Objects;

public class UpdateProductQuantityDto {
  private BigInteger productId;
  private BigInteger commandId;
  private Integer productQuantity;

  /**
   *
   */
  public UpdateProductQuantityDto() {
  }

  public UpdateProductQuantityDto(BigInteger productId, BigInteger commandId, Integer productQuantity) {
    this.productId = productId;
    this.commandId = commandId;
    this.productQuantity = productQuantity;
  }

  public BigInteger getProductId() {
    return productId;
  }

  public void setProductId(BigInteger productId) {
    this.productId = productId;
  }

  public BigInteger getCommandId() {
    return commandId;
  }

  public void setCommandId(BigInteger commandId) {
    this.commandId = commandId;
  }

  public Integer getProductQuantity() {
    return productQuantity;
  }

  public void setProductQuantity(Integer productQuantity) {
    this.productQuantity = productQuantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UpdateProductQuantityDto that = (UpdateProductQuantityDto) o;
    return Objects.equals(productId, that.productId) && Objects.equals(commandId, that.commandId) && Objects.equals(productQuantity, that.productQuantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId, commandId, productQuantity);
  }

  @Override
  public String toString() {
    return "UpdateProductQuantityInCommandSchema{" +
            "productIdList=" + productId +
            ", commandId=" + commandId +
            ", productQuantity=" + productQuantity +
            '}';
  }
}
