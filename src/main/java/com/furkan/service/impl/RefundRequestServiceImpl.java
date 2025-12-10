package com.furkan.service.impl;

import com.furkan.dto.request.DtoRefundRequestIU;
import com.furkan.dto.response.*;
import com.furkan.enums.OrderItemStatus;
import com.furkan.enums.OrderStatus;
import com.furkan.enums.RefundRequestStatus;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.*;
import com.furkan.repository.*;
import com.furkan.service.IOrderItemService;
import com.furkan.service.IPaymentService;
import com.furkan.service.IRefundRequestService;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RefundRequestServiceImpl implements IRefundRequestService {

    @Autowired
    private RefundRequestRepository refundRequestRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IOrderItemService orderItemService;

    private DtoRefundRequest dtoConverter(RefundRequest input) {
        DtoRefundRequest refundRequest = new DtoRefundRequest();

        BeanUtils.copyProperties(input, refundRequest);

        refundRequest.setOrderId(input.getOrder().getId());

        refundRequest.setUserId(input.getUser().getId());

        if (input.getOrderItem() != null) {
            DtoOrderItem dtoOrderItem = new DtoOrderItem();
            BeanUtils.copyProperties(input.getOrderItem(), dtoOrderItem);

            if (input.getOrderItem().getProduct() != null) {
                DtoProduct dtoProduct = new DtoProduct();
                BeanUtils.copyProperties(input.getOrderItem().getProduct(), dtoProduct);
                if (input.getOrderItem().getProduct().getSeller() != null) {
                    DtoUser dtoSeller = new DtoUser();
                    BeanUtils.copyProperties(input.getOrderItem().getProduct().getSeller(), dtoSeller);
                    dtoProduct.setSeller(dtoSeller);
                }
                dtoOrderItem.setProduct(dtoProduct);
            }
            refundRequest.setOrderItem(dtoOrderItem);
        }

        return refundRequest;
    }

    @Override
    public DtoRefundRequest createRefundRequest(DtoRefundRequestIU input) {
        OrderItem item = orderItemRepository.findById(input.getOrderItemId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_NOT_FOUND, input.getOrderItemId().toString())));

        Order order = item.getOrder();
        User user = order.getUser();

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrder(order);
        refundRequest.setOrderItem(item);
        refundRequest.setUser(user);
        refundRequest.setReason(input.getReason());
        refundRequest.setStatus(RefundRequestStatus.PENDING);
        refundRequest.setCreatedAt(new Date());
        refundRequest.setUpdatedAt(new Date());

        item.setStatus(OrderItemStatus.RETURN_REQUESTED);

        order.setStatus(OrderStatus.PARTIALLY_RETURN_REQUESTED);
        orderRepository.save(order);

        RefundRequest saved = refundRequestRepository.save(refundRequest);
        return dtoConverter(saved);
    }

    @Override
    public DtoRefundRequest findRefundRequestById(Long refundRequestId) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REFUND_REQUEST_NOT_FOUND, refundRequestId.toString())));
        return dtoConverter(refundRequest);
    }

    @Override
    public List<DtoRefundRequest> findAllRefundRequestsByUserId(Long userId) {
        List<DtoRefundRequest> dtoRefundRequests = new ArrayList<>();
        List<RefundRequest> refundRequests = refundRequestRepository.findAllByUserId(userId);
        if (refundRequests.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_REFUND_REQUEST_FOUND_FOR_THIS_USER, userId.toString()));
        }
        for (RefundRequest refundRequest : refundRequests) {
            dtoRefundRequests.add(dtoConverter(refundRequest));
        }
        return dtoRefundRequests;
    }

    @Override
    public List<DtoRefundRequest> findAllRefundRequests() {
        List<DtoRefundRequest> dtoRefundRequests = new ArrayList<>();
        List<RefundRequest> refundRequests = refundRequestRepository.findAll();
        if (refundRequests.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.REFUND_REQUEST_LIST_IS_EMPTY, null));
        }
        for (RefundRequest refundRequest : refundRequests) {
            dtoRefundRequests.add(dtoConverter(refundRequest));
        }
        return dtoRefundRequests;
    }

    private void checkStatusPending(RefundRequestStatus status) {
        if (!status.equals(RefundRequestStatus.PENDING)) {
            throw new BaseException(new ErrorMessage(MessageType.REFUND_REQUEST_ALREADY_BEEN_HANDLED, null));
        }
    }

    @Override
    public DtoRefundRequest approveRefundRequest(Long refundRequestId) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REFUND_REQUEST_NOT_FOUND, refundRequestId.toString())));

        checkStatusPending(refundRequest.getStatus());

        refundRequest.setStatus(RefundRequestStatus.APPROVED);
        refundRequest.setUpdatedAt(new Date());
        RefundRequest saved = refundRequestRepository.save(refundRequest);

       OrderItem item = refundRequest.getOrderItem();
       try {
           paymentService.refundPaymentItem(item.getId());
       } catch (StripeException e) {
           System.out.println(e.getMessage());
       }

        return dtoConverter(saved);
    }

    @Override
    public DtoRefundRequest rejectRefundRequest(Long refundRequestId) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REFUND_REQUEST_NOT_FOUND, refundRequestId.toString())));

        checkStatusPending(refundRequest.getStatus());

        refundRequest.setStatus(RefundRequestStatus.REJECTED);
        refundRequest.setUpdatedAt(new Date());
        RefundRequest saved = refundRequestRepository.save(refundRequest);

        OrderItem item = refundRequest.getOrderItem();
        item.setStatus(OrderItemStatus.REFUND_REJECTED);

        orderItemService.updateOrderAggregateStatus(refundRequest.getOrder());

        return dtoConverter(saved);
    }

    @Override
    public DtoRefundRequest cancelRefundRequest(Long refundRequestId) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REFUND_REQUEST_NOT_FOUND, refundRequestId.toString())));

        checkStatusPending(refundRequest.getStatus());

        refundRequest.setStatus(RefundRequestStatus.CANCELLED);
        refundRequest.setUpdatedAt(new Date());
        RefundRequest saved = refundRequestRepository.save(refundRequest);

        OrderItem item = refundRequest.getOrderItem();
        item.setStatus(OrderItemStatus.REFUND_REJECTED);

        orderItemService.updateOrderAggregateStatus(refundRequest.getOrder());

        return dtoConverter(saved);
    }

    @Override
    public List<DtoRefundRequest> findAllRefundRequestsBySellerId(Long sellerId) {
        List<RefundRequest> refundRequests = refundRequestRepository.findAllByOrderItem_Product_SellerId(sellerId);
        if (refundRequests.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.REFUND_REQUEST_LIST_IS_EMPTY, null));
        }

        return refundRequests.stream()
                .map(this::dtoConverter)
                .collect(Collectors.toList());
    }

    @Override
    public DtoRefundRequest findRefundRequestForSeller(Long refundRequestId, Long sellerId) {
        RefundRequest refundRequest = refundRequestRepository.findByIdAndOrderItem_Product_Seller_Id(refundRequestId, sellerId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REFUND_REQUEST_NOT_FOUND, refundRequestId.toString())));
        return dtoConverter(refundRequest);
    }
}
