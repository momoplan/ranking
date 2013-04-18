package com.ruyicai.ranking.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJson
@RooJavaBean
@RooToString
@Entity
@RooEntity(versionField = "", table = "USERRANKING", identifierField = "userno", persistenceUnit = "persistenceUnit")
public class UserRanking {

	@Id
	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "TOTALPRIZEAMT", precision = 0, scale = 0)
	private BigDecimal totalPrizeAmt;

	@Column(name = "LASTMODIFYTIME")
	private Date lastModifyTime;

	@Transactional
	public static UserRanking saveOrUpdateUserRanking(String userno, BigDecimal prize) {
		UserRanking userRanking = UserRanking.findUserRanking(userno);
		if (userRanking == null) {
			userRanking = new UserRanking();
			userRanking.setUserno(userno);
			userRanking.setTotalPrizeAmt(prize);
			userRanking.setLastModifyTime(new Date());
			userRanking.persist();
			userRanking.flush();
		} else {
			userRanking.setTotalPrizeAmt(userRanking.getTotalPrizeAmt() == null ? prize : userRanking
					.getTotalPrizeAmt().add(prize));
			userRanking.setLastModifyTime(new Date());
			userRanking.merge();
		}
		return userRanking;
	}
}
