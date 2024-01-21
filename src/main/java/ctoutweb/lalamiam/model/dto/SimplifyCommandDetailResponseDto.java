package ctoutweb.lalamiam.model.dto;

import ctoutweb.lalamiam.model.ProductWithQuantity;

import java.math.BigInteger;
import java.util.List;

public record SimplifyCommandDetailResponseDto(
        BigInteger commandId,
        List<ProductWithQuantity> productInCommandList,
        Integer commandPreparationTime,
        Integer numberOProductInCommand,
        Double commandPrice
) {}
