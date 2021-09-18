/**
 * (c) Hitachi, Ltd. 2018. All rights reserved.
 */
package com.gemini.toolkit.common.utils;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

/**
 * 
 * @Description get bussiness message
 */
@Component
public class MessageBundle {
    private ReloadableResourceBundleMessageSource messageSource;

	public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getMessage(String code) {
		return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
	}

	public String getMessage(String code, String [] arg) {
		return messageSource.getMessage(code, arg, LocaleContextHolder.getLocale());
	}

	public String getMessage(String code, Locale locale) {
		return messageSource.getMessage(code, null, locale);
	}

	public String getMessage(String code, String [] arg, Locale locale) {
		return messageSource.getMessage(code, arg, locale);
	}
}
