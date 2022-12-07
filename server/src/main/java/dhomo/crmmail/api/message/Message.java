/*
 * Message.java
 *
 * Created on 2018-08-10, 16:07
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
package dhomo.crmmail.api.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.mail.imap.IMAPMessage;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import dhomo.crmmail.api.exception.IsotopeException;
import dhomo.crmmail.api.lead.dto.LeadDto_id_name;
import dhomo.crmmail.api.lead.leadEvents.LeadEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "email_message", indexes = {@Index(name = "idx_message_messageid", columnList = "messageId")})
@TypeDefs({@TypeDef(name = "list-array", typeClass = ListArrayType.class), @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message extends LeadEvent {

    private static final String CET_ZONE_ID = "CET";
    public static final String HEADER_IN_REPLY_TO = "In-Reply-To";
    public static final String HEADER_REFERENCES = "References";
    public static final String HEADER_LIST_UNSUBSCRIBE = "List-Unsubscribe";

    // id письма в папке, может меняться
    @Transient
    private Long uid;

    // id email'a уникальный
    @Column(nullable = false, unique = true, updatable = false)
    private String messageId;

    @Transient
    private Long modseq;

    @Column(name = "from_col", columnDefinition = "text[]")
    @Type(type = "list-array")
    private List<String> from;

    @Column(columnDefinition = "text[]")
    @Type(type = "list-array")
    private List<String> replyTo;

    @NotEmpty(groups = {SmtpSend.class})
    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private List<Recipient> recipients;

    private String subject;

    private ZonedDateTime receivedDate;

    private Long size;

    @Transient
    private Boolean flagged, seen, recent, deleted;

    @Lob
    @Column(columnDefinition = "text")
    private String content;

    @OneToMany
    private List<Attachment> attachments = new ArrayList<>();

    @Column(name = "references_col", columnDefinition = "text[]")
    @Type(type = "list-array")
    private List<String> references = new ArrayList<>();

    @Column(columnDefinition = "text[]")
    @Type(type = "list-array")
    private List<String> inReplyTo;

    @Column(columnDefinition = "text[]")
    @Type(type = "list-array")
    private List<String> listUnsubscribe;


    @Transient
    @JsonProperty("leadsShort")
    Set<LeadDto_id_name> leadDtoIdNameSet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Message message = (Message) o;
        return messageId != null && Objects.equals(messageId, message.messageId);
    }

    @Transient
    @Override
    public String getType() {
        return "Message";
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Message{" + "uid=" + uid + ", messageId='" + messageId + '\'' + '}';
    }

    /**
     * Maps an {@link com.sun.mail.imap.IMAPMessage} to a {@link Message}.
     *
     * This method should only map those fields that are retrieved performed an IMAP fetch command (ENVELOPE,
     * UID, FLAGS...)
     *
     * To map other fields use a separate method.
     *
     * @param clazz Class of the new Message instance
     * @param folder where the message is located
     * @param imapMessage original message to map
     * @return mapped Message with fulfilled envelope fields
     */
    //F extends Folder & UIDFolder вполне можно заменить F extends IMAPFolder, никто кроме него и не подходит,
    // и вряд ли планируется какая-то другая реализация.
    // или вообще дженерик убрать наследников IMAPFolder в этот метод тоже вряд kb будут оправлять
    public static <M extends Message, F extends Folder & UIDFolder> M from(
            Class<M> clazz, F folder, IMAPMessage imapMessage) {

        final M ret;
        if (imapMessage != null) {
            try {
                ret = clazz.newInstance();
                ret.setUid(folder.getUID(imapMessage));
                ret.setMessageId(imapMessage.getMessageID());
                ret.setFrom(processAddress(imapMessage.getFrom()));
                ret.setReplyTo(processAddress(imapMessage.getReplyTo()));
                // Process only recipients received in ENVELOPE (don't use getAllRecipients)
                ret.setRecipients(Stream.of(
                        processAddress(RecipientType.TO, imapMessage.getRecipients(RecipientType.TO)),
                        processAddress(RecipientType.CC, imapMessage.getRecipients(RecipientType.CC)),
                        processAddress(RecipientType.BCC, imapMessage.getRecipients(RecipientType.BCC))
                ).flatMap(Collection::stream).collect(Collectors.toList()));
                ret.setSubject(imapMessage.getSubject());
                ret.setReceivedDate(imapMessage.getReceivedDate().toInstant().atZone(ZoneId.of(CET_ZONE_ID)));
                ret.setSize(imapMessage.getSizeLong());
                ret.setInReplyTo(Arrays.asList(
                        Optional.ofNullable(imapMessage.getHeader(HEADER_IN_REPLY_TO)).orElse(new String[0])));
                ret.setReferences(Arrays.asList(
                        Optional.ofNullable(imapMessage.getHeader(HEADER_REFERENCES)).orElse(new String[0])));
                ret.setListUnsubscribe(Arrays.asList(
                        Optional.ofNullable(imapMessage.getHeader(HEADER_LIST_UNSUBSCRIBE)).orElse(new String[0])));
                final Flags flags = imapMessage.getFlags();
                ret.setFlagged(flags.contains(Flags.Flag.FLAGGED));
                ret.setSeen(flags.contains(Flags.Flag.SEEN));
                ret.setRecent(flags.contains(Flags.Flag.RECENT));
                ret.setDeleted(flags.contains(Flags.Flag.DELETED));
            } catch (ReflectiveOperationException | MessagingException e) {
                throw new IsotopeException("Error parsing IMAP Message", e);
            }
        } else {
            ret = null;
        }
        return ret;
    }

    public static <F extends Folder & UIDFolder> Message from(F folder, IMAPMessage imapMessage) {
        return from(Message.class, folder, imapMessage);
    }

    private static List<Recipient> processAddress(RecipientType recipient, Address... addresses) {
        return processAddress(addresses).stream()
                .map(a -> new Recipient(recipient.toString(), a))
                .collect(Collectors.toList());
    }

    private static List<String> processAddress(Address... addresses) {
        return Stream.of(Optional.ofNullable(addresses).orElse(new Address[0]))
                .map(address -> {
                    if (address instanceof InternetAddress) {
                        final InternetAddress internetAddress = (InternetAddress) address;
                        return internetAddress.getPersonal() == null ? internetAddress.getAddress() :
                                String.format("\"%s\" <%s>", internetAddress.getPersonal(), internetAddress.getAddress());
                    } else {
                        return address.toString();
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Validation Group interface for SMTP send operations
     */
    public interface SmtpSend {}

}
