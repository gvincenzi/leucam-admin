package org.leucam.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.leucam.admin.dto.type.ActionType;
import org.leucam.admin.dto.type.ColorType;
import org.leucam.admin.dto.type.FrontBackType;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderDTO implements Comparable<OrderDTO>{
    private Long orderId;
    private ActionType actionType;
    private FrontBackType frontBackType;
    private ColorType colorType;
    private Double numberOfCopies;
    private Double pagesPerSheet;
    private UserDTO user;
    private ProductDTO product;
    private Boolean paid = Boolean.FALSE;
    private BigDecimal amount;
    private String paymentExternalReference;
    private LocalDateTime paymentExternalDateTime;

    @Override
    public int compareTo(OrderDTO orderDTO) {
        return this.orderId.compareTo(orderDTO.orderId);
    }

    @Override
    public String toString() {
        return "\nID : " + orderId +
                "\nFile PDF : " + product +
                "\nTipo di ordine=" + actionType.getLabel() +
                "\nBianco e Nero o Colore=" + colorType.getLabel() +
                "\nFronte/Retro=" + frontBackType.getLabel() +
                "\nPagine per foglio=" + pagesPerSheet +
                "\nNumero di copie=" + numberOfCopies +
                (paid ? "\n\n**Totale pagato con credito interno= " + NumberFormat.getCurrencyInstance().format(amount) : "\n\n**Quest'ordine non è ancora stato pagato**");
    }
}
