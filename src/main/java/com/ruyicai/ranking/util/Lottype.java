package com.ruyicai.ranking.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class Lottype {

	private static Map<String, String> map = new LinkedHashMap<String, String>();

	static {
		map.put("T01006", "足彩半全场");
		map.put("T01001", "超级大乐透");
		map.put("T01008", "北京单场");
		map.put("T01010", "多乐彩");
		map.put("T01012", "十一运夺金");
		map.put("J00004", "竞彩足球半全场");
		map.put("J00001", "竞彩足球胜负平");
		map.put("T01005", "进球彩");
		map.put("T01002", "排列三");
		map.put("T01011", "排列五");
		map.put("F47102", "七乐彩");
		map.put("T01009", "七星彩");
		map.put("F47105", "群英会");
		map.put("T01004", "任九场");
		map.put("T01003", "胜负彩");
		map.put("T01007", "时时彩");
		map.put("F47104", "双色球");
		map.put("F47103", "3D");
		map.put("T01013", "22选5");
	}

	public static Map<String, String> getMap() {
		return map;
	}

	public static String getName(String lotno) {
		return map.get(lotno);
	}
}
