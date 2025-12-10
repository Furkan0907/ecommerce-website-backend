package com.furkan.controller.impl;

import com.furkan.controller.IRestOrderItemController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoOrderItemIU;
import com.furkan.dto.response.DtoOrderItem;
import com.furkan.service.IOrderItemService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class RestOrderItemControllerImpl extends RestBaseController implements IRestOrderItemController {

    @Autowired
    private IOrderItemService orderItemService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessOrder(#input.orderId)")
    @Override
    public RootEntity<DtoOrderItem> createOrderItem(@Valid @RequestBody DtoOrderItemIU input) {
        return ok(orderItemService.createOrderItem(input));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderItemOwner(#id, authentication.name)")
    @Override
    public RootEntity<DtoOrderItem> findOrderItemById(@PathVariable Long id) {
        return ok(orderItemService.findOrderItemById(id));
    }

    @GetMapping("/by-order-id/{orderId}")
    @PreAuthorize("hasRole('SELLER') or @securityService.canAccessOrder(#orderId)")
    @Override
    public RootEntity<List<DtoOrderItem>> findAllByOrderId(@PathVariable Long orderId) {
        return ok(orderItemService.findAllByOrderId(orderId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderItemOwner(#id, authentication.name)")
    @Override
    public RootEntity<DtoOrderItem> updateOrderItem(@PathVariable Long id, @Valid @RequestBody DtoOrderItemIU input) {
        return ok(orderItemService.updateOrderItem(id, input));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderItemOwner(#id, authentication.name)")
    @Override
    public RootEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ok();
    }

    @PutMapping("{id}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public RootEntity<DtoOrderItem> deliverOrderItem(@PathVariable Long id) {
        return ok(orderItemService.deliverOrderItem(id));
    }

    @PutMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public RootEntity<DtoOrderItem> markOrderItemShipped(@PathVariable Long id) {
        return ok(orderItemService.markOrderItemShipped(id));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#id, authentication.name)")
    @Override
    public RootEntity<DtoOrderItem> cancelOrderItem(@PathVariable Long id) {
        return ok(orderItemService.cancelOrderItem(id));
    }
}
