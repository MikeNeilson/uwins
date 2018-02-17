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
package lan.capstone.uwins.common;

import java.util.HashMap;

public class pki
{
    public static String userFromDN(String dn)
    {
        String[] parts = dn.split(",");
        HashMap<String, String> dn_map = new HashMap();
        for (String c : parts) {
           String[] p2 = c.split("=");
           dn_map.put(p2[0].trim().toLowerCase(), p2[1].trim());
        }
        String cn = (String)dn_map.get("cn");
        String[] p3 = cn.split("\\.");
        if (cn.matches("^.*\\.[0-9]*$")){
           return p3[(p3.length - 1)];
        }
        if (cn.matches("^.*\\.capstone.lan")) {
           return p3[0];
        }
        return null;
    }
}
