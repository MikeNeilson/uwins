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
package lan.capstone.uwins.model.simulation.uwins.testmodel;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.ejb.Stateless;
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
                              propertyValue ="uwinsl:/jms/computations/rc"),
        @ActivationConfigProperty(propertyName = "useJndi",
                              propertyValue ="true")
})
public class RCTestRainSummer implements MessageListener, MessageDrivenBean{

    MessageDrivenContext ctx;  
    
    @PostConstruct
    public void hello(){
        System.err.println("Computation Bean ready");
    }
    
    
    public RCTestRainSummer(){
        super();
        System.err.println("Computation Bean constructing");
    }
    
    @Override
    public void onMessage(Message message) {
        TextMessage tm = (TextMessage)message;
        try {
            System.err.println(tm.getText());
        } catch (JMSException ex) {
            System.err.println(ex);
            ex.printStackTrace(System.err);
            Logger.getLogger(RCTestRainSummer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException {
        this.ctx = ctx;
    }

    @Override
    public void ejbRemove() throws EJBException {
        
    }
    
}
