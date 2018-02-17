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
package lan.capstone.uwins.simplecases;

import java.io.PrintStream;
import java.security.KeyStore;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import lan.capstone.uwins.security.auth.activemq.SmartCardConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;

public class MessageSenderTest {

    public static void main(String[] args)
            throws Exception {
        KeyStore ks = KeyStore.getInstance("Windows-MY");
        ks.load(null, null);
        ActiveMQSslConnectionFactory connSourceFactory = new SmartCardConnectionFactory("ssl://dc1.uwins.capstone.lan:61616");
        connSourceFactory.setKeyStore("");
        connSourceFactory.setKeyStoreType("Windows-MY");
        connSourceFactory.setTrustStore("C:\\uwins\\pki\\root.ts");
        connSourceFactory.setTrustStorePassword("system");
        ActiveMQSslConnectionFactory connSinkFactory = new SmartCardConnectionFactory("ssl://dc2.uwins.capstone.lan:61616");
        connSinkFactory.setKeyStore("");
        connSinkFactory.setKeyStoreType("Windows-MY");
        connSinkFactory.setTrustStore("C:\\uwins\\pki\\root.ts");
        connSinkFactory.setTrustStorePassword("system");

        Connection source = connSourceFactory.createConnection();
        Connection sink = connSinkFactory.createConnection();
        try {
            sink.start();
            Session source_session = source.createSession(false, 1);
            Session consumer_session = sink.createSession(false, 1);

            Destination out = source_session.createQueue("test.queue");

            MessageProducer producer = source_session.createProducer(out);
            producer.setDeliveryMode(2);
            TextMessage message = source_session.createTextMessage("This is a test");
            producer.send(message);

        } catch (Exception err) {

            System.out.println(err.getLocalizedMessage());
        }

        sink.stop();
        source.stop();
        sink.close();
        source.close();
    }
}

