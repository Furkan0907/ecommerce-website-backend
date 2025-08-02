package com.furkan.service.impl;

import com.furkan.dto.request.DtoCartItemIU;
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
import com.furkan.repository.CartItemRepository;
import com.furkan.repository.CartRepository;
import com.furkan.repository.ProductRepository;
import com.furkan.service.ICartItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartItemServiceImpl implements ICartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    private CartItem saveCartItem(DtoCartItemIU input) {
        CartItem cartItem = new CartItem();
        Cart cart = cartRepository.findById(input.getCartId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CART_NOT_FOUND, input.getCartId().toString())));

        Product product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, input.getProductId().toString())));

        cartItem.setCreatedAt(new Date());
        cartItem.setUpdatedAt(new Date());
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(input.getQuantity());

        return cartItem;
    }

    @Override
    public DtoCartItem dtoConverter(CartItem cartItem) {
        DtoCartItem dtoCartItem = new DtoCartItem();
        BeanUtils.copyProperties(cartItem, dtoCartItem);
        Product product = cartItem.getProduct();
        if (product != null) {
            DtoProduct dtoProduct = new DtoProduct();
            BeanUtils.copyProperties(product, dtoProduct);
            dtoCartItem.setProduct(dtoProduct);
        }
        return dtoCartItem;
    }

    @Override
    public List<DtoCartItem> dtoListConverter(List<CartItem> cartItems) {
        List<DtoCartItem> dtoCartItemList = new ArrayList<>();
        if (cartItems.isEmpty()) {
            return new ArrayList<>();
        }
        for (CartItem cartItem : cartItems) {
            dtoCartItemList.add(dtoConverter(cartItem));
        }
        return dtoCartItemList;
    }

    @Override
    public DtoCartItem createCartItem(DtoCartItemIU input) {
        CartItem cartItem = cartItemRepository.save(saveCartItem(input));
        return dtoConverter(cartItem);
    }

    @Override
    public DtoCartItem findCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ITEM_NOT_FOUND_IN_CART, id.toString())));
        return dtoConverter(cartItem);
    }

    @Override
    public List<DtoCartItem> findAllCartItems() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        return dtoListConverter(cartItems);
    }

    @Override
    public DtoCartItem updateCartItem(Long id, DtoCartItemIU input) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ITEM_NOT_FOUND_IN_CART, id.toString())));
        cartItem.setUpdatedAt(new Date());
        cartItem.setQuantity(input.getQuantity());
        Cart cart = cartRepository.findById(input.getCartId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CART_NOT_FOUND, input.getCartId().toString())));
        Product product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, input.getProductId().toString())));
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        return dtoConverter(updatedCartItem);
    }

    @Override
    public void deleteCartItem(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ITEM_NOT_FOUND_IN_CART, id.toString())));
        cartItemRepository.delete(cartItem);
    }
}
