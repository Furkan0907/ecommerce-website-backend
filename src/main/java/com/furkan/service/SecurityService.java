package com.furkan.service;

import com.furkan.repository.CartItemRepository;
import com.furkan.repository.CartRepository;
import com.furkan.repository.UserRepository;
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
}
