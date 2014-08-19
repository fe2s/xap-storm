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


