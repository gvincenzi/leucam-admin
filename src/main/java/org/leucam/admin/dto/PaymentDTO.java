package org.leucam.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private String paymentId;
    private LocalDateTime paymentDateTime;
    private Long orderId;
}
