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
package org.apache.dubbo.demo.provider;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.demo.DemoService;

import java.util.concurrent.CountDownLatch;

public class Application {
    public static void main(String[] args) throws Exception {
        if (isClassic(args)) {
            startWithExport();
        } else {
            startWithBootstrap();
        }
    }

    private static final String ZK_URL = "zookeeper://192.168.0.13:2181";

    private static boolean isClassic(String[] args) {
        return args.length > 0 && "classic".equalsIgnoreCase(args[0]);
    }

    private static void startWithBootstrap() {
        ServiceConfig<DemoServiceImpl> service = new ServiceConfig<>();
        service.setInterface(DemoService.class);
        service.setRef(new DemoServiceImpl());

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(new ApplicationConfig("dubbo-demo-api-provider"))
            .registry(new RegistryConfig(ZK_URL))
            .service(service)
            .start()
            .await();
    }

    private static void startWithExport() throws InterruptedException {
        // 服务提供者
        ServiceConfig<DemoServiceImpl> service = new ServiceConfig<>();
        // 要对外提供的接口
        service.setInterface(DemoService.class);
        // 接口实现类
        service.setRef(new DemoServiceImpl());
        // 服务提供者名称
        service.setApplication(new ApplicationConfig("dubbo-demo-api-provider"));
        // zk注册中心地址
        service.setRegistry(new RegistryConfig(ZK_URL));
        // 上报元数据地址
        service.setMetadataReportConfig(new MetadataReportConfig(ZK_URL));
        // 对外提供服务
        service.export();

        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }
}
