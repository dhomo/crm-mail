/*
 * ConfigurationDto.java
 *
 * Created on 2019-04-06, 19:09
 *
 * Copyright 2019 Marc Nuri
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
package dhomo.crmmail.api.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import dhomo.crmmail.api.resource.IsotopeResource;

/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2019-04-06.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class ConfigurationDto extends IsotopeResource {

}
