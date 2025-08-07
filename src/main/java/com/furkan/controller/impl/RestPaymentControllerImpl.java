package com.furkan.controller.impl;

import com.furkan.controller.IRestPaymentController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoPayment;
import com.furkan.enums.PaymentStatus;
import com.furkan.service.IPaymentService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class RestPaymentControllerImpl extends RestBaseController implements IRestPaymentController {

    @Autowired
    private IPaymentService paymentService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    @Override
    public RootEntity<DtoPayment> createPayment(@Valid @RequestBody DtoPaymentIU input) {
        return ok(paymentService.createPayment(input));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#orderId)")
    @GetMapping("/by-order-id/{orderId}")
    @Override
    public RootEntity<DtoPayment> findPaymentByOrderId(@PathVariable Long orderId) {
        return ok(paymentService.findPaymentByOrderId(orderId));
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/by-user-id/{userId}")
    @Override
    public RootEntity<List<DtoPayment>> findPaymentsByUserId(@PathVariable Long userId) {
        return ok(paymentService.findPaymentsByUserId(userId));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#orderId)")
    @GetMapping("/has-successful/{orderId}")
    @Override
    public RootEntity<Boolean> hasSuccessfulPayment(@PathVariable Long orderId) {
        return ok(paymentService.hasSuccessfulPayment(orderId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    @Override
    public RootEntity<DtoPayment> updatePaymentStatus(@PathVariable Long orderId, @RequestParam PaymentStatus newStatus,
                                                      @RequestParam String transactionId) {
        return ok(paymentService.updatePaymentStatus(orderId, newStatus, transactionId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/refund")
    @Override
    public RootEntity<DtoPayment> refundPayment(@PathVariable Long orderId) {
        return ok(paymentService.refundPayment(orderId));
    }
}
