package utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*定义的一个标识符类*/
public class StringUtils
{	
	/* 判断该名字是否是合法标识符*/
	public static boolean isValidIdentifier (String name)
	{
		if (name.isEmpty())
			return false;
		
		/* OutWalls是程序使用的，用户不能使用*/
		if (name.equals("OuterWalls"))
			return false;

		Pattern p = Pattern.compile("\\w+"); //对正则表达式编译，这里是可匹配任何一个字母和数字和下划线
		
		Matcher m = p.matcher(name); //对name进行解释和匹配
		return m.matches();
	}
	
	/* 检测数字*/
	public static Integer isInteger (String input)
	{
		Integer value = null;
		
		try {
			value = new Integer(input);
		} catch (NumberFormatException e) {
			;
		}
		
		return value;
	}
	
	public static Float isFloat (String input)
	{
		Float value = null;
		
		try {
			value = new Float(input);
		} catch (NumberFormatException e) {
			;
		}
		
		return value;
	}
	
	public static String generateUniqueName ()
	{
		/* 当addGizmo时调用该函数为组件取名.*/
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
