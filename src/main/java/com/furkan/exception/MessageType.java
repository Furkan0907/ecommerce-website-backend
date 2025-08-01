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

    GENERAL_EXCEPTION("9999", "genel bir hata oluştu");

    String code;

    String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
