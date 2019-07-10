package com.sensing.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sensing.core.bean.TemplateDb;
import com.sensing.core.utils.Pager;

/**
 * @author mingxingyu
 */
public interface ITemplateDbDAO {
	public int saveTemplateDb(TemplateDb templateDb) throws Exception;

	public TemplateDb getTemplateDb(java.lang.Integer id, Integer isDeleted) throws Exception;

	public int removeTemplateDb(java.lang.Integer id) throws Exception;

	public int updateTemplateDb(TemplateDb templateDb) throws Exception;

	public List<TemplateDb> queryList(Pager pager) throws Exception;

	public int selectCount(Pager pager) throws Exception;

	public List<TemplateDb> queryTemplateDbByName(String templatedbName);

	public List<TemplateDb> queryTemplateDb(Map<String, Object> params);

	/**
	 * 根据模板库的id获取模板库的数量，判断库id集合的模板库是否都存在
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2019年5月11日 下午4:02:47
	 */
	public int selectDbCount(List<Integer> list) throws Exception;

	/**
	 * 更新车辆库的车辆数量
	 * 
	 * @param tDbId
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2019年5月11日 下午4:57:36
	 */
	public int updateTemplateDbSize(Integer tDbId) throws Exception;

	public List<TemplateDb> queryTemplateDbByType(@Param("itemId")Integer itemId) throws Exception;
}
