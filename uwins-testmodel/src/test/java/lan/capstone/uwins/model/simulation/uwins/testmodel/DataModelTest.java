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
import java.util.ArrayList;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mike Neilson
 */
public class DataModelTest {
    
    String config;
    
    public DataModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        config = "markov_matrix: 0.9, 0.1; 0.1, 0.9 : DRY : 0\r\n";
        config+= "month_distribution: 15,5  ; 20, 9  ; 10,2  ; 5,1  ; "
                                    + "0,0.5;  0, 0.5;  0,0.5; 0,0.5;"
                                    + "0,0.5;  0, 0.5; 15,5  ;15,1   : 1000\r\n";
        config+= "start: 2000-01-01T00:00:00UTC\r\n";
        config+= "raininess: .5, .25: 100\r\n";
        config+= "interval: 900\r\n";
        config+= "tsname: uwins://TEST.Precip.Total.15Minute.15Minutes.Simulation\r\n";
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getNextValue method, of class DataModel.
     */
    @Test
    public void testGetNextValue() throws Exception {
        System.out.println("getNextValue");
        ZonedDateTime zdt = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));                        
        long current_step = zdt.toInstant().toEpochMilli();
        DataModel instance = new DataModel();
        instance.load(config);
        double expResult = 0.0;        
        ArrayList<Double> results = new ArrayList<>();
        for( int i = 0; i < 96*60; i++ ){
            double f = instance.getNextValue(current_step);
            Date d = new Date(current_step);
            if( f != Double.NEGATIVE_INFINITY){
                System.out.println(String.format("%s -> %f", d,f));
                
            }
            current_step += 15*60*1000; // one minute per cycles
        //    results.add(  instance.getNextValue( current_step ) );
        }
        /*for( Double f: results){
        System.out.println("result = " + f );
        }*/
        //assertEquals(expResult, result, 0.01);
        // TODO review the generated test code and remove the default call to fail.
        
    }
    
    @Test
    public void testLoad() throws Exception{
        
        
         
        DataModel dm = new DataModel();
        dm.load(config );
        assertTrue("Constrution of Model Succeded", true);
                
                
    }
    
}
