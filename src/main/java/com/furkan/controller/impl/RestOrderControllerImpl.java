package com.furkan.controller.impl;

import com.furkan.controller.IRestOrderController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoOrderIU;
import com.furkan.dto.response.DtoOrder;
import com.furkan.enums.OrderStatus;
import com.furkan.model.Order;
import com.furkan.service.IOrderService;
import com.furkan.utils.PagerUtil;
import com.furkan.utils.RestPageableEntity;
import com.furkan.utils.RestPageableRequest;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class RestOrderControllerImpl extends RestBaseController implements IRestOrderController {

    @Autowired
    private IOrderService orderService;

    @PreAuthorize("@securityService.isOnlyCustomer()")
    @PostMapping()
    @Override
    public RootEntity<DtoOrder> createOrder(@Valid @RequestBody DtoOrderIU input) {
        return ok(orderService.createOrder(input));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessOrder(#id)")
    @GetMapping("/{id}")
    @Override
    public RootEntity<DtoOrder> findOrderById(@PathVariable Long id) {
        return ok(orderService.findOrderById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Override
    public RootEntity<RestPageableEntity<DtoOrder>> findAllOrders(@ModelAttribute RestPageableRequest pageableRequest) {
        Page<Order> page = orderService.findAllOrders(toPageable(pageableRequest));
        List<DtoOrder> content = orderService.dtoListConverter(page.getContent());
        RestPageableEntity<DtoOrder> pageableResponse = toPageableResponse(page, content);
        return ok(pageableResponse);
    }

    @PreAuthorize("@securityService.canAccessOrdersOfUser(#userId)")
    @GetMapping("/by-user-id/{userId}")
    @Override
    public RootEntity<List<DtoOrder>> findOrdersByUserId(@PathVariable Long userId) {
        return ok(orderService.findOrdersByUserId(userId));
    }

    @PreAuthorize("@securityService.canAccessOrder(#id) and @securityService.isOnlyCustomer()")
    @PutMapping("/{id}")
    @Override
    public RootEntity<DtoOrder> updateOrder(@PathVariable Long id, @Valid @RequestBody DtoOrderIU input) {
        return ok(orderService.updateOrder(id, input));
    }

    @PreAuthorize("@securityService.isOnlyAdmin()")
    @DeleteMapping("/{id}")
    @Override
    public RootEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ok();
    }

    @PreAuthorize("@securityService.canAccessOrder(#orderId) and @securityService.isOnlyCustomer()")
    @PutMapping("/{orderId}/confirm-payment")
    @Override
    public RootEntity<DtoOrder> confirmOrderPayment(@PathVariable Long orderId) {
        return ok(orderService.confirmOrderPayment(orderId));
    }

    @PreAuthorize("@securityService.canAccessOrder(#orderId) and @securityService.isOnlyCustomer()")
    @PutMapping("/{orderId}/cancel")
    @Override
    public RootEntity<DtoOrder> cancelOrder(@PathVariable Long orderId) {
        return ok(orderService.cancelOrder(orderId));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessOrder(#orderId)")
    @GetMapping("/{orderId}/status")
    @Override
    public RootEntity<OrderStatus> getOrderStatus(@PathVariable Long orderId) {
        return ok(orderService.getOrderStatus(orderId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/{sellerId}/pageable")
    @Override
    public RootEntity<RestPageableEntity<DtoOrder>> findPageableOrdersBySeller(
           @PathVariable Long sellerId, @ModelAttribute RestPageableRequest pageableRequest, @RequestParam(required = false) String status) {
        Pageable pageable = PagerUtil.toPageable(pageableRequest);

        Page<Order> page = orderService.findOrdersBySeller(sellerId, pageable, status);

        List<DtoOrder> content = page.getContent().stream()
                .map(order -> orderService.dtoConverterForSeller(order, sellerId))
                .collect(Collectors.toList());

        RestPageableEntity<DtoOrder> pageableResponse = PagerUtil.toPageableResponse(page, content);

        return ok(pageableResponse);
    }
}
