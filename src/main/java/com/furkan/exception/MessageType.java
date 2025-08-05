package com.furkan.exception;

import lombok.Getter;

@Getter
public enum MessageType {

    REFRESH_TOKEN_IS_EXPIRED("1009", "refresh tokenın süresi dolmuştur"),
    USER_NOT_FOUND("1001", "idli user bulunamadı"),
    USERNAME_NOT_FOUND("1006", "username bulunamadı"),
    USERNAME_ALREADY_EXISTS("1007", "bu usernameli kayıt zaten var"),
    EMAIL_ALREADY_EXISTS("1008", "bu email zaten kayıtlı"),
    EMAIL_NOT_FOUND("1009", "kayıtlı email bulunamadı"),
    REFRESH_TOKEN_NOT_FOUND("1003", "refresh token bulunamadı"),

    PRODUCT_NOT_FOUND("2001", "verilen id'li ürün bulunamadı"),
    PRODUCT_NAME_NOT_FOUND("2002", "verilen isimli ürün bulunamadı"),
    PRODUCT_BRAND_NOT_FOUND("2003", "verilen markalı ürün bulunamadı"),
    PRODUCT_CATEGORY_NOT_FOUND("2004", "verilen kategoride ürün bulunamadı"),
    PRODUCT_PRICE_BETWEEN_NOT_FOUND("2005", "verilen miktar aralığında ürün bulunamadı"),
    PRODUCT_AVAILABILITY_NOT_FOUND("2006", "mevcut stokta ürün bulunamadı"),
    PRODUCT_FILTER_NOT_FOUND("2007", "verilen kriterde ürün bulunamadı"),

    CART_NOT_FOUND("3001", "verilen id'li sepet bulunamadı"),
    CART_LIST_IS_EMPTY("3002", "sistemde hiç kayıtlı sepet yok"),
    NO_CARD_FOUND_FOR_THIS_USER("3003", "bu kullanıcıya sahip sepet bulunamadı"),

    ITEM_NOT_FOUND_IN_CART("4001", "sepette bu idli ürün bulunamadı"),

    ORDER_NOT_FOUND("5001", "böyle bir sipariş bulunamadı"),
    NO_ORDER_FOUND_FOR_THIS_USER("5002", "bu userId li kullancıda böyle bir sipariş bulunamadı"),
    ORDER_ALREADY_COMPLETED("5003", "sipariş zaten teslim edilmiş"),
    ORDER_ALREADY_CANCELLED("5004", "bu sipariş iptal edilmiş"),

    ORDER_ITEM_NOT_FOUND("6001", "sipariş içinde böyle bir ürün bulunamadı"),

    NO_PAYMENT_FOUND_FOR_THIS_ORDER("7001", "sipariş için ödeme bilgisi bulunamadı"),
    PAYMENT_LIST_IS_EMPTY_FOR_THIS_USER("7002", "bu kullanıcının ödeme geçmişi bulunmamaktadır"),

    REFUND_REQUEST_NOT_FOUND("8001", "iade talebi bulunamadı"),
    REFUND_REQUEST_LIST_IS_EMPTY("8002", "hiçbir iade talebi yok"),
    NO_REFUND_REQUEST_FOUND_FOR_THIS_USER("8003", "bu kullanıcıya ait iade isteği yok"),
    REFUND_REQUEST_ALREADY_BEEN_HANDLED("8004", "iade talebi zaten sonuçlandırılmış"),

    GENERAL_EXCEPTION("9999", "genel bir hata oluştu");

    String code;

    String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
