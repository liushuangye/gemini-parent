package com.gemini.toolkit.sysparam.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.gemini.toolkit.common.exception.PgInputCheckException;
import com.gemini.toolkit.common.utils.CheckParamUtil;
import com.gemini.toolkit.common.utils.CommonConsts;
import com.gemini.toolkit.common.utils.ExcelUtil;
import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.sysparam.entity.TStaModuleEntity;
import com.gemini.toolkit.sysparam.mapper.TCodeMapper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gemini.toolkit.sysparam.entity.TCodeEntity;
import com.gemini.toolkit.sysparam.form.CodeForm;
import com.gemini.toolkit.sysparam.service.TCodeService;
import com.gemini.toolkit.sysparam.service.TStaModuleService;

/**
 * <p>
 * 系统配置 服务实现类
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */
@Service
public class TCodeServiceImpl extends ServiceImpl<TCodeMapper, TCodeEntity> implements TCodeService {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private TStaModuleService tStaModuleService;

	/**
	 * 获取系统配置
	 * 
	 * @param String
	 * @return
	 */
	public R getTCode(CodeForm codeForm, String sysName) {
		String busId = codeForm.getBusId();
		Integer currentPage = codeForm.getCurrentPage();
		Integer pagesize = codeForm.getPagesize();
		if (currentPage == null || pagesize == null) {
			throw new PgInputCheckException("sysparam.page.error",
					messageSource.getMessage("sysparam.page.error", null, LocaleContextHolder.getLocale()));
		}

		Page<TCodeEntity> page = new Page<TCodeEntity>(codeForm.getCurrentPage(), codeForm.getPagesize());
		Page<TCodeEntity> tCodeEntityList = null;
		if (CommonConsts.ALLTEXT.equals(busId)) {
			String busIdList = tStaModuleService.getTreeCodeString(sysName);
			tCodeEntityList = baseMapper.getTCodeAll(page, busIdList);
		} else {
			tCodeEntityList = baseMapper.getTCode(page, busId);
		}

		return R.ok().put("data", tCodeEntityList);
	}

	/**
	 * 保存系统配置
	 * 
	 * @param List<TCodeEntity>
	 * @param String
	 * @return
	 */
	public R save(List<TCodeEntity> tCodeEntityList, String staffId) {

		for (TCodeEntity tCodeEntity : tCodeEntityList) {
			check(tCodeEntity, staffId);
		}
		saveOrUpdateBatch(tCodeEntityList);
		return R.ok();
	}

	/**
	 * 删除系统配置
	 * 
	 * @param TCodeEntity
	 * @param String
	 * @return R
	 */
	public R delete(TCodeEntity tCodeEntity, String staffId) {

		check(tCodeEntity, staffId);
		updateById(tCodeEntity);

		return R.ok();
	}

	/**
	 * 系统配置下载excel
	 * 
	 * @param response
	 * @param busId
	 * @return
	 */
	public void tCodeOutPutExcel(HttpServletResponse response, String busId, String sysName) {

		// 打开工作簿
		HSSFWorkbook workBook = ExcelUtil.openExcelTemplate("excelTemplate/sysparamTemplate.xls");
		// 获取要出力的sheet
		HSSFSheet sheet = workBook.getSheetAt(0);

		String busName = null;
		// 获取要输出的数据
		List<TCodeEntity> tCodeList = null;
		if (CommonConsts.ALLTEXT.equals(busId)) {
			String busIdList = tStaModuleService.getTreeCodeString(sysName);
			tCodeList = baseMapper.getTCodeAll(busIdList);
			busName = CommonConsts.ALLTEXT;
		} else {
			tCodeList = baseMapper.getTCode(busId);
			TStaModuleEntity tStaModuleEntity = tStaModuleService
					.getOne(new LambdaQueryWrapper<TStaModuleEntity>().eq(TStaModuleEntity::getBusId, busId));
			busName = tStaModuleEntity.getBusName();
		}

		// 设置单元格样式
		HSSFCellStyle textStyle = workBook.createCellStyle();
		HSSFDataFormat format = workBook.createDataFormat();
		// 自动换行
		textStyle.setWrapText(true);
		// 单元格为文字列
		textStyle.setDataFormat(format.getFormat("@"));
		HSSFCell cell = null;
		// 写入数据
		for (int i = 0; i < tCodeList.size(); i++) {
			// 创建表格行
			HSSFRow row = sheet.createRow(i + 1);
			// 数据种类
			cell = row.createCell(0);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeType());
			// 数据编号
			cell = row.createCell(1);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeId());
			// 数据名称
			cell = row.createCell(2);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeName());
			// 数据简称
			cell = row.createCell(3);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeRnm());
			// 值１
			cell = row.createCell(4);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeValue1());
			// 值2
			cell = row.createCell(5);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeValue2());
			// 值3
			cell = row.createCell(6);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeValue3());
			// 值4
			cell = row.createCell(7);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeValue4());
			// 值5
			cell = row.createCell(8);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeValue5());
			// 字典项目备注
			cell = row.createCell(9);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getCodeRemark());
			// 备用字段1
			cell = row.createCell(10);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation1());
			// 备用字段2
			cell = row.createCell(11);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation2());
			// 备用字段3
			cell = row.createCell(12);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation3());
			// 备用字段4
			cell = row.createCell(13);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation4());
			// 备用字段5
			cell = row.createCell(14);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation5());
			// 备用字段6
			cell = row.createCell(15);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation6());
			// 备用字段7
			cell = row.createCell(16);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation7());
			// 备用字段8
			cell = row.createCell(17);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation8());
			// 备用字段9
			cell = row.createCell(18);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation9());
			// 备用字段10
			cell = row.createCell(19);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getPreparation10());
			// 排序码
			cell = row.createCell(20);
			cell.setCellStyle(textStyle);
			if (tCodeList.get(i).getDisplayOrder() != null) {
				cell.setCellValue(Long.toString(tCodeList.get(i).getDisplayOrder()));
			} else {
				cell.setCellValue("");
			}
			// 业务编码
			cell = row.createCell(21);
			cell.setCellStyle(textStyle);
			cell.setCellValue(tCodeList.get(i).getBusId());
		}
		// 获取byte数组
		byte[] res = ExcelUtil.workbook2ByteArray(workBook);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("content-Type", "application/vnd.ms-excel");
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String filename = busName + "_" + df.format(new Date()) + ".xls";
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");
			OutputStream outputStream;
			outputStream = response.getOutputStream();
			outputStream.write(res);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PgInputCheckException("sysparam.code.downloadError",
					messageSource.getMessage("sysparam.code.downloadError", null, LocaleContextHolder.getLocale()));
		}
	}

	/**
	 * 系统配置导入
	 * 
	 * @param request
	 * @return
	 */
	public R importXLS(MultipartHttpServletRequest request, String staffId) {
		MultipartFile multipartFile = request.getFile("file");
		String fileName = request.getParameter("fileName");

		if (fileName == null || !fileName.endsWith(CommonConsts.XLS)) {
			throw new PgInputCheckException("sysparam.file.typeError",
					messageSource.getMessage("sysparam.file.typeError", null, LocaleContextHolder.getLocale()));
		}
		try {
			InputStream fileInput = multipartFile.getInputStream();
			HSSFWorkbook wookbook = new HSSFWorkbook(fileInput);
			// 获取sheet行数
			HSSFSheet vSheet = wookbook.getSheetAt(0);
			int vRows = vSheet.getLastRowNum();
			// 查询tcode中数据
			List<TCodeEntity> tCodeSaveList = new ArrayList<TCodeEntity>();
			List<TCodeEntity> tCodeUpdateList = new ArrayList<TCodeEntity>();

			Map<String, TCodeEntity> tCodeAllMap = new HashMap<String, TCodeEntity>();
			List<TCodeEntity> tCodeAll = list(new LambdaQueryWrapper<TCodeEntity>()
					.eq(TCodeEntity::getCodeType, "SYS_PARA").eq(TCodeEntity::getDeleteFlg, "0"));
			for (TCodeEntity tCode : tCodeAll) {
				tCodeAllMap.put(tCode.getCodeId(), tCode);
			}
			// containsKey

			// 遍历sheet
			for (int i = 0; i < vRows; i++) {
				Row row = vSheet.getRow(i + 1);
				TCodeEntity tCodeEntity = new TCodeEntity();
				// 数据种类
				tCodeEntity.setCodeType(ExcelUtil.getcell(row.getCell(0)));
				// 数据编号
				tCodeEntity.setCodeId(ExcelUtil.getcell(row.getCell(1)));
				// 数据名称
				tCodeEntity.setCodeName(ExcelUtil.getcell(row.getCell(2)));
				// 数据简称
				tCodeEntity.setCodeRnm(ExcelUtil.getcell(row.getCell(3)));
				// 值１
				tCodeEntity.setCodeValue1(ExcelUtil.getcell(row.getCell(4)));
				// 值2
				tCodeEntity.setCodeValue2(ExcelUtil.getcell(row.getCell(5)));
				// 值3
				tCodeEntity.setCodeValue3(ExcelUtil.getcell(row.getCell(6)));
				// 值4
				tCodeEntity.setCodeValue4(ExcelUtil.getcell(row.getCell(7)));
				// 值5
				tCodeEntity.setCodeValue5(ExcelUtil.getcell(row.getCell(8)));
				// 字典项目备注
				tCodeEntity.setCodeRemark(ExcelUtil.getcell(row.getCell(9)));
				// 备用字段1
				tCodeEntity.setPreparation1(ExcelUtil.getcell(row.getCell(10)));
				// 备用字段2
				tCodeEntity.setPreparation2(ExcelUtil.getcell(row.getCell(11)));
				// 备用字段3
				tCodeEntity.setPreparation3(ExcelUtil.getcell(row.getCell(12)));
				// 备用字段4
				tCodeEntity.setPreparation4(ExcelUtil.getcell(row.getCell(13)));
				// 备用字段5
				tCodeEntity.setPreparation5(ExcelUtil.getcell(row.getCell(14)));
				// 备用字段6
				tCodeEntity.setPreparation6(ExcelUtil.getcell(row.getCell(15)));
				// 备用字段7
				tCodeEntity.setPreparation7(ExcelUtil.getcell(row.getCell(16)));
				// 备用字段8
				tCodeEntity.setPreparation8(ExcelUtil.getcell(row.getCell(17)));
				// 备用字段9
				tCodeEntity.setPreparation9(ExcelUtil.getcell(row.getCell(18)));
				// 备用字段10
				tCodeEntity.setPreparation10(ExcelUtil.getcell(row.getCell(19)));
				if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(row.getCell(20)))) {
					// 排序码
					try {
						tCodeEntity.setDisplayOrder(Long.valueOf(ExcelUtil.getcell(row.getCell(20)).trim()));
					} catch (NumberFormatException e) {
						e.printStackTrace();
						throw new PgInputCheckException("sysparam.code.displaycode.NumberFormatError", messageSource
								.getMessage("sysparam.code.displaycode.NumberFormatError", null, LocaleContextHolder.getLocale()));
					}
				}
				// 业务编码
				tCodeEntity.setBusId(ExcelUtil.getcell(row.getCell(21)));
				// 查询数据库中该数据是否已存在
				if (tCodeAllMap.containsKey(tCodeEntity.getCodeId())) {
					TCodeEntity tmp = tCodeAllMap.get(tCodeEntity.getCodeId());
					// 排他键
					tCodeEntity.setUpdateKey(tmp.getUpdateKey() + 1);
					// id
					tCodeEntity.setId(tmp.getId());
					// 更新时间
					tCodeEntity.setUpdateDateTime(new Date());
					// 更新用户ID
					tCodeEntity.setUpdateUserId(staffId);
					tCodeUpdateList.add(tCodeEntity);
				} else {
					// 排他键
					tCodeEntity.setUpdateKey(1);
					// 删除标志
					tCodeEntity.setDeleteFlg("0");
					// 作成时间
					tCodeEntity.setCreateDateTime(new Date());
					// 作成用户ID
					tCodeEntity.setCreateUserId(staffId);
					// 更新时间
					tCodeEntity.setUpdateDateTime(new Date());
					// 更新用户ID
					tCodeEntity.setUpdateUserId(staffId);
					// UUID
					tCodeEntity.setUuid(UUID.randomUUID().toString());
					tCodeSaveList.add(tCodeEntity);
				}
				R r = CheckParamUtil.checkTCode(tCodeEntity);
				if ((int) r.get("code") == 1) {
					String[] tmp = { (String) r.get("msg") };
					throw new PgInputCheckException("sysparam.common.notEmpty",
							messageSource.getMessage("sysparam.common.notEmpty", tmp, LocaleContextHolder.getLocale()));
				}
				if ((int) r.get("code") == 2) {
					String[] tmp = { (String) r.get("msg") };
					throw new PgInputCheckException("sysparam.common.lengthMaxError",
							messageSource.getMessage("sysparam.common.lengthMaxError", tmp, LocaleContextHolder.getLocale()));
				}
			}
			saveBatch(tCodeSaveList);
			updateBatchById(tCodeUpdateList);
			// 关闭文件
			wookbook.close();

		} catch(PgInputCheckException e) {
			e.printStackTrace();
			throw e;
			

		} catch (Exception e) {
			e.printStackTrace();
			throw new PgInputCheckException("sysparam.code.uploadError",
					messageSource.getMessage("sysparam.code.uploadError", null, LocaleContextHolder.getLocale()));
		}

		return R.ok();
	}

	/**
	 * check
	 */
	private void check(TCodeEntity tCodeEntity, String staffId) {
		R r = CheckParamUtil.checkTCode(tCodeEntity);
		if ((int) r.get("code") == 1) {
			String[] tmp = { (String) r.get("msg") };
			throw new PgInputCheckException("sysparam.common.notEmpty",
					messageSource.getMessage("sysparam.common.notEmpty", tmp, LocaleContextHolder.getLocale()));
		}
		if ((int) r.get("code") == 2) {
			String[] tmp = { (String) r.get("msg") };
			throw new PgInputCheckException("sysparam.common.lengthMaxError",
					messageSource.getMessage("sysparam.common.lengthMaxError", tmp, LocaleContextHolder.getLocale()));
		}
		TCodeEntity code = getOne(
				new LambdaQueryWrapper<TCodeEntity>().eq(TCodeEntity::getUuid, tCodeEntity.getUuid()));
		if (code.getUpdateKey() != tCodeEntity.getUpdateKey()) {
			throw new PgInputCheckException("sysparam.code.updateError",
					messageSource.getMessage("sysparam.code.updateError", null, LocaleContextHolder.getLocale()));
		}
		tCodeEntity.setUpdateDateTime(new Date());
		tCodeEntity.setUpdateUserId(staffId);
		tCodeEntity.setUpdateKey(tCodeEntity.getUpdateKey() + 1);
	}

}
