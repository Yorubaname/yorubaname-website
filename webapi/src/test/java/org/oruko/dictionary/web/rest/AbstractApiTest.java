package org.oruko.dictionary.web.rest;

import org.oruko.dictionary.web.exception.ApiExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;

/**
 * Abstract class for testing the API endpoints
 *
 * @author Dadepo Aderemi.
 */
public abstract class AbstractApiTest {

    protected ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver;
        exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(ApiExceptionHandler.class).resolveMethod(exception);
                if (method == null) {
                    return null;
                }
                return new ServletInvocableHandlerMethod(new ApiExceptionHandler(), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }
}
