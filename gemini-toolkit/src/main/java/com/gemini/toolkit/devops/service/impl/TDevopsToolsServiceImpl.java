package com.gemini.toolkit.devops.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gemini.toolkit.basedata.service.impl.AbsDataImport;
import com.gemini.toolkit.common.exception.PgApplicationException;
import com.gemini.toolkit.common.exception.PgInputCheckException;
import com.gemini.toolkit.devops.dto.DevopsFileUploadDto;
import com.gemini.toolkit.devops.dto.DevopsToolsDetailDto;
import com.gemini.toolkit.devops.dto.DevopsToolsPageDto;
import com.gemini.toolkit.devops.entity.TDevopsToolsEntity;
import com.gemini.toolkit.devops.mapper.TDevopsLogDetailsMapper;
import com.gemini.toolkit.devops.mapper.TDevopsToolsMapper;
import com.gemini.toolkit.devops.xml.model.Devops;
import com.gemini.toolkit.devops.xml.model.Param;
import com.gemini.toolkit.devops.xml.model.Sql;
import com.gemini.toolkit.devops.xml.model.Step;
import com.gemini.toolkit.login.form.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gemini.toolkit.common.utils.DevopsUtils;
import com.gemini.toolkit.common.utils.GetUserInfo;
import com.gemini.toolkit.common.utils.JaxbUtil;
import com.gemini.toolkit.common.utils.ValidationXml;
import com.gemini.toolkit.devops.service.TDevopsToolsService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 运维工具列表 服务实现类
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */
@Service
@Slf4j
public class TDevopsToolsServiceImpl extends ServiceImpl<TDevopsToolsMapper, TDevopsToolsEntity>
		implements TDevopsToolsService {
	private static final Logger log = LoggerFactory.getLogger(TDevopsToolsServiceImpl.class);
	@Autowired
    DevopsUtils devopsUtils;
	
	@Autowired
	private com.gemini.toolkit.common.utils.SqlParse SqlParse;
	
	@Value("${db.type}")
	private String dbType; 
	
	@Autowired
	TDevopsToolsMapper mapper;
	@Autowired
	private MessageSource messageSource;

	@Autowired
    TDevopsLogDetailsMapper logDetailsMapper;
	

	@Override
	public Page<DevopsToolsPageDto> page(Integer pageNum, Integer pageSize, String templateName) {

		Page<DevopsToolsPageDto> page = new Page<>(pageNum, pageSize, true);

		return mapper.getInfoPage(page, templateName);
	}

	@Override
	public boolean saveData(DevopsFileUploadDto uploadFile) {

		if (uploadFile.getXml() == null) {

			throw new PgInputCheckException("devops.xml.notEmpty",
					messageSource.getMessage("devops.xml.notEmpty", null, LocaleContextHolder.getLocale()));
		}
		try {

			// 加载xsd文件
			URL xsdUrl = this.getClass().getResource("/valid.xsd");
			if (xsdUrl == null) {
				throw new PgApplicationException("devops.xsd.notFound",
						messageSource.getMessage("devops.xsd.notFound", null, LocaleContextHolder.getLocale()));
			} 
			MultipartFile multifile = uploadFile.getXml();

			// 校验xml
			String validRes = ValidationXml.validateXMLByXSD(multifile.getName(),multifile.getInputStream(), xsdUrl.toString());
			
			

			if (StringUtils.isNotEmpty(validRes)) {
				throw new PgApplicationException("devops.xml.validate.faild",
						messageSource.getMessage("devops.xml.validate.faild", new String[] {validRes}, LocaleContextHolder.getLocale()));
			}
			
			// 解析xml
			String xml = new String(multifile.getBytes());
			Devops devops = JaxbUtil.converyToJavaBean(xml, Devops.class);
			
			//  check xml 格式
			String error = busiCheck(devops);
			
			if(StringUtils.isNotEmpty(error)) {
				log.error("xml业务check未通过。{}",error);
				throw new PgApplicationException("devops.xml.validate.faild",
						messageSource.getMessage("devops.xml.validate.faild", new String[] {error}, LocaleContextHolder.getLocale()));
			}
			
			// 预执行sql，验证sql是否可用
			previewSql(devops);
			// 保存数据
			TDevopsToolsEntity entity = new TDevopsToolsEntity();

			String devopsName = uploadFile.getDevopsName();
			String devopsDesc = uploadFile.getDeptdesc();
			
			if(StringUtils.isEmpty(uploadFile.getDevopsName())) {
				devopsName = devops.getName();
			}
			
			if(StringUtils.isEmpty(uploadFile.getDeptdesc())) {
				devopsDesc = devops.getDesc();
			}
			
			entity.setTemplateXml(multifile.getBytes());

			entity.setUuid(UUID.randomUUID().toString());
			entity.setDevopsName(devopsName);
			entity.setDeptdesc(devopsDesc);
			entity.setDeleteFlg("0");
			entity.setUpdateKey(0);
			Date nowtime = new Date();
			entity.setCreateDateTime(nowtime);
			entity.setUpdateDateTime(nowtime);
			UserInfo user = GetUserInfo.getUserInfo();
			if (user != null) {
				entity.setCreateUserId(user.getStaffId());
				entity.setUpdateUserId(user.getStaffId());
			}
			mapper.insert(entity);
		} catch (IOException e) {

			log.error("工具集保存失败,原因:{}",e.getMessage());
			return false;
		}
		return true;
	}

	public DevopsToolsDetailDto getDetailById(Integer id) {

		TDevopsToolsEntity entity = mapper.selectById(id);
		DevopsToolsDetailDto detail = new DevopsToolsDetailDto();
		if (entity != null) {
			detail.setId(entity.getId());
			detail.setUuid(entity.getUuid());
			detail.setDeptdesc(entity.getDeptdesc());
			detail.setDevopsName(entity.getDevopsName());

			byte[] b = entity.getTemplateXml();
			String xml = new String(b);
			Devops devops = JaxbUtil.converyToJavaBean(xml, Devops.class);
			detail.setDevops(devops);
		}

		return detail;
	}


	@Override
	public boolean updateData(DevopsFileUploadDto uploadFile) {

		try {
			if (StringUtils.isEmpty(uploadFile.getDevopsName())) {

				throw new PgInputCheckException("devops.devopsname.notEmpty",
						messageSource.getMessage("devops.devopsname.notEmpty", null, LocaleContextHolder.getLocale()));
			}

			if (uploadFile.getXml() != null) {

				MultipartFile multifile = uploadFile.getXml();
				// 加载xsd文件
				URL xsdUrl = this.getClass().getResource("/valid.xsd");
				if (xsdUrl == null) {
					throw new PgApplicationException("devops.xsd.notFound",
							messageSource.getMessage("devops.xsd.notFound", null, LocaleContextHolder.getLocale()));
				} else {

				}

				// 校验xml
				String validRes = ValidationXml.validateXMLByXSD(multifile.getName(),multifile.getInputStream(), xsdUrl.toString());
				
				

				if (StringUtils.isNotEmpty(validRes)) {
					throw new PgApplicationException("devops.xml.validate.faild",
							messageSource.getMessage("devops.xml.validate.faild", new String[] {validRes}, LocaleContextHolder.getLocale()));
				}
			}

			// 保存数据
			TDevopsToolsEntity entity = new TDevopsToolsEntity();
			entity = mapper.selectById(uploadFile.getId());
			if (entity == null) {
				throw new PgApplicationException("devops.data.notFound",
						messageSource.getMessage("devops.data.notFound", null, LocaleContextHolder.getLocale()));
			}

			if (uploadFile.getXml() != null) {

				entity.setTemplateXml(uploadFile.getXml().getBytes());
			}
			entity.setDevopsName(uploadFile.getDevopsName());
			entity.setDeptdesc(uploadFile.getDeptdesc());
			entity.setUpdateDateTime(new Date());
			UserInfo user = GetUserInfo.getUserInfo();
			if (user != null) {
				entity.setUpdateUserId(user.getStaffId());
			}
			mapper.updateByUpdateKey(entity);

		} catch (IOException e) {
			log.error("工具集更新失败,原因:{}",e.getMessage());
			return false;
		}

		return true;
	}


	/**
	 * 删除运维工具
	 * @param id
	 * @return
	 */
	public boolean deleteToolkitById(Long id) {
		
		TDevopsToolsEntity entity = this.getById(id);
		if(entity != null) {
			entity.setDeleteFlg("1");
			entity.setUpdateDateTime(new Date());
			UserInfo user =	GetUserInfo.getUserInfo();
			entity.setUpdateUserId(user.getStaffId());
			entity.setUpdateKey(entity.getUpdateKey() + 1);
			this.updateById(entity);
			
			return true;
		} 
			
		log.error("工具集({})删除失败。",id);
		
		
		return false;
	}
	/**
	 * check param
	 * 1.是否重复，2是否是空key 3.显示名称是否为空
	 * 4.其他
	 * @return
	 */
	private String busiCheck(Devops devops) {
		
		StringBuilder sb = new StringBuilder();
		// check每个step
		List<Step> stepList = devops.getSteps().getStep();
		List<Param> paramList  = null;
		Map<String,String> paramMap = null;
		List<Sql> sqlList  = null;
		Map<Integer,Integer> sqlMap = null;
		
		for(Step step : stepList) {
		    // check 是否有重复的key
			 paramList = step.getParamsOfWhere().getParam();
			 paramMap = new HashMap<String, String>();
			 for(Param param : paramList) {
				 
				 if(!paramMap.containsKey(param.getKey())) {
					 paramMap.put(param.getKey(), param.getKey());
					 
				 } else {
					 sb.append("Step{"+step.getId()+"}的paramsOfWhere元素中，参数重复.key:{"+param.getKey()+"}。");
					 sb.append("\r\n");
				 }
				 if(StringUtils.isEmpty(param.getValue())) {
					 sb.append("Step{"+step.getId()+"}的paramsOfWhere元素中，参数key:{"+param.getKey()+"}的显示内容不可为空。");
					 sb.append("\r\n");
				 }
				 
			 }
			 if(step.getParamsOfSet() != null) {
				 
				 paramList = step.getParamsOfSet().getParam();
				 paramMap = new HashMap<String, String>();
				 for(Param param : paramList) {
					 
					 if(!paramMap.containsKey(param.getKey())) {
						 paramMap.put(param.getKey(), param.getKey());
						 
					 } else {
						 sb.append("Step{"+step.getId()+"}的paramsOfSet元素中，参数重复.key:{"+param.getKey()+"}。");
						 sb.append("\r\n");
					 }
					 if(StringUtils.isEmpty(param.getValue())) {
						 sb.append("Step{"+step.getId()+"}的paramsOfSet元素中，参数key:{"+param.getKey()+"}的显示内容不可为空。");
						 sb.append("\r\n");
					 }
					 
				 }
			 }
			// check execsql 1：order是否重复，2是否有可执行sql脚本
			 sqlList = step.getExecSqls().getSql();
			 sqlMap = new HashMap<>();
			 for (Sql sql : sqlList) {
				 if(StringUtils.isEmpty(sql.getDbtype()) || StringUtils.equals(sql.getDbtype(), dbType)) {
					 if(!sqlMap.containsKey(sql.getOrder())) {
						 
						 sqlMap.put(sql.getOrder(), sql.getOrder());
					 } else {
						 sb.append("Step{"+step.getId()+"}的execSqls元素中，order编号重复。 order:{"+sql.getOrder()+"}。");
						 sb.append("\r\n");
					 }
					 if(StringUtils.isEmpty(sql.getContent())) {
						 sb.append("Step{"+step.getId()+"}的execSqls元素中，sql脚本不能为空。 order:{"+sql.getOrder()+"}。");
						 sb.append("\r\n");
					 }
				 }
			 }
			 
			 // check 备份和回滚的sql需要一一对应
			 if((step.getBackupSqls() == null && step.getRollbackSqls() != null) || (step.getBackupSqls() != null && step.getRollbackSqls() == null)) {
				 
				 sb.append("Step{"+step.getId()+"}的backupSql和rollbackSql定义不完整，两者必须同时定义。");
				 sb.append("\r\n");
				 
			 } else if(step.getBackupSqls() != null && step.getRollbackSqls() != null)  {
				 
					 
				 sqlList = step.getBackupSqls().getSql();
				 sqlMap = new HashMap<>();
				 for (Sql sql : sqlList) {
					 if(StringUtils.isEmpty(sql.getDbtype()) || StringUtils.equals(sql.getDbtype(), dbType)) {
						 if(!sqlMap.containsKey(sql.getOrder())) {
							 
							 sqlMap.put(sql.getOrder(), sql.getOrder());
						 } else {
							 sb.append("Step{"+step.getId()+"}的backupSqls元素中，order编号重复。 order:{"+sql.getOrder()+"}。");
							 sb.append("\r\n");
						 }
						 if(StringUtils.isEmpty(sql.getContent())) {
							 sb.append("Step{"+step.getId()+"}的backupSqls元素中，sql脚本不能为空。 order:{"+sql.getOrder()+"}。");
							 sb.append("\r\n");
						 }
						 if(StringUtils.isEmpty(sql.getName())) {
							 sb.append("Step{"+step.getId()+"}的backupSqls元素中，name属性不能为空。 order:{"+sql.getOrder()+"}。");
							 sb.append("\r\n");
						 }
					 }
				 }
				 
				 sqlList = step.getRollbackSqls().getSql();
				 sqlMap = new HashMap<>();
				 for (Sql sql : sqlList) {
					 if(StringUtils.isEmpty(sql.getDbtype()) || StringUtils.equals(sql.getDbtype(), dbType)) {
						 if(!sqlMap.containsKey(sql.getOrder())) {
							 
							 sqlMap.put(sql.getOrder(), sql.getOrder());
						 } else {
							 sb.append("Step{"+step.getId()+"}的rollbackSqls元素中，order编号重复。 order:{"+sql.getOrder()+"}。");
							 sb.append("\r\n");
						 }
						 if(StringUtils.isEmpty(sql.getContent())) {
							 sb.append("Step{"+step.getId()+"}的rollbackSqls元素中，sql脚本不能为空。 order:{"+sql.getOrder()+"}。");
							 sb.append("\r\n");
						 }
						 if(StringUtils.isEmpty(sql.getName())) {
							 sb.append("Step{"+step.getId()+"}的rollbackSqls元素中，name属性不能为空。 order:{"+sql.getOrder()+"}。");
							 sb.append("\r\n");
						 }
					 }
				 }
			 }
			 
			
		}
		
		return sb.toString();
	}

	/**
	 * 获取工具列表 
	 */
	public List<Map<String, String>> getAllDevops() {
		
		return mapper.getAllDevops();
	}

	/**
	 * 预执行sql
	 * <br>使用sqlparse校验sql的格式</br>
	 * @param devops
	 * @return
	 */
	private void previewSql(Devops devops) {
		
		List<Step> stepList = devops.getSteps().getStep();
		List<String> execSqls = null;
		List<Param> paramList = null;
		Map<String,Object> dumyParam = null;
		List<String> errorList = null;
		
		for(Step step : stepList) {
			dumyParam = new HashMap<>();
			paramList = new ArrayList<>();
			// 获取参数
			paramList.addAll(step.getParamsOfWhere().getParam());
			
			if(step.getParamsOfSet() != null && step.getParamsOfSet().getParam().size() > 0) {
				
				paramList.addAll(step.getParamsOfSet().getParam());
			}
			
			// 生成dumy参数
			for(Param param : paramList) {
				
				if(StringUtils.equals(param.getType(), "Integer")) {
					dumyParam.put(param.getKey(), 1001);
				} else if(StringUtils.equals(param.getType(), "DateTime")) {
					dumyParam.put(param.getKey(), "2021-06-01");
				} else {
					dumyParam.put(param.getKey(), "1001");
				}
			}
			
			// 获取sql
			 execSqls = devopsUtils.getExecSqls(step.getExecSqls().getSql());
			 
			 errorList =  execExplain(execSqls,dumyParam);
			 
			 if(errorList.size() > 0) {
				 
				 // Step{0}中{1}中定义的以下sql格式不正确，请修改后重试。\r\n{2}， 
				 throw new PgApplicationException("devops.xml.validate.sqlparser",
							messageSource.getMessage("devops.xml.validate.sqlparser", new String[] {String.valueOf(step.getId()),"execSqls",errorList.toString()}, LocaleContextHolder.getLocale()));
			 }
			 if(step.getBackupSqls() != null && step.getBackupSqls().getSql().size() > 0) {
				 execSqls = devopsUtils.getExecSqls(step.getExecSqls().getSql());
				 errorList =  execExplain(execSqls,dumyParam);
				 if(errorList.size() > 0) {
					 
					 // Step{0}中{1}中定义的以下sql格式不正确，请修改后重试。\r\n{2}， 
					 throw new PgApplicationException("devops.xml.validate.sqlparser",
								messageSource.getMessage("devops.xml.validate.sqlparser", new String[] {String.valueOf(step.getId()),"backupSqls",errorList.toString()}, LocaleContextHolder.getLocale()));
				 }
			 }
			 
			 if(step.getRollbackSqls() != null && step.getRollbackSqls().getSql().size() > 0) {
				 execSqls = devopsUtils.getExecSqls(step.getRollbackSqls().getSql());
				 errorList =  execExplain(execSqls,dumyParam);
				 if(errorList.size() > 0) {
					 
					 // Step{0}中{1}中定义的以下sql格式不正确，请修改后重试。\r\n{2}， 
					 throw new PgApplicationException("devops.xml.validate.sqlparser",
								messageSource.getMessage("devops.xml.validate.sqlparser", new String[] {String.valueOf(step.getId()),"rollbackSqls",errorList.toString()}, LocaleContextHolder.getLocale()));
				 }
			 }

		}
		
		
	}
	
	private List<String> execExplain(List<String> sqlList, Map<String,Object> dumyParam) {
		
		List<String> errorList = new ArrayList<>();
		for (String sql : sqlList) {
			String parseSql = SqlParse.getParseSql(sql, dumyParam);
			try {
				List<SQLStatement> statementList = SQLUtils.parseStatements(parseSql, dbType);

			} catch (ParserException e) {
				log.error(e.getMessage());
				errorList.add(sql);
			}

		}
		return errorList;
	}
}
