package com.gemini.toolkit.devops.service.impl;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.gemini.toolkit.basedata.service.impl.AbsDataImport;
import com.gemini.toolkit.common.exception.PgApplicationException;
import com.gemini.toolkit.common.exception.PgInputCheckException;
import com.gemini.toolkit.devops.mapper.TDevopsLogDetailsMapper;
import com.gemini.toolkit.devops.mapper.TDevopsTaskMapper;
import com.gemini.toolkit.devops.mapper.TDevopsToolsMapper;
import com.gemini.toolkit.enums.SqlType;
import com.gemini.toolkit.login.form.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gemini.toolkit.common.utils.Constants;
import com.gemini.toolkit.common.utils.DevopsUtils;
import com.gemini.toolkit.common.utils.GetUserInfo;
import com.gemini.toolkit.common.utils.JaxbUtil;
import com.gemini.toolkit.common.utils.TimeStampKeyMap;
import com.gemini.toolkit.devops.dto.DevopsTaskDto;
import com.gemini.toolkit.devops.dto.DevopsTaskPageDto;
import com.gemini.toolkit.devops.dto.DevopsToolsBackupData;
import com.gemini.toolkit.devops.dto.DevopsToolsExecStepDto;
import com.gemini.toolkit.devops.dto.DevopsToolsRollbackSql;
import com.gemini.toolkit.devops.entity.TDevopsLogDetailsEntity;
import com.gemini.toolkit.devops.entity.TDevopsTaskDetailsEntity;
import com.gemini.toolkit.devops.entity.TDevopsTaskEntity;
import com.gemini.toolkit.devops.entity.TDevopsToolsEntity;
import com.gemini.toolkit.devops.mapper.TDevopsTaskDetailsMapper;
import com.gemini.toolkit.devops.service.TDevopsTaskService;
import com.gemini.toolkit.devops.xml.model.Devops;
import com.gemini.toolkit.devops.xml.model.Param;
import com.gemini.toolkit.devops.xml.model.Sql;
import com.gemini.toolkit.devops.xml.model.Step;
import com.gemini.toolkit.devops.xml.model.StepType;



/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author BHH
 * @since 2021-05-19
 */
@Service

public class TDevopsTaskServiceImpl extends ServiceImpl<TDevopsTaskMapper, TDevopsTaskEntity>
		implements TDevopsTaskService {
	private static final Logger log = LoggerFactory.getLogger(TDevopsTaskServiceImpl.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
    DevopsUtils devopsUtils;
	
	@Autowired
	private com.gemini.toolkit.common.utils.SqlParse SqlParse;

	@Autowired
	TDevopsTaskMapper tDevopsTaskMapper;

	@Autowired
	TDevopsTaskDetailsMapper tDevopsTaskDetailsMapper;

	@Autowired
    TDevopsToolsMapper devopsToolsMapper;

	@Autowired
    TDevopsLogDetailsMapper logDetailsMapper;

	@Value("${db.type}")
	String dbType;

	/**
	 * ??????????????????????????????step
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> execStep(DevopsToolsExecStepDto dto) {
		Map<String, Object> result = new HashMap<>();
		try {
			Long devopsId = dto.getId();

			TDevopsTaskEntity entity = tDevopsTaskMapper.selectById(devopsId);
			log.info("????????????{}????????????,step:{},????????????:{}", dto.getId(), dto.getStepId(), dto.getParams());

			// **************************************************
			// ??????StepID ????????????step???xml??????
			Step step = null;

			if (entity == null) {
				throw new PgApplicationException("devops.task.notFound",
						messageSource.getMessage("devops.task.notFound", null, LocaleContextHolder.getLocale()));
			}

			if (entity != null && entity.getTemplateXml() != null) {
				Devops devops = JaxbUtil.converyToJavaBean(new String(entity.getTemplateXml()), Devops.class);

				for (Step s : devops.getSteps().getStep()) {

					if (dto.getStepId() == s.getId()) {
						step = s;
						break;
					}
				}

				TDevopsLogDetailsEntity logDetails = new TDevopsLogDetailsEntity();
				logDetails.setDevopsTaskId(entity.getUuid());
				logDetails.setDeptdesc(step.getDesc());
				logDetails.setDevopsStep(step.getName());
				logDetails.setDevopsType(step.getType().value());
				UserInfo user = GetUserInfo.getUserInfo();
				logDetails.setCreateUserId(user.getStaffId());
				logDetails.setUpdateUserId(user.getStaffId());
				Date nowTime = new Date();
				logDetails.setCreateDateTime(nowTime);
				logDetails.setUpdateDateTime(nowTime);
				logDetails.setUuid(UUID.randomUUID().toString());
				logDetails.setDeleteFlg("0");
				logDetails.setUpdateKey(1);

				// **************************************************
				if (step != null) {

					List<Param> listParams = new ArrayList<>();
					listParams.addAll(step.getParamsOfWhere().getParam());
					if (step.getParamsOfSet() != null && step.getParamsOfSet().getParam().size() > 0) {
						listParams.addAll(step.getParamsOfSet().getParam());

					}
					// ?????????????????????
					Map<String, Object> inputParams = devopsUtils.parseParam(dto.getParams(), listParams);
					// **************************************************
					// ??????ExecSql??????,??????ExecSql??????
					List<Sql> sqlList = step.getExecSqls().getSql();
					List<String> execSqls = devopsUtils.getExecSqls(sqlList);
					// ??????sql
					List<String> parsedSqls = new ArrayList<String>();
					if (execSqls != null && execSqls.size() > 0) {
						execSqls.forEach(s -> {
							String parseSql = SqlParse.getParseSql(s, inputParams);
							parsedSqls.add(parseSql);
						});
					}

					// ??????????????????
					TDevopsTaskDetailsEntity taskDetailsEntity = tDevopsTaskDetailsMapper
							.selectOne(new LambdaQueryWrapper<TDevopsTaskDetailsEntity>()
									.eq(TDevopsTaskDetailsEntity::getDevopsTaskId, entity.getUuid())
									.eq(TDevopsTaskDetailsEntity::getDevopsStepId, dto.getStepId())
									.eq(TDevopsTaskDetailsEntity::getDeleteFlg, "0"));

					if (!StringUtils.equals(taskDetailsEntity.getStatus(), Constants.EXEC_STATUS_0)
							&& !StringUtils.equals(taskDetailsEntity.getDevopsType(), StepType.QUERY.value())) {
						// ??????step?????????????????????????????????
						throw new PgApplicationException("devops.task.exec.repeat", messageSource
								.getMessage("devops.task.exec.repeat", null, LocaleContextHolder.getLocale()));
					}
					// ?????????????????????
					taskDetailsEntity.setExecParams(JSON.toJSONString(inputParams));
					// ??????????????????
					taskDetailsEntity.setStatus(Constants.EXEC_STATUS_1);

					// ??????
					if (StepType.UPDATE.equals(step.getType())) {

						Map<String, String> backupList = null;
						Map<String, String> rollbackList = null;

						// **************************************************
						// xml???????????????????????????????????????????????????
						Map<String, Map<String, String>> res = devopsUtils.createBackupAndRollbackSql(step, inputParams,
								parsedSqls);
						backupList = res.get(Constants.BACKUPLIST);
						rollbackList = res.get(Constants.ROLLBACKLIST);
						List<DevopsToolsBackupData> backDatalist = new ArrayList<>();
						DevopsToolsBackupData backData = null;
						boolean isAutoGen = true;
						if (step.getBackupSqls() != null && step.getBackupSqls().getSql().size() > 0
								&& step.getRollbackSqls() != null && step.getRollbackSqls().getSql().size() > 0) {
							isAutoGen = false;
						}
						// ????????????sql
						for (Entry<String, String> entry : backupList.entrySet()) {
							backData = new DevopsToolsBackupData();
							backData.setUnionKey(entry.getKey());
							backData.setBackupSql(entry.getValue());
							backData.setAutoGen(isAutoGen);
							backData.setData(tDevopsTaskMapper.execQuery(entry.getValue()));
							backDatalist.add(backData);
						}
						// ???????????????
						logDetails.setDevopsBackUpData(JSON.toJSONString(backDatalist));
						taskDetailsEntity.setDevopsBackUpData(JSON.toJSONString(backDatalist));

						List<DevopsToolsRollbackSql> rollbackSqlList = new ArrayList<>();
						DevopsToolsRollbackSql rollbackSql = null;
						for (Entry<String, String> entry : rollbackList.entrySet()) {
							rollbackSql = new DevopsToolsRollbackSql();
							rollbackSql.setUnionKey(entry.getKey());
							rollbackSql.setAutoGen(isAutoGen);
							rollbackSql.setRollbackSql(entry.getValue());
							rollbackSqlList.add(rollbackSql);
						}
						// ?????????sql
						logDetails.setDevopsRollbackSql(JSON.toJSONString(rollbackSqlList));
						taskDetailsEntity.setDevopsRollbackSql(JSON.toJSONString(rollbackSqlList));

						// ??????????????????
						for (String s : parsedSqls) {
							tDevopsTaskMapper.execUpdate(s);
							log.info("????????????sql:{}", s);
						}

						logDetails.setDevopsExecSql(JSON.toJSONString(parsedSqls));
						// logDetails.setExecParams(JSON.toJSONString(inputParams));
						taskDetailsEntity.setDevopsExecSql(JSON.toJSONString(parsedSqls));
						// ???????????????????????????
						// taskDetailsEntity.setExecParams(JSON.toJSONString(inputParams));

						result.put("result", "ok");

					} else if (StepType.QUERY.equals(step.getType())) {
						// **************************************************
						// ????????????????????????????????????????????????????????????
						Map<String, List<Map<String, Object>>> queryResultList = new LinkedHashMap<>();

						for (int i = 0; i < parsedSqls.size(); i++) {
							List<Map<String, Object>> queryResult = null;
							String sqlStr = parsedSqls.get(i);
							queryResult = tDevopsTaskMapper.execQuery(sqlStr);
							queryResultList.put("??????." + (i+1) , queryResult);
							log.info("????????????sql:{}", sqlStr);
							log.info("????????????:{}", queryResult);

						}

						// ???????????????
						logDetails.setDevopsExecSql(JSON.toJSONString(parsedSqls));
						logDetails.setDevopsExecData(JSON.toJSONString(queryResultList));
						// logDetails.setExecParams(JSON.toJSONString(inputParams));
						// ????????????sql?????????????????????????????? ????????????????????????
						taskDetailsEntity.setDevopsExecSql(JSON.toJSONString(parsedSqls));
						taskDetailsEntity.setDevopsExecData(JSON.toJSONString(queryResultList));
						// ???????????????????????????
						// taskDetailsEntity.setExecParams(JSON.toJSONString(inputParams));

						result.put("result", queryResultList);

					}

					// ????????????
					taskDetailsEntity.setExecParams(JSON.toJSONString(dto.getParams()));
					taskDetailsEntity.setUpdateKey(taskDetailsEntity.getUpdateKey() + 1);
					tDevopsTaskDetailsMapper.updateById(taskDetailsEntity);

					logDetails.setExecParams(JSON.toJSONString(dto.getParams()));
					logDetailsMapper.insert(logDetails);

				}

			}
			log.info("?????????????????????.??????id:{},step:{}", dto.getId(), dto.getStepId());
		} catch (Exception e) {
			log.error("?????????????????????.??????id:{},step:{},??????:{}", dto.getId(), dto.getStepId(), e.getMessage());
			throw new PgApplicationException("devops.task.execstep.error", messageSource.getMessage(
					"devops.task.execstep.error", new String[] { e.getMessage() }, LocaleContextHolder.getLocale()));
		}

		return result;
	}



	@Override
	/**
	 * ?????????????????????????????????
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean createTask(DevopsTaskDto task) {

		if (StringUtils.isEmpty(task.getTaskName())) {
			throw new PgInputCheckException("devops.task.taskname.notEmpty",
					messageSource.getMessage("devops.task.taskname.notEmpty", null, LocaleContextHolder.getLocale()));
		}

		if (task.getDevopsId() == null) {
			throw new PgInputCheckException("devops.task.devopsId.notEmpty",
					messageSource.getMessage("devops.task.devopsId.notEmpty", null, LocaleContextHolder.getLocale()));
		}

		TDevopsToolsEntity tDevopsToolsEntity = devopsToolsMapper.selectOne(new LambdaQueryWrapper<TDevopsToolsEntity>()
				.eq(TDevopsToolsEntity::getId, task.getDevopsId()).eq(TDevopsToolsEntity::getDeleteFlg, "0"));

		if (tDevopsToolsEntity != null) {
			TDevopsTaskEntity taskEntity = new TDevopsTaskEntity();
			taskEntity.setServiceName(task.getTaskName());
			taskEntity.setTemplateXml(tDevopsToolsEntity.getTemplateXml());
			taskEntity.setDevopsName(tDevopsToolsEntity.getDevopsName());
			taskEntity.setDevopsTaskId(tDevopsToolsEntity.getId());
			taskEntity.setDeptdesc(tDevopsToolsEntity.getDeptdesc());

			String uuid = UUID.randomUUID().toString();
			UserInfo user = GetUserInfo.getUserInfo();
			taskEntity.setCreateUserId(user.getStaffId());
			taskEntity.setUpdateUserId(user.getStaffId());
			Date nowTime = new Date();
			taskEntity.setCreateDateTime(nowTime);
			taskEntity.setUpdateDateTime(nowTime);
			taskEntity.setUuid(uuid);
			taskEntity.setDeleteFlg("0");
			taskEntity.setUpdateKey(1);

			byte[] b = tDevopsToolsEntity.getTemplateXml();
			String xml = new String(b);
			Devops devops = JaxbUtil.converyToJavaBean(xml, Devops.class);

			TDevopsTaskDetailsEntity detail = null;
			List<TDevopsTaskDetailsEntity> detailList = new ArrayList<>();
			List<Step> stepList = devops.getSteps().getStep();

			for (Step step : stepList) {
				detail = new TDevopsTaskDetailsEntity();
				detail.setDevopsTaskId(uuid);
				detail.setDeptdesc(step.getDesc());
				detail.setDevopsStepId(step.getId());
				detail.setDevopsStep(step.getName());
				detail.setDevopsType(step.getType().value());
				detail.setStatus(Constants.EXEC_STATUS_0);

				detail.setCreateUserId(user.getStaffId());
				detail.setUpdateUserId(user.getStaffId());
				detail.setCreateDateTime(nowTime);
				detail.setUpdateDateTime(nowTime);
				detail.setUuid(UUID.randomUUID().toString());
				detail.setDeleteFlg("0");
				detail.setUpdateKey(1);
				detailList.add(detail);
			}

			// ????????????
			if (taskEntity != null) {
				tDevopsTaskMapper.insert(taskEntity);

			}

			if (detailList.size() > 0) {
				for (TDevopsTaskDetailsEntity entity : detailList) {

					tDevopsTaskDetailsMapper.insert(entity);
				}

			}
		} else {

			throw new PgInputCheckException("devops.task.toolkit.notFound",
					messageSource.getMessage("devops.task.toolkit.notFound", null, LocaleContextHolder.getLocale()));

		}

		return true;
	}

	@Override
	public boolean updateTask(DevopsTaskDto task) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override

	public Page<DevopsTaskPageDto> page(Integer pageNum, Integer pageSize, String taskName, String startDate,
                                        String endDate) {
		Page<DevopsTaskPageDto> page = new Page<>(pageNum, pageSize, true);

		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		return tDevopsTaskMapper.getInfoPage(page, taskName, startDate, endDate);
	}

	/**
	 * ????????????????????????????????????step
	 */

	@Transactional(rollbackFor = Exception.class)
	public boolean execStepRollback(DevopsToolsExecStepDto dto) {

		try {
			
			TDevopsTaskEntity task = tDevopsTaskMapper.selectOne(new LambdaQueryWrapper<TDevopsTaskEntity>()
					.eq(TDevopsTaskEntity::getDeleteFlg, "0").eq(TDevopsTaskEntity::getId, dto.getId()));
			
			if (task == null) {
				throw new PgApplicationException("devops.task.notFound",
						messageSource.getMessage("devops.task.notFound", null, LocaleContextHolder.getLocale()));
			}
			
			String taskUuid = task.getUuid();
			// TODO check??????step????????????
			// ?????????????????? ???step????????????????????????????????????step???????????????????????????????????????
			// select * from t_devops_task_details where devops_step_id > #{task.stepId}
			// and delete_flg = '0' and devops_type = 'update' and status = '1' and devops_task_id = #{taskUuid}
			Integer count = tDevopsTaskDetailsMapper.getNoRollBack(dto.getStepId(), taskUuid);
			if(count > 0) {
				throw new PgApplicationException("devops.task.rollback.notlast",
						messageSource.getMessage("devops.task.rollback.notlast", null, LocaleContextHolder.getLocale()));
			}
			
			// ????????????step??????
			TDevopsTaskDetailsEntity detailStep = tDevopsTaskDetailsMapper
					.selectOne(new LambdaQueryWrapper<TDevopsTaskDetailsEntity>()
							.eq(TDevopsTaskDetailsEntity::getDevopsTaskId, taskUuid)
							.eq(TDevopsTaskDetailsEntity::getDevopsStepId, dto.getStepId())
							.eq(TDevopsTaskDetailsEntity::getDeleteFlg, "0"));
			
			// ??????step?????????query,?????????????????????
			if (StringUtils.equals(detailStep.getDevopsType(), StepType.QUERY.value())) {
				// ?????????????????????????????????
				throw new PgApplicationException("devops.task.rollback.notAllow",
						messageSource.getMessage("devops.task.rollback.notAllow", null, LocaleContextHolder.getLocale()));
			}
			
			// ?????????????????????
			if (StringUtils.equals(detailStep.getStatus(), Constants.EXEC_STATUS_2)) {
				// ???????????????????????????
				throw new PgApplicationException("devops.task.rollback.repeat",
						messageSource.getMessage("devops.task.rollback.repeat", null, LocaleContextHolder.getLocale()));
			}
			
			String rollbackSql = detailStep.getDevopsRollbackSql();
			
			List<DevopsToolsRollbackSql> rollbakcSqlList = JSON.parseArray(rollbackSql, DevopsToolsRollbackSql.class);
			
			String backupdataStr = detailStep.getDevopsBackUpData();
			List<DevopsToolsBackupData> backupdataList = JSON.parseArray(backupdataStr, DevopsToolsBackupData.class);
			Map<String, DevopsToolsBackupData> backupdataMap = new HashMap<>();
			
			if (backupdataList.size() > 0) {
				
				backupdataList.stream().forEach(o -> {
					backupdataMap.put(o.getUnionKey(), o);
					
				});
			}
			List<Map<String, Object>> backDataList = null;
			StringBuilder sb = null;
			StringBuilder values = null;
			Map<String, Object> paramMap = null;
			List<String> paresdSqlList = new ArrayList<>();
			if (rollbakcSqlList.size() > 0) {
				for (DevopsToolsRollbackSql rollback : rollbakcSqlList) {
					
					// ?????????sql
					String rollbackSqlStr = rollback.getRollbackSql();
					// ?????????????????????
					String type = devopsUtils.getSqlType(rollbackSqlStr);
					
					// ??????????????????
					DevopsToolsBackupData backData = backupdataMap.get(rollback.getUnionKey());
					backDataList = backData.getData();
					if (rollback.isAutoGen()) {
						
						if (StringUtils.equals(SqlType.UPDATE.getValue(), type)) {
							// ?????????update tablename set ${@sets@} where xxx
							rollbackSqlStr = rollbackSqlStr.replace("${@sets@}", "${sets}").replace("${@id@}", "${id}");
							for (Map<String, Object> map : backDataList) {
								sb = new StringBuilder();
								paramMap = new HashMap<>();
								for (Entry<String, Object> entry : map.entrySet()) {
									
									sb.append(entry.getKey());
									sb.append(" = ");
									// ????????????
									if(TimeStampKeyMap.map.containsKey(entry.getKey()) && entry.getValue() != null) {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
										String timeStampStr = String.valueOf(entry.getValue());
										String timeStamp = sdf.format(new Date(Long.parseLong(timeStampStr))); // ?????????????????????
										sb.append(" '");
										sb.append(timeStamp);
										sb.append("' ");
									} else  if (entry.getValue() instanceof Integer) {
										sb.append(entry.getValue());
									}  else {
										sb.append(" '");
										sb.append(entry.getValue());
										sb.append("' ");
									}
									sb.append(" ,");
								}
								
								// ??????????????????and
								sb.delete(sb.length() - 1, sb.length());
								// ????????????
								paramMap.put("sets", sb.toString());
								// ????????????
								// ????????????id??????????????? id???????????????????????????
								if(!map.containsKey("id") || map.get("id") == null || StringUtils.isEmpty(String.valueOf(map.get("id")))) {
									
									throw new PgApplicationException("devops.task.rollback.notfoundid",
											messageSource.getMessage("devops.task.rollback.notfoundid", null, LocaleContextHolder.getLocale()));
								}
								paramMap.put("id", map.get("id"));
								// ????????????
								paresdSqlList.add(SqlParse.getParseSql(rollbackSqlStr, paramMap));
							}
						} else if (StringUtils.equals(SqlType.INSERT.getValue(), type)) {
							rollbackSqlStr = rollbackSqlStr.replace("${@keys@}", "${keys}").replace("${@values@}",
									"${values}");
							// ?????????update tablename set ${@sets@} where xxx
							for (Map<String, Object> map : backDataList) {
								sb = new StringBuilder();
								values = new StringBuilder();
								paramMap = new HashMap<>();
								for (Entry<String, Object> entry : map.entrySet()) {
									
									sb.append(entry.getKey());
									sb.append(",");
									// ????????????
									if(TimeStampKeyMap.map.containsKey(entry.getKey()) && entry.getValue() != null) {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
										String timeStampStr = String.valueOf(entry.getValue());
										String timeStamp = sdf.format(new Date(Long.parseLong(timeStampStr))); // ?????????????????????
										values.append(" '");
										values.append(timeStamp);
										values.append("' ");
									} else  if (entry.getValue() instanceof Integer) {
										values.append(entry.getValue());
									} else {
										values.append(" '");
										values.append(entry.getValue());
										values.append("' ");
									}
									values.append(",");
								}
								
								// ??????????????????and
								sb.delete(sb.length() - 1, sb.length());
								values.delete(values.length() - 1, values.length());
								paramMap.put("keys", sb.toString());
								paramMap.put("values", values.toString());
								paresdSqlList.add(SqlParse.getParseSql(rollbackSqlStr, paramMap));
							}
						} else if (StringUtils.equals(SqlType.DELETE.getValue(), type)) {
							for (Map<String, Object> map : backDataList) {
								paramMap = new HashMap<>();
								String uuid = map.get("uuid").toString();
								paramMap.put("uuid", uuid);
								paresdSqlList.add(SqlParse.getParseSql(rollbackSqlStr, paramMap));
							}
							
						}
					} else {
						for (Map<String, Object> map : backDataList) {
							paresdSqlList.add(SqlParse.getParseSql(rollbackSqlStr.replace("#{", "${"), map));
						}
						
					}
				}
				if (paresdSqlList.size() > 0) {
					boolean resflg = false;
					try {
						resflg = tDevopsTaskMapper.execRollback(paresdSqlList);
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
						throw new PgApplicationException(e.getMessage());
					}
					if (resflg) {
						
						// ?????????????????? ??????
						detailStep.setStatus(Constants.EXEC_STATUS_2);
						UserInfo user = GetUserInfo.getUserInfo();
						detailStep.setUpdateUserId(user.getStaffId());
						detailStep.setUpdateDateTime(new Date());
						detailStep.setUpdateKey(detailStep.getUpdateKey() + 1);
						
						tDevopsTaskDetailsMapper.updateById(detailStep);
						
						// ??????????????????
						TDevopsLogDetailsEntity logDetails = new TDevopsLogDetailsEntity();
						logDetails.setDevopsTaskId(taskUuid);
						logDetails.setDeptdesc(detailStep.getDeptdesc());
						logDetails.setDevopsStep(detailStep.getDevopsStep());
						// ??????????????????????????????rollback
						logDetails.setDevopsType("rollback");
						logDetails.setCreateUserId(user.getStaffId());
						logDetails.setUpdateUserId(user.getStaffId());
						Date nowTime = new Date();
						logDetails.setCreateDateTime(nowTime);
						logDetails.setUpdateDateTime(nowTime);
						logDetails.setUuid(UUID.randomUUID().toString());
						logDetails.setDeleteFlg("0");
						// ?????????sql
						logDetails.setDevopsExecSql(JSON.toJSONString(paresdSqlList));
						logDetails.setUpdateKey(1);
						logDetailsMapper.insert(logDetails);
						return true;
					}
					
				}
				
			}
		} catch(Exception e) {
			log.error("???????????????{}",e.getMessage());
			throw new PgApplicationException(e.getMessage());
		}

		return false;
	}

	@Override
	public Map<String, Object> getTaskById(Long id) {

		Map<String, Object> result = new HashMap<>();

		TDevopsTaskEntity task = tDevopsTaskMapper.selectOne(new LambdaQueryWrapper<TDevopsTaskEntity>()
				.eq(TDevopsTaskEntity::getDeleteFlg, "0").eq(TDevopsTaskEntity::getId, id));
		if (task == null) {
			throw new PgApplicationException("devops.task.notFound",
					messageSource.getMessage("devops.task.notFound", null, LocaleContextHolder.getLocale()));
		}
		String taskUuid = task.getUuid();
		// ??????xml
		byte[] b = task.getTemplateXml();
		String xml = new String(b);
		Devops devops = JaxbUtil.converyToJavaBean(xml, Devops.class);

		// ????????????
		List<TDevopsTaskDetailsEntity> detailList = tDevopsTaskDetailsMapper
				.selectList(new LambdaQueryWrapper<TDevopsTaskDetailsEntity>()
						.eq(TDevopsTaskDetailsEntity::getDevopsTaskId, taskUuid)
						.eq(TDevopsTaskDetailsEntity::getDeleteFlg, "0"));

		// ???????????????????????????
		Map<String, Map<String, Object>> execHistory = new HashMap<>();
		Map<String, Object> paramMap = null;
		if (detailList != null && detailList.size() > 0) {

			for (TDevopsTaskDetailsEntity detail : detailList) {
				paramMap = new HashMap<>();
				// ????????????
				paramMap.put("status", detail.getStatus());
				// ???????????????????????????
				if (!StringUtils.equals(detail.getStatus(), Constants.EXEC_STATUS_0)) {
					if (StringUtils.isNotEmpty(detail.getExecParams())) {

						paramMap.put("execParam", JSONObject.parseObject(detail.getExecParams(), Map.class));
					}
					// ????????????map,????????????
					if (StringUtils.isNotEmpty(detail.getDevopsExecData())) {
						paramMap.put("execData", JSONObject.parseObject(detail.getDevopsExecData(),
								new TypeReference<LinkedHashMap<String, Object>>() {
								}, Feature.OrderedField));
					}
				}
				execHistory.put(String.valueOf(detail.getDevopsStepId()), paramMap);
			}
		}

		// ????????????
		result.put("name", task.getDevopsName());
		// ????????????
		result.put("desc", task.getDeptdesc());
		// xml????????????
		result.put("devops", devops);
		// ?????????????????????
		result.put("execHis", execHistory);

		return result;
	}

	/**
	 * ???????????? ??????????????????????????????????????????????????????????????????
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteTaskById(Long id) {

		Boolean flg = false;
		// ?????????task
		TDevopsTaskEntity task = this.getOne(new LambdaQueryWrapper<TDevopsTaskEntity>()
				.eq(TDevopsTaskEntity::getId, id).eq(TDevopsTaskEntity::getDeleteFlg, "0"));

		if (task != null) {

			String taskUuid = task.getUuid();
			// ??????????????????step???update???????????????????????????0?????????
			Integer count = tDevopsTaskDetailsMapper.selectCount(new LambdaQueryWrapper<TDevopsTaskDetailsEntity>()
					.eq(TDevopsTaskDetailsEntity::getDevopsTaskId, taskUuid)
					.eq(TDevopsTaskDetailsEntity::getDeleteFlg, "0")
					.eq(TDevopsTaskDetailsEntity::getDevopsType, StepType.UPDATE.value())
					.ne(TDevopsTaskDetailsEntity::getStatus, Constants.EXEC_STATUS_0));

			if (count > 0) {
				log.error("??????????????????????????????????????????{}",id);
				// ???????????????????????????????????????
				throw new PgApplicationException("devops.task.delete.notAllow",
						messageSource.getMessage("devops.task.delete.notAllow", null, LocaleContextHolder.getLocale()));
			}

			// ??????????????????????????????
			UserInfo user = GetUserInfo.getUserInfo();

			task.setDeleteFlg("1");
			task.setUpdateDateTime(new Date());
			task.setUpdateUserId(user.getStaffId());
			List<TDevopsTaskDetailsEntity> detailList = tDevopsTaskDetailsMapper
					.selectList(new LambdaQueryWrapper<TDevopsTaskDetailsEntity>()
							.eq(TDevopsTaskDetailsEntity::getDevopsTaskId, taskUuid)
							.eq(TDevopsTaskDetailsEntity::getDeleteFlg, "0"));

			// ??????
			this.updateById(task);

			for (TDevopsTaskDetailsEntity entity : detailList) {

				entity.setDeleteFlg("1");
				entity.setUpdateDateTime(new Date());
				entity.setUpdateUserId(user.getStaffId());
				tDevopsTaskDetailsMapper.updateById(entity);
			}
			flg = true;
		} else {
			log.error("?????????????????????????????????{}",id);
			// ??????????????????
			throw new PgApplicationException("devops.task.notFound",
					messageSource.getMessage("devops.task.notFound", null, LocaleContextHolder.getLocale()));
		}

		return flg;
	}
}
