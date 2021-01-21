package com.psaw.storm.processor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

import static org.apache.storm.kafka.spout.FirstPollOffsetStrategy.EARLIEST;

import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;


public class CreateStormTopology {
	
	
    private static final String KAFKA_LOCAL_BROKER = "localhost:9092";

    public static void main(String[] args) throws Exception {
        new CreateStormTopology().runMain(args);
    }

    protected void runMain(String[] args) throws Exception {
        final String brokerUrl = args.length > 0 ? args[0] : KAFKA_LOCAL_BROKER;
        System.out.println("Running with broker url: " + brokerUrl);

        Config tpConf = getConfig();
        
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("chatAppTopology", tpConf, getTopologyKafkaSpout(getKafkaSpoutConfig(brokerUrl)));
        
        Thread.sleep(10000);
	      
	      //cluster.shutdown();
    }

    protected Config getConfig() {
        Config config = new Config();
        config.setDebug(true);
        return config;
    }

    protected StormTopology getTopologyKafkaSpout(KafkaSpoutConfig<String, String> spoutConfig) {
        final TopologyBuilder tp = new TopologyBuilder();
        tp.setSpout("kafka_spout", new KafkaSpout<>(spoutConfig), 1);
        tp.setBolt("kafka_bolt", new MessageBolt())
            .shuffleGrouping("kafka_spout");
        /*    .shuffleGrouping("kafka_spout", TOPIC_2_STREAM);
        tp.setBolt("kafka_bolt_1", new KafkaSpoutTestBolt()).shuffleGrouping("kafka_spout", TOPIC_2_STREAM);*/
        return tp.createTopology();
    }

    protected KafkaSpoutConfig<String, String> getKafkaSpoutConfig(String bootstrapServers) {
        /*ByTopicRecordTranslator<String, String> trans = new ByTopicRecordTranslator<>(
            (r) -> new Values(r.topic(), r.partition(), r.offset(), r.key(), r.value()),
            new Fields("topic", "partition", "offset", "key", "value"), "chatApp");*/
        /*trans.forTopic(TOPIC_2,
            (r) -> new Values(r.topic(), r.partition(), r.offset(), r.key(), r.value()),
            new Fields("topic", "partition", "offset", "key", "value"), TOPIC_2_STREAM);*/
        return KafkaSpoutConfig.builder(bootstrapServers, new String[]{"chatApp"})
        		/*.setProcessingGuarantee(ProcessingGuarantee.AT_LEAST_ONCE)*/
            .setProp(ConsumerConfig.GROUP_ID_CONFIG, "chatApp")
            /*.setProp(Config.TOPOLOGY_MAX_SPOUT_PENDING, 2048)
            .setProp(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 16384)*/
            .setRetry(getRetryService())
            /*.setRecordTranslator(trans)*/
            .setOffsetCommitPeriodMs(10_000)
            .setFirstPollOffsetStrategy(EARLIEST)
            .setMaxUncommittedOffsets(250)
            .build();
    }

    protected KafkaSpoutRetryService getRetryService() {
        return new KafkaSpoutRetryExponentialBackoff(TimeInterval.microSeconds(500),
            TimeInterval.milliSeconds(2), Integer.MAX_VALUE, TimeInterval.seconds(10));
    }

}
