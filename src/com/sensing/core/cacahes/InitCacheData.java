package com.sensing.core.cacahes;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sensing.core.service.IChannelService;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.task.JobsAndTaskTimer;
import com.sensing.core.utils.time.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sensing.core.service.IRpcLogService;
import com.sensing.core.service.ISysResourceService;



/**
 *
 * com.sensing.core.cacahes
 * @author haowenfeng
 * @date 2018年1月10日 下午3:55:40
 */
@SuppressWarnings("all")
public class InitCacheData  implements ServletContextListener{

	private static final Log log = LogFactory.getLog(InitCacheData.class);

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		destroyJDBCDrivers();
        destroySpecifyThreads();
	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext());
        try {
            // 启动项目时，将mysql通道抓拍状态设置成0，防止数据库中的状态和抓拍不同步
            IChannelService channel = wac.getBean(IChannelService.class);
            channel.initChannelState();
            JobsAndTaskTimer timer = wac.getBean(JobsAndTaskTimer.class);
            timer.startJobsWithInit();

            ISysResourceService rpc = wac.getBean(ISysResourceService.class);
            rpc.queryResource();
        } catch (Exception e) {
            log.error("InitCacheData初始化资源数据报错" + StringUtils.getStackTrace(e));
        }

    }

	/**
	 * tomcat关闭的时候，关闭其他的进程
	 */
	public static final List<String> MANUAL_DESTROY_THREAD_IDENTIFIERS = Arrays.asList("QuartzScheduler", "scheduler_Worker");
	/**
	 * 关闭系统中的线程
	 *
	 * @author mingxingyu
	 * @date   2017年12月19日 上午11:51:56
	 */
	private void destroySpecifyThreads() {
		final Set<Thread> threads = Thread.getAllStackTraces().keySet();
		for (Thread thread : threads) {
			if (needManualDestroy(thread)) {
				synchronized (this) {
					try {
						thread.stop();
						log.debug(String.format("Destroy  %s successful",thread));
					} catch (Exception e) {
						log.warn(String.format("Destroy %s error", thread),e);
					}
				}
			}
		}
	}
	/**
	 * 关闭系统中的定时任务
	 * @param thread
	 * @return
	 * @author mingxingyu
	 * @date   2017年12月19日 上午11:52:32
	 */
	private boolean needManualDestroy(Thread thread) {
		final String threadName = thread.getName();
		for (String manualDestroyThreadIdentifier : MANUAL_DESTROY_THREAD_IDENTIFIERS) {
			if (threadName.contains(manualDestroyThreadIdentifier)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 关闭数据库链接
	 *
	 * @author mingxingyu
	 * @date   2017年12月19日 上午11:52:43
	 */
	private void destroyJDBCDrivers() {
		final Enumeration<Driver> drivers = DriverManager.getDrivers();
		Driver driver;
		while (drivers.hasMoreElements()) {
			driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				log.debug(String.format("Deregister JDBC driver %s successful", driver));
			} catch (SQLException e) {
				log.warn(String.format("Deregister JDBC driver %s error",driver), e);
			}
		}
	}

}
