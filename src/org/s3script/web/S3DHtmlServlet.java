package org.s3script.web;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.s3script.S3DHtmlPaser;
import org.songjian.utils.ExceptionUtils;
import org.songjian.utils.StringConvertUtils;
import org.songjian.utils.StringUtils;

/**
 * Servlet implementation class S3DHtmlFilter
 */
public class S3DHtmlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public S3DHtmlServlet() {
		super();
	}

	private static String C_sParamName_InitPath = "s3scriptpath";

	public void init() throws ServletException {
		try {
			String fPath;
			// 初始化配置信息的路径
			fPath = (String) getServletContext().getInitParameter(C_sParamName_InitPath);
			WebInstance.init_First(fPath);
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}

	private void doIt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fMoudleName;
		request.setCharacterEncoding(StringConvertUtils.getBytesCharset());
		response.setCharacterEncoding(StringConvertUtils.getBytesCharset());
		fMoudleName = StringUtils.getUrlMoudleName(request.getRequestURI(), request.getContextPath());
		ExceptionUtils.checkNull(fMoudleName, "MoudleName");
		String fPathName;
		S3DHtmlPaser fPaser;

		response.setContentType("text/html");
		fPathName = StringUtils.getAddPathStr(WebInstance.getBasePath(), fMoudleName);

		FileReader fReader;
		Writer fWriter;

		fReader = new FileReader(fPathName);
		try {
			fPaser = new S3DHtmlPaser();
			fWriter = response.getWriter();
			fPaser.doConvertHtml(fReader, fWriter, WebInstance.ScriptMethodManagerInstance(), true);
			fWriter.flush();
		} finally {
			fReader.close();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doIt(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doIt(request, response);
	}

}
