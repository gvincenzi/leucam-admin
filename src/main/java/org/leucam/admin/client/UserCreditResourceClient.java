package org.leucam.admin.client;

import org.leucam.admin.dto.RechargeUserCreditLogDTO;
import org.leucam.admin.dto.UserCreditDTO;
import org.leucam.admin.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("leucam-payment-service/internal-credit")
public interface UserCreditResourceClient {
    @GetMapping("/all")
    List<UserCreditDTO> findAll();

    @GetMapping("/{userId}")
    UserCreditDTO findById(@PathVariable("userId") Long userId);

    @PostMapping("/{additionalCredit}")
    UserCreditDTO newCredit(@RequestBody UserDTO userDTO, @PathVariable("additionalCredit") BigDecimal additionalCredit);

    @GetMapping("/{userId}/log")
    List<RechargeUserCreditLogDTO> findRechargeUserCreditLogByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/totalUserCredit")
    BigDecimal totalUserCredit();
}
