package com.gigaspaces.storm.trident.sqlquery;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.trident.state.readonly.XAPReadOnlyStateFactory;
import com.j_spaces.core.client.SQLQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.MapGet;
import storm.trident.tuple.TridentTuple;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Oleksiy_Dyagilev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SqlQueryReadOnlyStateTest {

    @Autowired
    private GigaSpace space;

    @Before
    public void setUp() throws Exception {
        List<Person> people = Arrays.asList(
                new Person("joe", 23, randomPhoto()),
                new Person("bob", 55, randomPhoto()),
                new Person("sally", 41, randomPhoto()),
                new Person("mike", 52, randomPhoto())
        );
        space.writeMultiple(people.toArray());

    }

    private StormTopology buildTopology(LocalDRPC drpc) {
        TridentTopology topology = new TridentTopology();

        SQLQuery<Person> sqlQuery = new SQLQuery<>(Person.class, "name = ? AND age > 30").setProjections("age");
        TridentState peopleOlderThan30ByName = topology.newStaticState(XAPReadOnlyStateFactory.bySqlQuery(sqlQuery));

        topology.newDRPCStream("age", drpc)
                .stateQuery(peopleOlderThan30ByName, new Fields("args"), new MapGet(), new Fields("person"))
                .each(new Fields("person"), new ExtractAge(), new Fields("age"))
                .project(new Fields("age"));

        return topology.build();
    }

    @Test
    public void testTopology() throws Exception {
        LocalDRPC drpc = new LocalDRPC();

        Config conf = new Config();
        conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://*/*/space?groups=sqlquery-test");

        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("test_sqlquery", conf, buildTopology(drpc));

        Thread.sleep(2000);

        assertEquals("[[55]]", drpc.execute("age", "bob"));

        cluster.shutdown();
        drpc.shutdown();
    }

    private byte[] randomPhoto() {
        return BigInteger.valueOf((long) (Math.random() * 100000)).toByteArray();
    }

    private static class ExtractAge extends BaseFunction {
        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            Person person = (Person) tuple.get(0);
            if (person != null) {
                collector.emit(new Values(person.getAge()));
            }
        }
    }
}
