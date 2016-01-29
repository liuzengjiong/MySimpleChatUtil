package server;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
/**
 * @TODO��Ⱥ�ķ���
 * @fileName : server.ChatRoom.java
 * date | author | version |   
 * 2015��11��12�� | Jiong | 1.0 |
 */
public class ChatRoom {
	private int chatRoomNum; //�����
	private String roomName; //��������
	private List<SocketChannel> users = new ArrayList<SocketChannel>(); //�÷�����û�����
	
	public ChatRoom(int roomNum,String roomName){
		chatRoomNum = roomNum;
		this.roomName = roomName;
		if(roomName==null||roomName.length()==0){
			this.roomName = roomNum+"��������";
		}
	}
	//TODO �û����뷿��
	public void addUser(SocketChannel channel){
		users.add(channel);
	}
	//TODO �û��˳�����
	public void removeUser(SocketChannel channel){
		users.remove(channel);
	}
	//TODO�����ط�����û��б�
	public List<SocketChannel> getUsersList(){
		return users;
	}
	//TODO�����ط���Ĺ���Ա
	public SocketChannel getAdmin(){
		return users.get(0);
	}
	//TODO�����ز�������
	public int getPartNum(){
		return users.size();
	}
	//TODO�����ط����
	public int getChatRoomNum(){
		return chatRoomNum;
	}
	//TODO ���ط�����
	public String getRoomName(){
		return roomName;
	}
	//TODO �����Ƿ����Ա
	public boolean isAdmin(SocketChannel channel){
		if(users!=null&&users.indexOf(channel)==0){
			return true;
		}
		return false;
	}
	//TODO �����Ƿ񷿼��Ա
	public boolean isMember(SocketChannel channel){
		for(SocketChannel cha:users){
			if(cha.equals(channel))
				return true;
		}
		return false;
	}
}

