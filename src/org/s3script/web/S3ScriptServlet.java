package org.s3script.web;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.s3script.S3ScriptTemplate;
import org.s3script.IS3ScriptTemplate;
import org.songjian.utils.ExceptionUtils;
import org.songjian.utils.JSONUtils;
import org.songjian.utils.StreamUtils;
import org.songjian.utils.StringConvertUtils;
import org.songjian.utils.StringUtils;

/**
 * Servlet implementation class S3DHtmlServlet
 */
public class S3ScriptServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public final static int C_iType_Script = 0;
	public final static int C_iType_Define = 1;
	public final static int C_iType_Source = 2;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public S3ScriptServlet() {
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

	private void doGetDefine(S3ScriptTemplate aTemplet, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		aTemplet.parseCallMethod(WebInstance.ScriptMethodManagerInstance());
		response.getWriter().write(JSONUtils.getJSonString(aTemplet));

	}

	private void doParse(IS3ScriptTemplate aTemplet, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/javascript");
		aTemplet.doConvertTemplate(response.getWriter(), WebInstance.ScriptMethodManagerInstance());
	}

	private void doGetSource(IS3ScriptTemplate aTemplet, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		StreamUtils.writeFromReader(aTemplet.getBodyReader(), response.getWriter());
	}

	private String testIsNamespace(String aMoudleName) {
		return WebInstance.ScriptMethodManagerInstance().findNamespaceByFileName(aMoudleName);
	}

	private void writeTemplate(Writer fWriter, String aFileName) throws ServletException, IOException {
		IS3ScriptTemplate fTemplet;
		String fPathName = StringUtils.getAddPathStr(WebInstance.getBasePath(), aFileName);
		fTemplet = new S3ScriptTemplate();
		try {
			fTemplet.init(fPathName);
			fTemplet.doConvertTemplate(fWriter, WebInstance.ScriptMethodManagerInstance());
		} finally {
			fTemplet.closeit();
		}
	}

	private void doGetNamespaceParse(String aNameSpace, HttpServletResponse response) throws ServletException, IOException {
		List<IS3ScriptTemplate> fList;
		fList = WebInstance.ScriptMethodManagerInstance().getNamespaceFiles(aNameSpace);
		response.setContentType("text/javascript");
		Writer fWriter;
		IS3ScriptTemplate fTemplet;
		fWriter = response.getWriter();
		fWriter.write(String.format("var %s={};\r\n", aNameSpace));
		for (int i = 0; i < fList.size(); i++) {
			fTemplet = fList.get(i);
			writeTemplate(fWriter, fTemplet.getFileName());
		}

	}

	private void doIt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fMoudleName;
		request.setCharacterEncoding(StringConvertUtils.getBytesCharset());
		response.setCharacterEncoding(StringConvertUtils.getBytesCharset());

		fMoudleName = StringUtils.getUrlMoudleName(request.getRequestURI(), request.getContextPath());
		ExceptionUtils.checkNull(fMoudleName, "MoudleName");
		S3ScriptTemplate fTemplet;
		String fPathName;
		int fType;
		fType = C_iType_Script;
		String fCallType;
		fCallType = request.getParameter("calltype");
		if (StringUtils.isBlank(fCallType))
			fType = C_iType_Script;
		else if (fCallType.equalsIgnoreCase("define"))
			fType = C_iType_Define;
		else if (fCallType.equalsIgnoreCase("source"))
			fType = C_iType_Source;

		// 处理请求命名空间的Js文件
		String fNameSpace;
		fNameSpace = testIsNamespace(fMoudleName);
		if (fNameSpace != null) {
			ExceptionUtils.assertFormat(fType == C_iType_Script, "namespace js state error,state=%d,namspace=%s,filename=%s", fType, fMoudleName, fNameSpace);
			doGetNamespaceParse(fNameSpace, response);
			return;
		}

		fTemplet = new S3ScriptTemplate();
		fPathName = StringUtils.getAddPathStr(WebInstance.getBasePath(), fMoudleName);
		try {
			fTemplet.init(fPathName);
			fTemplet.setFileName(fMoudleName);

			switch (fType) {
			case C_iType_Source:
				doGetSource(fTemplet, response);
				break;
			case C_iType_Define:
				doGetDefine(fTemplet, response);
				break;

			case C_iType_Script:
			default:
				doParse(fTemplet, response);
				break;
			}
		} finally {
			fTemplet.closeit();
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
