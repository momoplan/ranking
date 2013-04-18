package com.ruyicai.ranking.util;

import java.util.ArrayList;
import java.util.List;

public class PackageSendUtil {

	/**
	 * 将按内容分组的短信集合，转换为以batchsize数量为一组的短信集合
	 * 
	 * @param list
	 *            按内容分组的短信集合
	 * @param batchsize
	 *            梦网一个请求最多发送多少个号码
	 * @return Object[0]:短信内容,Object[1]:短信号码以","分割,Object[2]:detailId串以","分割
	 */
	@SuppressWarnings("unchecked")
	public static List<Object[]> reorganizeData(List<Object[]> list, Integer batchsize) {
		if (batchsize == null || batchsize > 100) {
			batchsize = 100;
		}
		List<Object[]> result = new ArrayList<Object[]>();
		if (list != null && list.size() > 0) {
			for (Object[] objArray : list) {
				String text = (String) objArray[0];
				List<Object[]> mobileIds = (List<Object[]>) objArray[1];
				if (mobileIds != null && mobileIds.size() > 0) {
					StringBuffer mobileBuffer = new StringBuffer();
					StringBuffer idBuffer = new StringBuffer();
					for (int i = 1; i <= mobileIds.size(); i++) {
						String mobile = (String) mobileIds.get(i - 1)[0];
						mobileBuffer.append(mobile + ",");
						Long id = (Long) mobileIds.get(i - 1)[1];
						idBuffer.append(id + ",");
						if (i == mobileIds.size()) {
							result.add(new Object[] { text, delEndSplit(mobileBuffer), delEndSplit(idBuffer) });
							mobileBuffer.delete(0, mobileBuffer.length());
							idBuffer.delete(0, idBuffer.length());
							break;
						}
						if (i % batchsize == 0) {
							result.add(new Object[] { text, delEndSplit(mobileBuffer), delEndSplit(idBuffer) });
							mobileBuffer.delete(0, mobileBuffer.length());
							idBuffer.delete(0, idBuffer.length());
						}
					}
				}
			}
		}
		return result;
	}

	private static String delEndSplit(final StringBuffer sb) {
		String str = sb.toString();
		if (str.endsWith(",")) {
			return str.substring(0, str.length() - 1);
		} else {
			return str.toString();
		}
	}
}
