package com.ruyicai.ranking;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.ranking.consts.RankingType;
import com.ruyicai.ranking.domain.PrizeDetail;
import com.ruyicai.ranking.domain.UserRanking;
import com.ruyicai.ranking.domain.UserRankingHistory;
import com.ruyicai.ranking.service.RankingService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class RankingTest {

	Logger logger = LoggerFactory.getLogger(RankingTest.class);

	@Autowired
	RankingService rankingService;

	@Test
	public void testCreate() {
		for (PrizeDetail detail : PrizeDetail.findAllPrizeDetails()) {
			detail.remove();
		}
		for (UserRanking ranking : UserRanking.findAllUserRankings()) {
			ranking.remove();
		}
		PrizeDetail prizeDetail = PrizeDetail.createPrizeDetail("123456", "123", "F47104", new BigDecimal(10000),
				new Date());
		logger.info(prizeDetail.toString());

		UserRanking ranking = UserRanking.findUserRanking("123");
		logger.info(ranking.toString());
		Assert.assertTrue(ranking.getTotalPrizeAmt().compareTo(new BigDecimal(10000)) == 0);
	}

	@Test
	public void testComputeHistoryData() {
		rankingService.computeHistoryData("2012-01-01", "2012-02-02");
	}

	@Test
	public void testComputeCaseLotHistoryData() {
		rankingService.computeCaseLotHistoryData(null, null);
	}

	@Test
	public void testPrizeDetail() {
		String rankingJson = PrizeDetail.statisticalPrizeDetailByType("2012-01-11", 1);
		logger.info(rankingJson);
		UserRankingHistory history = UserRankingHistory.saveOrUpdateUserRankingHistory(rankingJson, "2012-01-11", 1);
		logger.info(history.toString());
	}

	@Test
	public void testGroupBy() {
		logger.info(UserRankingHistory.findUserRankingHistory("2011-12-14", RankingType.DAY.value).toString());
		logger.info(UserRankingHistory.findUserRankingHistory("2011-12-14", RankingType.WEEK.value).toString());
		logger.info(UserRankingHistory.findUserRankingHistory("2011-12", RankingType.MONTH.value).toString());
		logger.info(UserRankingHistory.findUserRankingHistory("2011", RankingType.YEAR.value).toString());
		logger.info(UserRankingHistory.findUserRankingHistory("2011-12-14", RankingType.ALL.value).toString());
	}
}
