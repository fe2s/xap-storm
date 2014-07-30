package com.gigaspaces.streaming.benchmark;

import com.gigaspaces.streaming.offset.service.PartitionedStreamService;
import com.gigaspaces.streaming.registry.ConsumerRegistryService;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.remoting.ExecutorRemotingProxyConfigurer;

/**
 * @author Oleksiy_Dyagilev
 */
public class TestRegistry {

//    public static void main(String[] args) {
//        String spaceUrl = "jini://*/*/space?locators=127.0.0.1";
//        String streamId = "myStream";
//
//        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(spaceUrl);
//        GigaSpace gigaSpace = new GigaSpaceConfigurer(urlSpaceConfigurer.space()).gigaSpace();
//
//        ConsumerRegistryService service = new ExecutorRemotingProxyConfigurer<>(gigaSpace, ConsumerRegistryService.class).proxy();
//
//        for (int i = 0; i < 10; i++) {
//            int routing = service.registerConsumer(streamId);
//            System.out.println(routing);
//        }
//    }
}
