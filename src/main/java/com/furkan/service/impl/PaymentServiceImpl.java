package com.furkan.service.impl;

import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoPayment;
import com.furkan.enums.OrderStatus;
import com.furkan.enums.PaymentMethod;
import com.furkan.enums.PaymentStatus;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Order;
import com.furkan.model.Payment;
import com.furkan.repository.OrderRepository;
import com.furkan.repository.PaymentRepository;
import com.furkan.service.IOrderService;
import com.furkan.service.IPaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentUpdateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.net.Webhook;
import com.stripe.exception.SignatureVerificationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IOrderService orderService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    private DtoPayment dtoConverter(Payment payment) {
        DtoPayment dtoPayment = new DtoPayment();
        BeanUtils.copyProperties(payment, dtoPayment);
        dtoPayment.setOrderId(payment.getOrder().getId());
        dtoPayment.setTransactionId(payment.getTransactionId());
        dtoPayment.setStatus(payment.getStatus());
        return dtoPayment;
    }

    private String createStripeCheckoutSession(Order order) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/payment-success?orderId=" + order.getId())
                .setCancelUrl("http://localhost:4200/payment-cancel?orderId=" + order.getId())
                .setCustomerEmail(order.getUser().getEmail())
                .putMetadata("orderId", order.getId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("try")
                                                .setUnitAmount(order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Order #" + order.getId())
                                                        .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);

        if (session.getPaymentIntent() != null) {
            PaymentIntent pi = PaymentIntent.retrieve(session.getPaymentIntent());
            Map<String, String> meta = pi.getMetadata();
            if (meta == null) {
                meta = new HashMap<>();
            }
            meta.put("orderId", order.getId().toString());
            PaymentIntentUpdateParams updateParams = PaymentIntentUpdateParams.builder()
                    .putAllMetadata(meta)
                    .build();
            pi.update(updateParams);
        }

        return session.getId();
    }

    @Override
    public DtoPayment createPayment(DtoPaymentIU input) {
        Order order = orderRepository.findById(input.getOrderId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, input.getOrderId().toString())));


        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ALREADY_CANCELLED, order.getId().toString()));
        }

        boolean hasPaid = paymentRepository.findByOrderId(order.getId())
                .stream().anyMatch(p -> p.getStatus() == PaymentStatus.COMPLETED);

        if (hasPaid) {
            throw new BaseException(new ErrorMessage(MessageType.PAYMENT_ALREADY_COMPLETED, input.getOrderId().toString()));
        }

        List<Payment> pendingPayments = paymentRepository.findByOrderIdAndStatus(order.getId(), PaymentStatus.PENDING);
        for (Payment p : pendingPayments) {
            p.setStatus(PaymentStatus.FAILED);
            p.setUpdatedAt(new Date());
            paymentRepository.save(p);
        }

        try {
            String sessionId = createStripeCheckoutSession(order);

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setCreatedAt(new Date());
            payment.setUpdatedAt(new Date());
            payment.setAmount(order.getTotalAmount());
            payment.setMethod(input.getMethod() != null ? input.getMethod() : PaymentMethod.CREDIT_CARD);
            payment.setTransactionId(sessionId);
            payment.setStatus(PaymentStatus.PENDING);
            Payment savedPayment = paymentRepository.save(payment);

            DtoPayment dto = dtoConverter(savedPayment);
            dto.setTransactionId(sessionId);
            return dto;
        } catch (StripeException e) {
            throw new RuntimeException("Stripe payment creation failed", e);
        }
    }

    @Override
    public DtoPayment findPaymentByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_PAYMENT_FOUND_FOR_THIS_ORDER, orderId.toString()));
        }

        Payment latestPayment = payments.stream()
                .max(Comparator.comparing(Payment::getCreatedAt))
                .orElseThrow();

        return dtoConverter(latestPayment);
    }

    @Override
    public List<DtoPayment> findPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        if (payments.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PAYMENT_LIST_IS_EMPTY_FOR_THIS_USER, userId.toString()));
        }
        List<DtoPayment> dtoPaymentList = new ArrayList<>();
        for (Payment payment : payments) {
            dtoPaymentList.add(dtoConverter(payment));
        }
        return dtoPaymentList;
    }

    @Override
    public boolean hasSuccessfulPayment(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_PAYMENT_FOUND_FOR_THIS_ORDER, orderId.toString()));
        }

        Payment latestPayment = payments.stream()
                .max(Comparator.comparing(Payment::getCreatedAt))
                .orElseThrow();

        return latestPayment.getStatus() == PaymentStatus.COMPLETED;
    }

    @Override
    public DtoPayment updatePaymentStatus(Long orderId, PaymentStatus newStatus, String transactionId) {
        List<Payment> payments = paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.PENDING);
        if (payments.isEmpty()) {
            System.out.println("No pending payment found order: " + orderId + ", skipping update.");
            return null;
        }

        System.out.println("Pending payments for order " + orderId + ": " + payments.size());
        payments.forEach(p -> System.out.println("Payment ID: " + p.getId() + ", Status: " + p.getStatus()));

        Payment payment = payments.get(0);
        payment.setStatus(newStatus);
        if (transactionId != null) {
            payment.setTransactionId(transactionId);
        }
        payment.setUpdatedAt(new Date());

        Payment saved = paymentRepository.save(payment);

        if (newStatus == PaymentStatus.COMPLETED) {
            orderService.confirmOrderPayment(orderId);
        }

        return dtoConverter(saved);
    }

    @Override
    public DtoPayment refundPayment(Long orderId) throws StripeException {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_PAYMENT_FOUND_FOR_THIS_ORDER, orderId.toString()));
        }

        Payment payment = payments.stream()
                .max(Comparator.comparing(Payment::getCreatedAt))
                .orElseThrow();

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BaseException(new ErrorMessage(MessageType.PAYMENT_NOT_COMPLETED, orderId.toString()));
        }

        Stripe.apiKey = stripeSecretKey;

        RefundCreateParams params = RefundCreateParams.builder()
                        .setPaymentIntent(payment.getTransactionId())
                                .build();

        Refund refund = Refund.create(params);

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(new Date());
        Payment refundedPayment = paymentRepository.save(payment);

        Order order = refundedPayment.getOrder();
        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(new Date());
        orderRepository.save(order);

        return dtoConverter(refundedPayment);
    }

    @Override
    public void handleStripeWebhook(String payload, String sigHeader) {
        try {
            Stripe.apiKey = stripeSecretKey;

            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

            StripeObject stripeObject = event.getData().getObject();

            if (stripeObject instanceof Session session) {
                String orderIdStr = session.getMetadata().get("orderId");
                if (orderIdStr != null) {
                    Long orderId = Long.parseLong(orderIdStr);
                    List<Payment> payments = paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.PENDING);
                    if (!payments.isEmpty()) {
                        Payment payment = payments.get(0);
                        payment.setStatus(PaymentStatus.COMPLETED);
                        payment.setTransactionId(session.getId());
                        payment.setUpdatedAt(new Date());
                        paymentRepository.save(payment);
                        orderService.confirmOrderPayment(orderId);
                    }
                }
            } else if (stripeObject instanceof PaymentIntent pi) {
                Payment paymentByTx = paymentRepository.findByTransactionId(pi.getId())
                        .orElseThrow(() -> new RuntimeException("Payment not found for PaymentIntent: " + pi.getId()));

                updatePaymentStatus(paymentByTx.getOrder().getId(), PaymentStatus.COMPLETED, pi.getId());
            } else if (stripeObject instanceof Charge ch) {
                Payment paymentByTx = paymentRepository.findByTransactionId(ch.getId())
                        .orElseThrow(() -> new RuntimeException("Payment not found for Charge: " + ch.getId()));

                updatePaymentStatus(paymentByTx.getOrder().getId(), PaymentStatus.COMPLETED, ch.getId());
            } else {
                System.out.println("Ignored Stripe object type: " + stripeObject.getClass().getSimpleName());
            }

        } catch (SignatureVerificationException e) {
            throw new RuntimeException("⚠️ Webhook imza doğrulaması başarısız", e);
        } catch (Exception e) {
            System.err.println("Webhook processing exception: " + e.getMessage());
        }
    }
}
