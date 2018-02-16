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
package lan.capstone.uwins.jndimqref;

import com.oracle.jrockit.jfr.Producer;
import java.util.Enumeration;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;


/**
 *
 * @author Mike Neilson
 */
@Singleton
@Lock(LockType.READ)
public class SchedBeanRef {
    
    @Resource( lookup="openejb:Resource/jms/ConnectionFactory")
    javax.jms.ConnectionFactory connectionFactory;
    
    @Resource( lookup = "uwinsl:/jms/TEST/QUEUE" )
    javax.jms.Destination queue;
   
    @Schedule(second="*/30", minute="*", hour="*", persistent = false)
    public void run() throws JMSException, Exception{
        System.err.println("Okay, something actually works " + connectionFactory);   
        Connection conn = connectionFactory.createConnection();        
        conn.start();
        Session sess = conn.createSession(false, 0);
        MessageProducer prod = sess.createProducer(queue);
        TextMessage msg = new ActiveMQTextMessage();
        msg.setText("Message from scheduler test");
        prod.send(msg);
        System.err.println("message sent");
        conn.stop();
        conn.close();
    }
    
    @Schedule( second="*/15", minute="*", hour="*", persistent=false)
    public void read() throws Exception{
        return;
        /*
        System.err.println("trying to read message");
        Connection conn = connectionFactory.createConnection();
        conn.start();
        Session sess = conn.createSession(false, 0);
        MessageConsumer reader = sess.createConsumer(queue);
        System.err.println("Consumer created");
        TextMessage msg = (TextMessage) reader.receive(500 );
        if( msg != null ){
            System.err.println(msg.getText());
        } else {
            System.err.println("No messages received");
        }
        conn.stop();
        */
    }

}
