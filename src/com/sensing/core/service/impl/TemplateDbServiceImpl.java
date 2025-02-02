package com.sensing.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.sensing.core.bean.SysTypecode;
import com.sensing.core.bean.TemplateDb;
import com.sensing.core.utils.Exception.BussinessException;
import com.sensing.core.utils.Pager;
import com.sensing.core.service.ITemplateDbService;
import com.sensing.core.service.ITemplateObjMotorService;
import com.sensing.core.service.ITemplateService;
import com.sensing.core.dao.IJobsDAO;
import com.sensing.core.dao.ISysTypecodeDAO;
import com.sensing.core.dao.ITemplateDbDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mingxingyu
 */
@Service
@Transactional(rollbackFor = { Exception.class })
public class TemplateDbServiceImpl implements ITemplateDbService {

	private static final Log log = LogFactory.getLog(ITemplateDbService.class);

	@Resource
	public ITemplateDbDAO templateDbDAO;
	@Resource
	public IJobsDAO jobsDAO;
	@Resource
	public ITemplateService templateService;
	@Resource
	public ITemplateObjMotorService templateObjMotorService;
	@Resource
	public ISysTypecodeDAO sysTypecodeDAO;

	public TemplateDbServiceImpl() {
		super();
	}

	@Override
	public TemplateDb saveNewTemplateDb(TemplateDb templateDb) throws Exception {
//		templateDb.setIsUsed((short) 1);
		templateDb.setIsDeleted((short) 0);
		templateDb.setCreateTime(new Date().getTime() / 1000);
		templateDbDAO.saveTemplateDb(templateDb);
		// 通知比对
		CallSaveNewTemplateDb(templateDb);
		templateDb = motorDBAttrConvert(templateDb);
		return templateDb;
	}

	private void CallSaveNewTemplateDb(TemplateDb templateDb) {

	}

	@Override
	public TemplateDb updateTemplateDb(TemplateDb templateDb) throws Exception {
		try {
			TemplateDb db = templateDbDAO.getTemplateDb(templateDb.getId(), 0);
			if (db != null) {
				if (templateDb.getTemplateDbName() == null) {
					templateDb.setTemplateDbName(db.getTemplateDbName());
				}
				if (templateDb.getTemplateDbType() == null) {
					templateDb.setTemplateDbType(db.getTemplateDbType());
				}
				if (templateDb.getTemplateDbDescription() == null) {
					templateDb.setTemplateDbDescription(db.getTemplateDbDescription());
				}
				if (templateDb.getIsUsed() == null) {
					templateDb.setIsUsed(db.getIsUsed());
				}
				if (templateDb.getTemplateDbSize() == null) {
					templateDb.setTemplateDbSize(db.getTemplateDbSize());
				}
				if (templateDb.getIsDeleted() == null) {
					templateDb.setIsDeleted(db.getIsDeleted());
				}
				if (templateDb.getCreateTime() == null) {
					templateDb.setCreateTime(db.getCreateTime());
				}
				if (templateDb.getCreateUser() == null) {
					templateDb.setCreateUser(db.getCreateUser());
				}
				if (templateDb.getModifyUser() == null) {
					templateDb.setModifyUser(db.getModifyUser());
				}
				templateDb.setModifyTime(new Date().getTime() / 1000);
				templateDbDAO.updateTemplateDb(templateDb);
				// 逻辑删除状态，调用比对
				if (templateDb.getIsDeleted() == 1) {
					CallRemoveFaceTemplateDb(templateDb.getId());
					// 车辆信息和模版库都要逻辑删除
					// 模版信息逻辑删除
					templateService.logicalDeleted(templateDb.getId());
					// 车辆信息逻辑删除
					templateObjMotorService.logicalDeleted(templateDb.getId());
				}
				templateDb = motorDBAttrConvert(templateDb);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用updateTemplateDb方法更新车辆库失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚
		}
		return templateDb;
	}

	/**
	 * @Description: 删除模板库时调用比对服务接口
	 * @param id
	 * @return void
	 * @author dongsl
	 * @Date 2017年8月30日下午3:43:42
	 */
	public void CallRemoveFaceTemplateDb(Integer id) {
//		// 调用比对服务接口，返回数据
//		RpcLog rl = new RpcLog();
//		// 比对集群循环通知
//		List<String> addrList = ConfigInfoCacahMapUtil.getCmpAddrList();
//		if (addrList != null && addrList.size() > 0) {
//			for (int i = 0; i < addrList.size(); i++) {
//				String cmpAddr = addrList.get(i);
//				String[] cmpAddrs = cmpAddr.split(":");
//				if (cmpAddrs != null && cmpAddrs.length == 2) {
//					String ip = cmpAddrs[0];
//					Integer port = Integer.parseInt(cmpAddrs[1]);
//
//					try {
//						rl.setCallTime(new Date());
//						rl.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
//						rl.setModule(Constants.SEVICE_MODEL_BDSF);
//						rl.setTodo("删除模板库");
//						rl.setName("DeleteDB");
//						rl.setIp(ip);
//						rl.setPort(port);
//						rl.setRpcType("thrift");
//						rl.setMemo(id.toString());
//						Date d1 = new Date();
//						// 调用比对接口
//						int r = cmpService.DeleteDB(id, ip, port);
//						Date d2 = new Date();
//						rl.setMs((int) (d2.getTime() - d1.getTime()));
//						rl.setResult("成功");
//						if (r < 0) {
//							rl.setResult("失败");
//							rl.setErrorMsg("调用比对接口删除模板库失败！");
//							log.error("调用比对接口删除模板库失败！");
//						}
//						try {
//							rpcLogDAO.saveRpcLog(rl);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//					} catch (Exception e) {
//						rl.setMs(0);
//						rl.setResult("失败");
//						rl.setErrorMsg(e.toString());
//						try {
//							rpcLogDAO.saveRpcLog(rl);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//						log.error("调用比对接口删除模板库异常", e.fillInStackTrace());
//					}
//				}
//			}
//		}
	}

	@Override
	public TemplateDb findTemplateDbById(Integer id, Integer isDeleted) throws Exception {
		if (id != null && isDeleted != null) {
			TemplateDb db = templateDbDAO.getTemplateDb(id, isDeleted);
			return db;
		} else {
			return null;
		}
	}

	@Override
	public void removeTemplateDb(Integer id) throws Exception {
		try {
			templateDbDAO.removeTemplateDb(id);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	@Override
	public Pager queryPage(Pager pager) throws Exception {
		pager.getF().put("isDeleted", "0");
		List<TemplateDb> list = templateDbDAO.queryList(pager);
		for (TemplateDb templateDb : list) {
			templateDb = motorDBAttrConvert(templateDb);
		}
		int totalCount = templateDbDAO.selectCount(pager);
		pager.setTotalCount(totalCount);
		pager.setResultList(list);
		return pager;
	}

	private TemplateDb motorDBAttrConvert(TemplateDb templateDb) throws Exception {
		if (templateDb != null) {
			if (templateDb.getTemplateDbType() != null) {
				String itemId = String.valueOf(templateDb.getTemplateDbType());
				List<SysTypecode> list = sysTypecodeDAO.selectSysTypeCodeByTypeCodeAndItemId("TEMPLATEDB_TYPE", itemId);
				if (list != null && list.size() > 0) {
					SysTypecode typecode = list.get(0);
					templateDb.setTemplateDbTypeTag(typecode.getItemValue());
				} 
//				else {
//					switch (templateDb.getTemplateDbType()) {
//					case 1:
//						templateDb.setTemplateDbTypeTag("重点车辆信息库  ");
//						break;
//					case 2:
//						templateDb.setTemplateDbTypeTag("被盗抢车辆库  ");
//						break;
//					case 3:
//						templateDb.setTemplateDbTypeTag("涉案车辆信息库 ");
//						break;
//					case 4:
//						templateDb.setTemplateDbTypeTag("单目标布控 ");
//						break;
//					default:
//						break;
//					}
//				}
			}
			if (templateDb.getIsUsed() != null) {
				switch (templateDb.getIsUsed()) {
				case 1:
					templateDb.setIsUsedTag("启用 ");
					break;
				case 2:
					templateDb.setIsUsedTag("不启用 ");
					break;
				default:
					break;
				}
			}
		}
		return templateDb;
	}

	@Override
	public List<TemplateDb> queryTemplateDbByName(String templatedbName) {
		List<TemplateDb> list = templateDbDAO.queryTemplateDbByName(templatedbName);
		return list;
	}

	@Override
	public List<TemplateDb> queryTemplateDb(Map<String, Object> param) {
		List<TemplateDb> list = templateDbDAO.queryTemplateDb(param);
		return list;
	}

	@Override
	public List<TemplateDb> queryTemplateDbByType(Integer itemId) throws Exception {
		List<TemplateDb> list = templateDbDAO.queryTemplateDbByType(itemId);
		return list;
	}

}