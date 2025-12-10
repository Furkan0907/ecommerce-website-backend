package com.furkan.controller.impl;

import com.furkan.controller.IRestRefundRequestController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoRefundRequestIU;
import com.furkan.dto.response.DtoRefundRequest;
import com.furkan.service.IRefundRequestService;
import com.furkan.service.SecurityService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refund-requests")
public class RestRefundRequestControllerImpl extends RestBaseController implements IRestRefundRequestController {

    @Autowired
    private IRefundRequestService refundRequestService;

    @Autowired
    private SecurityService securityService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    @Override
    public RootEntity<DtoRefundRequest> createRefundRequest(@Valid @RequestBody DtoRefundRequestIU input) {
        return ok(refundRequestService.createRefundRequest(input));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{refundRequestId}")
    @Override
    public RootEntity<DtoRefundRequest> findRefundRequestById(@PathVariable Long refundRequestId) {
        return ok(refundRequestService.findRefundRequestById(refundRequestId));
    }

    @PreAuthorize("hasRole('USER') and #userId == principal.id")
    @GetMapping("/by-user-id/{userId}")
    @Override
    public RootEntity<List<DtoRefundRequest>> findAllRefundRequestsByUserId(@PathVariable Long userId) {
        return ok(refundRequestService.findAllRefundRequestsByUserId(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Override
    public RootEntity<List<DtoRefundRequest>> findAllRefundRequests() {
        return ok(refundRequestService.findAllRefundRequests());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{refundRequestId}/approve")
    @Override
    public RootEntity<DtoRefundRequest> approveRefundRequest(@PathVariable Long refundRequestId) {
        return ok(refundRequestService.approveRefundRequest(refundRequestId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{refundRequestId}/reject")
    @Override
    public RootEntity<DtoRefundRequest> rejectRefundRequest(@PathVariable Long refundRequestId) {
        return ok(refundRequestService.rejectRefundRequest(refundRequestId));
    }

    @PreAuthorize("hasRole('USER') and @securityService.isRefundRequestOwner(#refundRequestId, principal.id)")
    @PutMapping("/{refundRequestId}/cancel")
    @Override
    public RootEntity<DtoRefundRequest> cancelRefundRequest(@PathVariable Long refundRequestId) {
        return ok(refundRequestService.cancelRefundRequest(refundRequestId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/{refundRequestId}")
    @Override
    public RootEntity<DtoRefundRequest> findRefundRequestForSeller(@PathVariable Long refundRequestId) {
        Long sellerId = securityService.getCurrentUserId();
        DtoRefundRequest refundRequest = refundRequestService.findRefundRequestForSeller(refundRequestId, sellerId);
        return ok(refundRequest);
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    @Override
    public RootEntity<List<DtoRefundRequest>> findAllRefundRequestsBySellerId() {
        Long sellerId = securityService.getCurrentUserId();
        List<DtoRefundRequest> list = refundRequestService.findAllRefundRequestsBySellerId(sellerId);
        return ok(list);
    }
}
