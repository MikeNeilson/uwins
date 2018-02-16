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

/**
 *
 * @author Mike Neilson
 */
public class MarkovChain {

    double mat[][] = null;
    int state = 0;
    
    public MarkovChain(double[][] mat, int state) {
        this.mat = mat;
        this.state = state;
    }

    

    int chooseState(double roll) {
        double row[] = mat[state];
        
        for( int i = 0; i < mat.length; i++){
            roll -= row[i];
            if( roll <= 0.0 ){
                state = i;
            }
        }
            
        return this.state;
        
    }
    
}
