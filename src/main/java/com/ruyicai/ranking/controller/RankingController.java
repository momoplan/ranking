package com.ruyicai.ranking.controller;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.ranking.domain.PrizeDetail;
import com.ruyicai.ranking.domain.UserRankingHistory;
import com.ruyicai.ranking.exception.RuyicaiException;
import com.ruyicai.ranking.service.RankingService;
import com.ruyicai.ranking.util.DateUtil;
import com.ruyicai.ranking.util.ErrorCode;
import com.ruyicai.ranking.util.RandomUtil;

@Controller
public class RankingController {

	private Logger logger = LoggerFactory.getLogger(RankingController.class);

	@Autowired
	private RankingService rankingService;

	/**
	 * 计算历史中奖记录
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/computeHistoryData")
	public @ResponseBody
	ResponseData computeHistoryData(
			@RequestParam(value = "start", required = false, defaultValue = "") String startTime,
			@RequestParam(value = "end", required = false, defaultValue = "") String endTime) {
		logger.info("/computeHistoryData");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rankingService.computeHistoryData(startTime, endTime);
		} catch (RuyicaiException e) {
			logger.error("计算历史中奖记录,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("计算历史中奖记录,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 计算合买历史中奖记录
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/computeCaseLotHistoryData")
	public @ResponseBody
	ResponseData computeCaseLotHistoryData(
			@RequestParam(value = "start", required = false, defaultValue = "") String startTime,
			@RequestParam(value = "end", required = false, defaultValue = "") String endTime) {
		logger.info("/computeCaseLotHistoryData");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rankingService.computeCaseLotHistoryData(startTime, endTime);
		} catch (RuyicaiException e) {
			logger.error("计算合买历史中奖记录,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("计算合买历史中奖记录,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 查询排名信息
	 * 
	 * @param time
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/selectUserRankingHistory")
	public @ResponseBody
	ResponseData selectUserRankingHistory(@RequestParam("time") String time, @RequestParam("type") Integer type) {
		logger.info("/selectUserRankingHistory");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			UserRankingHistory userRankingHistory = UserRankingHistory.findUserRankingHistory(time, type);
			String rankingJson = userRankingHistory.getRankingJson();
			rd.setValue(rankingJson);
		} catch (RuyicaiException e) {
			logger.error("查询用户中奖排行异常,time:" + time + ",type:" + type + "," + e.getMessage(), e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("查询用户中奖排行异常,time:" + time + ",type:" + type + "," + e.getMessage(), e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/createPrizeDetail")
	public @ResponseBody
	ResponseData createPrizeDetail(@RequestParam("userno") String userno, @RequestParam("date") String time,
			@RequestParam("amt") BigDecimal amt) {
		logger.info("/createPrizeDetail");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			Date date = new Date();
			Date encashtime = DateUtil.parse("yyyy-MM-dd", time);
			encashtime.setHours(date.getHours());
			encashtime.setMinutes(date.getMinutes());
			encashtime.setSeconds(date.getSeconds());
			rd.setValue(PrizeDetail.createPrizeDetail(RandomUtil.getUUID(), userno, "111111", amt, encashtime));
		} catch (RuyicaiException e) {
			logger.error("创建中奖记录异常,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("创建中奖记录异常,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
