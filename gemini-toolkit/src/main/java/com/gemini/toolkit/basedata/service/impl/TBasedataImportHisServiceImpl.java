package com.gemini.toolkit.basedata.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.gemini.toolkit.basedata.dto.TemplateTypeDto;
import com.gemini.toolkit.basedata.entity.TBasedataImportHisEntity;
import com.gemini.toolkit.basedata.form.ImportHisForm;
import com.gemini.toolkit.basedata.mapper.TBasedataImportHisMapper;
import com.gemini.toolkit.common.exception.PgInputCheckException;
import com.gemini.toolkit.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gemini.toolkit.basedata.service.TBasedataImportHisService;
import com.gemini.toolkit.sysparam.entity.TCodeEntity;
import com.gemini.toolkit.sysparam.service.TCodeService;

/**
 * <p>
 * 基础数据导入履历 服务实现类
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */
@Service
public class TBasedataImportHisServiceImpl extends ServiceImpl<TBasedataImportHisMapper, TBasedataImportHisEntity>
		implements TBasedataImportHisService {
	
	@Autowired
	private TCodeService tCodeService;
	
	@Autowired
	private MessageSource messageSource;

	/**
	 * 获取基础数据导入履历
	 * 
	 * @param templateType
	 * @return
	 */
	public R getTBasedataImportHis(ImportHisForm importHisForm) {
		Integer currentPage = importHisForm.getCurrentPage();
		Integer pagesize = importHisForm.getPagesize();
		if(currentPage == null || pagesize == null) {
			throw new PgInputCheckException("sysparam.page.error",
					messageSource.getMessage("sysparam.page.error", null, LocaleContextHolder.getLocale()));
		}
		Page<TBasedataImportHisEntity> page = new Page<TBasedataImportHisEntity>(importHisForm.getCurrentPage(), importHisForm.getPagesize());
		Page<TBasedataImportHisEntity> tBasedataImportHisEntityList = baseMapper.getTBasedataImportHis(page,importHisForm.getTemplateType());
		return R.ok().put("data", tBasedataImportHisEntityList);
	}
	
	/**
	 * 获取模板类型
	 * @return
	 */
	public R getTemplateType() {
		QueryWrapper<TCodeEntity> entityWrapper = new QueryWrapper<TCodeEntity>();
		entityWrapper.orderBy(true, true, "display_order");
		entityWrapper.eq("code_type", "TEMPLATE_TYPE");
		entityWrapper.eq("delete_flg", "0");
		List<TCodeEntity> codeList = tCodeService.list(entityWrapper);
		List<TemplateTypeDto> TemplateTypeDtoList = new ArrayList<TemplateTypeDto>();
		for(TCodeEntity tcodeEntity:codeList) {
			TemplateTypeDto templateTypeDto = new TemplateTypeDto();
			templateTypeDto.setValue(tcodeEntity.getCodeId());
			templateTypeDto.setLabel(tcodeEntity.getCodeName());
			TemplateTypeDtoList.add(templateTypeDto);
		}
		return R.ok().put("data", TemplateTypeDtoList);
	}

}
