package server;
/**
 * @TODO：协议：表示进行的操作或反馈信息，整型
 * @fileName : .Protocol_Config.java
 * date | author | version |   
 * 2015年10月31日 | Jiong | 1.0 |
 */
public class Protocol_Config {

	
	//1000-1999作为。。。。。。。。
	public static final int LINK_SERVER = 1000 ; //连接服务器
	public static final int CREATE_HOME = 1001; //创建房间
	public static final int HOME_LIST = 1002; //房间列表
	public static final int PEOPLE_NUM = 1003; //参与人数
	public static final int IS_HOST = 1004; //是否庄家
	public static final int INTO_HOME = 1005; //是否庄家
	public static final int START_PAI = 1006; //发牌
	public static final int CAN_NEED = 1007; //可以要牌
	public static final int ComPetitor_Card = 1008; //对手要牌
	public static final int STOP_NEED = 1009; //停止要牌
	public static final int REST_PAI = 1010; //剩余牌数
	public static final int SHOW_PAI = 1011;//开玩家的牌
	public static final int CLEAR = 1012; //清除上一局内容
	public static final int SEND_MESS = 1013; //发送文本消息
	public static final int SEND_SCORE = 1014; //发送积分
	public static final int EXIT = 1015; //用户退出
	public static final int SHOW_ME = 1100; //直接显示接下来的消息
	public static final int GET_CARD = 1200; //获取牌
	public static final int ERROR = 1999;
	//2000-2999:聊天
	public static final int GET_CHATROOMLIST = 2001;
	public static final int CREATE_CHATROOM = 2000;
	public static final int ROOM_LIST = 2002;
	public static final int INTO_ROOM = 2003;
	public static final int SET_ROOM_TITLE = 2004;
	public static final int CHAT_MESS = 2005;
	public static final int CREATE_ROOM_SUCCESS = 2006;
	public static final int REGISTER_CHANNEL = 2007;
	public static final int HOME_USER_LIST = 2008;
	public static final int EXIT_ROOM = 2009;
	public static final int EXIT_CLIENT = 2010;
	public static final int GIVE_WARNING_WORD = 2011;
	public static final int BE_ADMIN = 2012;//成为管理员
	public static final int KICK_USER = 2013;//踢人
	public static final int CHANGE_INFO = 2014;
	
	public static final int SYS_MESS_PRIVATE_CHAT = 2778;
	public static final int SYS_MESS = 2777;
	public static final int SYS_ERROR = 2888;
	
	//约定roomNum为-1时为广场消息
	public static final int ROOMNUM_GROUND = -1;
	//约定roomNum为-2时为私聊消息
	public static final int ROOMNUM_PRIVATE_CHAT = -2;

	
}

