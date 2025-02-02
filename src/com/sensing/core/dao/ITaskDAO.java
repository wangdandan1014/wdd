package com.sensing.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sensing.core.bean.Channel;
import com.sensing.core.bean.RunningTaskCountReq;
import com.sensing.core.bean.Task;
import com.sensing.core.bean.TaskRespTempList;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.task.TaskBean;

/**
 * @author wenbo
 */
public interface ITaskDAO {

	/**
	 * 查询结构化任务和布控任务的合集
	 * 
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2019年1月18日 下午2:11:22
	 */
	public List<TaskBean> queryTaskAndJobsList() throws Exception;

	public int saveTask(Task task);

	public Task getTask(java.lang.String uuid);

	/**
	 * 通过任务名称获得任务
	 *
	 * @param taskName
	 * @param type
	 * @return
	 */
	public List<Task> getTaskByName(@Param("taskName") String taskName, @Param("type") Integer type);

	public int removeTask(java.lang.String uuid);

	public int updateTask(Task task);

	public int updateState(Task task);

	public List<Task> queryList(Pager pager);

	public List<TaskRespTempList> queryListByName(Pager pager);

	public int selectCount(Pager pager);

	public int selectCountByName(Pager pager);

	public List<Task> getUpdateStateTask(List<Integer> list);

	public List<Task> getHistoryUpdateStateTask(List<Integer> list);

	public int setUpdateStateTasks(List<Task> list);

	/**
	 * 更新任务状态，task中使用的是newState
	 * 
	 * @param task
	 * @return
	 */
	public int setUpdateStateTask(Task task);

	public int getrunningtaskCount(RunningTaskCountReq req);

	public List<Task> getTaskCount();

	public List<Task> queryTaskRunningUuidsByDeviceId(String deviceId);

	public List<Channel> queryDeviceByDeviceId(String deviceId);

	public Integer getOfflineVideoCount();

}
