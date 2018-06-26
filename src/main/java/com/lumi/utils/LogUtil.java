package com.lumi.utils;

import java.util.Arrays;

/**
 * 日志工具类
 * @author xiatiansong
 */
public class LogUtil {
	public static final String LOG_SPLIT = " ";
	public static final String LOG_MARKS_QUOTATION = "\"";

	public static String getLogStrLine(String keyWord, Exception e) {
		if(e != null){
			String msg = Arrays.toString(e.getStackTrace());
			msg.replaceAll("\n", "\t");
			StringBuffer sb = new StringBuffer();
			sb.append("KeyWord: ")
			  .append(keyWord)
			  .append("\t")
			  .append("Message:")
			  .append(Arrays.toString(e.getStackTrace()))
			  .append("\t")
			  .append("Exception:")
			  .append(e);
			return sb.toString();
		}
		return null;
	}
	
	public static String getLogStrLine(String keyWord, Throwable e) {
		if(e != null){
			String msg = Arrays.toString(e.getStackTrace());
			msg.replaceAll("\n", "\t");
			StringBuffer sb = new StringBuffer();
			sb.append("KeyWord: ")
			  .append(keyWord)
			  .append("\t")
			  .append("Message:")
			  .append(Arrays.toString(e.getStackTrace()))
			  .append("\t")
			  .append("Exception:")
			  .append(e);
			return sb.toString();
		}
		return null;
	}
	
	public static String getLogStr(String keyWord, Exception e) {
		if(e != null){
			String msg = Arrays.toString(e.getStackTrace());
			StringBuffer sb = new StringBuffer();
			sb.append("KeyWord: ")
			  .append("\\n")
			  .append(keyWord)
			  .append("\\n")
			  .append("Message:")
			  .append("\\n")
			  .append(msg)
			  .append("\\n")
			  .append("Exception:")
			  .append("\\n")
			  .append(e);
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * 日志信息获取
	 *  
	 * @param functionName 功能说明
	 * @param status 状态
	 * @param inputParams 输入参数
	 * @param outputParams 输出参数
	 * @param exceptionMsg 异常信息
	 * @return String
	 * @author:Jiyong.Wei 
	 * @date:2013-4-9
	 */
	public static String getLogStr(String functionName, String status, String exceptionMsg) {
		StringBuffer sb = new StringBuffer();
		sb.append(functionName).append(LOG_SPLIT);
		sb.append(status).append(LOG_SPLIT);
		sb.append(LOG_MARKS_QUOTATION);
		sb.append(LOG_SPLIT);
		sb.append(LOG_MARKS_QUOTATION);
		sb.append(exceptionMsg != null ? exceptionMsg : "");
		sb.append(LOG_MARKS_QUOTATION);
		return sb.toString();
	}
}
