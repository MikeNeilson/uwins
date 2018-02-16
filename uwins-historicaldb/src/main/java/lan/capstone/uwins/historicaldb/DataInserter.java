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
package lan.capstone.uwins.historicaldb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import lan.capstone.uwins.common.KairosName;


/**
 *
 * @author Mike Neilson
 */
@MessageDriven( activationConfig =  {        
        @ActivationConfigProperty(propertyName = "destinationLookup" ,
                              propertyValue ="uwinsl:/jms/data/rc"),
        @ActivationConfigProperty(propertyName = "useJndi",
                              propertyValue ="true")
})
public class DataInserter implements MessageListener{

    private Socket kairos = null;
    
    @Resource
    javax.jms.ConnectionFactory connectionFactory;
    
    
    @Resource( lookup = "uwinsl:/jms/computations/rc")
    javax.jms.Destination computations;
    
    /**
     * DataInserter.onMessage.
     * Handles pushing data to the database.
     * Currently only of the format text/uwins-simple-data-csv
     * which is time series name in URL format, time, value
     * can put multiple lines in message
     * TODO: add units processing
     * TODO: handle not bailing on a single line;
     * 
     * @param message Message containing data
     */
    @Override
    public void onMessage(Message message) {
        System.err.println("Inserting data");
        Connection conn = null;
        
        try {
            conn = connectionFactory.createConnection();
            Session sess = conn.createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
            MessageProducer comps = sess.createProducer(computations);
            if( kairos == null ) {            
                kairos = new Socket( "localhost", 4242 );            
            }
            
            PrintStream out = new PrintStream(kairos.getOutputStream());
            StringBuilder builder = new StringBuilder();
            TextMessage tm = (TextMessage)message;
            if( tm.getStringProperty("content-type").equalsIgnoreCase("text/uwins-simple-data-csv")){
                String lines [] = tm.getText().trim().split("\\r?\\n|\\r");
                for( String line: lines ){
                    System.err.println("Processing: " + line );
                    String parts[] = line.trim().split(",");
                    KairosName kn = KairosName.fromUri(parts[0]);
                    out.print("put ");
                    out.print(kn.getMetric() + " ");
                    out.print(parts[1] + " ");
                    out.print(parts[2]);
                    out.print("\n");
                    
                    // check for computations and insert, for not it all goes
                    TextMessage tm2 = sess.createTextMessage();
                    tm2.setStringProperty("timeseries_name", parts[0]);
                    tm2.setStringProperty("content-type", "text/uwins-simple-data-csv");
                    tm2.setText(line);
                    comps.send(tm2);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(DataInserter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(DataInserter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DataInserter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if( conn != null ) try {
                conn.close();
            } catch (JMSException ex) {
                Logger.getLogger(DataInserter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @PreDestroy
    void kill_network() throws IOException{
        if( this.kairos != null ){
            this.kairos.close();
        }
    }
}
