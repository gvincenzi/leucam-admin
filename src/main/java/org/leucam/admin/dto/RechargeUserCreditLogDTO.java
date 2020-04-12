package org.leucam.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RechargeUserCreditLogDTO {
    private BigDecimal oldCredit;
    private BigDecimal newCredit;
    private String rechargeUserCreditType;
    private LocalDateTime rechargeDateTime;
}
