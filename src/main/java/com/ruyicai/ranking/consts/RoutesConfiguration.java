package com.ruyicai.ranking.consts;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoutesConfiguration {

	private Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Autowired
	private CamelContext camelContext;

	@PostConstruct
	public void init() throws Exception {
		logger.info("init camel routes");
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1).redeliveryDelay(3000);
				from("jms:queue:VirtualTopicConsumers.ranking.orderPirzeend?concurrentConsumers=20").to(
						"bean:orderEncashListener?method=orderEncashCustomer").routeId("中奖排名");
				from("jms:queue:VirtualTopicConsumers.ranking.encashCaselotBuy?concurrentConsumers=10").to(
						"bean:encashCaselotBuyListener?method=encashCaselotBuyCustomer").routeId("合买中奖排名");
			}
		});
	}
}
