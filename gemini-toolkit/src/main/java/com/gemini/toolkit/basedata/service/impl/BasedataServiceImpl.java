package com.gemini.toolkit.basedata.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.gemini.toolkit.basedata.entity.TBasedataImportHisEntity;
import com.gemini.toolkit.basedata.entity.TBasedataTempDownloadHisEntity;
import com.gemini.toolkit.basedata.mapper.CustomizeMapper;
import com.gemini.toolkit.basedata.mapper.TBasedataTempDownloadHisMapper;
import com.gemini.toolkit.basedata.service.*;
import com.gemini.toolkit.common.exception.PgApplicationException;
import com.gemini.toolkit.common.exception.PgInputCheckException;
import com.gemini.toolkit.login.form.UserInfo;
import com.gemini.toolkit.basedata.service.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gemini.toolkit.common.utils.CheckParamUtil;
import com.gemini.toolkit.common.utils.CommonConsts;
import com.gemini.toolkit.common.utils.ExcelUtil;
import com.gemini.toolkit.common.utils.GetUserInfo;
import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.sysparam.entity.TCodeEntity;
import com.gemini.toolkit.sysparam.mapper.TCodeMapper;

/**
 * <p>
 * 基础数据导入履历 服务实现类
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */
/**
 * @author lijx2019
 *
 */
@Service
public class BasedataServiceImpl implements BasedataService {

	@Autowired
    CustomizeMapper customizeMapper;

	@Autowired
	TBasedataImportHisService tBasedataImportHisService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
    TBasedataTempDownloadHisMapper tBasedataTempDownloadHisMapper;

	@Autowired
	TCodeMapper tCodeMapper;

	@Autowired
	BaseDataDownService downService;

	@Autowired
	OrganizeStaffService organizeStaffService;

	@Autowired
    CompanyStaffService companyStaffService;

	@Autowired
	EthicsStaffService ethicsStaffService;

	@Autowired
	ProfessionalInfoService professionalInfoService;

	@Value("${db.type}")
	String dbType;


	@Autowired
	StandardService standardService;
	@Autowired
    AttendMeetingService attendMeetingService;
	@Autowired
	AchievementAwardService achievementAwardService;
	@Autowired
	AppraisalResultsService appraisalResultsService;
	@Autowired
	PaperSubmissionService paperSubmissionService;
	@Autowired
    SoftwareCopyrightLZYService softwareCopyrightLZYService;
	@Autowired
	NewDrugCertificateService newDrugCertificateService;
	@Autowired
    AcademicLecturesService academicLecturesService;
	@Autowired
	AcademicPapersService academicPapersService;
	@Autowired
	AcademicWorksService academicWorksService;
	@Autowired
	ResearchReportService researchReportService;
	@Autowired
	HostConferenceService hostConferenceService;
	@Autowired
    CopyrightService copyrightService;
	@Autowired
	PatentService patentService;
	@Autowired
	PatentLZYService patentLZYService;


	/**
	 * 导入模板
	 *
	 * @param request
	 * @param staffId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public R importXLS(MultipartHttpServletRequest request) {
		MultipartFile multipartFile = request.getFile("file");
		String templateType = request.getParameter("templateType");
		String fileName = request.getParameter("fileName");
		String modelFlg = request.getParameter("modelFlg");
		String tempalteName = null;
		R r = null;
		try {
			InputStream fileInput = multipartFile.getInputStream();
			HSSFWorkbook wookbook = new HSSFWorkbook(fileInput);

			TCodeEntity tCodeEntity = tCodeMapper
					.selectOne(new LambdaQueryWrapper<TCodeEntity>().eq(TCodeEntity::getCodeId, templateType)
							.eq(TCodeEntity::getCodeType, "TEMPLATE_TYPE").eq(TCodeEntity::getDeleteFlg, "0"));
			// 获取模板名称
			tempalteName = tCodeEntity.getCodeValue1();
			// 获取模板类型名称
			String templateTypeName = tCodeEntity.getCodeName();

			String dbImportType = dbType;
			// 不同模板调用不同实现类进行数据导入
			if (CommonConsts.ORGANIZERS_STAFF.equals(templateType)) {
				r = organizeStaffService.importExecl(wookbook, templateType, tempalteName, templateTypeName, modelFlg,
						fileName,dbImportType);
			} else if (CommonConsts.COMPANY_STAFF.equals(templateType)) {
				r = companyStaffService.importExecl(wookbook, templateType, tempalteName, templateTypeName, modelFlg,
						fileName,dbImportType);
			} else if (CommonConsts.ETHICS_STAFF.equals(templateType)) {
				r = ethicsStaffService.importExecl(wookbook, templateType, tempalteName, templateTypeName, modelFlg,
						fileName,dbImportType);
			} else if (CommonConsts.PROFESSIONAL_INFO.equals(templateType)) {
				r = professionalInfoService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			} else if (CommonConsts.STANDARD.equals(templateType)){
				r = standardService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			} else if (CommonConsts.ATTEND_MEETING.equals(templateType)){
				r = attendMeetingService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			} else if (CommonConsts.ACHIEVEMENT_AWARD.equals(templateType)){
				r = achievementAwardService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			} else if (CommonConsts.APPRAISAL_RESULTS.equals(templateType)){
				r = appraisalResultsService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.PAPER_SUBMISSION.equals(templateType)){
				r = paperSubmissionService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.SOFTWARE_COPYRIGHT_LZY.equals(templateType)){
				r = softwareCopyrightLZYService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.NEW_DRUG_CERTIFICATE.equals(templateType)){
				r = newDrugCertificateService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.ACADEMIC_LECTURES.equals(templateType)){
				r = academicLecturesService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.ACADEMIC_PAPERS.equals(templateType)){
				r = academicPapersService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.ACADEMIC_WORKS.equals(templateType)){
				r = academicWorksService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.RESEARCH_REPORT.equals(templateType)){
				r = researchReportService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.HOST_CONFERENCE.equals(templateType)){
				r = hostConferenceService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.COPYRIGHT.equals(templateType)){
				r = copyrightService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.PATENT.equals(templateType)){
				r = patentService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}else if (CommonConsts.PATENT_LZY.equals(templateType)){
				r = patentLZYService.importExecl(wookbook, templateType, tempalteName, templateTypeName,
						modelFlg, fileName,dbImportType);
			}
			wookbook.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new PgApplicationException("sysparam.code.uploadError",
					messageSource.getMessage("sysparam.code.uploadError", null, LocaleContextHolder.getLocale()));
		}
		return r;
	}

	/**
	 * 组织人员excel导出
	 *
	 * @param response
	 * @return
	 */
	public void outPutXLS(HttpServletResponse response, String templateType) {

		TCodeEntity tCodeEntity = tCodeMapper
				.selectOne(new LambdaQueryWrapper<TCodeEntity>().eq(TCodeEntity::getCodeId, templateType)
						.eq(TCodeEntity::getCodeType, "TEMPLATE_TYPE").eq(TCodeEntity::getDeleteFlg, "0"));
		// 获取模板名称
		String tempalteName = tCodeEntity.getCodeValue1();
		// 获取历史下载数据
		TBasedataTempDownloadHisEntity tBasedataTempDownloadHisEntity = tBasedataTempDownloadHisMapper
				.selectOne(new LambdaQueryWrapper<TBasedataTempDownloadHisEntity>()
						.eq(TBasedataTempDownloadHisEntity::getTemplateType, templateType)
						.eq(TBasedataTempDownloadHisEntity::getTempalteName, tempalteName)
						.eq(TBasedataTempDownloadHisEntity::getDeleteFlg, "0"));

		// 打开工作簿
		HSSFWorkbook workBook = ExcelUtil.openExcelTemplate("excelTemplate/" + tempalteName + "_template.xls");

		// 遍历所有要导出数据的sheet
		List<String> sheetNameList = downService.sheetCheck(workBook);

		// 获取导出数据
		downService.downData(sheetNameList, workBook);

		// 带有人员信息得sheet
		downService.downafterData(sheetNameList, workBook);

		// 获取当前时间
		String nowDate = LocalDateTime.now().toString();
		// 设置数据版本
		workBook.getSheetAt(0).getRow(6).getCell(4).setCellValue(ExcelUtil.MD5(nowDate));

		// 获取byte数组
		byte[] res = ExcelUtil.workbook2ByteArray(workBook);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("content-Type", "application/vnd.ms-excel");
		try {
			response.addHeader("Content-Disposition", "attachment;filename=\""
					+ URLEncoder.encode(tCodeEntity.getCodeValue2(), "UTF-8").replaceAll("\\+", "%20") + "\"");
			OutputStream outputStream;
			outputStream = response.getOutputStream();
			outputStream.write(res);
			outputStream.flush();
			// 获取登录者id
			UserInfo user = GetUserInfo.getUserInfo();

			// 设置下载履历
			// 获取模板版本号
			String staticVersion = ExcelUtil.getcell(workBook.getSheetAt(0).getRow(5).getCell(4));
			Date nowtime = new Date();
			if (CheckParamUtil.isEmpty(tBasedataTempDownloadHisEntity)) {
				tBasedataTempDownloadHisEntity = new TBasedataTempDownloadHisEntity();
				tBasedataTempDownloadHisEntity.setTempalteName(tempalteName);
				tBasedataTempDownloadHisEntity.setTemplateType(templateType);
				tBasedataTempDownloadHisEntity.setCreateUserId(user.getStaffId());
				tBasedataTempDownloadHisEntity.setCreateDateTime(nowtime);
				tBasedataTempDownloadHisEntity.setUpdateKey(0);
				tBasedataTempDownloadHisEntity.setDeleteFlg("0");
				tBasedataTempDownloadHisEntity.setUuid(UUID.randomUUID().toString());
				tBasedataTempDownloadHisEntity.setStaticVersion(staticVersion);
				tBasedataTempDownloadHisEntity.setDynamicVersion(nowDate);
				tBasedataTempDownloadHisEntity.setUpdateUserId(user.getStaffId());
				tBasedataTempDownloadHisEntity.setUpdateDateTime(nowtime);
				tBasedataTempDownloadHisMapper.insert(tBasedataTempDownloadHisEntity);
			} else {
				tBasedataTempDownloadHisEntity.setStaticVersion(staticVersion);
				tBasedataTempDownloadHisEntity.setDynamicVersion(nowDate);
				tBasedataTempDownloadHisEntity.setUpdateUserId(user.getStaffId());
				tBasedataTempDownloadHisEntity.setUpdateDateTime(nowtime);
				tBasedataTempDownloadHisEntity.setUpdateKey(tBasedataTempDownloadHisEntity.getUpdateKey() + 1);
				tBasedataTempDownloadHisMapper.updateById(tBasedataTempDownloadHisEntity);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new PgInputCheckException("basedata.code.downloadError",
					messageSource.getMessage("basedata.code.downloadError", null, LocaleContextHolder.getLocale()));
		}
	}

	/**
	 * 文件下载
	 *
	 * @param response
	 * @param id
	 */
	public void outPutFile(HttpServletResponse response, Long id) {

		TBasedataImportHisEntity tBasedataImportHis = tBasedataImportHisService
				.getOne(new LambdaQueryWrapper<TBasedataImportHisEntity>().eq(TBasedataImportHisEntity::getId, id));
		// 获取byte数组
		byte[] res = tBasedataImportHis.getFileContent();
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("content-Type", "application/vnd.ms-excel");
		try {
			String filename = tBasedataImportHis.getTempalteName();
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20") + "\"");
			OutputStream outputStream;
			outputStream = response.getOutputStream();
			outputStream.write(res);
			outputStream.flush();

//			BufferedOutputStream bos = null;
//	        FileOutputStream fos = null;
//	        File file = null;
//            file = new File("D:" + File.separator + "xx.xls");
//            fos = new FileOutputStream(file);
//            bos = new BufferedOutputStream(fos);
//            bos.write(res);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PgInputCheckException("basedata.code.downloadError",
					messageSource.getMessage("basedata.code.downloadError", null, LocaleContextHolder.getLocale()));
		}
	}
}
