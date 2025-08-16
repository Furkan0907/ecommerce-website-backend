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
    INVALID_PASSWORD("1004", "şifre geçerli değil"),

    NO_ADDRESS_FOUND_FOR_THIS_USER("1501", "bu kullanıcıya ait adres bulunamadı"),
    ADDRESS_NOT_FOUND("1502", "bu id ye sahip adres bulunamadı"),

    PRODUCT_NOT_FOUND("2001", "verilen id'li ürün bulunamadı"),
    PRODUCT_NAME_NOT_FOUND("2002", "verilen isimli ürün bulunamadı"),
    PRODUCT_BRAND_NOT_FOUND("2003", "verilen markalı ürün bulunamadı"),
    PRODUCT_CATEGORY_NOT_FOUND("2004", "verilen kategoride ürün bulunamadı"),
    PRODUCT_PRICE_BETWEEN_NOT_FOUND("2005", "verilen miktar aralığında ürün bulunamadı"),
    PRODUCT_AVAILABILITY_NOT_FOUND("2006", "mevcut stokta ürün bulunamadı"),
    PRODUCT_FILTER_NOT_FOUND("2007", "verilen kriterde ürün bulunamadı"),
    OUT_OF_STOCK("2008", "ürünün talep edilen kadar stok durumu yoktur"),

    CART_NOT_FOUND("3001", "verilen id'li sepet bulunamadı"),
    CART_LIST_IS_EMPTY("3002", "sistemde hiç kayıtlı sepet yok"),
    NO_CARD_FOUND_FOR_THIS_USER("3003", "bu kullanıcıya sahip sepet bulunamadı"),

    ITEM_NOT_FOUND_IN_CART("4001", "sepette bu idli ürün bulunamadı"),

    ORDER_NOT_FOUND("5001", "böyle bir sipariş bulunamadı"),
    NO_ORDER_FOUND_FOR_THIS_USER("5002", "bu userId li kullancıda böyle bir sipariş bulunamadı"),
    ORDER_ALREADY_COMPLETED("5003", "sipariş zaten teslim edilmiş"),
    ORDER_ALREADY_CANCELLED("5004", "bu sipariş iptal edilmiş"),
    ORDER_IS_NOT_PENDING("5005", "sipariş ödenmiş"),
    ORDER_MUST_BE_CONFIRMED("5006", "sipariş ödemesi yapılmadan kargoya verilemiyor"),
    ORDER_MUST_BE_SHIPPED("5007", "sipariş daha kargoya verilmedi"),
    CAN_NOT_CANCEL_ORDER("5008", "siparişi artık iptal edemez"),

    ORDER_ITEM_NOT_FOUND("6001", "sipariş içinde böyle bir ürün bulunamadı"),

    NO_PAYMENT_FOUND_FOR_THIS_ORDER("7001", "sipariş için ödeme bilgisi bulunamadı"),
    PAYMENT_LIST_IS_EMPTY_FOR_THIS_USER("7002", "bu kullanıcının ödeme geçmişi bulunmamaktadır"),
    PAYMENT_ALREADY_COMPLETED("7003", "ödeme yapılmış"),
    PAYMENT_FAILED("7004", "ödeme işlemi başarılı olamadı"),
    PAYMENT_NOT_COMPLETED("7005", "ödeme henüz tamamlanmadı"),
    REFUND_FAILED("7006", "geri ödeme gerçekleştirilemedi"),
    No_PENDING_PAYMENT_FOUND("7007", "işlenen ödeme bulunamadı"),

    REFUND_REQUEST_NOT_FOUND("8001", "iade talebi bulunamadı"),
    REFUND_REQUEST_LIST_IS_EMPTY("8002", "hiçbir iade talebi yok"),
    NO_REFUND_REQUEST_FOUND_FOR_THIS_USER("8003", "bu kullanıcıya ait iade isteği yok"),
    REFUND_REQUEST_ALREADY_BEEN_HANDLED("8004", "iade talebi zaten sonuçlandırılmış"),

    REVIEW_NOT_FOUND("9001", "ürün için böyle bir yorum bulunamadı"),
    NO_REVIEW_FOUND_FOR_THIS_USER("9002", "bu kullanıcının yaptığı yorum bulunamadı"),
    USER_NOT_BOUGHT_PRODUCT("9003", "kullanıcı bu ürünü almadan ürüne yorum yapamaz"),
    ALREADY_REVIEWED("9004", "kullanıcı bir ürün için en fazla bir yorum yapabilir"),

    COMPLAINT_NOT_FOUND("9501", "şikayet bulunamadı"),
    COMPLAINT_IS_ALREADY_RESOLVED("9502", "şikayet zaten çözülmüş"),

    GENERAL_EXCEPTION("9999", "genel bir hata oluştu");

    String code;

    String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
