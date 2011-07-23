/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.user.service;

import org.apache.wicket.Session;
import org.apache.wicket.request.cycle.RequestCycle;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * Resolves the username within a service. Callback.
 * @author Carsten Hufe
 */
@Component("usernameResolver")
public class UsernameResolverImpl implements UsernameResolver {

    @Override
    public String getUsername() {
        if (RequestCycle.get() != null) {
            PortalSession session = ((PortalSession) Session.get());
            User user = session.getUser();
            return user.getUsername();
        }
        return UserConstants.UNKNOWN_USERNAME;
    }
}
