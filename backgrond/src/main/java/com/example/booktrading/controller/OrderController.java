package com.example.booktrading.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.dto.OrderDTO;
import com.example.booktrading.entity.vo.OrderVO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Tag(name = "订单管理", description = "订单创建、状态流转等")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<IPage<OrderVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type) {
        IPage<OrderVO> pageResult = orderService.list(page, size, type);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }
    
    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody OrderDTO dto) {
        Long id = orderService.create(dto);
        return Result.success(id);
    }
    
    @Operation(summary = "卖家确认订单")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirm(id);
        return Result.success();
    }
    
    @Operation(summary = "买家支付")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.pay(id);
        return Result.success();
    }
    
    @Operation(summary = "卖家发货")
    @PutMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id, @RequestParam String shippingInfo) {
        orderService.ship(id, shippingInfo);
        return Result.success();
    }
    
    @Operation(summary = "买家确认收货")
    @PutMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) {
        orderService.receive(id);
        return Result.success();
    }
    
    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        orderService.cancel(id, reason);
        return Result.success();
    }
}
