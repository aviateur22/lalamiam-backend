package ctoutweb.lalamiam.mapper;

import ctoutweb.lalamiam.factory.Factory;
import ctoutweb.lalamiam.model.ProductWithQuantity;
import ctoutweb.lalamiam.repository.entity.CommandProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class CommandProductListMapper implements BiFunction<List<ProductWithQuantity>, Long , List<CommandProductEntity>> {
  @Override
  public List<CommandProductEntity> apply(List<ProductWithQuantity> productWithQuantityList, Long commandId) {
    return productWithQuantityList
            .stream()
            .map(productWithQuantity-> Factory.getCommandProduct(
                    commandId,
                    productWithQuantity.getProductId(),
                    productWithQuantity.getProductQuantity()
            )).collect(Collectors.toList());
  }
}
