package com.sensing.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.ImageFile;
import com.sensing.core.bean.Template;
import com.sensing.core.bean.TemplateDb;
import com.sensing.core.bean.TemplateObjMotor;
import com.sensing.core.dao.ITemplateDAO;
import com.sensing.core.dao.ITemplateDbDAO;
import com.sensing.core.dao.ITemplateObjMotorDAO;
import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.service.ITemplateDbService;
import com.sensing.core.service.ITemplateObjMotorService;
import com.sensing.core.service.ITemplateService;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.UuidUtil;
import com.sensing.core.utils.httputils.HttpDeal;
import com.sensing.core.utils.results.ResultUtils;

/**
 * @author mingxingyu
 */
@Service
@Transactional(rollbackFor = { Exception.class })
public class TemplateObjMotorServiceImpl implements ITemplateObjMotorService {

	private static final Log log = LogFactory.getLog(ITemplateObjMotorService.class);

	@Resource
	public ITemplateObjMotorDAO templateObjMotorDAO;
	@Resource
	public ITemplateDbDAO templateDbDAO;
	@Resource
	public ITemplateDAO templateDAO;
	@Resource
	public ICapAttrConvertService capAttrConvertService;
	@Resource
	public ITemplateDbService templateDbService;
	@Resource
	public ITemplateService templateService;

	public TemplateObjMotorServiceImpl() {
		super();
	}

	@Override
	public ResponseBean saveNewTemplateObjMotor(JSONObject m, TemplateObjMotor templateObjMotor, ResponseBean result)
			throws Exception {
		try {
			String id = UuidUtil.getUuid();
			templateObjMotor.setUuid(id);
			templateObjMotor.setCreateTime(new Date().getTime() / 1000);
			templateObjMotor.setIsDeleted((short) 0);
			if (templateObjMotor.getPlateColor() == 30000) {
				templateObjMotor.setPlateColor(null);
			}
			if (templateObjMotor.getVehicleClass() == 30000) {
				templateObjMotor.setVehicleClass(null);
			}
			if (templateObjMotor.getVehicleColor() == 30000) {
				templateObjMotor.setVehicleColor(null);
			}
			if (templateObjMotor.getVehicleBrandTag() != null && templateObjMotor.getVehicleBrandTag().equals("品牌不限")) {
				templateObjMotor.setVehicleBrandTag(null);
			}
			if ("".equals(templateObjMotor.getVehicleModelTag())) {
				templateObjMotor.setVehicleModelTag(null);
			}
			if ("".equals(templateObjMotor.getVehicleStylesTag())) {
				templateObjMotor.setVehicleStylesTag(null);
			}
			templateObjMotorDAO.saveTemplateObjMotor(templateObjMotor);
			// 更新车辆库的车辆数量
			templateDbDAO.updateTemplateDbSize(templateObjMotor.getTemplatedbId());

			// 保存图片
			if (m != null) {
				String motorListStr = m.getString("motorList");
				if (StringUtils.isNotEmpty(motorListStr)) {
					JSONArray motorList = JSONArray.parseArray(motorListStr);
					List<Template> templateList = JSONObject.parseArray(motorList.toJSONString(), Template.class);
					for (Template template : templateList) {
						String data = "";
						String postfix = "";
						byte[] imgByte = null;
						String image = template.getImageData();
						String[] d = image.split(";base64,");
						if (d != null && d.length == 2) {
							postfix = "." + d[0].split(":")[1].split("/")[1];
							data = d[1];
						} else {
							throw new RuntimeException("数据格式错误！");
						}
						imgByte = Base64.decodeBase64(data.getBytes("UTF-8"));
						if (imgByte == null || imgByte.length == 0) {
							throw new RuntimeException("图片错误！");
						}
						String uuid = UuidUtil.getUuid();
						// 保存图片到服务器
						String seceneURI = uuid + postfix;
						String secenePut = HttpDeal.doPut(seceneURI, imgByte);
						ImageFile seceneImageFile = JSONObject.toJavaObject(JSONObject.parseObject(secenePut),
								ImageFile.class);
						String imgUrl = "";
						if (seceneImageFile.getError() >= 0) {
							imgUrl = seceneImageFile.getMessage();
							Template t = new Template();
							t.setUuid(uuid);
							t.setCreateTime(new Date().getTime() / 1000);
							t.setIsDeleted((short) 0);
							t.setObjUuid(templateObjMotor.getUuid());
							t.setTemplatedbId(templateObjMotor.getTemplatedbId());
							t.setIndex(template.getIndex());
							t.setImageUrl(imgUrl);
							templateService.saveNewTemplate(t);
						} else {
							throw new RuntimeException("未获取到图片的地址！");
						}
						// 保存主图片的uuid
						if (templateObjMotor.getMainTemplateIndex() != null) {
							int index = templateObjMotor.getMainTemplateIndex();
							if (template.getIndex() == index) {
								templateObjMotor.setMainTemplateUuid(uuid);
								templateObjMotor.setMainTemplateUrl(imgUrl);
								updateTemplateObjMotor(templateObjMotor, result);
							}
						}
					}
				}
				result = ResultUtils.success(result, "model", templateObjMotor);
			} else {
				if (CollectionUtils.isEmpty(templateObjMotor.getTemplateList())) {
					return result;
				}
				List<Template> templateList = templateObjMotor.getTemplateList();
				for (int i = 0; i < templateList.size(); i++) {
					Template t = templateList.get(i);
					t.setCreateTime(new Date().getTime() / 1000);
					t.setIsDeleted((short) 0);
					t.setObjUuid(templateObjMotor.getUuid());
					t.setTemplatedbId(templateObjMotor.getTemplatedbId());
					templateService.saveNewTemplate(t);

//                    // 保存主图片的uuid
//                    if (templateObjMotor.getMainTemplateIndex() != null) {
//                        int index = templateObjMotor.getMainTemplateIndex();
//                        if (t.getIndex() == index) {
//                            templateObjMotor.setMainTemplateUuid(uuid);
//                            templateObjMotor.setMainTemplateUrl(imgUrl);
//                            updateTemplateObjMotor(templateObjMotor, result);
//                        }
//                    }

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用saveNewTemplateObjMotor方法保存车辆信息失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚                                                                                   
			result = ResultUtils.error(result, 100, "添加车辆信息失败！");
		}
		return result;
	}

	@Override
	public ResponseBean updateTemplateObjMotor(TemplateObjMotor templateObjMotor, ResponseBean result)
			throws Exception {
		try {
			TemplateObjMotor motor = templateObjMotorDAO.getTemplateObjMotor(templateObjMotor.getUuid());
			if (templateObjMotor.getCreateTime() == null) {
				templateObjMotor.setCreateTime(motor.getCreateTime());
			}
			if (templateObjMotor.getIsDeleted() == null) {
				templateObjMotor.setIsDeleted(motor.getIsDeleted());
			}
			if (templateObjMotor.getMainTemplateIndex() == null) {
				templateObjMotor.setMainTemplateIndex(motor.getMainTemplateIndex());
			}
			if (templateObjMotor.getMainTemplateUuid() == null) {
				templateObjMotor.setMainTemplateUuid(motor.getMainTemplateUuid());
			}
			if (templateObjMotor.getMainTemplateUrl() == null) {
				templateObjMotor.setMainTemplateUrl(motor.getMainTemplateUrl());
			}
			if (templateObjMotor.getMemo() == null) {
				templateObjMotor.setMemo(motor.getMemo());
			}
			if (templateObjMotor.getOwnerAddr() == null) {
				templateObjMotor.setOwnerAddr(motor.getOwnerAddr());
			}
			if (templateObjMotor.getOwnerContactinfo() == null) {
				templateObjMotor.setOwnerContactinfo(motor.getOwnerContactinfo());
			}
			if (templateObjMotor.getOwnerIdno() == null) {
				templateObjMotor.setOwnerIdno(motor.getOwnerIdno());
			}
			if (templateObjMotor.getOwnerName() == null) {
				templateObjMotor.setOwnerName(motor.getOwnerName());
			}
			if (templateObjMotor.getOwnerTel() == null) {
				templateObjMotor.setOwnerTel(motor.getOwnerTel());
			}
			if (templateObjMotor.getPlateColor() == null) {
				templateObjMotor.setPlateColor(motor.getPlateColor());
			} else if (templateObjMotor.getPlateColor() == 30000) {
				templateObjMotor.setPlateColor(null);
			}
			if (templateObjMotor.getPlateNo() == null) {
				templateObjMotor.setPlateNo(motor.getPlateNo());
			}
			if (templateObjMotor.getTemplatedbId() == null) {
				templateObjMotor.setTemplatedbId(motor.getTemplatedbId());
			}
			if (templateObjMotor.getVehicleBrandTag() == null) {
				templateObjMotor.setVehicleBrandTag(motor.getVehicleBrandTag());
			} else if ("".equals(templateObjMotor.getVehicleBrandTag())) {
				templateObjMotor.setVehicleBrandTag(null);
			}
//			if (templateObjMotor.getVehicleBrandTag() == "") {
//				templateObjMotor.setVehicleBrandTag(motor.getVehicleBrandTag());
//			} else {
//				templateObjMotor.setVehicleBrandTag(null);
//			}
			if (templateObjMotor.getVehicleClass() == null) {
				templateObjMotor.setVehicleClass(motor.getVehicleClass());
			} else if (templateObjMotor.getVehicleClass() == 30000) {
				templateObjMotor.setVehicleClass(null);
			}
			if (templateObjMotor.getVehicleColor() == null) {
				templateObjMotor.setVehicleColor(motor.getVehicleColor());
			} else if (templateObjMotor.getVehicleColor() == 30000) {
				templateObjMotor.setVehicleColor(null);
			}
			if (templateObjMotor.getVehicleModelTag() == null) {
				templateObjMotor.setVehicleModelTag(motor.getVehicleModelTag());
			} else if ("".equals(templateObjMotor.getVehicleModelTag())) {
				templateObjMotor.setVehicleModelTag(null);
			}
			if (templateObjMotor.getVehicleStylesTag() == null) {
				templateObjMotor.setVehicleStylesTag(motor.getVehicleStylesTag());
			} else if ("".equals(templateObjMotor.getVehicleStylesTag())) {
				templateObjMotor.setVehicleStylesTag(null);
			}
			templateObjMotor.setModifyTime(new Date().getTime() / 1000);
			templateObjMotorDAO.updateTemplateObjMotor(templateObjMotor);
			// 逻辑删除
			if (templateObjMotor.getIsDeleted() != null && templateObjMotor.getIsDeleted() == 1) {
				// 更新图片信息
				List<Template> list = templateDAO.getTemplateByObjUuid(templateObjMotor.getUuid());
				for (Template template : list) {
					template.setModifyTime(new Date().getTime() / 1000);
					template.setIsDeleted((short) 1);
					templateDAO.updateTemplate(template);
				}
				// 更新车辆库的车辆数量
				templateDbDAO.updateTemplateDbSize(templateObjMotor.getTemplatedbId());
			}
			result = ResultUtils.success(result, "model", templateObjMotor);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用updateTemplateObjMotor方法更新车辆信息失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚 
			result = ResultUtils.error(result, 100, "更新车辆信息失败！");
		}
		return result;
	}

	@Override
	public TemplateObjMotor findTemplateObjMotorById(java.lang.String uuid) throws Exception {
		return templateObjMotorDAO.getTemplateObjMotor(uuid);
	}

	@Override
	public void removeTemplateObjMotor(String uuid) throws Exception {
		templateObjMotorDAO.removeTemplateObjMotor(uuid);
		//模板车辆物理删除，未更新车辆库的车辆数量
//		// 更新车辆库的车辆数量
//		templateDbDAO.updateTemplateDbSize();
	}

	@Override
	public Pager queryPage(Pager pager) throws Exception {
		pager.getF().put("isDeleted", "0");
		String plateNo = pager.getF().get("plateNo");
		if (StringUtils.isNotEmpty(plateNo)) {
			String latestPlateNo = transferSpecialChar(plateNo);
			pager.getF().put("plateNo", latestPlateNo);
		}
		String templatedbId = pager.getF().get("templatedbId");
		if (StringUtils.isNotEmpty(templatedbId)) {
			TemplateDb templateDb = templateDbDAO.getTemplateDb(Integer.parseInt(templatedbId), 0);
			if (templateDb != null) {
				pager.addF("templatedbName", templateDb.getTemplateDbName());
			}
		}
		List<TemplateObjMotor> list = templateObjMotorDAO.queryList(pager);
		for (TemplateObjMotor templateObjMotor : list) {
			String tPlateNo = templateObjMotor.getPlateNo().replace("_", "?").replaceAll("\\%", "*");
			String upperCasePlateNo = tPlateNo.toUpperCase();
			templateObjMotor.setPlateNo(upperCasePlateNo);
			// set库名称
			TemplateDb templateDb = templateDbDAO.getTemplateDb(templateObjMotor.getTemplatedbId(), 0);
			if (templateDb != null) {
				templateObjMotor.setTemplatedbName(templateDb.getTemplateDbName());
				pager.addF("templatedbName", templateDb.getTemplateDbName());
			}
			// convert 属性值
			templateObjMotor = capAttrConvertService.templateObjMotorConvert(templateObjMotor);
			// set 图片url和主模版url
			List<Template> templateList = templateDAO.getTemplateByObjUuid(templateObjMotor.getUuid());
			if (templateList != null && templateList.size() > 0) {
				for (Template template : templateList) {
					template.setImageUrl(Constants.PHOTO_URL_PERSIST + template.getImageUrl());
					if (template.getUuid().equals(templateObjMotor.getMainTemplateUuid())) {
						templateObjMotor.setMainTemplateUrl(template.getImageUrl());
						templateObjMotor.setMainTemplateIndex((int) template.getIndex());
					}
				}
				templateObjMotor.setTemplateList(templateList);
			}
		}
		int totalCount = templateObjMotorDAO.selectCount(pager);
		pager.setTotalCount(totalCount);
		pager.setResultList(list);
		return pager;
	}

	private String transferSpecialChar(String plateNo) {
		String newPlateNo = plateNo.replace("？", "_").replace("?", "_").replaceAll("\\*", "%");
		String replaceUnderline = newPlateNo.replaceAll("_", "");
		String latestPlateNo = "";
		if (StringUtils.isNotEmpty(replaceUnderline)) {
			String replacePercent = replaceUnderline.replaceAll("%", "");
			if (replacePercent.length() == 0 && newPlateNo.indexOf("_") == 0) {
				latestPlateNo = "\\";
				int i = newPlateNo.indexOf("%");
				char[] charNo = newPlateNo.toCharArray();
				for (int j = 0; j < charNo.length; j++) {
					if (j == i) {
						latestPlateNo += "\\" + charNo[j];
					} else {
						latestPlateNo += charNo[j];
					}
				}
			} else if (replacePercent.length() == 0 && newPlateNo.indexOf("%") == 0) {
				latestPlateNo = "\\";
				int i = newPlateNo.indexOf("_");
				char[] charNo = newPlateNo.toCharArray();
				for (int j = 0; j < charNo.length; j++) {
					if (j == i) {
						latestPlateNo += "\\" + charNo[j];
					} else {
						latestPlateNo += charNo[j];
					}
				}
			} else if (replacePercent.length() != 0) {
				latestPlateNo = newPlateNo;
			}
		} else {
			latestPlateNo = "\\" + newPlateNo;
		}
		return latestPlateNo;
	}

	/**
	 * 逻辑删除模板库下的所有模板
	 */
	public void logicalDeleted(Integer templateDbId) throws Exception {
		templateObjMotorDAO.logicalDeleted(templateDbId);
		// 更新车辆库的车辆数量
		templateDbDAO.updateTemplateDbSize(templateDbId);
	}

	@Override
	public List<TemplateObjMotor> queryByTemplateDbId(Integer templateDbId) throws Exception {
		List<TemplateObjMotor> list = templateObjMotorDAO.queryByTemplateDbId(templateDbId);
		return list;
	}

	@Override
	public void saveObjMotorInSimpleDB(String plateNo) {
		try {
			TemplateObjMotor templateObjMotor = new TemplateObjMotor();
			String id = UuidUtil.getUuid();
			templateObjMotor.setUuid(id);
			templateObjMotor.setCreateTime(new Date().getTime() / 1000);
			templateObjMotor.setIsDeleted((short) 0);
			templateObjMotor.setPlateNo(plateNo);
			templateObjMotor.setTemplatedbId(1);// 单目标库
			templateObjMotorDAO.saveTemplateObjMotor(templateObjMotor);

			// 更新车辆库的车辆数量
			templateDbDAO.updateTemplateDbSize(templateObjMotor.getTemplatedbId());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用saveObjMotorInSimpleDB方法保存车辆信息失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚 
		}

	}

	@Override
	public List<TemplateObjMotor> queryTemplateObjMotor(Map<String, Object> params) {
		List<TemplateObjMotor> list = templateObjMotorDAO.queryTemplateObjMotor(params);
		return list;
	}

	@Override
	public ResponseBean cutMotorToOtherDB(String objUuid, String dbId, ResponseBean result) throws Exception {
		try {
			TemplateObjMotor motor = templateObjMotorDAO.getTemplateObjMotor(objUuid);
			TemplateDb newDB = templateDbDAO.getTemplateDb(Integer.parseInt(dbId), 0);
			TemplateDb oldDB = templateDbDAO.getTemplateDb(motor.getTemplatedbId(), 0);
			List<Template> list = templateDAO.getTemplateByObjUuid(objUuid);

			// 更新目标车辆的库id和库名称
			motor.setTemplatedbId(Integer.parseInt(dbId));
			motor.setModifyTime(new Date().getTime() / 1000);
			motor.setTemplatedbName(newDB.getTemplateDbName());
			templateObjMotorDAO.updateTemplateObjMotor(motor);
			// 更新车辆图片信息
			if (list != null && list.size() > 0) {
				for (Template template : list) {
					template.setTemplatedbId(Integer.parseInt(dbId));
					template.setModifyTime(new Date().getTime() / 1000);
					templateDAO.updateTemplate(template);
				}
			}
			// 更新车辆库的车辆数量
			templateDbDAO.updateTemplateDbSize(newDB.getId());
			result = ResultUtils.success(result, "model", motor);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用cutMotorToOtherDB方法转移车辆信息失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚 
			result = ResultUtils.error(result, 100, "转移车辆信息失败！");
		}
		return result;
	}

	@Override
	public ResponseBean copyMotorToOtherDB(String objUuid, String dbId, ResponseBean result) throws Exception {
		try {
			TemplateObjMotor motor = templateObjMotorDAO.getTemplateObjMotor(objUuid);
			TemplateDb newDB = templateDbDAO.getTemplateDb(Integer.parseInt(dbId), 0);
			List<Template> list = templateDAO.getTemplateByObjUuid(objUuid);

			// 添加新目标车辆的库id、库名称、uuid
			String id = UuidUtil.getUuid();
			motor.setUuid(id);
			motor.setTemplatedbId(Integer.parseInt(dbId));
			motor.setCreateTime(new Date().getTime() / 1000);
			motor.setModifyTime(new Date().getTime() / 1000);
			motor.setTemplatedbName(newDB.getTemplateDbName());
			templateObjMotorDAO.saveTemplateObjMotor(motor);
			// 添加车辆图片信息
			if (list != null && list.size() > 0) {
				for (Template template : list) {
					String uuid = UuidUtil.getUuid();
					template.setUuid(uuid);
					template.setTemplatedbId(Integer.parseInt(dbId));
					template.setCreateTime(new Date().getTime() / 1000);
					template.setModifyTime(new Date().getTime() / 1000);
					templateDAO.saveTemplate(template);
				}
			}
			// 更新车辆库的车辆数量
			templateDbDAO.updateTemplateDbSize(newDB.getId());
			
			result = ResultUtils.success(result, "model", motor);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用cutMotorToOtherDB方法复制车辆信息失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚 
			result = ResultUtils.error(result, 100, "复制车辆信息失败！");
		}
		return result;
	}

	@Override
	public TemplateObjMotor findByMainTemplateUuid(String uuid) throws Exception {
		TemplateObjMotor motor = templateObjMotorDAO.findByMainTemplateUuid(uuid);
		return motor;
	}

}
