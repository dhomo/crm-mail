/*
 * IsotopeResource.java
 *
 * Created on 2018-08-11, 8:31
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
package dhomo.crmmail.api.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;


/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2018-08-11.
 */
@JsonIgnoreProperties(value = {"links"}, ignoreUnknown = true)
public class IsotopeResource extends RepresentationModel<IsotopeResource> {

    @SuppressWarnings("EmptyMethod")
    @JsonProperty("_links")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSerialize(using = LinkSerializer.class)
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

    @JsonProperty("_links")
    @JsonDeserialize(using = LinkDeserializer.class)
    public void setLinks(Links links) {
        add(links);
    }
}
