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
package lan.capstone.uwins.common.naming;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.activemq.command.ActiveMQDestination;

public class UWINSNamingContext implements Context {

    Hashtable env = null;
    Connection conn;
    PreparedStatement lookupName;

    public UWINSNamingContext(Hashtable env) {
        this.env = env;
        try {
            System.err.println("We have made the context");

            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/uwins?ssl=false&user=tomee");
            this.lookupName = conn.prepareStatement("select get_destination_ref as jndi_ref from get_destination_ref(?)");
        } catch (Exception ex) {
            System.err.println("failed to connect to uwins database " + ex.getLocalizedMessage());
        }
    }

    public UWINSNamingContext() {
    }

    public Object lookup(Name name) throws NamingException {
        return lookup(name.toString());
    }

    public Object lookup(String name) throws NamingException {
        System.err.println("Checking: " + name);
        try {
            if ((!name.startsWith("uwinsl:")) && (!name.startsWith("uwins:"))) {
                throw new NamingException("Not a uwins name");
            }

            int idx = name.indexOf("jms");
            if (idx > 0) {
                String text = name.substring(idx + 3);
                System.err.println("Getting physical name for " + text);
                this.lookupName.setString(1, text);
                ResultSet rs = this.lookupName.executeQuery();
                if (rs.next()) {
                    System.err.println("we have a thing");
                    String ref = rs.getString("jndi_ref");
                    if ((ref == null) || (ref.trim().equalsIgnoreCase(""))) {
                        throw new NamingException("The destination exists, but has no information defined");
                    }
                    String[] p = ref.split(":");

                    System.err.println("Found physical name " + p[0]);
                    return ActiveMQDestination.createDestination(p[0], Byte.parseByte(p[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new NamingException("Can't find name with in the uwins system " + e.getLocalizedMessage());
        }

        return null;
    }

    public void bind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void bind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void rebind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException("Not supportedt.");
    }

    public void rebind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void unbind(Name name) throws NamingException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void unbind(String name) throws NamingException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void rename(Name oldName, Name newName) throws NamingException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void rename(String oldName, String newName) throws NamingException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroySubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroySubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Context createSubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Context createSubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object lookupLink(Name name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object lookupLink(String name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NameParser getNameParser(Name name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NameParser getNameParser(String name) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String composeName(String name, String prefix) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Hashtable<?, ?> getEnvironment() throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void close() throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNameInNamespace() throws NamingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
