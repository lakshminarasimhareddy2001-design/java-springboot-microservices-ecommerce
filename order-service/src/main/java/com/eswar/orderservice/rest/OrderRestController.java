package com.eswar.orderservice.rest;

import com.eswar.orderservice.dto.OrderDto;
import com.eswar.orderservice.dto.OrderResponseDto;
import com.eswar.orderservice.dto.PageResponse;
import com.eswar.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderRestController {

    private final IOrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderDto orderDto,Principal principal){
        return ResponseEntity.created(URI.create("/api/v1/orders")).body( orderService.createOrder(orderDto,principal));
    }

    // ✅ ADMIN only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<OrderResponseDto>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getALlOrders(pageable));
    }

    // ✅ USER sees only their orders
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<OrderResponseDto>> getMyOrders(Principal principal,Pageable pageable) {

        String userId = principal.getName();

        return ResponseEntity.ok(orderService.getOrdersByCustomerId(userId,pageable));
    }




}
