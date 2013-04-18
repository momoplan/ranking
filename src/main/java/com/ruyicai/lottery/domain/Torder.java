package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJson
@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "TORDER", identifierField = "id", persistenceUnit = "lotteryPersistenceUnit")
public class Torder {

	@Id
	@Column(name = "ID", columnDefinition = "VARCHAR2", length = 32)
	private String id;

	@Column(name = "BATCHCODE", length = 15)
	private String batchcode;

	@Column(name = "LOTNO", length = 6)
	private String lotno;

	@Column(name = "AMT")
	private BigDecimal amt;

	@Column(name = "PAYTYPE")
	private BigDecimal paytype;

	@Column(name = "ORDERSTATE")
	private BigDecimal orderstate;

	@Column(name = "BETTYPE", length = 20)
	private BigDecimal bettype;

	@Column(name = "PRIZESTATE")
	private BigDecimal prizestate;

	@Column(name = "ORDERPRIZEAMT")
	private BigDecimal orderprizeamt;

	@Column(name = "ORDERPREPRIZEAMT")
	private BigDecimal orderpreprizeamt;

	/** @deprecated 是否有战绩 0：没有，1：有 */
	@Column(name = "hasachievement")
	private BigDecimal hasachievement;

	@Column(name = "WINBASECODE", length = 60)
	private String winbasecode;

	@Column(name = "ORDERTYPE")
	private BigDecimal ordertype;

	@Column(name = "TSUBSCRIBEFLOWNO", length = 16)
	private String tsubscribeflowno;

	@Column(name = "TLOTCASEID", length = 16)
	private String tlotcaseid;

	@Column(name = "CREATETIME", columnDefinition = "TIMESTAMP(6)")
	private Date createtime;

	@Column(name = "USERNO", length = 32)
	private String userno;

	@Column(name = "BUYUSERNO", length = 32)
	private String buyuserno;

	@Column(name = "MEMO")
	private String memo;

	@Column(name = "SUBACCOUNT")
	private String subaccount;

	@Column(name = "BETNUM")
	private BigDecimal betnum;

	@Column(name = "CANCELTIME")
	private Date canceltime;

	@Column(name = "ENDTIME")
	private Date endtime;

	@Column(name = "ODESC")
	private String desc;

	@Column(name = "BETCODE")
	private String betcode;

	@Column(name = "ALREADYTRANS")
	private BigDecimal alreadytrans;

	@Column(name = "LOTMULTI")
	private BigDecimal lotmulti;

	@Column(name = "PRIZEINFO")
	private String prizeinfo;

	@Column(name = "ORDERINFO")
	private String orderinfo;

	@Column(name = "BODY")
	private String body;

	@Column(name = "INSTATE")
	private BigDecimal instate;

	@Column(name = "PAYSTATE")
	private BigDecimal paystate;

	/** 方案类型 */
	@Column(name = "LOTSTYPE")
	private BigDecimal lotsType;

	@Column(name = "ENCASHTIME")
	private Date encashtime;

	@Column(name = "EVENTCODE")
	private String eventcode;

	@Column(name = "AGENCYNO")
	private String agencyno;

	@Column(name = "CHANNEL")
	private String channel;

	@Column(name = "SUBCHANNEL")
	private String subchannel;

	public static List<Torder> findTorders(int firstResult, int maxResults, String startTime, String endTime) {
		StringBuilder sql = new StringBuilder(
				"SELECT o FROM Torder o WHERE o.orderprizeamt > 0 and o.tlotcaseid is null ");
		if (StringUtils.isNotBlank(startTime)) {
			sql.append(" and to_char(o.encashtime,'YYYY-MM-DD') >= :startTime ");
		}
		if (StringUtils.isNotBlank(endTime)) {
			sql.append(" and to_char(o.encashtime,'YYYY-MM-DD') <= :endTime ");
		}
		sql.append(" ORDER BY o.createtime DESC");
		EntityManager em = Torder.entityManager();
		TypedQuery<Torder> typedQuery = em.createQuery(sql.toString(), Torder.class).setFirstResult(firstResult)
				.setMaxResults(maxResults);
		if (StringUtils.isNotBlank(startTime)) {
			typedQuery.setParameter("startTime", startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			typedQuery.setParameter("endTime", endTime);
		}
		return typedQuery.getResultList();
	}
}
