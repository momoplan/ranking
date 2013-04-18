package com.ruyicai.ranking.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.CaseLotBuy;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.ranking.domain.PrizeDetail;

@Service
public class RankingService {

	private Logger logger = LoggerFactory.getLogger(RankingService.class);

	@Value("${ruyicaiUserno}")
	private String ruyicaiUserno;

	@Autowired
	private LotteryService lotteryService;

	public void computeHistoryData(String startTime, String endTime) {
		long time1 = System.currentTimeMillis();
		logger.info("开始计算历史中奖排名");
		Integer perSize = 500;
		Integer index = 0;
		List<Torder> torders = Torder.findTorders(index, perSize, startTime, endTime);
		while (null != torders && !torders.isEmpty()) {
			logger.info("index:" + index);
			for (Torder order : torders) {
				if (order == null) {
					continue;
				}
				String userno = order.getUserno();
				if (userno.equals(ruyicaiUserno)) {
					logger.info("如意彩账户购买,不计算排名");
					continue;
				}
				if (StringUtils.isNotBlank(order.getTlotcaseid())) {
					logger.info("合买中奖，不计算排名");
					continue;
				}
				Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
				if (tuserinfo == null) {
					continue;
				}
				if (!tuserinfo.getSubChannel().equals("00092493")) {
					continue;
				}
				String orderUserno = order.getUserno();
				String lotno = order.getLotno();
				BigDecimal prizeAmt = order.getOrderpreprizeamt();
				Date encashtime = order.getEncashtime() == null ? order.getCreatetime() : order.getEncashtime();
				if (StringUtils.isNotBlank(orderUserno) && StringUtils.isNotBlank(lotno)
						&& prizeAmt.compareTo(BigDecimal.ZERO) > 0) {
					logger.info("增加中奖记录orderId:{},userno:{},lotno:{},prizeAmt:{}", new String[] { order.getId(),
							orderUserno, lotno, prizeAmt.toString() });
					try {
						PrizeDetail.createPrizeDetail(order.getId(), orderUserno, lotno, prizeAmt, encashtime);
					} catch (Exception e) {
						logger.error("计算历史中奖异常orderId" + order.getId() + ",userno:" + orderUserno + ",lotno" + lotno
								+ ",prizeAmt" + prizeAmt, e);
					}
				}
			}
			index += perSize;
			torders = Torder.findTorders(index, perSize, startTime, endTime);

		}
		logger.info("结束计算历史中奖排名" + ((System.currentTimeMillis() - time1) / 1000) + "秒");
	}

	public void computeCaseLotHistoryData(String startTime, String endTime) {
		long time1 = System.currentTimeMillis();
		logger.info("开始计算合买历史中奖排名");
		Integer perSize = 500;
		Integer index = 0;
		List<CaseLot> caseLots = CaseLot.findCaseLots(index, perSize, startTime, endTime);
		while (null != caseLots && !caseLots.isEmpty()) {
			logger.info("index:" + index);
			for (CaseLot caseLot : caseLots) {
				if (caseLot == null) {
					continue;
				}
				logger.info("中奖合买caselotId:" + caseLot.getId());
				List<CaseLotBuy> caseLotBuys = CaseLotBuy.findCaseLotBuys(caseLot.getId());
				for (CaseLotBuy caseLotBuy : caseLotBuys) {
					if (caseLotBuy == null) {
						continue;
					}
					if (caseLotBuy.getPrizeAmt() != null && caseLotBuy.getPrizeAmt().compareTo(BigDecimal.ZERO) > 0) {
						logger.info(
								"增加合买中奖记录userno:{},prizeAmt:{},caseLotBuyId:{}",
								new String[] { caseLotBuy.getUserno(), caseLotBuy.getPrizeAmt() + "",
										caseLotBuy.getId() + "" });
						try {
							PrizeDetail.createPrizeDetail(caseLotBuy.getId() + "@1", caseLotBuy.getUserno(), "",
									caseLotBuy.getPrizeAmt(), new Date());
						} catch (Exception e) {
							logger.error(
									"计算合买历史中奖异常caseLotBuyId" + caseLotBuy.getId() + ",userno:" + caseLotBuy.getUserno()
											+ ",prizeAmt" + caseLotBuy.getPrizeAmt(), e);
						}
					}
					if (caseLotBuy.getCommisionPrizeAmt() != null
							&& caseLotBuy.getCommisionPrizeAmt().compareTo(BigDecimal.ZERO) > 0) {
						logger.info(
								"增加合买佣金记录userno:{},prizeAmt:{},caseLotBuyId:{}",
								new String[] { caseLotBuy.getUserno(), caseLotBuy.getPrizeAmt() + "",
										caseLotBuy.getId() + "" });
						try {
							PrizeDetail.createPrizeDetail(caseLotBuy.getId() + "@2", caseLotBuy.getUserno(), "",
									caseLotBuy.getCommisionPrizeAmt(), new Date());
						} catch (Exception e) {
							logger.error(
									"计算合买历史中奖佣金异常caseLotBuyId" + caseLotBuy.getId() + ",userno:"
											+ caseLotBuy.getUserno() + ",prizeAmt" + caseLotBuy.getPrizeAmt(), e);
						}
					}
				}
			}
			index += perSize;
			caseLots = CaseLot.findCaseLots(index, perSize, startTime, endTime);
		}
		logger.info("结束计算合买历史中奖排名" + ((System.currentTimeMillis() - time1) / 1000) + "秒");
	}
}
