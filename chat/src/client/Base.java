package client;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import server.Protocol_Config;
import model.User;

/**
 * @TODO：
 * @fileName : client.Base.java
 * date | author | version |   
 * 2015年11月12日 | Jiong | 1.0 |
 */
public class Base {
	ChatHandler handler;//与服务器链接的处理类
	List<RoomChatClient> frameList = new ArrayList<RoomChatClient>();//用户打开的窗口
	List<PrivateChat> privateChatList = new ArrayList<PrivateChat>();//用户的私聊窗口
	List<PrivateChat> waitLookPrivateChatList = new ArrayList<PrivateChat>();//等待用户查看的私聊窗口
	PrivateChatListDialog privateChatListDialog;
	GroundChatClient roomListDialog = null;//房间列表
	User user = new User();
	String[] warningWord = null;
	
	private GroundChatClient getRoomList(){
		if(roomListDialog==null){
			roomListDialog = new GroundChatClient(this,new String[0]);
		}
		return roomListDialog;
	}
	public void setWarningWord(String[] str){
		warningWord = str;
	}
	public String[] getWarningWord(){
		return warningWord;
	}
	public User getUser(){
		return user;
	}
	public void setUser(User user){
		handler.changeUser(user);
	}
	public Base(){
		//将这个类的对象交给handler处理
		handler = new ChatHandler(this);
		if(!handler.linkServer()){
			JOptionPane.showMessageDialog(null, "连接服务器失败！", "提示", JOptionPane.CLOSED_OPTION, null);
			return;
		}
		String name;
		try{
			name = 
				JOptionPane.showInputDialog(null,"输入昵称有助于标识自己：\n","提示",JOptionPane.PLAIN_MESSAGE,null,null,"匿名").toString();
		}catch(Exception e){
			name = "匿名";
		}
		if(name.replaceAll(" ", "").length()==0)
			name = "匿名";
		user.setId(handler.getID());
		user.setIp(handler.getIp());
		user.setLocalPort(handler.getPort());
		user.setNickName(name);
		handler.register(user.getId(),user);//注册管道
		handler.requestList();
	}
	public void createRoom(String roomName){
		handler.createRoom(roomName);
	}
	public void createRoomSuccess(int roomNum){
		RoomChatClient client = new RoomChatClient(this);
		frameList.add(client);
		client.setRoomNum(roomNum);
	}
	public void setRoomTitle(int roomNum,String title){
		RoomChatClient client = getClientByNum(roomNum);
		int num = 0;
		while(client==null&&num++<3){
			try {
				Thread.sleep(1000);
				client =  getClientByNum(roomNum);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(num==4){
			showError("获取不到房间");
			return;
		}
		client.setRoomTitle(title);
	}
	public void setRoomList(String[] str){
		if(roomListDialog!=null){
			roomListDialog.setList(str);
		}else{
			roomListDialog =  new GroundChatClient(this,str);
		}
	}
	public void intoRoom(String homeTitle){
		if(homeTitle.indexOf('[')==0){
			String reg = "\\[(\\d+)\\]";
			Pattern pattern = Pattern.compile(reg);//样式
			Matcher matcher = pattern.matcher(homeTitle);//匹配
			if(matcher.find()){
				int roomNum = Integer.valueOf(matcher.group(1));
				handler.intoRoom(roomNum);
			}
		}else{
			System.out.println("等待创建房间");
			while(homeTitle.indexOf('[')!=0){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			System.out.println("创建成功");
		}
	}
	public void setTree(int roomNum,String[] list){
		//广场用户列表
		if(roomNum==Protocol_Config.ROOMNUM_GROUND){
			getRoomList().setTree(list);
		}else{
			RoomChatClient client = getClientByNum(roomNum);
			client.setTree(list);
		}
	}
	public void sendMess(int roomNum,String mess){
		handler.sendMess(roomNum,user.getNickName(), mess);
	}
	public void sendPrivateMess(int roomNum,String id,String mess){
		handler.sendMess(roomNum,id, mess);
	}
	public void setPrivateChatListDialog(){
		String[] list = new String[waitLookPrivateChatList.size()];
		for(int i=0;i<waitLookPrivateChatList.size();i++){
			list[i] = waitLookPrivateChatList.get(i).getChaterName();
		}
		if(privateChatListDialog==null){
			privateChatListDialog = new PrivateChatListDialog(this, list);
		}else{
			privateChatListDialog.setList(list);
		}
	}
	public void setPrivateChatListDialogVisible(boolean visible){
		privateChatListDialog.setVisible(visible);
	}
	public void receiveMess(int roomNum,String nickname,String mess){
		//广场消息
		if(roomNum==Protocol_Config.ROOMNUM_GROUND){
			getRoomList().receiceMess(nickname, mess);
		//私聊消息
		}else if(roomNum==Protocol_Config.ROOMNUM_PRIVATE_CHAT){
			String id = nickname.substring(nickname.lastIndexOf("/"),nickname.lastIndexOf(")"));
			PrivateChat pc = getPrivateChatById(id);
			if(pc==null){
				PrivateChat nPc = getWaitPrivateChatById(id);
				if(nPc==null){
					nPc = new PrivateChat(this, nickname,false);
					waitLookPrivateChatList.add(nPc);
				}
				nPc.receiceMess(nickname, mess);
				setPrivateChatListDialog();
				if(privateChatListDialog==null||!privateChatListDialog.isVisible())
					getRoomList().setLabChaterListVisible(true);
			}else{
				pc.receiceMess(nickname, mess);
				if(!pc.isVisible()){
					if(getWaitPrivateChatById(id)==null)
						waitLookPrivateChatList.add(pc);
					setPrivateChatListDialog();
					if(privateChatListDialog==null||!privateChatListDialog.isVisible())
						getRoomList().setLabChaterListVisible(true);
				}
			}
		//聊天室消息	
		}else{
			RoomChatClient client = getClientByNum(roomNum);
			client.receiceMess(nickname,mess);
		}
	}
	public void setLabChaterListVisible(boolean visible){
		getRoomList().setLabChaterListVisible(visible);
	}
	public void sysMess(int roomNum,String mess){
		if(roomNum==Protocol_Config.ROOMNUM_GROUND){
			getRoomList().sysMess(mess);
		}else{
			RoomChatClient client = getClientByNum(roomNum);
			client.sysMess(mess);
		}
	}
	public void sysMess_privateChat(String id,String mess){
		PrivateChat pc = getPrivateChatById(id);
		if(pc==null)
			return;
		pc.sysMess(mess);
	}
	public void setAdmin(int roomNum){
		RoomChatClient client = getClientByNum(roomNum);
		client.setAdmin();
	}
	public void exitRoom(int roomNum){
		handler.exitRoom(roomNum);
		frameList.remove(getClientByNum(roomNum));
	}
	public void exitClient(){
		handler.exitClient();
	}
	public String getNickname(){
		return user.getNickName();
	}
	
	//进入私聊界面
	public void gotoPrivateChat(String nickName){
		String id = nickName.substring(nickName.lastIndexOf("/"),nickName.lastIndexOf(")"));
		PrivateChat waitPc = getWaitPrivateChatById(id);
		PrivateChat pc = getPrivateChatById(id);
		if(waitPc!=null){
			waitPc.setExtendedState(JFrame.NORMAL);//最大化
			/*waitPc.setAlwaysOnTop(true);
			waitPc.setAlwaysOnTop(false);*/
			waitPc.setChaterName(nickName);
			waitPc.setVisible(true);
			waitPc.setFocu();
			waitLookPrivateChatList.remove(waitPc);
			setPrivateChatListDialog();
			if(pc==null)
				privateChatList.add(waitPc);
			return;
		}
		if(pc!=null){
			pc.setExtendedState(JFrame.NORMAL);//最大化
			/*pc.setAlwaysOnTop(true);
			pc.setAlwaysOnTop(false);*/
			pc.setChaterName(nickName);
			pc.setVisible(true);
			pc.setFocu();
		}else{
			privateChatList.add(new PrivateChat(this, nickName));
		}
	}
	//踢人
	public void kickUser(int roomNum,String id){
		handler.kickUser(roomNum, id);
	}
	//被踢
	public void beKicked(int roomNum){
		RoomChatClient client = getClientByNum(roomNum);
		if(client==null)
			return;
		showError("你被踢出房间："+client.getTitle());
		frameList.remove(client);
		client.dispose();
	}
/*	public void closePrivateChat(String id){
		privateChatList.remove(getPrivateChatById(id));
	}*/
	//根据联系人id获取私聊界面
	public PrivateChat getPrivateChatById(String id){
		for(PrivateChat pri:privateChatList){
			if(pri.getChaterId().equals(id))
				return pri;
		}
		return null;
	}
	//根据联系人id获取私聊界面
		public PrivateChat getWaitPrivateChatById(String id){
			for(PrivateChat pri:waitLookPrivateChatList){
				if(pri.getChaterId().equals(id))
					return pri;
			}
			return null;
		}
	
	//根据房间号获取客户端
	public RoomChatClient getClientByNum(int roomNum){
		for(RoomChatClient client:frameList){
			if(client.getRoomNum()==roomNum){
				return client;
			}
		}
		return null;
	}
	public void showError(String mess){
		if(mess==null)
			mess = "服务器报告了一个错误消息";
		JOptionPane.showMessageDialog(null, mess, "提示", JOptionPane.CLOSED_OPTION, null);
	}
}

                                                                                                                                                                                                    