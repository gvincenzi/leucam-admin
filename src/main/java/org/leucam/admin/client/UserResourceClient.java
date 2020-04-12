package org.leucam.admin.client;

import org.leucam.admin.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("leucam-order-service/users")
public interface UserResourceClient {
    @GetMapping("/")
    List<UserDTO> findByActiveTrue();

    @GetMapping("/all")
    List<UserDTO> findAll();

    @GetMapping("/{id}")
    UserDTO findById(@PathVariable("id") Long id);

    @PostMapping()
    UserDTO addUser(@RequestBody UserDTO userDTO);

    @PutMapping("/{id}")
    UserDTO updateUser(@PathVariable("id") Long id, @RequestBody UserDTO userDTO);

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") Long id);
}
