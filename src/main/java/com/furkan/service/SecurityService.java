package com.furkan.service;

import com.furkan.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RefundRequestRepository refundRequestRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public boolean isCartItemOwner(Long cartItemId, String username) {
        return cartItemRepository.findById(cartItemId)
                .map(cartItem -> {
                    String ownerUsername = cartItem.getCart().getUser().getUsername();
                    return ownerUsername.equals(username);
                })
                .orElse(false);
    }

    public boolean canAccessCart(Long cartId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || cartRepository.findById(cartId)
                .map(cart -> cart.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }

    public boolean isOrderItemOwner(Long orderItemId, String username) {
        return orderItemRepository.findById(orderItemId)
                .map(orderItem -> {
                    String ownerUsername = orderItem.getOrder().getUser().getUsername();
                    return ownerUsername.equals(username);
                })
                .orElse(false);
    }

    public boolean canAccessOrder(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || orderRepository.findById(orderId)
                .map(order -> order.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }

    public boolean canModifyOrder(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || orderRepository.findById(orderId)
                .map(order -> order.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }

    public boolean isOrderOwner(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }

    public boolean isOnlyCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")) &&
                auth.getAuthorities().stream()
                        .noneMatch(a -> a.getAuthority().equals("ROLE_SELLER") || a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isOnlySeller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SELLER")) &&
                auth.getAuthorities().stream()
                        .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isOnlyAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isOnlyUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")) &&
                auth.getAuthorities().stream()
                        .noneMatch(a ->
                                a.getAuthority().equals("ROLE_SELLER") ||
                                a.getAuthority().equals("ROLE_ADMIN") ||
                                a.getAuthority().equals("ROLE_CUSTOMER")
                        );
    }

    public boolean canAccessOrdersOfUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || userRepository.findById(userId)
                .map(user -> user.getUsername().equals(currentUsername))
                .orElse(false);
    }

    public boolean isRefundRequestOwner(Long refundRequestId, Long userId) {
        return refundRequestRepository
                .findById(refundRequestId)
                .map(r -> r.getUser().getId().equals(userId))
                .orElse(false);
    }

    public boolean isReviewOwner(Long reviewId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        return reviewRepository.findById(reviewId)
                .map(r -> r.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }
}
