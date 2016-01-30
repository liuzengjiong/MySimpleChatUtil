package util;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @TODO：
 * @fileName : util.AttributeSetUtil.java
 * date | author | version |   
 * 2015年11月13日 | Jiong | 1.0 |
 */
public class AttributeSetUtil {
	static SimpleAttributeSet selfSet = null; //自己的聊天信息的字体
	static SimpleAttributeSet otherSet = null;//其他人的字体
	static SimpleAttributeSet headSet = null;//消息头的字体
	static SimpleAttributeSet sysSet = null;//系统提示的字体
	
	public  static SimpleAttributeSet getSelfAttr(){
		if(selfSet==null){
			selfSet = new SimpleAttributeSet();
		    StyleConstants.setFontFamily(selfSet, "宋体");
            StyleConstants.setBold(selfSet, true);
            StyleConstants.setItalic(selfSet, false);
           StyleConstants.setFontSize(selfSet, 16);
           StyleConstants.setForeground(selfSet, Color.BLUE);
		}
		return selfSet;
	}
	public static  SimpleAttributeSet getOtherAttr(){
		if(otherSet==null){
			otherSet = new SimpleAttributeSet();
		    StyleConstants.setFontFamily(otherSet, "宋体");
            StyleConstants.setBold(otherSet, false);
            StyleConstants.setItalic(otherSet, false);
           StyleConstants.setFontSize(otherSet, 16);
           StyleConstants.setForeground(otherSet, Color.BLACK);
		}
		return otherSet;
	}
	public static  SimpleAttributeSet getHeadAttr(){
		if(headSet==null){
			headSet = new SimpleAttributeSet();
		    StyleConstants.setFontFamily(headSet, "宋体");
            StyleConstants.setBold(headSet, false);
            StyleConstants.setItalic(headSet, false);
           StyleConstants.setFontSize(headSet, 14);
           StyleConstants.setForeground(headSet, Color.GRAY);
		}
		return headSet;
	}
	public static  SimpleAttributeSet getSysAttr(){
		if(sysSet==null){
			sysSet = new SimpleAttributeSet();
		    StyleConstants.setFontFamily(sysSet, "宋体");
            StyleConstants.setBold(sysSet, true);
            StyleConstants.setItalic(sysSet, false);
           StyleConstants.setFontSize(sysSet, 12);
           StyleConstants.setForeground(sysSet, Color.RED);
		}
		return sysSet;
	}
}

