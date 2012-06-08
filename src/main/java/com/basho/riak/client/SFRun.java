/*
 * This file is provided to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.basho.riak.client;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.basho.riak.client.raw.http.HTTPClientConfig;
import com.basho.riak.client.raw.pbc.PBClientConfig;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import com.yammer.metrics.reporting.ConsoleReporter;
import com.yammer.metrics.reporting.CsvReporter;

/**
 * @author russell
 *
 */
public class SFRun {
    
    private static final String DEFAULT_URL = "http://127.0.0.1:8098/riak";
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PB_PORT = 8091;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        CsvReporter.enable(new File("metrics"), 1, TimeUnit.SECONDS);
        
        final StringBuilder report = new StringBuilder();
        
        final Random random = new Random(UUID.randomUUID().getLeastSignificantBits() + new Date().getTime());
        final int size = 1024;
        final int runtimeSeconds = 60;
               
        SFRunner[] runners = {};
        try {
            runners = createRunners();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for(SFRunner runner : runners) {
            Timer t = Metrics.newTimer(runner.getClass(), "PUT", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
            long startTime = System.nanoTime();
            long stopTime = startTime + TimeUnit.NANOSECONDS.convert(runtimeSeconds, TimeUnit.SECONDS);

            int successes = 0;
            int failures = 0;
            
            boolean run = true;
            while (run) {
                TimerContext tc = t.time();
                try {
                    runner.execute("b", UUID.randomUUID().toString(), data(random, size), "foo");
                    successes++;
                } catch (IOException e) {
                    failures++;
                } finally {
                    tc.stop();
                }

                if(System.nanoTime() > stopTime) {
                    run=false;
                }
            }
            
            report.append(runner.getClass().getName()).append(": ")
            .append("Successes - ").append(successes)
            .append(" Failures - ").append(failures)
            .append(" ops/sec - ").append((successes + failures) / runtimeSeconds).append("\n");
    
        }
        
        System.out.println(report.toString());
        System.exit(0);
        
    }
    
    private static byte[] data(Random random, int size) {
        byte[] data = new byte[size];
        random.nextBytes(data);
        return data;    
    }
    
    private static SFRunner[] createRunners() throws IOException {
        HTTPClientConfig http = new HTTPClientConfig.Builder().withMaxConnctions(50).build();
        return new SFRunner[] {new HTTPSFRunner(DEFAULT_URL)};// RawClientSFRunner(http)};//, new HTTPSFRunner(DEFAULT_URL), new OldClientRunner(DEFAULT_URL)};
    }

}
