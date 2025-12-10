package com.furkan.controller;

import com.furkan.dto.request.DtoRefundRequestIU;
import com.furkan.dto.response.DtoRefundRequest;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestRefundRequestController {

    RootEntity<DtoRefundRequest> createRefundRequest(DtoRefundRequestIU input);

    RootEntity<DtoRefundRequest> findRefundRequestById(Long refundRequestId);

    RootEntity<List<DtoRefundRequest>> findAllRefundRequestsByUserId(Long userId);

    RootEntity<List<DtoRefundRequest>> findAllRefundRequests();

    RootEntity<DtoRefundRequest> approveRefundRequest(Long refundRequestId);

    RootEntity<DtoRefundRequest> rejectRefundRequest(Long refundRequestId);

    RootEntity<DtoRefundRequest> cancelRefundRequest(Long refundRequestId);

    RootEntity<List<DtoRefundRequest>> findAllRefundRequestsBySellerId();

    RootEntity<DtoRefundRequest> findRefundRequestForSeller(Long refundRequestId);
}
