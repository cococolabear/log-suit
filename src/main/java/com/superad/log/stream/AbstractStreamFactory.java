package com.superad.log.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public abstract class AbstractStreamFactory {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${application.kafka.group}")
	private String group;

	@Value("${application.kafka.zookeeper}")
	private String zookeeper;

	protected AtomicInteger continues = new AtomicInteger(2 * (Runtime.getRuntime().availableProcessors() + 1) + 1);

	private ConsumerConnector consumer;

	public abstract void onReceive(List<String> messages);

	public void start(String topic, int batch) {

		try {

			Properties props = new Properties();

			props.put("zookeeper.connect", zookeeper);
			props.put("group.id", group);
			props.put("auto.commit.enable", "false");
			props.put("auto.offset.reset", "smallest");
			props.put("zookeeper.session.timeout.ms", "100000");
			props.put("rebalance.backoff.ms", "20000");
			props.put("rebalance.max.retries", "10");
			props.put("zookeeper.connection.timeout.ms", "100000");

			ConsumerConfig config = new ConsumerConfig(props);

			consumer = Consumer.createJavaConsumerConnector(config);

			Map<String, Integer> topicMap = new HashMap<String, Integer>();
			topicMap.put(topic, 1);

			Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicMap);

			List<KafkaStream<byte[], byte[]>> kafkaStreams = consumerMap.get(topic);

			for (KafkaStream<byte[], byte[]> kafkaStream : kafkaStreams) {
				ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
				List<String> messages = new ArrayList<String>();
				while (iterator.hasNext()) {
					if (continues.get() > 0) {
						String msg = new String(iterator.next().message());
						messages.add(msg);
						if (messages.size() == batch) {
							onReceive(messages);
							messages.clear();
							continues.decrementAndGet();
						}
					} else {
						Thread.sleep(100);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected void commit() {
		if (consumer != null) {
			consumer.commitOffsets(true);
		}
	}
}
