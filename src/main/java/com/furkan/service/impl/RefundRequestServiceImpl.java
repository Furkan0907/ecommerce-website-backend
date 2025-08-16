package com.furkan.service.impl;

import com.furkan.dto.request.DtoRefundRequestIU;
import com.furkan.dto.response.*;
import com.furkan.enums.OrderStatus;
import com.furkan.enums.RefundRequestStatus;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Order;
import com.furkan.model.RefundRequest;
import com.furkan.model.User;
import com.furkan.repository.OrderRepository;
import com.furkan.repository.RefundRequestRepository;
import com.furkan.repository.UserRepository;
import com.furkan.service.IRefundRequestService;
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
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private DtoRefundRequest dtoConverter(RefundRequest input) {
        DtoRefundRequest refundRequest = new DtoRefundRequest();

        BeanUtils.copyProperties(input, refundRequest);

        DtoOrder dtoOrder = new DtoOrder();
        BeanUtils.copyProperties(input.getOrder(), dtoOrder);


        if (input.getOrder().getUser() != null) {
            DtoUser dtoUser = new DtoUser();
            BeanUtils.copyProperties(input.getOrder().getUser(), dtoUser);
            dtoOrder.setUser(dtoUser);
        }

        if (input.getOrder().getOrderItems() != null) {
            List<DtoOrderItem> dtoOrderItems = input.getOrder().getOrderItems().stream()
                    .map(item -> {
                        DtoOrderItem dtoItem = new DtoOrderItem();
                        BeanUtils.copyProperties(item, dtoItem);

                        if (item.getProduct() != null) {
                            DtoProduct dtoProduct = new DtoProduct();
                            BeanUtils.copyProperties(item.getProduct(), dtoProduct);
                            dtoItem.setProduct(dtoProduct);
                        }
                        return dtoItem;
                    }).collect(Collectors.toList());

            dtoOrder.setOrderItems(dtoOrderItems);
        }

        if (input.getOrder().getAddress() != null) {
            DtoAddress dtoAddress = new DtoAddress();
            BeanUtils.copyProperties(input.getOrder().getAddress(), dtoAddress);
            dtoOrder.setAddress(dtoAddress);
        }

        if (input.getOrder().getPayments() != null) {
            DtoPayment dtoPayment = new DtoPayment();
            BeanUtils.copyProperties(input.getOrder().getPayments(), dtoPayment);
            dtoOrder.setPayment(dtoPayment);
        }

        refundRequest.setOrder(dtoOrder);

        return refundRequest;
    }

    @Override
    public DtoRefundRequest createRefundRequest(DtoRefundRequestIU input) {
        RefundRequest refundRequest = new RefundRequest();

        Order order = orderRepository.findById(input.getOrderId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, input.getOrderId().toString())));

        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, order.getUser().getId().toString())));

        refundRequest.setOrder(order);
        refundRequest.setUser(user);
        refundRequest.setReason(input.getReason());
        refundRequest.setStatus(RefundRequestStatus.PENDING);
        refundRequest.setCreatedAt(new Date());
        refundRequest.setUpdatedAt(new Date());

        order.setStatus(OrderStatus.RETURN_REQUESTED);
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

        Order order = refundRequest.getOrder();
        order.setStatus(OrderStatus.RETURNED);
        orderRepository.save(order);
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

        Order order = refundRequest.getOrder();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
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

        Order order = refundRequest.getOrder();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        return dtoConverter(saved);
    }
}
