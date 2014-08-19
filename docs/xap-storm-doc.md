# Introduction #

Real-time processing is becoming very popular, and Storm is a popular open source framework and runtime used by Twitter for processing real-time data streams.  Storm addresses the complexity of running real time streams through a compute cluster by providing an elegant set of abstractions that make it easier to reason about your problem domain by letting you focus on data flows rather than on implementation details.  

Storm has many use cases: realtime analytics, online machine learning, continuous computation, distributed RPC, ETL, and more. Storm is fast: a benchmark clocked it at over a million tuples processed per second per node. It is scalable, fault-tolerant, guarantees your data will be processed, and is easy to set up and operate.

This pattern integrates XAP with Storm. XAP is used as stream data source and fast reliable persistent storage, whereas Storm is in charge of data processing. We support both pure Storm and Trident framework.

As part of this integration we provide classic **Word Counter** and **Twitter Reach** implementations on top of XAP and Trident.

Also, we demonstrate how to build highly available, scalable equivalent of **Realtime Google Analytics** application with XAP and Storm. Application can be deployed to cloud with one click using Cloudify. 

# Storm in a Nutshell #

Storm is a real time, open source data streaming framework that functions entirely in memory.  It constructs a processing graph that feeds data from an input source through processing nodes.  The processing graph is called a "topology".  The input data sources are called "spouts", and the processing nodes are called "bolts".  The data model consists of tuples.  Tuples flow from Spouts to the bolts, which execute user code. Besides simply being locations where data is transformed or accumulated, bolts can also join streams and branch streams. 

Storm is designed to be run on several machines to provide parallelism.  Storm topologies are deployed in a manner somewhat similar to a webapp or a XAP processing unit; a jar file is presented to a deployer which distributes it around the cluster where it is loaded and executed.  A topology runs until it is killed.

![alt tag](https://github.com/fe2s/xap-storm/blob/master/docs/images/storm-nutshell.png)

Beside Storm, there is a **Trident** – a high-level abstraction for doing realtime computing on top of Storm. Trident adds primitives like groupBy, filter, merge, aggregation to simplify common computation routines. Trident has consistent, exactly-once semantics, so it is easy to reason about Trident topologies. 

Capability to guarantee exactly-once semantics comes with additional cost. To guarantee that, incremental processing should be done on top of persistence data source. Trident has to ensure that all updates are idempotent. Usually that leads to lower throughput and higher latency than similar topology with pure Storm. 

# Spouts #

Basically, Spouts provide the source of tuples for Storm processing.  For spouts to be maximally performant and reliable, they need to provide tuples in batches, and be able to replay failed batches when necessary.  Of course, in order to have batches, you need storage, and to be able to replay batches, you need reliable storage.  XAP is about the highest performing, reliable source of data out there, so a spout that serves tuples from XAP is a natural combination.

[IMAGE HERE]

Depending on domain model and level of guarantees you want to provide, you choose either pure Storm or Trident. We provide Spout implementations for both – XAPSimpleSpout and XAPTranscationalTridentSpout respectively.

## Storm Spout ##

XAPSimpleSpout is a spout implementation for pure Storm that reads data in batches from XAP. On XAP side we introduce conception of stream. Please find SimpleStream – a stream implementation that supports writing data in single and batch modes and reading in batch mode. SimpleStream leverages XAP’s FIFO(First In, First Out) capabilities. 

[IMAGE HERE]

SimpleStream works with arbitrary space class that has FifoSupport.OPERATION annotation and implements Serializable. 

Here is an example how one may write data to SimpleStream and process it in Storm topology. Let’s consider we would like to build an application to analyze the stream of page views (user clicks) on website. At first, we create a data model that represents a page view 

```java
@SpaceClass(fifoSupport = FifoSupport.OPERATION)
public class PageView implements Serializable {
    private String id;
    private String page;
   private String sessionId;
   [getters setters omitted for brevity]
}
```

Now we would like to create a reference to stream instance and write some data. 

```java
SimpleStream<PageView> stream = new SimpleStream<>(space, new PageView());
stream.writeBatch(pageViews);
```

The second argument of SimpleStream is a template used to match objects during reading. 
If you want to have several streams with the same type, template objects should differentiate your streams.

Now let’s create a spout for PageView stream. 

```java
public class PageViewSpout extends XAPSimpleSpout<PageView> {
    public PageViewSpout() {
        super(new PageViewTupleConverter(), new PageView());
    }
}
```

To create a spout, we have to specify how we want our space class be converted to Storm tuple. That is exactly what TupleConverter knows about.

```java
class PageViewTupleConverter implements TupleConverter<PageView> {
    @Override
    public Fields tupleFields() {
        return new Fields("page", "session");
    }

    @Override
    public List<Object> spaceObjectToTuple(PageView pageView) {
        return Arrays.<Object>asList(pageView.getPage(), pageView.getSessionId());
    }
}  
```

At this point we have everything ready to build Storm topology with PageViewSpout. 

```java
Config conf = new Config();
conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://*/*/space");
conf.put(ConfigConstants. XAP_STREAM_BATCH_SIZE, 300);
TopologyBuilder builder = new TopologyBuilder();
builder.setSpout(“pageViewSpout”, new PageViewSpout());
```

ConfigConstants.XAP_SPACE_URL_KEY is a space URL

ConfigConstants. XAP_STREAM_BATCH_SIZE is a maximum number of items that spout reads from XAP with one hit.







