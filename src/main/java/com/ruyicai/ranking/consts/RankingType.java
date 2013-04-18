package com.ruyicai.ranking.consts;

public enum RankingType {

	DAY(1, "日统计"), WEEK(2, "周统计"), MONTH(3, "月统计"), YEAR(4, "年统计"), ALL(5, "总排行");

	public int value;

	public String memo;

	public int intValue() {
		return value;
	}

	private RankingType(int value, String memo) {
		this.value = value;
		this.memo = memo;
	}
}
