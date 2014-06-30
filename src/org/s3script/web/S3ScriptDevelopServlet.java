package org.s3script.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.s3script.IS3ScriptTemplate;
import org.s3script.S3ScriptTemplate;
import org.s3script.ScriptMethodManager.IOnLoadError;
import org.songjian.utils.FileUtils;
import org.songjian.utils.JSONUtils;
import org.songjian.utils.StreamUtils;
import org.songjian.utils.StringConvertUtils;
import org.songjian.utils.json.JSonWriteFilter;

/**
 * Servlet implementation class S3ScriptDevelopServlet
 */
public class S3ScriptDevelopServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public S3ScriptDevelopServlet() {
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
		request.setCharacterEncoding(StringConvertUtils.getBytesCharset());
		response.setCharacterEncoding(StringConvertUtils.getBytesCharset());
		response.setContentType("text/html");

		String ftemp = request.getParameter("s3reloadall");
		boolean fFlage = false;
		if (ftemp != null)
			fFlage = ftemp.equalsIgnoreCase("true");
		PrintWriter fOut = response.getWriter();
		if (fFlage){
			doReload(fOut);
			return ;
		}

		ftemp=request.getParameter("s3getall");
		if (ftemp != null)
			fFlage = ftemp.equalsIgnoreCase("true");
		
		if (fFlage){
			doGetList(fOut);
		}

	}
	
	public static void writeTemplateJSon(JSonWriteFilter aFilter,IS3ScriptTemplate aTemplate) throws IOException{
		S3ScriptTemplate ftemplate;
		ftemplate=(S3ScriptTemplate) aTemplate;
		String ftemp;
		ftemp=JSONUtils.getJSonString(ftemplate);
		aFilter.write(ftemp);
	}

	private void doGetList(PrintWriter aWriter) throws IOException {
		JSonWriteFilter fFilter = new JSonWriteFilter(aWriter);
		fFilter.writeObjHead();
//		
		IS3ScriptTemplate[] fList;
		fList=WebInstance.ScriptMethodManagerInstance().getAll();
		fFilter.writeFieldName("template");
		fFilter.writeArrayHead();
		for (int i = 0; i < fList.length; i++) {
			if (i!=0)
				fFilter.writeArraySeperator();
			writeTemplateJSon(fFilter,fList[i]);
		}
		fFilter.writeArrayEnd();
		fFilter.writePropertySeperator();
		fFilter.writeFieldValue("success", true);
		fFilter.writeObjEnd();
		fFilter.flush();
		
	}

	private class prvOnLoadError implements IOnLoadError {
		private boolean fHasError;

		public prvOnLoadError(JSonWriteFilter aFilter) {
			fHasError = false;
			fFilter = aFilter;
		}

		private JSonWriteFilter fFilter;

		@Override
		public void outException(String aTemplateFileName, String aLastErrorMsg, Exception aEx) throws IOException {
			File fFile;
			fFile = FileUtils.getNowDataFile(WebInstance.ScriptMethodManagerInstance().getErrorLogPath(), ".error");
			PrintWriter fWrite;

			fWrite = new PrintWriter(StreamUtils.createFileWriter(fFile, StringConvertUtils.getBytesCharset()));
			try {
				fWrite.print("template file =" + aTemplateFileName + " ");
				fWrite.println(aLastErrorMsg);
				aEx.printStackTrace(fWrite);
				fWrite.flush();
			} finally {
				fWrite.close();
			}
			if (!fHasError){
				fFilter.writeFieldName("error");
				fFilter.writeArrayHead();
			}
			else
				fFilter.writeArraySeperator();
			fFilter.println();
			fFilter.writeObjHead();
			fFilter.writeFieldValue("template", aTemplateFileName);
			fFilter.writePropertySeperator();
			fFilter.println();
			fFilter.writeFieldValue("pos", aLastErrorMsg);
			fFilter.writePropertySeperator();
			fFilter.println();
			fFilter.writeFieldValue("msg",  aEx.getMessage());
			fFilter.writeObjEnd();
			fFilter.println();

			fHasError = true;
			
		}

	}

	private void doReload(PrintWriter aWriter) throws IOException {
		JSonWriteFilter fFilter = new JSonWriteFilter(aWriter);
		prvOnLoadError fOnError = new prvOnLoadError(fFilter);
		fOnError.fHasError = false;
		fFilter.writeObjHead();
		WebInstance.reInit(fOnError);
		if (fOnError.fHasError){
			fFilter.writeArrayEnd();
			fFilter.writePropertySeperator();
		}
		fFilter.writeFieldValue("success", !fOnError.fHasError);
		fFilter.writeObjEnd();

		fFilter.flush();
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
