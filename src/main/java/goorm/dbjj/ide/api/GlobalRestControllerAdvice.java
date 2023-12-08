package goorm.dbjj.ide.api;

import goorm.dbjj.ide.api.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 RestController 에서 발생하는 예외를 처리하기 위한 클래스
 */
@Slf4j
@RestControllerAdvice(basePackages = "goorm.dbjj.ide")
public class GlobalRestControllerAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        e.printStackTrace();
        log.error("Exception catched in RestControllerAdvice : {}",e.getMessage());
        return ApiResponse.fail(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public ApiResponse<String> handleBaseException(BaseException e) {
        log.debug("Exception catched in RestControllerAdvice : {}",e.getMessage());
        return ApiResponse.fail(e.getMessage());
    }
}
