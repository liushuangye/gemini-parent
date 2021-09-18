package com.gemini.toolkit.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 时间戳类型的项目的map集合</br>
 * 
 * 自动生成的sql去做回滚时，对于时间错类型的key需要使用to_time
 * @author jintg
 *
 */
public class TimeStampKeyMap {

	
	public static  Map<String,String> map = new HashMap<String, String>();
	
	
	static {
		// 创建时间
		map.put("create_date_time", "create_date_time");
		// 更新时间
		map.put("update_date_time", "update_date_time");
	}
}
