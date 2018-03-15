/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.demo.consumer;

import com.alibaba.dubbo.demo.PerformanceDemoService;
import com.alibaba.dubbo.demo.VeryComplexDTO;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class Consumer {

    @State(Scope.Benchmark)
    public static class InstanceHolder {
        public PerformanceDemoService demoService;

        public InstanceHolder() {
            //Prevent to get IPV6 address,this way only work in debug mode
            //But you can pass use -Djava.net.preferIPv4Stack=true,then it work well whether in debug mode or not
            System.setProperty("java.net.preferIPv4Stack", "true");
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"META-INF/spring/dubbo-demo-consumer.xml"});
            context.start();
            demoService = (PerformanceDemoService) context.getBean("demoService"); // get remote service proxy
            System.out.println(demoService);
        }

    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include("Consumer")
                .warmupIterations(10)
                .measurementIterations(10)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Threads(100)
    @Timeout(time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public int add(InstanceHolder instanceHolder) {
       return instanceHolder.demoService.add(1, 2);
    }
//
//    String testSmallString(String smallStr) {
//        return demoService.testSmallString(smallStr);
//    }
//
//    String testBigString(String bigStr) {
//        return demoService.testBigString(bigStr);
//    }
//
//    VeryComplexDTO testComplexPOJO(VeryComplexDTO dto) {
//        return demoService.testComplexPOJO(dto) ;
//    }
}
