package com.example.S2S.exception;

import com.example.WebKtx.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse<Object>> handleAllException(Exception ex) {
        ApiResponse<Object> res = new ApiResponse<Object>();
        res.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Internal Server Error";
        }
        res.setResultDesc(errorMessage);
        res.setResponseTime(String.valueOf(LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(value = {
            IdInvalidException.class,
    })
    public ResponseEntity<ApiResponse<Object>> handleIdException(Exception ex) {
        ApiResponse<Object> res = new ApiResponse<Object>();
        res.setResultCode(HttpStatus.BAD_REQUEST.value());
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Exception occurs...";
        }
        res.setResultDesc(errorMessage);
        res.setResponseTime(String.valueOf(LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class,
    })
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(Exception ex) {
        ApiResponse<Object> res = new ApiResponse<Object>();
        res.setResultCode(HttpStatus.NOT_FOUND.value());
        res.setResultDesc("404 Not Found. URL may not exist...");
        res.setResponseTime(String.valueOf(LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        List<String> errors = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ApiResponse<Object> res = new ApiResponse<>();
        res.setResultCode(HttpStatus.BAD_REQUEST.value());

        res.setResultDesc(errors.size() == 1 ? errors.get(0) : String.join("; ", errors));
        res.setResponseTime(String.valueOf(LocalDateTime.now()));
        res.setData(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
        ApiResponse<Object> response = ApiResponse.builder()
                .resultCode(ex.getErrorCode().getCode())
                .resultDesc(ex.getMessage())
                .responseTime(String.valueOf(LocalDateTime.now()))
                .data(null)
                .build();

        return ResponseEntity.status(ex.getErrorCode().getStatusCode()).body(response);
    }
}
