package com.sensing.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;
import com.sensing.core.bean.SysTypecode;
import com.sensing.core.dao.ISysTypecodeDAO;
import com.sensing.core.service.ISysTypecodeService;
import com.sensing.core.utils.Exception.BussinessException;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.UuidUtil;

/**
 * @author wenbo
 */
@Service
public class SysTypecodeServiceImpl implements ISysTypecodeService {

	private static final Log log = LogFactory.getLog(ISysTypecodeService.class);

	@Resource
	public ISysTypecodeDAO sysTypecodeDAO;

	public SysTypecodeServiceImpl() {
		super();
	}

	/**
	 * 批量查询属性列表
	 * 
	 * @param pager
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年8月14日 下午2:29:41
	 */
	public Pager queryBatchPage(Pager pager) throws Exception {
		String typeCodeListStr = pager.getF().get("typeCodeList");
		if (typeCodeListStr == null || "".equals(typeCodeListStr)) {
			log.info("批量查询属性列表的typeCodeList为空。");
			return null;
		}

		List<SysTypecode> sysTypecodeList = new ArrayList<SysTypecode>();
		String[] typeCodeListArr = typeCodeListStr.split(",");
		// 根据typeCode查询子列表
		for (String typeCode : typeCodeListArr) {
			SysTypecode sysTypecode = new SysTypecode();
			sysTypecode.setTypeCode(typeCode);

			Pager queryPager = new Pager();
			queryPager.setPageFlag("all");// 查询全部的属性值
			queryPager.getF().put("typeCode", typeCode);

			List<SysTypecode> queryList = sysTypecodeDAO.queryList(queryPager);
			sysTypecode.setSubList(queryList);

			sysTypecodeList.add(sysTypecode);
		}
		pager.setResultList(sysTypecodeList);

		return pager;
	}

	@Override
	public SysTypecode saveNewSysTypecode(SysTypecode sysTypecode) throws Exception {
		try {
			String id = UuidUtil.getUuid();
			sysTypecode.setUuid(id);
			sysTypecodeDAO.saveSysTypecode(sysTypecode);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return sysTypecode;
	}

	@Override
	public SysTypecode updateSysTypecode(SysTypecode sysTypecode) throws Exception {
		try {
			sysTypecodeDAO.updateSysTypecode(sysTypecode);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return sysTypecode;
	}

	@Override
	public SysTypecode findSysTypecodeById(java.lang.String uuid) throws Exception {
		try {
			return sysTypecodeDAO.getSysTypecode(uuid);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	@Override
	public void removeSysTypecode(String uuid) throws Exception {
		try {
			sysTypecodeDAO.removeSysTypecode(uuid);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	@Override
	public Pager queryPage(Pager pager) throws Exception {
		try {
			List<SysTypecode> list = sysTypecodeDAO.queryList(pager);
			int totalCount = sysTypecodeDAO.selectCount(pager);
			pager.setTotalCount(totalCount);
			pager.setResultList(list);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return pager;
	}

	/**
	 * 根据type_code 和 item_id 查询一条记录
	 * 
	 * @param typeCode
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public List<SysTypecode> querySysTypeCodeByTypeCodeAndItemId(String typeCode, String itemId) throws Exception {
		return sysTypecodeDAO.selectSysTypeCodeByTypeCodeAndItemId(typeCode, itemId);
	}

	@Override
	public Integer getMaxItemIdByMemo(String memo) {
		return sysTypecodeDAO.getMaxItemIdByMemo(memo);
	}

	@Override
	public List<SysTypecode> getSysTypecodeByTypeCodeAndItemValue(String typeCode, String itemValue, String itemId) {
		List<SysTypecode> list = sysTypecodeDAO.getSysTypecodeByTypeCodeAndItemValue(typeCode, itemValue,
				itemId == null ? null : Integer.parseInt(itemId));
		return list;
	}

	@Override
	public void deleteByItemIdAndTypeCode(Integer itemId, String typeCode) throws Exception {
		sysTypecodeDAO.deleteByItemIdAndTypeCode(itemId, typeCode);
	}

	@Override
	public void updateSysTypecodeNew(SysTypecode model) throws Exception {
		List<SysTypecode> list = sysTypecodeDAO.selectSysTypeCodeByTypeCodeAndItemId("TEMPLATEDB_TYPE",
				String.valueOf(model.getItemId()));
		SysTypecode typecode = new SysTypecode();
		if (list != null && list.size() > 0) {
			typecode = list.get(0);
		}
		model.setUuid(typecode.getUuid());
		model.setTypeCode("TEMPLATEDB_TYPE");
		model.setMemo("库类型");
		if (StringUtils.isNullOrEmpty(model.getItemValue())) {
			model.setItemValue(typecode.getItemValue());
		}
		sysTypecodeDAO.updateSysTypecode(model);

	}

	@Override
	public int relevanceJobsCount(Integer itemId) throws Exception {
		int count = sysTypecodeDAO.relevanceJobsCount(itemId);
		return count;
	}

	@Override
	public List<SysTypecode> getSysTypecodeByItemValue(String string, String itemValue) {
		List<SysTypecode> list = sysTypecodeDAO.getSysTypecodeByItemValue("TEMPLATEDB_TYPE", itemValue);
		return list;
	}

	@Override
	public List<SysTypecode> querySysTypecodeByTypeCodeAndItemValue(String string, String itemValue) {
		List<SysTypecode> list = sysTypecodeDAO.querySysTypecodeByTypeCodeAndItemValue("TEMPLATEDB_TYPE",itemValue);
		return list;
	}

}