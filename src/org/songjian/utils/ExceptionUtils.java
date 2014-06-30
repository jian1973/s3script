package org.songjian.utils;

public class ExceptionUtils {

    public static void raiseException(String aMessage)
    {
        throw new RuntimeException(aMessage);
    }
    
    public static void raiseExceptionFormat(String aFormat, Object... args)
    {
        throw new RuntimeException(String.format(aFormat, args));
    }
    
    public static RuntimeException newExceptionFormat(String aFormat, Object... args)
    {
      return new RuntimeException(String.format(aFormat, args));
    }
    
    public static RuntimeException newException(Exception ex){
    	return new RuntimeException(ex);
    }
    

    public static void checkNull(Object aValue, String aValueName)
    {
        if (aValue == null)
            raiseExceptionFormat(IError.C_sError_ObjectIsNull_Name, aValueName);
    }
    
    public static void checkNull(boolean aIsNull,String aValueName){
        if (aIsNull)
            raiseExceptionFormat(IError.C_sError_ObjectIsNull_Name, aValueName);
    }
    
    public static void checkNull_Notfound(Object aValue, String aValueName){
        if (aValue == null)
            raiseExceptionFormat(IError.C_sError_ObjectIsNull_NotFound, aValueName);
    }

    public static void assertFormat(boolean aCondition,String aFormat, Object... args){
    	if (!aCondition)
    		raiseExceptionFormat(aFormat, args);
    }

	private static String fLastErrorMsg;
	private static String fLastErrorPos;

	public static String getLastErrorMsg() {
		return fLastErrorMsg;
	}

	public static String getLastErrorPos() {
		return fLastErrorPos;
	}
	
	
	public static void clearLastError(){
		fLastErrorMsg=null;
		fLastErrorPos=null;
	}
	public static void setLastErrorMsg(String aErrorMsg,String aErrorPos){
		fLastErrorMsg=aErrorMsg;
		fLastErrorPos=aErrorPos;
	}
    
}
