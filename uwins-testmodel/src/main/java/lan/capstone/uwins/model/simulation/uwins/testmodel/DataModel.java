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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.random.*;
import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

/**
 *
 * @author Mike Neilson
 */
public class DataModel {
    public static final int DRY=0;
    public static final int RAIN=1;
    
    private static final SimpleDateFormat ISO_FMT  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    
    private class MeanStd{
        public double mean;
        public double stdev;
    }
    
    
    private int interval;
    private String tsname;
    private MarkovChain chain;
    private double month_total = 0.0;
    //private MeanStd month_stats[];
    private NormalDistribution distribution[];
    private RandomGenerator markov_rg;
    private NormalDistribution rain_volume;
    
    private long date;
    
    public DataModel(){        
        
    }
    
    public void load( String config ) throws Exception{
        distribution = new NormalDistribution[12];
        
        
        for( String line: config.split("\\r?\\n|\\r")){
            try{
                if( line.trim().isEmpty() ) continue;            
                String parts[] = line.trim().split(":");
                String key = parts[0].trim().toLowerCase();
                String arg = parts[1].trim().toLowerCase();
                double mat[][] = null;
                int state;
                switch( key ){
                    case "markov_matrix":{
                        String rows[] = arg.split(";");
                        String st = parts[2].trim();
                        long seed = Long.parseLong(parts[3].trim());
                        int l = rows.length;
                        mat = new double[l][l];
                        for( int i=0; i < l; i++ ){
                            String columns[] = rows[i].split(",");
                            for( int j=0; j < l; j++ ){
                                mat[i][j] = Double.parseDouble(columns[j].trim());
                            }
                        }
                        if( "dry".equalsIgnoreCase(st)){
                            state = DRY;
                        } else if("rain".equalsIgnoreCase(st)) {
                            state = RAIN;
                        } else {
                            throw new Exception("markov state must be DRY or RAIN. state was " + st);
                        }                        
                        chain = new MarkovChain(mat,state);
                        markov_rg = new MersenneTwister(seed);
                        break;
                    }                
                    case "month_distribution":{                    
                        String sets[] = arg.split(";");
                        long seed = Long.parseLong(parts[2].trim());
                        RandomGenerator rg = new MersenneTwister(seed);                    
                        if( sets.length != 12 ) throw new Exception("you specify exatly 12 mean and standard deviations, one for each month, in order starting in January");
                        for( int i = 0; i < 12; i++){
                            String set = sets[i];
                            String pair[] = set.split(",");                        
                            double mean = Double.parseDouble(pair[0].trim());
                            double stdev = Double.parseDouble(pair[1].trim());
                            distribution[i] = new NormalDistribution(rg,mean,stdev);
                        }
                        break;
                    }
                    case "start":{
                        String date = parts[1] + ":" + parts[2] + ":" + parts[3];
                        this.date = ISO_FMT.parse(date).getTime();
                        break;
                    }
                    case "interval":{
                        this.interval = Integer.parseInt(arg.trim())*1000;
                        break;
                    }
                    case "tsname":{
                        if( parts.length == 2 ){
                            this.setTsname("uwins://"+arg);
                        } else {
                            int idx = line.indexOf(":");
                            this.setTsname(line.substring(idx+1).trim());
                        }

                        break;
                    }
                    case "raininess":{
                        String mean_stdev[] = arg.split(",");
                        long seed = Long.parseLong(parts[2].trim());

                        double mean = Double.parseDouble(mean_stdev[0].trim());
                        double stdev = Double.parseDouble(mean_stdev[1].trim());
                        this.rain_volume = new NormalDistribution( new MersenneTwister(seed),mean,stdev);
                    }                                
                }
            }
            catch(Exception e ){
                throw new Exception("Configuration error on line: " + line + " " + e.toString(), e);
            }
        }
    }
    
    public double getNextValue(long current_step ){
        if( current_step < date){
            return Double.NEGATIVE_INFINITY;
        }
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(current_step), ZoneId.of("UTC"));
        if( zdt.getDayOfMonth() == 1 && zdt.getHour()==0 & zdt.getMinute()==0 ){
            month_total = distribution[zdt.getMonthValue()-1].sample();
        }
        
        double amount = 0.01;
        if( chain.chooseState(markov_rg.nextDouble() ) == DRY){
            amount = 0;
        } else {
            if( month_total <=0 ){
                amount = 0.01;
            } else {
                amount = Math.max(0, rain_volume.sample() );
                month_total -= amount;                
            }
        }
        date+=interval;
        return amount;
    }

    /**
     * @return the tsname
     */
    public String getTsname() {
        return tsname;
    }

    /**
     * @param tsname the tsname to set
     */
    public void setTsname(String tsname) {
        this.tsname = tsname;
    }
    
    
}
