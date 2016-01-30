package server;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
/**
 * @TODO：群聊房间
 * @fileName : server.ChatRoom.java
 * date | author | version |   
 * 2015年11月12日 | Jiong | 1.0 |
 */
public class ChatRoom {
	private int chatRoomNum; //房间号
	private String roomName; //房间名称
	private List<SocketChannel> users = new ArrayList<SocketChannel>(); //该房间的用户队列
	
	public ChatRoom(int roomNum,String roomName){
		chatRoomNum = roomNum;
		this.roomName = roomName;
		if(roomName==null||roomName.length()==0){
			this.roomName = roomNum+"号聊天室";
		}
	}
	//TODO 用户加入房间
	public void addUser(SocketChannel channel){
		users.add(channel);
	}
	//TODO 用户退出房间
	public void removeUser(SocketChannel channel){
		users.remove(channel);
	}
	//TODO　返回房间的用户列表
	public List<SocketChannel> getUsersList(){
		return users;
	}
	//TODO　返回房间的管理员
	public SocketChannel getAdmin(){
		return users.get(0);
	}
	//TODO　返回参与人数
	public int getPartNum(){
		return users.size();
	}
	//TODO　返回房间号
	public int getChatRoomNum(){
		return chatRoomNum;
	}
	//TODO 返回房间名
	public String getRoomName(){
		return roomName;
	}
	//TODO 返回是否管理员
	public boolean isAdmin(SocketChannel channel){
		if(users!=null&&users.indexOf(channel)==0){
			return true;
		}
		return false;
	}
	//TODO 返回是否房间成员
	public boolean isMember(SocketChannel channel){
		for(SocketChannel cha:users){
			if(cha.equals(channel))
				return true;
		}
		return false;
	}
}

