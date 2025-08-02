package com.furkan.service.impl;

import com.furkan.dto.request.DtoCartIU;
import com.furkan.dto.response.DtoCart;
import com.furkan.dto.response.DtoCartItem;
import com.furkan.dto.response.DtoProduct;
import com.furkan.dto.response.DtoUser;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Cart;
import com.furkan.model.CartItem;
import com.furkan.model.Product;
import com.furkan.model.User;
import com.furkan.repository.CartItemRepository;
import com.furkan.repository.CartRepository;
import com.furkan.repository.ProductRepository;
import com.furkan.repository.UserRepository;
import com.furkan.service.ICartItemService;
import com.furkan.service.ICartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.prefs.BackingStoreException;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ICartItemService cartItemService;

    private Optional<CartItem> findCartItem(Long cartId, Long productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId);
    }

    private Cart saveCart(DtoCartIU input) {
        Cart cart = new Cart();
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, input.getUserId().toString())));
        BeanUtils.copyProperties(input, cart);
        cart.setCreatedAt(new Date());
        cart.setUpdatedAt(new Date());
        cart.setUser(user);
        return cart;
    }

    private DtoCart dtoConverter(Cart cart) {
        DtoCart dtoCart = new DtoCart();
        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(cart, dtoCart);
        BeanUtils.copyProperties(cart.getUser(), dtoUser);
        dtoCart.setUser(dtoUser);

        if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            List<DtoCartItem> dtoCartItems = cart.getCartItems().stream().map(cartItem -> {
                DtoCartItem dtoCartItem = new DtoCartItem();
                BeanUtils.copyProperties(cartItem, dtoCartItem);

                if (cartItem.getProduct() != null) {
                    DtoProduct dtoProduct = new DtoProduct();
                    BeanUtils.copyProperties(cartItem.getProduct(), dtoProduct);
                    dtoCartItem.setProduct(dtoProduct);
                }

                return dtoCartItem;
            }).toList();

            dtoCart.setCartItems(dtoCartItems);
        }
        return dtoCart;
    }

    private List<DtoCart> dtoListConverter(List<Cart> cartList) {
        List<DtoCart> dtoCartList = new ArrayList<>();
        for (Cart dbCart : cartList) {
            dtoCartList.add(dtoConverter(dbCart));
        }
        return dtoCartList;
    }

    @Override
    public DtoCart createCart(DtoCartIU cart) {
        Cart savedCart = cartRepository.save(saveCart(cart));
        return dtoConverter(savedCart);
    }

    @Override
    public DtoCart findCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CART_NOT_FOUND, id.toString())));
        return dtoConverter(cart);
    }

    @Override
    public List<DtoCart> findAllCarts() {
        List<Cart> cartList = cartRepository.findAll();
        if (cartList.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.CART_NOT_FOUND, null));
        }
        return dtoListConverter(cartList);
    }

    @Override
    public DtoCart updateCart(Long id, DtoCartIU updatedCart) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CART_NOT_FOUND, id.toString())));
        cart.setUpdatedAt(new Date());
        User user = userRepository.findById(updatedCart.getUserId())
                        .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, updatedCart.getUserId().toString())));
        cart.setUser(user);
        Cart newUpdatedCart = cartRepository.save(cart);
        return dtoConverter(newUpdatedCart);
    }

    @Override
    public void deleteCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CART_NOT_FOUND, id.toString())));
        cartRepository.delete(cart);
    }

    @Override
    public DtoCart findCartByUserId(Long id) {
        Cart cart = cartRepository.findByUserId(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_CARD_FOUND_FOR_THIS_USER, id.toString())));
        return dtoConverter(cart);
    }

    @Override
    public DtoCartItem addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_CARD_FOUND_FOR_THIS_USER, userId.toString())));

        Optional<CartItem> optionalCartItem = findCartItem(cart.getId(), productId);

        CartItem cartItem;
        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setUpdatedAt(new Date());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, productId.toString())));
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(new Date());
            cartItem.setUpdatedAt(new Date());
        }

        CartItem saved = cartItemRepository.save(cartItem);

        CartItem fullCartItem = cartItemRepository.findWithAllRelations(saved.getId())
                .orElseThrow(() -> new RuntimeException("CartItem not found after save"));
        return cartItemService.dtoConverter(fullCartItem);
    }

    @Override
    public DtoCartItem updateItemQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_CARD_FOUND_FOR_THIS_USER, userId.toString())));

        CartItem foundItem = null;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getId().equals(productId)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem == null) {
            throw new BaseException(new ErrorMessage(MessageType.ITEM_NOT_FOUND_IN_CART, productId.toString()));
        }

        foundItem.setQuantity(quantity);
        foundItem.setUpdatedAt(new Date());

        CartItem updated = cartItemRepository.save(foundItem);
        return cartItemService.dtoConverter(updated);
    }

    @Override
    public void removeItemFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_CARD_FOUND_FOR_THIS_USER, userId.toString())));

        CartItem foundItem = null;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getId().equals(productId)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem == null) {
            throw new BaseException(new ErrorMessage(MessageType.ITEM_NOT_FOUND_IN_CART, productId.toString()));
        }

        cart.getCartItems().remove(foundItem);
        cartItemRepository.delete(foundItem);

        cart.setUpdatedAt(new Date());
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_CARD_FOUND_FOR_THIS_USER, userId.toString())));

        List<CartItem> items = new ArrayList<>(cart.getCartItems());

        for (CartItem item : items) {
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
        }

        cart.setUpdatedAt(new Date());
        cartRepository.save(cart);
    }

    @Override
    public List<DtoCartItem> getCartItems(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new BaseException(new ErrorMessage(MessageType.CART_NOT_FOUND, cartId.toString()));
        }
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        return cartItemService.dtoListConverter(items);
    }
}
