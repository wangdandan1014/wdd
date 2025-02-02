package com.sensing.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.sensing.core.bean.TemplateObjMotor;

public class ReadExcel {
	// 总行数
	private int totalRows = 0;
	// 总条数
	private int totalCells = 0;
	// 错误信息接收器
	private String errorMsg;

	// 构造方法
	public ReadExcel() {
	}

	// 获取总行数
	public int getTotalRows() {
		return totalRows;
	}

	// 获取总列数
	public int getTotalCells() {
		return totalCells;
	}

	// 获取错误信息
	public String getErrorInfo() {
		return errorMsg;
	}

	/**
	 * 读EXCEL文件，获取信息集合
	 * 
	 * @param fielName
	 * @return
	 */
	@SuppressWarnings("resource")
	public List<TemplateObjMotor> getExcelInfo(InputStream mFile) {
		List<TemplateObjMotor> list = null;
		try {
//			list = createExcel(mFile, false);
			Workbook wb = new XSSFWorkbook(mFile);
			Sheet sheet = wb.getSheet("Sheet1");
			if (sheet != null) {
				int rows = sheet.getLastRowNum();// 一共有多少行
				if (rows == 0) {
					return null;
				}
			} else {
				return null;
			}
			list = readExcelValue(wb);// 读取Excel里面客户的信息
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 读EXCEL文件，获取信息集合
	 * 
	 * @param fielName
	 * @return
	 */
	@SuppressWarnings("resource")
	public List<TemplateObjMotor> getExcelInfo(MultipartFile mFile) {
		String fileName = mFile.getOriginalFilename();// 获取文件名
		List<TemplateObjMotor> list = null;
		try {
			if (!validateExcel(fileName)) {// 验证文件名是否合格
				return null;
			}
			boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
			if (isExcel2007(fileName)) {
				isExcel2003 = false;
			}
//			List<TemplateObjMotor> list = createExcel(mFile.getInputStream(), isExcel2003);
			Workbook wb = null;
			if (isExcel2003) {// 当excel是2003时,创建excel2003
				wb = new HSSFWorkbook(mFile.getInputStream());
			} else {// 当excel是2007时,创建excel2007
				wb = new XSSFWorkbook(mFile.getInputStream());
			}
			Sheet sheet = wb.getSheet("Sheet1");
			int rows = sheet.getLastRowNum();// 一共有多少行
			if (rows == 0) {
//				throw new Exception("请填写数据");
				return null;
			}
			list = readExcelValue(wb);// 读取Excel里面客户的信息
//				return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据excel里面的内容读取客户信息
	 * 
	 * @param is          输入流
	 * @param isExcel2003 excel是2003还是2007版本
	 * @return
	 * @throws IOException
	 */
	public List<TemplateObjMotor> createExcel(InputStream is, boolean isExcel2003) {
		try {
			Workbook wb = null;
			if (isExcel2003) {// 当excel是2003时,创建excel2003
				wb = new HSSFWorkbook(is);
			} else {// 当excel是2007时,创建excel2007
				wb = new XSSFWorkbook(is);
			}
			List<TemplateObjMotor> list = readExcelValue(wb);// 读取Excel里面客户的信息
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取Excel模版信息
	 * 
	 */
	private List<TemplateObjMotor> readExcelValue(Workbook wb) {
		// 得到第一个shell
		Sheet sheet = wb.getSheetAt(0);
		// 得到Excel的行数
		this.totalRows = sheet.getPhysicalNumberOfRows();
		// 得到Excel的列数(前提是有行数)
		if (totalRows > 1 && sheet.getRow(0) != null) {
			this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
		}
		List<TemplateObjMotor> motorList = new ArrayList<TemplateObjMotor>();
		// 循环Excel行数
		try {
			for (int r = 1; r <= totalRows; r++) {
				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				// 循环Excel的列
				TemplateObjMotor tom = new TemplateObjMotor();
				for (int c = 0; c < this.totalCells; c++) {
					try {
						Cell cell = row.getCell(c);
						if (null != cell && !cell.equals("")) {
							CellType cellTypeEnum = cell.getCellTypeEnum();
							switch (cellTypeEnum) {
							case STRING:
								if (c == 1) {// 车牌号
									String plateNo = cell.getStringCellValue();
									tom.setPlateNo(plateNo);
								} else if (c == 2) {// 车辆颜色
									String vehicleColor = cell.getStringCellValue();
									tom.setVehicleColorTag(vehicleColor);
								} else if (c == 3) {// 车辆类型
									String vehicleClass = cell.getStringCellValue();
									tom.setVehicleClassTag(vehicleClass);
								} else if (c == 4) {// 车牌颜色
									String plateColor = cell.getStringCellValue();
									tom.setPlateColorTag(plateColor);
								} else if (c == 5) {// 车辆品牌
									String vehicleBrandTag = cell.getStringCellValue();
									tom.setVehicleBrandTag(vehicleBrandTag);
								} else if (c == 6) {// 车主
									String ownerName = cell.getStringCellValue();
									tom.setOwnerName(ownerName);
								} else if (c == 7) {// 联系电话
									String ownerTel = cell.getStringCellValue();
									tom.setOwnerTel(ownerTel);
								} else if (c == 8) {// 身份证号
									String ownerIdno = cell.getStringCellValue();
									tom.setOwnerIdno(ownerIdno);
								} else if (c == 9) {// 家庭住址
									String ownerAddr = cell.getStringCellValue();
									tom.setOwnerAddr(ownerAddr);
								} else if (c == 10) {// 描述
									String memo = cell.getStringCellValue();
									tom.setMemo(memo);
								}
								break;
							case NUMERIC:
								if (c == 0) {// 序号
//									double id = cell.getNumericCellValue();
									tom.setNo(numberFormat(cell)+"");
								} else if (c == 1) {// 车牌号
									tom.setPlateNo(numberFormat(cell) + "");
								} else if (c == 6) {// 车主 --可能输入数字测试
									tom.setOwnerName(numberFormat(cell) + "");
								} else if (c == 7) {// 联系电话
									String ownerTel = numberFormat(cell);
									tom.setOwnerTel(ownerTel);
								} else if (c == 9) {// 家庭住址 --可能输入数字测试
									tom.setOwnerAddr(numberFormat(cell) + "");
								} else if (c == 10) {// 描述 --可能输入数字测试
									tom.setMemo(numberFormat(cell) + "");
								}
								break;
							default:
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
				try {
					if (StringUtils.checkObjFieldIsNotNull(tom)) {
						motorList.add(tom);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return motorList;
	}

	private String numberFormat(Cell cell) {
		if (cell != null) {
			DecimalFormat dfs = new DecimalFormat("0");
			String str = dfs.format(cell.getNumericCellValue());
			return str;
		} else {
			return null;
		}
	}

	/**
	 * 验证EXCEL文件
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean validateExcel(String filePath) {
		if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
			errorMsg = "文件名不是excel格式";
			return false;
		}
		return true;
	}

	// @描述：是否是2003的excel，返回true是2003
	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	// @描述：是否是2007的excel，返回true是2007
	public static boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}

}
