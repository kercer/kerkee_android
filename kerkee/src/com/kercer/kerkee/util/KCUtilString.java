package com.kercer.kerkee.util;

public class KCUtilString
{

	public final static String EMPTY_STR = "";
	public final static String NULL_STR = "null";

	public static boolean isEmpty(String src)
	{
		return EMPTY_STR.equals(src) || NULL_STR.equals(src) || src == null;
	}
}
