package server;
/**
 * @TODO��Э�飺��ʾ���еĲ���������Ϣ������
 * @fileName : .Protocol_Config.java
 * date | author | version |   
 * 2015��10��31�� | Jiong | 1.0 |
 */
public class Protocol_Config {

	
	//1000-1999��Ϊ����������������
	public static final int LINK_SERVER = 1000 ; //���ӷ�����
	public static final int CREATE_HOME = 1001; //��������
	public static final int HOME_LIST = 1002; //�����б�
	public static final int PEOPLE_NUM = 1003; //��������
	public static final int IS_HOST = 1004; //�Ƿ�ׯ��
	public static final int INTO_HOME = 1005; //�Ƿ�ׯ��
	public static final int START_PAI = 1006; //����
	public static final int CAN_NEED = 1007; //����Ҫ��
	public static final int ComPetitor_Card = 1008; //����Ҫ��
	public static final int STOP_NEED = 1009; //ֹͣҪ��
	public static final int REST_PAI = 1010; //ʣ������
	public static final int SHOW_PAI = 1011;//����ҵ���
	public static final int CLEAR = 1012; //�����һ������
	public static final int SEND_MESS = 1013; //�����ı���Ϣ
	public static final int SEND_SCORE = 1014; //���ͻ���
	public static final int EXIT = 1015; //�û��˳�
	public static final int SHOW_ME = 1100; //ֱ����ʾ����������Ϣ
	public static final int GET_CARD = 1200; //��ȡ��
	public static final int ERROR = 1999;
	//2000-2999:����
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
	public static final int BE_ADMIN = 2012;//��Ϊ����Ա
	public static final int KICK_USER = 2013;//����
	public static final int CHANGE_INFO = 2014;
	
	public static final int SYS_MESS_PRIVATE_CHAT = 2778;
	public static final int SYS_MESS = 2777;
	public static final int SYS_ERROR = 2888;
	
	//Լ��roomNumΪ-1ʱΪ�㳡��Ϣ
	public static final int ROOMNUM_GROUND = -1;
	//Լ��roomNumΪ-2ʱΪ˽����Ϣ
	public static final int ROOMNUM_PRIVATE_CHAT = -2;

	
}

