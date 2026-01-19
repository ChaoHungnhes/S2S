package com.example.S2S.exception;

import com.example.S2S.common.Enum.ErrorCode;

public class AppException extends RuntimeException {
    /*AppException kế thừa RuntimeException, nghĩa là nó là một loại Exception có thể được ném ra (throw) mà không cần khai báo với throws.
    Điều này giúp chúng ta quản lý lỗi dễ dàng hơn mà không cần try-catch tại mọi nơi.*/
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // Gọi constructor của RuntimeException, truyền vào thông báo lỗi lấy từ
        // ErrorCode.
        this.errorCode = errorCode;
    }

    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
