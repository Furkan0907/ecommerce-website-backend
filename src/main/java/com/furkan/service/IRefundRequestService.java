package com.furkan.service;

import com.furkan.dto.request.DtoRefundRequestIU;
import com.furkan.dto.response.DtoRefundRequest;

import java.util.List;

public interface IRefundRequestService {

    DtoRefundRequest createRefundRequest(DtoRefundRequestIU input);

    DtoRefundRequest findRefundRequestById(Long refundRequestId);

    List<DtoRefundRequest> findAllRefundRequestsByUserId(Long userId);

    List<DtoRefundRequest> findAllRefundRequests();

    DtoRefundRequest approveRefundRequest(Long refundRequestId);

    DtoRefundRequest rejectRefundRequest(Long refundRequestId);

    DtoRefundRequest cancelRefundRequest(Long refundRequestId);

    List<DtoRefundRequest> findAllRefundRequestsBySellerId(Long sellerId);

    DtoRefundRequest findRefundRequestForSeller(Long refundRequestId, Long sellerId);
}
