package com.justinmobile.tsm.system.manager.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.system.dao.MobileSectionDao;
import com.justinmobile.tsm.system.domain.MobileSection;
import com.justinmobile.tsm.system.manager.MobileSectionManager;

@Service("mobileSectionManager")
public class MobileSectionManagerImpl extends EntityManagerImpl<MobileSection, MobileSectionDao> implements MobileSectionManager {

	@Autowired
	protected MobileSectionDao mobileSectionDao;

	private final String SHEET_NAME = "号码汇总";

	private final String[] TITLES = new String[] { "省份", "城市", "城市区号", "万号段", "归属SCP号码", "SCP ID", "归属SCP名称", "彩信中心名称", "彩信中心ID", "启用局数据号" };

	public String getProvinceByMobile(String mobileNo) throws PlatformException {
		if (StringUtils.isBlank(mobileNo) || mobileNo.length() < 7) {
			return null;
		}
		try {
			String paragraph = mobileNo.substring(0, 7);
			List<MobileSection> list = mobileSectionDao.find("from " + MobileSection.class.getName() + " as ms where ms.paragraph = ?",
					paragraph);
			if (CollectionUtils.isEmpty(list)) {
				return null;
			}
			return list.get(0).getProvince();
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		}
	}

	public List<String> importExcelFile(String filePath) throws PlatformException {
		try {
			Sheet sheet = readSheet(filePath);
			return readData(sheet);
		} catch (IOException e) {
			throw new PlatformException(PlatformErrorCode.FILE_FORMAT_ERROR, e);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		}

	}

	/**
	 * 找文件，把需要的工作簿取出来
	 * 
	 * @param filePath
	 * @return
	 * @throws BiffException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Sheet readSheet(String filePath) throws IOException {
		int pointIndex = filePath.lastIndexOf(".");
		String suffixName = filePath.substring(pointIndex + 1);
		Workbook workBook = null;
		if ("xls".equals(suffixName)) {// 老版excel和新版excel使用不能的实现类
			workBook = new HSSFWorkbook(new FileInputStream(filePath));
		} else if ("xlsx".equals(suffixName)) {
			workBook = new XSSFWorkbook(new FileInputStream(filePath));
		}
		// 读取第一章表格内容
		int sheetLength = workBook.getNumberOfSheets();
		Sheet sheet = null;
		for (int i = 0; i < sheetLength; i++) {
			Sheet sheetAt = workBook.getSheetAt(i);
			if (SHEET_NAME.equals(sheetAt.getSheetName())) {// 找出"号码汇总"名称的工作簿
				sheet = sheetAt;
				break;
			}
		}
		if (sheet == null) {// 如果没有"号码汇总"名称的工作簿，则选择第一个
			sheet = workBook.getSheetAt(0);
		}
		return sheet;
	}

	/**
	 * 从工作簿中找到开始读取的行,并把数据读到List对象中
	 * 
	 * @param sheet
	 * @return
	 */
	private List<String> readData(Sheet sheet) {
		int dataIndex = 0;
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		dataIndex = getDataIndex(sheet, dataIndex, map);
		if (dataIndex == 0) {// 如果没找到标题的行，则认为文件的格式不正确
			throw new PlatformException(PlatformErrorCode.FILE_FORMAT_ERROR);
		}
		return insertData(sheet, dataIndex, map);
	}

	private List<String> insertData(Sheet sheet, int dataIndex, Map<Integer, Integer> map) {
		List<String> errors = new ArrayList<String>();
		List<MobileSection> mobileSections = new ArrayList<MobileSection>();
		Map<String, Integer> paragraphs = new HashMap<String, Integer>();
		for (int i = dataIndex; i <= sheet.getLastRowNum(); i++) {// 数据从标题一行的下一行开始取
			Row row = sheet.getRow(i);
			MobileSection ms = new MobileSection();
			String repeatError = null;
			for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
				String cellValue = getStringValue(row.getCell(j));
				Integer cellIndex = map.get(j);
				if (cellIndex == null) {//标题名称不正确的
					throw new PlatformException(PlatformErrorCode.FILE_FORMAT_ERROR);
				}
				if (StringUtils.isBlank(cellValue)) {// 如果是空数据，就设置为null，在域对象中加入根据index来存储对应的数据
					ms.setDataByIndex(cellIndex, null);
				} else {
					ms.setDataByIndex(cellIndex, cellValue);
				}
				if (cellIndex == 3) {//判断万号段是重复的
					if (paragraphs.containsKey(cellValue)) {
						repeatError = "错误发生在第" + (i + 1) + "行与第" + (paragraphs.get(cellValue) + 1) + "行的万号段重复，第" + (i + 1) + "行的内容将无法插入<br />";
					} else {
						MobileSection mobileSection = getByParagraph(ms.getParagraph());
						if (mobileSection != null) {
							repeatError = "万号段第" + (i + 1) + "行数据数据库已存在<br />";
						} else {
							paragraphs.put(cellValue, i);
						}
					}
				}
			}
			// 检查传入的内容是否符合标准
			String error = check(ms, i + 1);
			if (repeatError != null) {
				error += repeatError;
			}
			if (StringUtils.isBlank(error)) {
				mobileSections.add(ms);
			} else {
				errors.add(error);
			}
			if (mobileSections.size() >= 100) {// 每取出100条记录，做一次数据库保存，然后情况list
				mobileSectionDao.ImportData(mobileSections);
				mobileSections.clear();
				paragraphs.clear();
			}
		}
		if (CollectionUtils.isNotEmpty(mobileSections)) {
			mobileSectionDao.ImportData(mobileSections);
		}
		return errors;
	}

	//不好意思用中文了
	private String check(MobileSection ms, int dataIndex) {
		StringBuilder buf = new StringBuilder();
		if (StringUtils.isBlank(ms.getProvince())) {
			buf.append("省份不能为空数据，");
		}
		if (getLength(ms.getProvince()) > 32) {
			buf.append("省份最大长度不能超过32字符16汉字，");
		}
		if (getLength(ms.getCity()) > 32) {
			buf.append("城市最大长度不能超过32字符16汉字，");
		}
		if (!isNumber(ms.getDistrict(), true)) {
			buf.append("城市区号应为数字类型，");
		}
		if (getLength(ms.getDistrict()) > 32) {
			buf.append("城市区号最大长度不能超过32字符16汉字，");
		}
		if (StringUtils.isBlank(ms.getParagraph())) {
			buf.append("万号段不能为空数据，");
		}
		if (!isNumber(ms.getParagraph(), false)) {
			buf.append("万号段应为数字类型，");
		}
		if (getLength(ms.getParagraph()) > 7) {
			buf.append("万号段最大长度不能超过7字符3汉字，");
		}
		if (!isNumber(ms.getScpNumber(), true)) {
			buf.append("归属SCP号码应为数字类型，");
		}
		if (getLength(ms.getScpNumber()) > 8) {
			buf.append("归属SCP号码最大长度不能超过8字符4汉字，");
		}
		if (!isNumber(ms.getScpId(), true)) {
			buf.append("SCP ID应为数字类型，");
		}
		if (getLength(ms.getScpId()) > 3) {
			buf.append("SCP ID最大长度不能超过3字符1汉字，");
		}
		if (getLength(ms.getScpName()) > 64) {
			buf.append("归属SCP名称最大长度不能超过64字符32汉字，");
		}
		if (getLength(ms.getMmscenterName()) > 64) {
			buf.append("彩信中心名称最大长度不能超过64字符32汉字，");
		}
		if (!isNumber(ms.getMmscenterId(), true)) {
			buf.append("彩信中心ID应为数字类型，");
		}
		if (getLength(ms.getMmscenterId()) > 6) {
			buf.append("彩信中心ID最大长度不能超过6字符3汉字，");
		}
		if (!isNumber(ms.getOfficeData(), true)) {
			buf.append("启用局数据号应为数字类型，");
		}
		if (getLength(ms.getOfficeData()) > 3) {
			buf.append("启用局数据号最大长度不能超过3字符1汉字，");
		}
		if (buf.length() > 0) {
			buf.insert(0, "错误发生在第" + dataIndex + "行：");
			buf.deleteCharAt(buf.lastIndexOf("，"));
			buf.append("<br />");
		}
		return buf.toString();
	}
	
	private boolean isNumber(String str, boolean allowNull) {
		try {
			if (allowNull) {
				if ( str == null) {
					return true;
				}
			} else {
				if ( str == null) {
					return false;
				}
			}
			int i = Integer.parseInt(str);
			if (i >= 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private int getLength(String str) {
		try {
			if (StringUtils.isBlank(str)) {
				return 0;
			}
			return new String(str.getBytes("utf-8"), "8859_1").length();
		} catch (UnsupportedEncodingException e) {
			throw new PlatformException(PlatformErrorCode.PARAM_ERROR);
		}
	}

	private int getDataIndex(Sheet sheet, int dataIndex, Map<Integer, Integer> map) {
		int lastRowNum = sheet.getLastRowNum() > 10 ? 10 : sheet.getLastRowNum(); 
		boolean titleFlag = false;
		for (int i = sheet.getFirstRowNum(); i <= lastRowNum; i++) {// 找到标题行
			Row row = sheet.getRow(i);
			for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {// 比较标题行是否符合格式
				Cell cell = row.getCell(j);
				String cellValue = null;
				if (cell != null) {
					cellValue = cell.toString();// 全部转成字符串
				}
				int fileIndex = ArrayUtils.indexOf(TITLES, cellValue);
				if (fileIndex != -1) {
					if (row.getLastCellNum() != TITLES.length) {
						throw new PlatformException(PlatformErrorCode.FILE_FORMAT_ERROR);
					}
					map.put(j, fileIndex);
					titleFlag = true;
				}
			}
			if (titleFlag) {
				dataIndex = i + 1;
				break;
			}
		}
		if (!titleFlag) {
			throw new PlatformException(PlatformErrorCode.FILE_FORMAT_ERROR);
		}
		return dataIndex;
	}

	private String getStringValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return "";
		case Cell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_ERROR:
			return ErrorEval.getText(cell.getErrorCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		case Cell.CELL_TYPE_NUMERIC:
			Double cellValue = cell.getNumericCellValue();
			DecimalFormat numberFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();
			numberFormat.applyPattern("0");
			return numberFormat.format(cellValue);
		case Cell.CELL_TYPE_STRING:
			return cell.getRichStringCellValue().toString();
		default:
			return null;
		}
	}

	public List<String> getOwnerParaGraphByProvinceName(String provinceName) {
		String hsql = "from " + MobileSection.class.getName() + " as ms where ms.province = ?";
		List<MobileSection> msList = mobileSectionDao.find(hsql, provinceName);
		List<String> moibleList = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(msList)) {
			for (MobileSection ms : msList) {
				moibleList.add(ms.getParagraph());
			}
		}
		return moibleList;
	}

	@Override
	public MobileSection getByParagraph(String paragraph) throws PlatformException {
		try {
			return mobileSectionDao.findUniqueByProperty("paragraph", paragraph);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(String[] msIds) {
		try {
			mobileSectionDao.removeAll(msIds);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}