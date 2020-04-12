package org.leucam.admin.client;

import org.leucam.admin.dto.OrderDTO;
import org.leucam.admin.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("leucam-order-service/products")
public interface ProductResourceClient {
    @GetMapping("/all")
    List<ProductDTO> findAll();

    @GetMapping("/{id}")
    ProductDTO findById(@PathVariable("id") Long id);

    @GetMapping("/{id}/orders")
    List<OrderDTO> findProductOrders(@PathVariable Long id);

    @PostMapping()
    ProductDTO addProduct(@RequestBody ProductDTO productDTO);

    @PutMapping("/{id}")
    ProductDTO updateProduct(@PathVariable("id") Long id, @RequestBody ProductDTO productDTO);

    @DeleteMapping("/{id}")
    void deleteProduct(@PathVariable("id") Long id);
}
