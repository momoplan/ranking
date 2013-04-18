package com.ruyicai.ranking.controller.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class RankingDTO {
	private String userno;
	private String nickname;
	private String mobileId;
	private String username;
	private String prizeAmt;
}
