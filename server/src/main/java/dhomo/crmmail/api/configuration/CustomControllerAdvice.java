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

import dhomo.crmmail.api.exception.CMException;
import dhomo.crmmail.api.exception.CMInvalidFieldException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CMException.class)
    public <T extends CMException> ResponseEntity<Object> handleCMException(T exception, WebRequest request) {
        return handleExceptionInternal(exception, null, new HttpHeaders(), exception.getHttpStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {
        return handleCMException(new CMInvalidFieldException(ex.getBindingResult()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        return handleCMException(new CMInvalidFieldException(ex.getBindingResult()), request);
    }

    /**
     * A single place to customize the response body of all exception types.
     * @param ex the exception
     * @param body the body for the response. If null then ex.getMessage() is used
     * @param headers the headers for the response
     * @param status the response status
     * @param request the current request
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(body != null ? body : ex.getMessage(), headers, status);
    }
}
