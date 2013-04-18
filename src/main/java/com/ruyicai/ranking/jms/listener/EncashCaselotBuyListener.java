package com.ruyicai.ranking.jms.listener;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.ranking.domain.PrizeDetail;

@Service
public class EncashCaselotBuyListener {

	private Logger logger = LoggerFactory.getLogger(EncashCaselotBuyListener.class);

	@Value("${ruyicaiUserno}")
	private String ruyicaiUserno;

	@Transactional
	public void encashCaselotBuyCustomer(@Header("caselotid") String caselotid, @Header("money") Long money,
			@Header("caselotbuyid") Long caselotbuyid, @Header("userno") String userno, @Header("type") Integer type) {
		logger.info("合买中奖排行caselotid:{},money:{},caselotbuyid:{},userno:{},type:{}", new String[] { caselotid,
				money + "", caselotbuyid + "", userno, type + "" });
		if (StringUtils.isNotBlank(userno)) {
			if (userno.equals(ruyicaiUserno)) {
				return;
			}
			if (money <= 0) {
				return;
			}
			PrizeDetail.createPrizeDetail(caselotbuyid + "@" + type, userno, "", new BigDecimal(money), new Date());
		}
	}
}
