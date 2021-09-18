package com.gemini.toolkit.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ClassPathResource;

import com.gemini.toolkit.common.exception.PgApplicationException;
import com.gemini.toolkit.common.exception.PgInputCheckException;

import lombok.extern.slf4j.Slf4j;

/**
 * excel文件下载
 * 
 * @author
 *
 */
@Slf4j
public class ExcelUtil {
	
	/**
	 * 文件转byte数组
	 * @param workbook
	 * @return
	 */
	public static byte[] workbook2ByteArray(Workbook workbook) {
		byte[] res = null;
		// byte流
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			workbook.write(bao);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new PgApplicationException("IOException","workbook转byte数组失败");
		}
		res = bao.toByteArray();
		try {
			bao.close();
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new PgApplicationException("IOException","关闭ByteArrayOutputStream失败");
		}
		return res;
	}
	
	/**
	 * 打开exel模板文件
	 * 
	 * @param tempFileName
	 * @return
	 */
	public static HSSFWorkbook openExcelTemplate(String tempFileName) {
		
		ClassPathResource resource = new ClassPathResource(tempFileName);
		
		// 新建HSSFWorkbook
		HSSFWorkbook workbook = null;
		try {
			InputStream in = resource.getInputStream();
			workbook = new HSSFWorkbook(in);
		} catch (IOException e) {
			throw new PgInputCheckException("IOException","创建HSSFWorkbook失败");
		}

		return workbook;
	}

	/**
	 * 获取单元格值
	 * 
	 * @param String
	 * @return
	 */
	public static String getcell(Cell cell) {
		if (cell == null || cell.toString().trim().equals("")) {
			return null;
		}
		String strCell = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				String strDate = format.format(cell.getDateCellValue());
				strCell = String.valueOf(strDate);
			} else {
				DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
				strCell = decimalFormat.format(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			strCell = cell.getCellFormula();
			break;
		}
		return strCell;
	};
	
	/**
	 * 获取日期格式单元格值
	 * 
	 * @param String
	 * @return
	 */
	public static String getDateCell(Cell cell, String format) {
		if (cell == null || cell.toString().trim().equals("")) {
			return null;
		}
		String strCell = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {

				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
					String strDate = simpleDateFormat.format(cell.getDateCellValue());
					strCell = String.valueOf(strDate);
				} catch (Exception e) {
					e.printStackTrace();
					throw new PgApplicationException("basedata.date.NumberFormatError", "日期格式不正确，请修改后再进行导入");

				}
			}
			break;
		}
		return strCell;
	};
	
	/**
	 * 复制行
	 * 
	 * @param startRow 开始行
	 * @param endRow 结束行
	 * @param pPosition 目标行
	 * @param sheet 工作表对象
	 * @param sheet 是否复制值
	 */
	public static void copyRows(int startRow, int endRow, int pPosition, HSSFSheet sheet) {
		int pStartRow = startRow;
		int pEndRow = endRow;
		int targetRowFrom;
		int targetRowTo;
		int columnCount;
		CellRangeAddress region = null;
		int i;
		int j;
		if (pStartRow == -1 || pEndRow == -1) {
			return;
		}
		// 拷贝合并的单元格
		for (i = 0; i < sheet.getNumMergedRegions(); i++) {
			region = sheet.getMergedRegion(i);
			if ((region.getFirstRow() >= pStartRow) && (region.getLastRow() <= pEndRow)) {
				targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
				targetRowTo = region.getLastRow() - pStartRow + pPosition;
				CellRangeAddress newRegion = region.copy();
				newRegion.setFirstRow(targetRowFrom);
				newRegion.setFirstColumn(region.getFirstColumn());
				newRegion.setLastRow(targetRowTo);
				newRegion.setLastColumn(region.getLastColumn());
				sheet.addMergedRegion(newRegion);
			}
		}
		// 设置列宽
		for (i = pStartRow; i <= pEndRow; i++) {
			HSSFRow sourceRow = sheet.getRow(i);
			if(sourceRow==null) {
				 sourceRow = sheet.createRow(i);
			}
			columnCount = sourceRow.getLastCellNum();
			if (sourceRow != null) {
				HSSFRow newRow = sheet.createRow(pPosition - pStartRow + i);
				newRow.setHeight(sourceRow.getHeight());
				for (j = 0; j < columnCount; j++) {
					HSSFCell templateCell = sourceRow.getCell(j);
					if (templateCell != null) {
						HSSFCell newCell = newRow.createCell(j);
						copyCell(templateCell, newCell);
					}
				}
			}
		}
	}
	
	/**
	 * 复制单元格
	 * 
	 * @param srcCell 原始单元格
	 * @param distCell 目标单元格
	 */
	public static void copyCell(HSSFCell srcCell, HSSFCell distCell) {
		distCell.setCellStyle(srcCell.getCellStyle());
		if (srcCell.getCellComment() != null) {
			distCell.setCellComment(srcCell.getCellComment());
		}
		int srcCellType = srcCell.getCellType();
		distCell.setCellType(srcCellType);

		if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
				distCell.setCellValue(srcCell.getDateCellValue());
			} else {
				distCell.setCellValue(srcCell.getNumericCellValue());
			}
		} else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
			distCell.setCellValue(srcCell.getRichStringCellValue());
		} else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {
			// nothing21
		} else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
			distCell.setCellValue(srcCell.getBooleanCellValue());
		} else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
			distCell.setCellErrorValue(srcCell.getErrorCellValue());
		} else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
			distCell.setCellFormula(srcCell.getCellFormula());
		} else { // nothing29

		}
	}
	
    // 生成字符串的MD5值,用户密码加密
    public final static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] strTemp = s.getBytes("utf-8");
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 清除单元格内容
     * 
     * @param cell
     */
	public static void clearCell(Cell cell) {
		if (cell == null || cell.toString().trim().equals("")) {

		} else {
			cell.setCellComment(null);
			cell.setCellStyle(null);
			cell.setCellValue("");
			cell.setCellFormula(null);
		}
	}
    
    /**
     * 大写转换小写
     * 
     * @param stringIn 文字列
     * @return 処理後文字列
     */
    public static String UpperString(String stringIn) {
        try {
            if (stringIn == null) {
                return "";
            }
            return stringIn.toUpperCase();
        } catch (Exception ex) {
            return "";
        }
    }

	/**
	 * 
	 * @param offset 偏移量，如果给0，表示从A列开始，1，就是从B列
	 * @param rowId 第几行
	 * @param colCount 一共多少列
	 * @return 如果给入参 1,1,10. 表示从B1-K1。最终返回 $B$1:$K$1
	 * 
	 * @author denggonghai 2016年8月31日 下午5:17:49
	 */
	public static String getRange(int offset, int satartRow, int rowId, int colCount) {
		char start = (char)('A' + offset);
		if (colCount <= 25) {
			char end = (char)(start + colCount - 1);
			return "$" + start + "$" + satartRow + ":$:" + end + "$" + rowId;
		} else {
			char endPrefix = 'A';
			char endSuffix = 'A';
			if ((colCount - 25) / 26 == 0 || colCount == 51) {// 26-51之间，包括边界（仅两次字母表计算）
				if ((colCount - 25) % 26 == 0) {// 边界值
					endSuffix = (char)('A' + 25);
				} else {
					endSuffix = (char)('A' + (colCount - 25) % 26 - 1);
				}
			} else {// 51以上
				if ((colCount - 25) % 26 == 0) {
					endSuffix = (char)('A' + 25);
					endPrefix = (char)(endPrefix + (colCount - 25) / 26 - 1);
				} else {
					endSuffix = (char)('A' + (colCount - 25) % 26 - 1);
					endPrefix = (char)(endPrefix + (colCount - 25) / 26);
				}
			}
			return "$" + start + "$" + rowId + ":$:" + endPrefix + endSuffix + "$" + rowId;
		}
	}
}
