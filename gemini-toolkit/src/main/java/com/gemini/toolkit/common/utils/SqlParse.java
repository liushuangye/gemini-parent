package com.gemini.toolkit.common.utils;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gemini.toolkit.common.exception.PgApplicationException;

@Component
public class SqlParse {

	@Autowired
	private VelocityEngine velocityEngine;

	/**
	 * 模板解析
	 *
	 * @param sql
	 * @param paramData
	 * @return
	 */
	public String getParseSql(String sql, Map<String, Object> paramData) {
		try (StringWriter writer = new StringWriter()) {
			VelocityContext context = new VelocityContext(paramData);
			velocityEngine.evaluate(context, writer, "", sql);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PgApplicationException();
		}

	}

}
