package org.leucam.admin.client;

import org.leucam.admin.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("leucam-order-service/orders")
public interface OrderResourceClient {
    @GetMapping("/{id}")
    OrderDTO findById(@PathVariable("id") Long id);

    @PutMapping("/{id}")
    OrderDTO updateOrder(@PathVariable("id") Long id, @RequestBody OrderDTO orderDTO);

    @DeleteMapping("/{id}")
    void deleteOrder(@PathVariable("id") Long id);
}
