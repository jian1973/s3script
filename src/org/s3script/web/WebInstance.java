package org.s3script.web;

import java.io.IOException;

import org.s3script.*;
import org.s3script.ScriptMethodManager.IOnLoadError;
import org.songjian.utils.StringUtils;

public class WebInstance {

	private static ScriptMethodManager fScriptMethodManagerInstance;

	private static String fBasePath;

	public static String getBasePath() {
		return fBasePath;
	}

	public static void setBasePath(String aBasePath) {
		fBasePath = aBasePath;
	}

	public static ScriptMethodManager ScriptMethodManagerInstance() {
		return fScriptMethodManagerInstance;
	}

	public static void reInit(IOnLoadError aOnError) throws IOException {
		doLoad(aOnError);
	}

	private static void doLoad(IOnLoadError aOnError) throws IOException {
		fScriptMethodManagerInstance = new ScriptMethodManager();
		fScriptMethodManagerInstance.setErrorLogPath(StringUtils.getAddPathStr(fBasePath, "error"));
		fScriptMethodManagerInstance.loadFromPath(fBasePath,aOnError);
	}

	public static void init(String aPath) throws IOException {
		fBasePath = aPath;
		if (fScriptMethodManagerInstance == null)
			doLoad(null);

	}

	public static void init_First(String aPath) throws IOException {
		if (fBasePath != null)
			return;
		init(aPath);

	}

}
