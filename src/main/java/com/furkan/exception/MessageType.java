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
    GENERAL_EXCEPTION("9999", "genel bir hata oluştu");

    String code;

    String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
