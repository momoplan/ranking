// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.ranking.domain;

import com.ruyicai.ranking.domain.PrizeDetail;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect PrizeDetail_Roo_Json {
    
    public String PrizeDetail.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static PrizeDetail PrizeDetail.fromJsonToPrizeDetail(String json) {
        return new JSONDeserializer<PrizeDetail>().use(null, PrizeDetail.class).deserialize(json);
    }
    
    public static String PrizeDetail.toJsonArray(Collection<PrizeDetail> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<PrizeDetail> PrizeDetail.fromJsonArrayToPrizeDetails(String json) {
        return new JSONDeserializer<List<PrizeDetail>>().use(null, ArrayList.class).use("values", PrizeDetail.class).deserialize(json);
    }
    
}