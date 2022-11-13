/*
 * IsotopeControllerAdvice.java
 *
 * Created on 2018-08-17, 6:49
 *
 * Copyright 2018 Marc Nuri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package dhomo.crmmail.api.configuration;

import dhomo.crmmail.api.exception.InvalidFieldException;
import dhomo.crmmail.api.exception.IsotopeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.annotation.Nonnull;

import static dhomo.crmmail.api.http.HttpHeaders.ISOTOPE_EXCEPTION;

/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2018-08-17.
 */
@SuppressWarnings("unchecked")
@ControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {
    private static final int MISCELLANEOUS_HTTP_WARN_CODE = 199;
    private static final int MAX_HEADER_LENGTH = 500;

    // нифига не понятно зачем в хедеры писать, но трогать не стал, надо фронт сначала смотреть
    @ExceptionHandler(IsotopeException.class)
    public <T extends IsotopeException> ResponseEntity<String> handleIsotopeException(T exception) {
        final String message = exception.getMessage();
        final HttpHeaders headers = new HttpHeaders();
        headers.set(ISOTOPE_EXCEPTION, getClass().getName());
        headers.set(HttpHeaders.WARNING, String.format("%s %s \"%s\"",
                MISCELLANEOUS_HTTP_WARN_CODE, "-",
                message == null ? "" :
                        message.substring(0, Math.min(message.length(), MAX_HEADER_LENGTH))
                                .replaceAll("[\\n\\r]", "")));
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<>(message, headers, exception.getHttpStatus());
    }

    @Override
    protected ResponseEntity handleBindException(BindException ex,
                                                 HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleIsotopeException(new InvalidFieldException(ex.getBindingResult()));
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleIsotopeException(new InvalidFieldException(ex.getBindingResult()));
    }

//    default exception handler
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(ex.getMessage(), headers, status);
    }
}
