package com.ruyicai.ranking.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.ranking.consts.RankingType;
import com.ruyicai.ranking.controller.dto.RankingDTO;
import com.ruyicai.ranking.service.LotteryService;
import com.ruyicai.ranking.util.DateUtil;
import com.ruyicai.ranking.util.JsonUtil;

@RooJson
@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "PRIZEDETAIL", identifierField = "businessId", persistenceUnit = "persistenceUnit")
public class PrizeDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "BUSINESSID", length = 50)
	private String businessId;

	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "LOTNO", length = 50)
	private String lotno;

	@Column(name = "PRIZEAMT", precision = 0, scale = 0)
	private BigDecimal prizeAmt;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Autowired
	transient LotteryService lotteryService;

	@Transactional
	public static PrizeDetail createPrizeDetail(String businessId, String userno, String lotno, BigDecimal prizeAmt,
			Date encashTime) {
		PrizeDetail prizeDetail = new PrizeDetail();
		prizeDetail.setBusinessId(businessId);
		prizeDetail.setUserno(userno);
		prizeDetail.setLotno(lotno);
		prizeDetail.setPrizeAmt(prizeAmt);
		prizeDetail.setCreateTime(encashTime);
		prizeDetail.persist();
		UserRanking.saveOrUpdateUserRanking(userno, prizeAmt);
		return prizeDetail;
	}

	@SuppressWarnings("unchecked")
	public static String statisticalPrizeDetailByType(String time, Integer type) {
		if (StringUtils.isBlank(time)) {
			throw new IllegalArgumentException("The time arguments is require");
		}
		if (type == null) {
			throw new IllegalArgumentException("The type arguments is require");
		}
		EntityManager em = PrizeDetail.entityManager();
		List<Object[]> resultList = new ArrayList<Object[]>();
		String format = null;
		String sql = "";
		if (type == RankingType.DAY.value) {
			format = "%Y-%m-%d";
			if (StringUtils.countMatches(time, "-") != 2) {
				throw new IllegalArgumentException("时间格式错误");
			}
			sql = "SELECT o.userno,sum(o.prizeAmt) FROM PrizeDetail o WHERE date_format(o.createTime,'" + format
					+ "') = ? GROUP BY o.userno ORDER BY sum(o.prizeAmt) DESC";
			resultList = em.createQuery(sql).setParameter(1, time).setMaxResults(10).getResultList();
		} else if (type == RankingType.WEEK.value) {
			format = "%Y-%m-%d";
			if (StringUtils.countMatches(time, "-") != 2) {
				throw new IllegalArgumentException("时间格式错误");
			}
			String monDayStr = DateUtil.getMonDayStr(time);
			String sunDayStr = DateUtil.getSunDayStr(time);
			sql = "SELECT o.userno,sum(o.prizeAmt) FROM PrizeDetail o WHERE date_format(o.createTime,'" + format
					+ "') >= ? AND date_format(o.createTime,'" + format
					+ "') <= ? GROUP BY o.userno ORDER BY sum(o.prizeAmt) DESC";
			resultList = em.createQuery(sql).setParameter(1, monDayStr).setParameter(2, sunDayStr).setMaxResults(10)
					.getResultList();
		} else if (type == RankingType.MONTH.value) {
			format = "%Y-%m";
			if (StringUtils.countMatches(time, "-") != 1) {
				throw new IllegalArgumentException("时间格式错误");
			}
			sql = "SELECT o.userno,sum(o.prizeAmt) FROM PrizeDetail o WHERE date_format(o.createTime,'" + format
					+ "') = ? GROUP BY o.userno ORDER BY sum(o.prizeAmt) DESC";
			resultList = em.createQuery(sql).setParameter(1, time).setMaxResults(10).getResultList();
		} else if (type == RankingType.YEAR.value) {
			format = "%Y";
			if (StringUtils.countMatches(time, "-") != 0 && time.length() != 4) {
				throw new IllegalArgumentException("时间格式错误");
			}
			sql = "SELECT o.userno,sum(o.prizeAmt) FROM PrizeDetail o WHERE date_format(o.createTime,'" + format
					+ "') = ? GROUP BY o.userno ORDER BY sum(o.prizeAmt) DESC";
			resultList = em.createQuery(sql).setParameter(1, time).setMaxResults(10).getResultList();
		} else if (type == RankingType.ALL.value) {
			sql = "SELECT o.userno,sum(o.prizeAmt) FROM PrizeDetail o GROUP BY o.userno ORDER BY sum(o.prizeAmt) DESC";
			resultList = em.createQuery(sql).setMaxResults(10).getResultList();
		}

		List<RankingDTO> dtoList = new ArrayList<RankingDTO>();
		for (Object[] obj : resultList) {
			if (obj.length > 1) {
				String userno = String.valueOf(obj[0]);
				String prizeAmt = String.valueOf(obj[1]);
				if (StringUtils.isNotBlank(userno)) {
					RankingDTO dto = new RankingDTO();
					Tuserinfo tuserinfo = new PrizeDetail().lotteryService.findTuserinfoByUserno(userno);
					if (tuserinfo != null) {
						dto.setMobileId(tuserinfo.getMobileid());
						dto.setNickname(tuserinfo.getNickname());
						dto.setUsername(tuserinfo.getUserName());
						dto.setUserno(tuserinfo.getUserno());
						dto.setPrizeAmt(prizeAmt);
						dtoList.add(dto);
					}
				}
			}
		}
		return JsonUtil.toJson(dtoList);
	}
}
