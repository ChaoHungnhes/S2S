package com.example.S2S.common.Enum;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_EXISTED(1002, "user existed", HttpStatus.BAD_REQUEST), // code lỗi ~400
    USER_NOT_EXISTED(1005, "user not existed", HttpStatus.NOT_FOUND), // code lỗi ~404
    INVALID_KEY(1001, "invalid message key", HttpStatus.BAD_REQUEST),
    USER_NAMEINVALID(1003, "username must be least 3 character", HttpStatus.BAD_REQUEST),
    USER_PASSWORDINVALID(1004, "password must be least 8 character", HttpStatus.BAD_REQUEST),
    USER_DOBINVALID(1008, "your age must be at least {min}", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(
            9999,
            "uncategozied exception error",
            HttpStatus.INTERNAL_SERVER_ERROR), // ngoaị lệ chưa phân loại trong gloobalexceptionHandler, code lỗi ~500
    UNAUTHENTICATED(1006, "unauthenticated", HttpStatus.UNAUTHORIZED), // code lỗi ~401 - chưa đăng nhập
    UNAUTHORIZED(1007, "you do not have permission", HttpStatus.FORBIDDEN),// code lỗi ~403(0 có quyền truy cập)
    EMAIL_EXISTED(1010, "email existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1011, "user not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(1012, "role not found", HttpStatus.NOT_FOUND)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
