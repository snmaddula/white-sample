package com.example.demo.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import com.example.demo.dao.ProductSqlProcessor;
import com.example.demo.dto.Product;
import com.example.demo.edl.ProductEdlProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitConsumer implements ChannelAwareMessageListener {

	private ExecutorService executor = Executors.newFixedThreadPool(10);
	
	private final ObjectMapper mapper;
	private final RabbitProducer rabbitProducer;
	private final ProductSqlProcessor productSqlProcessor;
	private final ProductEdlProcessor productEdlProcessor;
	
	@Override
	public void onMessage(Message message, final Channel channel) throws Exception {
		try {
			String payload = new String(message.getBody());
			Product product = mapper.readValue(payload, Product.class);
			long tag = message.getMessageProperties().getDeliveryTag();
			log.info(">>>>>>>>>>>> Recieved Message : {}", product);
			executor.submit(() -> {
				boolean sqlSuccess = productSqlProcessor.process(product);
				boolean edlSuccess = productEdlProcessor.process(product);
				log.info("PRODUCT_ID = {}, TAG={}, EDL_SUCCESS = {}, SQL_SUCCESS = {}", product.getId(), tag, edlSuccess, sqlSuccess);
				if(sqlSuccess && edlSuccess) {
					ackMsg(channel, tag, product.getId());
				}else {
					ackMsg(channel, tag, product.getId());
					rabbitProducer.send(payload);
				}
			});
		} catch (Throwable t) {
			log.error(t.getMessage());
		}
	}
	
	private void ackMsg(Channel channel, long tag, long id) {
		try {
			channel.basicAck(tag, false);
			log.info("Message tag={}, id={} acknowledged", tag, id);
		}catch(Exception ex) {
			log.error("Exception while acknowledging the messaage", ex);
		}
	}

	/*
	private boolean processed(Message msg) {
		String payload = new String(msg.getBody());
		if (payload.contains("_ack_")) {
			return true;
		} else if (payload.contains("_err_")) {
			throw new RuntimeException("Simply Error");
		}
		return false;
	}
	*/
}
