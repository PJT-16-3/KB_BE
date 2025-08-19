package org.scoula.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestControllerAdvice // 핵심: JSON 바디로 돌려줌
public class CommonExceptionAdvice {

    /** FastAPI 등 외부에서 4xx/5xx를 보낸 경우: 상태코드/바디 그대로 패스스루 */
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<String> handleHttpStatus(HttpStatusCodeException e) {
        HttpHeaders headers = new HttpHeaders();
        MediaType ct = e.getResponseHeaders() != null ? e.getResponseHeaders().getContentType() : null;
        headers.setContentType(ct != null ? ct : MediaType.APPLICATION_JSON);

        String body = e.getResponseBodyAsString(); // 예: {"detail": "... '부여군' ..."}
        log.error("Downstream {}: {}", e.getStatusCode().value(), body);
        return new ResponseEntity<>(body, headers, e.getStatusCode());
    }

    /** 외부 통신 오류(타임아웃 등) */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClient(RestClientException e, HttpServletRequest req) {
        log.error("Downstream network error: {} {}", req.getMethod(), req.getRequestURI(), e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\":\"예측 서버와 통신에 실패했습니다. 잠시 후 다시 시도해 주세요.\"}");
    }

    /** 404 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handle404(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\":\"요청하신 경로를 찾을 수 없습니다.\"}");
    }

    /** 기타 모든 예외 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(Exception e, HttpServletRequest req) {
        log.error("Unhandled exception at {} {}", req.getMethod(), req.getRequestURI(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\":\" 잠시 후 다시 시도해 주세요.\"}");
    }
}
