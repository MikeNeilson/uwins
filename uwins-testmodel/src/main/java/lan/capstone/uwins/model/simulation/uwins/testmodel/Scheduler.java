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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Destination;

/**
 *
 * @author Mike Neilson
 */
@Singleton
@Lock(LockType.READ)
public class Scheduler {
    @EJB
    DataModelSet dms;
    
    @Resource( lookup="openejb:Resource/jms/ConnectionFactory" )
    javax.jms.ConnectionFactory connectionFactory;
    
    @Resource( lookup = "uwinsl:/jms/data/rc")
    Destination rc_data;
    
    long current_time;

    @PostConstruct
    public void setupmodels(){
        ZonedDateTime zdt = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));                
        current_time = zdt.toInstant().toEpochMilli();
    }
    
    public void reset_to_new_time(long time ){
        
    }
    
    @Schedule( second="*", minute="*", hour="*", persistent = false)
    private void generateData() throws JMSException{
        current_time += dms.getStep_size(); // need to add logic to handle missing steps
        Connection conn = null;
        try{
            System.err.println("Pushing Simulated Data for time " + new Date(current_time));
            conn = connectionFactory.createConnection() ;
            
            conn.start();
            Session sess = conn.createSession(false, 0);
            MessageProducer prod = sess.createProducer(rc_data);            
            TextMessage msg = sess.createTextMessage();
            for( DataModel dm: dms.getModels() ){
                msg.clearBody();
                msg.clearProperties();
                
                double v = dm.getNextValue(current_time);
                //System.err.println("Got " + v + " for "+dm.getTsname());
                if( v != Double.NEGATIVE_INFINITY) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(dm.getTsname()).append(",").append(current_time).append(",").append(v);                    
                    msg.setText(builder.toString());
                    msg.setStringProperty("timeseries_name", dm.getTsname());
                    msg.setStringProperty("content-type", "text/uwins-simple-data-csv");
                    prod.send(msg);
                }
            }
            
            prod.close();
        
            conn.stop();
        }catch( Exception e ){
            System.err.println(e);
            e.printStackTrace(System.err);
            throw e;
        } finally {    
            if( conn != null) conn.close();
        }
    }
    
    
}
