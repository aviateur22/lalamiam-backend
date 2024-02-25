package ctoutweb.lalamiam.model.dto;

import ctoutweb.lalamiam.model.ProductWithQuantity;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Données nécessaires pour enregistrer une commande
 */
public record PersitCommandDto(
        BigInteger storeId,
        BigInteger commandId,
        LocalDate commandDate,
        LocalDateTime consultationDate,
        List<ProductWithQuantity> selectProducts,
        String clientPhone,
        LocalDateTime selectSlotTime
) {
}