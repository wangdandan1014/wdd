package com.sensing.core.utils;

import com.sensing.core.utils.props.PropUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;


public class FileUtil {

	public static final int BYTESIZE = 1024; // 每次读取的大小 1KB
	public static String TEMP = null; // 保存文件的临时目录

	private static Logger logger = Logger.getLogger(FileUtil.class);

	private static final int BUFFER_SIZE = 2 * 1024;

	static {
		// 获取保存文件的临时目录 WEB-INF/temp/
		try {
//            PropUtils.getString("upload_video");
			TEMP = URLDecoder.decode(FileUtil.class.getClassLoader()
					.getResource(PropUtils.getString("cachedata_addr") + "/temp").getPath(), "utf-8");
//            TEMP = URLDecoder.decode("/Users/LXH/Desktop/temp/", "utf-8");
//            TEMP = URLDecoder.decode("D:\\", "utf-8");
			/// E:/workspace/workspace for
			/// j2ee/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/MyTest/WEB-INF/temp/
		} catch (Exception e) {
			logger.error("===============>temp directory init error.. please check===============>");
			e.printStackTrace();
		}
	}

	/**
	 * @param dir
	 * @return boolean
	 * @Description: 遍历删除文件夹下的所有内容
	 * @author dongsl
	 * @Date 2017年9月8日上午11:02:38
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static boolean isSameFile(String fileName1, String fileName2) {
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		try {
			fis1 = new FileInputStream(fileName1);
			fis2 = new FileInputStream(fileName2);

			int len1 = fis1.available();// 返回总的字节数
			int len2 = fis2.available();

			if (len1 == len2) {// 长度相同，则比较具体内容
				// 建立两个字节缓冲区
				byte[] data1 = new byte[len1];
				byte[] data2 = new byte[len2];

				// 分别将两个文件的内容读入缓冲区
				fis1.read(data1);
				fis2.read(data2);

				// 依次比较文件中的每一个字节
				for (int i = 0; i < len1; i++) {
					// 只要有一个字节不同，两个文件就不一样
					if (data1[i] != data2[i]) {
						// System.out.println("文件内容不一样");
						return false;
					}
				}
				// System.out.println("两个文件完全相同");
				return true;
			} else {
				// 长度不一样，文件肯定不同
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {// 关闭文件流，防止内存泄漏
			if (fis1 != null) {
				try {
					fis1.close();
				} catch (IOException e) {
					// 忽略
					e.printStackTrace();
				}
			}
			if (fis2 != null) {
				try {
					fis2.close();
				} catch (IOException e) {
					// 忽略
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 删除目录下所有的文件;
	 *
	 * @param path
	 */
	public static boolean deleteExcelPath(File file) {
		String[] files = null;
		if (file != null) {
			files = file.list();
		}

		if (file.isDirectory()) {
			for (int i = 0; i < files.length; i++) {
				boolean bol = deleteExcelPath(new File(file, files[i]));
//				if (bol) {
//					System.out.println("删除成功!");
//				} else {
//					System.out.println("删除失败!");
//				}
			}
		}
		return file.delete();
	}

	/**
	 * 保存文件
	 * 
	 * @param is
	 * @param fileName
	 * @return
	 */
	public static File saveTempFile(InputStream is, String fileName) {
		File temp = new File(fileName);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(new FileOutputStream(temp)); // 把文件流转为文件，保存在临时目录
			int len = 0;
			byte[] buf = new byte[10 * BYTESIZE]; // 缓冲区
			while ((len = bis.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (bis != null)
					bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}

	/**
	 * 删除文件 用来删除临时文件
	 *
	 * @param file
	 */
	public static void deleteTempFile(File file) {
		logger.warn("===============>begin delete temp file: =====================>" + file.getAbsolutePath());
		boolean result = file.delete();
		logger.warn("===============>delete result :===============>" + result);
	}

	/**
	 * 删除单个文件
	 *
	 * @param fileName 要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
//				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
//				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
//			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	/**
	 * 解压文件
	 *
	 * @param zipFile
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static List<String> unZipFiles(File zipFile, String fileName) throws IOException {
		List<String> list = new ArrayList<String>();
		// 解决zip文件中有中文目录或者中文文件
//        java.util.zip.ZipFile zip = new java.util.zip.ZipFile(zipFile, Charset.forName(getCharSet()));
		java.util.zip.ZipFile zip = new java.util.zip.ZipFile(zipFile, Charset.forName("gbk"));

		for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
			java.util.zip.ZipEntry entry = (java.util.zip.ZipEntry) entries.nextElement();
			String zipEntryName = entry.getName();
			InputStream in = zip.getInputStream(entry);
			if (fileName.contains(".zip")) {
				fileName = fileName.substring(0, fileName.indexOf("."));
			}
			String outPath = (fileName + "/" + zipEntryName).replaceAll("\\*", "/");
			if (entry.isDirectory() || entry.getName().contains("MACOSX") || entry.getName().contains("~")) {
				continue;
			}
			// 输出文件路径信息
			list.add(outPath);
			// 判断路径是否存在,不存在则创建文件路径
			File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
			if (!file.exists()) {
				file.mkdirs();
			} else {

			}
			// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
			if (new File(outPath).isDirectory()) {
				continue;
			}

			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = in.read(buf1)) > 0) {
				out.write(buf1, 0, len);
			}
			in.close();
			out.close();
		}
		return list;
	}

	private static String getCharSet() {
		Properties properties = System.getProperties();
		String chartset = properties.getProperty("sun.jnu.encoding");
		if (StringUtils.isEmptyOrNull(chartset)) {
			chartset = "gbk";
		}
		return chartset;
	}

}