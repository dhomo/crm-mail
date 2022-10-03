/*
 * LinkDeserializer.java
 *
 * Created on 2018-08-18, 8:06
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static dhomo.crmmail.api.resource.LinkSerializer.HREF;

/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2018-08-18.
 */
public class LinkDeserializer extends StdDeserializer<Links> {

    public LinkDeserializer() {
        this(null);
    }

    public LinkDeserializer(Class<Links> t) {
        super(t);
    }

    @Override
    public Links deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final Map<String, Map<String, String>> linksMap =
                p.getCodec().readValue(p, new TypeReference<Map<String, Map<String, String>>>(){});
        return Links.of(linksMap.entrySet().stream()
                .map(e ->  Link.of(e.getValue().get(HREF), e.getKey()))
                .collect(Collectors.toList()));
    }

}
