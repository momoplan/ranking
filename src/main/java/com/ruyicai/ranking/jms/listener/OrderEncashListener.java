package com.ruyicai.ranking.jms.listener;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.ranking.domain.PrizeDetail;
import com.ruyicai.ranking.service.LotteryService;

@Service
public class OrderEncashListener {

	private Logger logger = LoggerFactory.getLogger(OrderEncashListener.class);

	@Value("${ruyicaiUserno}")
	private String ruyicaiUserno;

	@Autowired
	private LotteryService lotteryService;

	@Transactional
	public void orderEncashCustomer(@Body String orderJson) {
		logger.info("中奖排名,orderJson:" + orderJson);
		if (StringUtils.isBlank(orderJson)) {
			return;
		}
		Torder order = Torder.fromJsonToTorder(orderJson);
		if (order == null) {
			return;
		}
		String userno = order.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不计算排名");
			return;
		}
		if (StringUtils.isNotBlank(order.getTlotcaseid())) {
			logger.info("合买中奖，不计算排名");
			return;
		}
		BigDecimal prizeAmt = order.getOrderpreprizeamt();
		if (prizeAmt.compareTo(BigDecimal.ZERO) <= 0) {
			logger.info("奖金小于等于0，不计算排名");
		}
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			return;
		}
		if (!tuserinfo.getSubChannel().equals("00092493")) {
			return;
		}
		String orderUserno = order.getUserno();
		String lotno = order.getLotno();
		Date encashtime = order.getEncashtime() == null ? new Date() : order.getEncashtime();
		if (StringUtils.isNotBlank(orderUserno) && StringUtils.isNotBlank(lotno)
				&& prizeAmt.compareTo(BigDecimal.ZERO) > 0) {
			logger.info("增加中奖记录orderId:{},userno:{},lotno:{},prizeAmt:{}", new String[] { order.getId(), orderUserno,
					lotno, prizeAmt.toString() });
			PrizeDetail.createPrizeDetail(order.getId(), orderUserno, lotno, prizeAmt, encashtime);
		}
	}
}
