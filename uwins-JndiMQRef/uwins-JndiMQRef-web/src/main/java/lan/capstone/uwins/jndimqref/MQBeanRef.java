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

import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author Mike Neilson
 */
@MessageDriven( activationConfig =  {        
        @ActivationConfigProperty(propertyName = "destinationLookup" ,
                              propertyValue ="uwinsl:/jms/TEST/QUEUE"),
        @ActivationConfigProperty(propertyName = "useJndi" ,
                              propertyValue ="true")
})
public class MQBeanRef implements MessageListener, MessageDrivenBean{

    //@Resource
    //javax.jms.ConnectionFactory connectionFactory;
    MessageDrivenContext ctx;
    
    public MQBeanRef(){
        super();
    }
    
    @Override
    public void onMessage(Message message) {
        System.err.println("**** Get Message");
        TextMessage msg = (TextMessage)message;
        try {
            System.err.println( msg.getText() );
        } catch (JMSException ex) {
            System.err.println(ex.getLocalizedMessage());
            Logger.getLogger(MQBeanRef.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @PostConstruct
    public void hello(){
        System.err.println("****** The bean is ready");
    }

    @Override
    public void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException {
        System.err.println(" MessageDrivenCONTEXT SET");
        this.ctx = ctx;
    }

    @Override
    public void ejbRemove() throws EJBException {
        
    }

}
