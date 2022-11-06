/*
 * SmtpResource.java
 *
 * Created on 2018-10-07, 20:12
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
package dhomo.crmmail.api.smtp;

import dhomo.crmmail.api.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2018-10-07.
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/smtp")
public class SmtpResource {

    private final ObjectFactory<SmtpService> smtpServiceFactory;

    @Autowired
    public SmtpResource(ObjectFactory<SmtpService> smtpServiceFactory) {
        this.smtpServiceFactory = smtpServiceFactory;
    }

    @PostMapping
    public ResponseEntity<Void> sendMessage(
            HttpServletRequest request, @Validated({Message.SmtpSend.class}) @RequestBody Message message) {

        log.debug("Sending SMTP message");
        smtpServiceFactory.getObject().sendMessage(request, message);
        return ResponseEntity.noContent().build();
    }

}
