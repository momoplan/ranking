package com.ruyicai.ranking.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.ranking.service.MemcachedService;

@RooJson
@RooJavaBean
@RooToString
@Entity
@RooEntity(versionField = "", table = "UserRankingHistory", identifierField = "id", persistenceUnit = "persistenceUnit")
public class UserRankingHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "rankingJson", columnDefinition = "text")
	private String rankingJson;

	@Column(name = "time", length = 20)
	private String time;

	@Column(name = "type", length = 2)
	private Integer type;

	@Column(name = "LASTMODIFYTIME")
	private Date lastModifyTime;

	@Autowired
	transient MemcachedService<UserRankingHistory> memcachedService;

	@Transactional
	public static UserRankingHistory saveOrUpdateUserRankingHistory(String rankingJson, String time, Integer type) {
		if (StringUtils.isBlank(rankingJson)) {
			throw new IllegalArgumentException("The rankingJson argument rankingJson is require");
		}
		if (time == null || time.length() == 0) {
			throw new IllegalArgumentException("The time argument is required");
		}
		if (type == null) {
			throw new IllegalArgumentException("The type argument is required");
		}
		UserRankingHistory history = findUserRankingHistoryFromDB(time, type);
		if (history == null) {
			history = new UserRankingHistory();
			history.setRankingJson(rankingJson);
			history.setTime(time);
			history.setType(type);
			history.setLastModifyTime(new Date());
			history.persist();
			history.flush();
		} else {
			history.setRankingJson(rankingJson);
			history.setLastModifyTime(new Date());
			history.merge();
		}
		if (history != null) {
			new UserRankingHistory().memcachedService.set("UserRankingHistory" + time + type, history);
		}
		return history;
	}

	public static UserRankingHistory findUserRankingHistory(String time, Integer type) {
		if (time == null || time.length() == 0) {
			throw new IllegalArgumentException("The time argument is required");
		}
		if (type == null) {
			throw new IllegalArgumentException("The type argument is required");
		}
		UserRankingHistory userRankingHistory = new UserRankingHistory().memcachedService.get("UserRankingHistory"
				+ time + type);
		if (userRankingHistory != null) {
			return userRankingHistory;
		} else {
			UserRankingHistory history = findOrCreateUserRankingHistoryFromDB(time, type);
			if (history != null) {
				new UserRankingHistory().memcachedService.set("UserRankingHistory" + time + type, history);
			}
			return history;
		}
	}

	public static UserRankingHistory findUserRankingHistoryFromDB(String time, Integer type) {
		if (time == null || time.length() == 0) {
			throw new IllegalArgumentException("The time argument is required");
		}
		if (type == null) {
			throw new IllegalArgumentException("The type argument is required");
		}
		EntityManager em = UserRankingHistory.entityManager();
		TypedQuery<UserRankingHistory> q = em
				.createQuery(
						"SELECT o FROM UserRankingHistory AS o WHERE o.time = :time  AND o.type = :type ORDER BY o.lastModifyTime DESC ",
						UserRankingHistory.class);
		q.setParameter("time", time);
		q.setParameter("type", type);
		List<UserRankingHistory> list = q.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public static UserRankingHistory findOrCreateUserRankingHistoryFromDB(String time, Integer type) {
		UserRankingHistory history = findUserRankingHistoryFromDB(time, type);
		if (history != null) {
			return history;
		} else {
			String rankingJson = PrizeDetail.statisticalPrizeDetailByType(time, type);
			return UserRankingHistory.saveOrUpdateUserRankingHistory(rankingJson, time, type);
		}
	}
}
