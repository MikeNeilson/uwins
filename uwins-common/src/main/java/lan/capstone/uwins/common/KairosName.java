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
package lan.capstone.uwins.common;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Mike Neilson
 */
public class KairosName {

    
    String metric;
    Map<String,String> tags;
    
    public KairosName( String metric, Map<String,String> tags){
        this.metric = metric;
        this.tags = tags;
    }
    
    public KairosName( String metric ){
        this.metric = metric;
        this.tags = new HashMap<String,String>();
    }
    
    public void addTag(String tagname, String value ){
        this.tags.put(tagname, value);
    }
    
    public String getTag(String tagname){
        return this.tags.get(tagname);
    }
    
    public String getTagString(){
        StringBuilder builder = new StringBuilder();
        for( String tag: tags.keySet()){
            builder.append(" ").append(tag).append("=").append(tags.get(tag));
        }
        return builder.toString();
    }
    
    public String getMetric(){
        return this.metric;
    }
    
    public String toFormat(String fmt ) throws Exception{
        switch( fmt.toLowerCase() ){
            case "uwins":{
                StringBuilder builder = new StringBuilder();
                builder.append("uwins://");
                builder.append(metric).append(".");
                builder.append(getTag("type")).append(".");
                builder.append(getTag("interval")).append(".");
                builder.append(getTag("duration")).append(".");
                builder.append(getTag("version"));
                return builder.toString();                
            }
            default:{
                throw new Exception(fmt + " is not a valid name type in this system");
            }
        }
    }
    
    public static KairosName fromUri( String name ) throws Exception{
        System.err.println("GOT URL: " + name );        
        return fromUri( new URI(name));
    }
    
    public static KairosName fromUri(URI uri) throws Exception {
        String scheme = uri.getScheme();
        switch( scheme.toLowerCase() ) {
            case "uwins":{
                // uwins name Location,Parameter, Type,Interval,Duration,Version
                String tsname = uri.getHost();
                System.err.println(tsname);
                String parts[] = tsname.split("\\.");
                if( parts.length != 6 ){
                    throw new MalformedURLException(String.format("Invalid name %s. There must be 6 elements seperated by a period (.). You have %d", tsname, parts.length));
                }
                String metric = parts[0] + "." + parts[1];
                KairosName kn = new KairosName(metric);
                kn.addTag("type", parts[2]);
                kn.addTag("interval", parts[3]);
                kn.addTag("duration", parts[4]);
                kn.addTag("version", parts[5]);
                return kn;                                
            } 
            default:{
                throw new Exception("scheme is not valid: " + scheme );
            }
                
        }
    }
    
    
    
}
