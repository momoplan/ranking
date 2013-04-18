package com.ruyicai.ranking.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.ranking.consts.RankingType;
import com.ruyicai.ranking.domain.PrizeDetail;
import com.ruyicai.ranking.domain.UserRankingHistory;

@Service
public class QuartzService {

	private Logger logger = LoggerFactory.getLogger(QuartzService.class);

	public void process() {
		logger.info("定时更新中奖统计");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf1.format(new Date());
		String rankingJson1 = PrizeDetail.statisticalPrizeDetailByType(time1, RankingType.DAY.value);
		logger.info("更新排行time:{},rankingType:{},json:{}", new String[] { time1, RankingType.DAY.value + "",
				rankingJson1 });
		UserRankingHistory.saveOrUpdateUserRankingHistory(rankingJson1, time1, RankingType.DAY.value);

		String rankingJson2 = PrizeDetail.statisticalPrizeDetailByType(time1, RankingType.WEEK.value);
		logger.info("更新排行time:{},rankingType:{},json:{}", new String[] { time1, RankingType.WEEK.value + "",
				rankingJson2 });
		UserRankingHistory.saveOrUpdateUserRankingHistory(rankingJson2, time1, RankingType.WEEK.value);

		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM");
		String time3 = sdf3.format(new Date());
		String rankingJson3 = PrizeDetail.statisticalPrizeDetailByType(time3, RankingType.MONTH.value);
		logger.info("更新排行time:{},rankingType:{},json:{}", new String[] { time3, RankingType.MONTH.value + "",
				rankingJson3 });
		UserRankingHistory.saveOrUpdateUserRankingHistory(rankingJson3, time3, RankingType.MONTH.value);

		SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
		String time4 = sdf4.format(new Date());
		String rankingJson4 = PrizeDetail.statisticalPrizeDetailByType(time4, RankingType.YEAR.value);
		logger.info("更新排行time:{},rankingType:{},json:{}", new String[] { time4, RankingType.YEAR.value + "",
				rankingJson4 });
		UserRankingHistory.saveOrUpdateUserRankingHistory(rankingJson4, time4, RankingType.YEAR.value);

		String time5 = "all";
		String rankingJson5 = PrizeDetail.statisticalPrizeDetailByType(time5, RankingType.ALL.value);
		logger.info("更新排行time:{},rankingType:{},json:{}", new String[] { time5, RankingType.ALL.value + "",
				rankingJson5 });
		UserRankingHistory.saveOrUpdateUserRankingHistory(rankingJson5, time5, RankingType.ALL.value);
	}

}
