package com.sensing.core.aop;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sensing.core.bean.RpcLog;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.utils.Constants;

/**
 * Servlet implementation class SystemInitServlet
 */
@Component
@WebServlet("/SystemInitServlet")
public class SystemInitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void init() {
		try {
			super.init();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(getServletContext());
			IRpcLogService rpc = wac.getBean(IRpcLogService.class);
			RpcLog rpcLog = new RpcLog();
			rpcLog.setCallTime(new Date());
			rpcLog.setCreateUser("系统");
			rpcLog.setType(Constants.RPC_LOG_TYPE_RUNNING);
			rpcLog.setTodo("启动");
			rpcLog.setErrorMsg("系統启动成功！");
			rpcLog.setResult("成功");
			rpcLog.setMemo(Constants.INTERFACE_CALL_TYPE_INIT);
			rpc.saveNewRpcLog(rpcLog);
		} catch (ServletException e) {
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(getServletContext());
			IRpcLogService rpc = wac.getBean(IRpcLogService.class);
			RpcLog rpcLog = new RpcLog();
			rpcLog.setCallTime(new Date());
			rpcLog.setCreateUser("系统");
			rpcLog.setType(Constants.RPC_LOG_TYPE_RUNNING);
			rpcLog.setErrorMsg("系統启动异常！");
			rpcLog.setMemo(Constants.INTERFACE_CALL_TYPE_INIT);
			rpcLog.setTodo("启动");
			rpcLog.setResult("异常");
			rpc.saveNewRpcLog(rpcLog);
		}

	}

	public void destroy() {
		WebApplicationContext wac = WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext());
		IRpcLogService rpc = wac.getBean(IRpcLogService.class);
		RpcLog rpcLog = new RpcLog();
		rpcLog.setCallTime(new Date());
		rpcLog.setCreateUser("系统");
		rpcLog.setType(Constants.RPC_LOG_TYPE_RUNNING);
		rpcLog.setMemo(Constants.INTERFACE_CALL_TYPE_INIT);
		rpcLog.setResult("成功");
		rpcLog.setTodo("关闭");
		rpcLog.setErrorMsg("系統关闭成功！");
		rpc.saveNewRpcLog(rpcLog);
		super.destroy();
	}

}
