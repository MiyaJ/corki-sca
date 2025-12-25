package com.corki.gateway.handler;

import com.corki.common.enums.ResponseEnum;
import com.corki.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关全局异常处理器
 *
 * @author corki
 * @date 2025/12/24
 */
@Slf4j
@Order(-1)
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * 处理
     *
     * @param exchange 交换
     * @param ex       前例
     * @return {@link Mono }<{@link Void }>
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        R<Object> result;
        HttpStatus httpStatus;

        if (ex instanceof ResponseStatusException rse) {
            // ResponseStatusException 处理
            httpStatus = HttpStatus.valueOf(rse.getStatusCode().value());
            result = R.fail(httpStatus.value(), rse.getReason());
        } else {
            // 其他异常
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            result = R.fail(ResponseEnum.ERROR);
            log.error("网关异常: ", ex);
        }

        response.setStatusCode(httpStatus);

        // 构建响应体
        String responseBody = toJson(result);
        DataBuffer buffer = response.bufferFactory()
                .wrap(responseBody.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 将 R 对象转换为 JSON 字符串
     */
    private String toJson(R<Object> r) {
        return String.format("{\"code\":%d,\"message\":\"%s\",\"data\":%s}",
                r.getCode(),
                r.getMessage(),
                r.getData() != null ? "\"{}\"" : "null");
    }
}
