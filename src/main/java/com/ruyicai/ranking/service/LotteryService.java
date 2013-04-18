package com.ruyicai.ranking.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.ranking.controller.ResponseData;
import com.ruyicai.ranking.exception.RuyicaiException;
import com.ruyicai.ranking.util.ErrorCode;
import com.ruyicai.ranking.util.HttpUtil;
import com.ruyicai.ranking.util.JsonUtil;
import com.ruyicai.ranking.util.StringUtil;

@Service
public class LotteryService {

	private Logger logger = LoggerFactory.getLogger(LotteryService.class);

	@Autowired
	MemcachedService<String> memcachedService;

	@Value("${lotteryurl}")
	String lotteryurl;

	/**
	 * @param userno
	 *            用户编号
	 * @return Tuserinfo
	 */
	public Tuserinfo findTuserinfoByUserno(String userno) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		Tuserinfo tuserinfo = null;
		String url = lotteryurl + "/tuserinfoes?find=ByUserno&json&userno=" + userno;
		try {
			String userJson = memcachedService.get(StringUtil.join("_", "Tuserinfo", userno));
			if (StringUtils.isNotBlank(userJson)) {
				tuserinfo = Tuserinfo.fromJsonToTuserinfo(userJson);
			}
			if (tuserinfo != null) {
				logger.info("find user from cache");
				return tuserinfo;
			}
			logger.info("find user from lottery,userno:" + userno);
			String result = HttpUtil.getResultMessage(url.toString());
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					String value = jsonObject.getString("value");
					tuserinfo = Tuserinfo.fromJsonToTuserinfo(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + "失败" + e.getMessage());
			throw new RuyicaiException(ErrorCode.ERROR);
		}
		return tuserinfo;
	}

	/**
	 * @param userno
	 *            用户编号
	 * @return Tuserinfo
	 */
	@SuppressWarnings("unchecked")
	public Tuserinfo findTuserinfoByMobileid(String mobileid) {
		if (StringUtils.isBlank(mobileid)) {
			throw new IllegalArgumentException("the argument mobileid is required");
		}
		Tuserinfo tuserinfo = null;
		String url = lotteryurl + "/tuserinfoes?find=ByMobileid&json&mobileid=" + mobileid;
		try {
			String result = HttpUtil.getResultMessage(url.toString());
			ResponseData rd = JsonUtil.fromJsonToObject(result, ResponseData.class);
			if (rd != null) {
				if (rd.getErrorCode().equals(ErrorCode.OK.value)) {
					Map<String, Object> map = (Map<String, Object>) rd.getValue();
					if (map.containsKey("userno")) {
						String uno = (String) map.get("userno");
						if (StringUtils.isNotBlank(uno)) {
							tuserinfo = new Tuserinfo();
							tuserinfo.setUserno(uno);
							tuserinfo.setName((String) map.get("name"));
							tuserinfo.setNickname((String) map.get("nickname"));
							tuserinfo.setAgencyno((String) map.get("agencyno"));
							tuserinfo.setMobileid((String) map.get("mobileid"));
							tuserinfo.setChannel((String) map.get("channel"));
							tuserinfo.setSubChannel((String) map.get("subChannel"));
							tuserinfo.setEmail((String) map.get("email"));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + "失败" + e.getMessage());
			throw new RuyicaiException("请求lottery失败");
		}
		return tuserinfo;
	}

	/**
	 * 赠送彩金
	 * 
	 * @param userno
	 * @param amt
	 * @param subchannel
	 * @param channel
	 * @param memo
	 * @return
	 */
	public Boolean directChargeProcess(String userno, BigDecimal amt, String subchannel, String channel, String memo) {
		Boolean flag = false;
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument mobileid is required");
		}
		if (amt == null) {
			throw new IllegalArgumentException("the argument mobileid is required");
		}
		logger.info("赠送彩金 user:{},amt:{}", new String[] { userno, amt.toString() });
		String subchannelStr = subchannel == null ? " " : subchannel;
		String channelStr = channel == null ? " " : channel;
		String url = lotteryurl + "/taccounts/doDirectChargeProcess";
		StringBuffer params = new StringBuffer();
		params.append("userno=" + userno).append("&amt=" + amt.toString()).append("&accesstype=2")
				.append("&subchannel=" + subchannelStr).append("&channel=" + channelStr);
		if (memo != null) {
			params.append("&memo=" + memo);
		}
		try {
			String result = HttpUtil.post(url, params.toString());
			ResponseData rd = JsonUtil.fromJsonToObject(result, ResponseData.class);
			if (rd.getErrorCode().equals("0")) {
				flag = true;
			} else {
				logger.error("赠送彩金错误信息:" + rd.getValue());
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + ",参数 " + params.toString() + ".失败" + e.getMessage());
			throw new RuyicaiException("请求lottery失败");
		}
		return flag;
	}

	/**
	 * 追号
	 * 
	 * @param body
	 * @return 追号flowno(不为null),如果失败，抛出异常
	 */
	public String subscribeOrder(String body) {
		if (StringUtils.isBlank(body)) {
			throw new IllegalArgumentException("the argument body is required");
		}
		String flowno = null;
		logger.info("追号body:{}", new String[] { body });
		String url = lotteryurl + "/bet/subscribeOrder";
		String params = "body=" + body;
		try {
			String result = HttpUtil.post(url, params);
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					String value = jsonObject.getString("value");
					if (StringUtils.isNotBlank(value)) {
						flowno = value;
					}
				} else {
					String value = jsonObject.getString("value");
					logger.error("errorCode:" + errorCode + ",value:" + value);
					throw new RuyicaiException("请求lottery失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + ",参数 " + params.toString() + ".失败" + e.getMessage(), e);
			throw new RuyicaiException("请求lottery失败");
		}
		logger.info("追号flowno:" + flowno);
		return flowno;
	}

	/**
	 * 查询用户一段时间内的消费记录次数
	 * 
	 * @param userno
	 *            用户编号
	 * @param date
	 *            时间字符串(格式:2011-11-11 11:11:11)
	 * @return
	 */
	public BigDecimal findTtransactionSum(String userno, String date) {
		BigDecimal count = null;
		String url = null;
		try {
			url = lotteryurl + "/select/findTtransactionCount4Action?userno=" + userno + "&date="
					+ URLEncoder.encode(date, "UTF-8");
			String result = HttpUtil.getResultMessage(url.toString());
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					String value = jsonObject.getString("value");
					if (StringUtils.isNotBlank(value)) {
						count = new BigDecimal(value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + "失败" + e.getMessage());
			throw new RuyicaiException("请求lottery失败");
		}
		return count;
	}

	public void addDrawAmount(String userno, String ttransactionid, BigDecimal drawamt) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		if (StringUtils.isBlank(ttransactionid)) {
			throw new IllegalArgumentException("the argument ttransactionid is required");
		}
		if (drawamt == null) {
			throw new IllegalArgumentException("the argument drawamt is required");
		}
		logger.info("增加可提现金额userno:{},ttransactionid:{},drawamt:{}",
				new String[] { userno, ttransactionid, drawamt.toString() });
		String url = lotteryurl + "/taccounts/addDrawAmount";
		String params = "userno=" + userno + "&ttransactionid=" + ttransactionid + "&drawamt=" + drawamt;
		try {
			String result = HttpUtil.post(url, params);
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					logger.info("增加可提现金额成功");
				} else {
					logger.error("增加可提现金额失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + ",参数 " + params.toString() + ".失败" + e.getMessage());
			throw new RuyicaiException("请求lottery失败");
		}
	}
}
