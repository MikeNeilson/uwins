/*
 * Copyright 2018 Mike Neilson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lan.capstone.uwins.common.naming.uwinsl;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import lan.capstone.uwins.common.naming.UWINSNamingContext;

public class uwinslURLContextFactory
        implements ObjectFactory {

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
            throws Exception {
        if (obj == null) {
            return new UWINSNamingContext(environment);
        }

        if ((obj instanceof String)) {
            UWINSNamingContext context = new UWINSNamingContext(environment);
            return context.lookup((String) obj);
        }

        if ((obj instanceof String[])) {
            UWINSNamingContext context = new UWINSNamingContext(environment);
            for (String s : (String[]) obj) {
                try {
                    return context.lookup(s);
                } catch (NamingException localNamingException) {
                }
            }
            throw new NamingException("failed to lookup any of the provided names ");
        }
        return null;
    }
}
