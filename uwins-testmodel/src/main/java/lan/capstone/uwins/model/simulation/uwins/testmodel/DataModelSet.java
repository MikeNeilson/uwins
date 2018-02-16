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

import java.util.ArrayList;
import javax.ejb.Singleton;
import javax.ejb.Stateful;

/**
 *
 * @author Mike Neilson
 */
@Singleton
public class DataModelSet {
    private ArrayList<DataModel> models;
    private int step_size = 15*60*1000; // default to 1 minute per second
    
    public DataModelSet() throws Exception{
        models = new ArrayList<>();
        
        String config = "markov_matrix: 0.9, 0.1; 0.1, 0.9 : DRY : 0\r\n";
        config+= "month_distribution: 15,5  ; 20, 9  ; 10,2  ; 5,1  ; "
                                    + "0,0.5;  0, 0.5;  0,0.5; 0,0.5;"
                                    + "0,0.5;  0, 0.5; 15,5  ;15,1   : 1000\r\n";
        config+= "start: 2000-01-01T00:00:00UTC\r\n";
        config+= "raininess: .1, .1: 100\r\n";
        config+= "interval: 900\r\n";
        config+= "tsname: uwins://TEST.Precip.Total.15Minute.15Minutes.Simulation\r\n";
        DataModel dm = new DataModel();
        dm.load(config);
        models.add( dm );        
        
    }

    /**
     * @return the models
     */
    public ArrayList<DataModel> getModels() {
        return models;
    }

    /**
     * @param models the models to set
     */
    public void setModels(ArrayList<DataModel> models) {
        this.models = models;
    }

    /**
     * @return the step_size
     */
    public int getStep_size() {
        return step_size;
    }

    /**
     * @param step_size the step_size to set
     */
    public void setStep_size(int step_size) {
        this.step_size = step_size;
    }
    
    
    
}
