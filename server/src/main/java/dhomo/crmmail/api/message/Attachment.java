/*
 * Attachment.java
 *
 * Created on 2018-09-11, 7:07
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
import dhomo.crmmail.api.resource.IsotopeResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Attachment extends IsotopeResource {

    @Id
    private String contentId;
    private String fileName;
    private String contentType;
    private Integer size;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

    public Attachment(String contentId, String fileName, String contentType, Integer size) {
        this.contentId = contentId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Attachment that = (Attachment) o;
        return contentId != null && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
