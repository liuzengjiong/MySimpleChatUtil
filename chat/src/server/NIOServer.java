package server;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.User;

/**
 * @TODO：socketChannel服务器
 * @fileName : .Protocol_Config.java
 * date | author | version |   
 * 2015年10月31日 | Jiong | 1.0 |
 */
public class NIOServer extends Protocol_DataExpress{
	//通道管理器  
    private Selector selector;
    String path = System.getProperty("user.dir");
    String warningWordPath = path + "\\warningWord.txt";
    String accountPath = path + "\\account.txt"; //保存用户账号
    
    //TODO　以下为聊天新增参数
    private List<ChatRoom> crList = new ArrayList<ChatRoom>();
    private Map<String,User> userMap = new HashMap<String,User>();
    private Map<String,SocketChannel> channelMap = new HashMap<String,SocketChannel>();
    int maxRoomNum = 0;
    /** 
     * 启动服务端测试 
     * @throws IOException  
     */  
    public static void main(String[] args) throws IOException {  
        NIOServer server = new NIOServer();  
        server.initServer(12000);  
        server.listen();  
    }  
    
    /** 
     * 获得一个ServerSocket通道，并对该通道做一些初始化的工作 
     * @param port  绑定的端口号 
     * @throws IOException 
     */  
    public void initServer(int port) throws IOException {  
        // 获得一个ServerSocket通道  
        ServerSocketChannel serverChannel = ServerSocketChannel.open();  
        // 设置通道为非阻塞  
        serverChannel.configureBlocking(false);  
        // 将该通道对应的ServerSocket绑定到port端口  
        serverChannel.socket().bind(new InetSocketAddress(port));  
        // 获得一个通道管理器  
        this.selector = Selector.open();  
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，  
        //当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。  
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);  
    }  
  
    /** 
     * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理 
     * @throws IOException 
     */  
    public void listen() throws IOException {  
        System.out.println("服务端启动成功！");  
        // 轮询访问selector  
        try{
        	//SocketChannel temp = null;
	        while (true) {
	            //当注册的事件到达时，方法返回；否则,该方法会一直阻塞  
	            selector.select();  
	            // 获得selector中选中的项的迭代器，选中的项为注册的事件  
	            Iterator ite = this.selector.selectedKeys().iterator();  
	            while (ite.hasNext()) {  
	            	
	                SelectionKey selectionKey = (SelectionKey) ite.next();
	                //TODO 这里是为了捕获客户端的非正常关闭
	                try{
		                // 删除已选的key,以防重复处理  
		                ite.remove();  
		                // 客户端请求连接事件  
		                if (selectionKey.isAcceptable()) {  
		                    ServerSocketChannel server = (ServerSocketChannel) selectionKey  
		                            .channel();  
		                    // 获得和客户端连接的通道  
		                    SocketChannel channel = server.accept(); 
		                    //temp = channel;
		                    System.out.println("取得和一个客户端的连接");
		                    // 设置成非阻塞  
		                    channel.configureBlocking(false);  
		  
		                    //在这里可以给客户端发送信息哦  
		                    //channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息").getBytes()));  
		                    //在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。  
		                    channel.register(this.selector, SelectionKey.OP_READ);  
		                      
		                    // 获得了可读的事件  
		                } else if (selectionKey.isReadable()) {  
		                        read(selectionKey);  
		                } 
	                }catch(Exception e){
	                	SocketChannel temp = (SocketChannel) selectionKey.channel();
	                	 if(temp!=null){
	            			 specialClose(temp);
	            			 selectionKey.cancel();
	            			 temp.socket().close();
	            			 temp.close();
	            			 System.out.println("关闭");
	            		 }
	                }
	  
	            }  
        	 }
        }  catch(Exception e){
         	e.printStackTrace();
         	System.out.println("错误！："+e.getMessage());
         }
       
    }
    public void specialClose(SocketChannel channel){
    	//TODO 更新所有用户的房间列表
        updateRoomList();
    	//关闭聊天的客户端
    	closeClient(channel);
    }
    /** 
     * 处理读取客户端发来的信息 的事件 
     * @param key 
     * @throws IOException  
     */  
    public void read(SelectionKey key) throws IOException{  
    	//System.out.println("到达read方法");
    	 // 服务器可读取消息:得到事件发生的Socket通道 
        SocketChannel channel = (SocketChannel) key.channel();  
        //先获取动作码，判断要进行什么动作
        int action = getInt(channel);
        //System.out.println("action:"+action);
        //聊天室
        if(action>=2000&&action<=2999)
   	        switch(action){
   	        case Protocol_Config.REGISTER_CHANNEL:
   	        	register(channel);break;
   	        case Protocol_Config.CREATE_CHATROOM:
   	        	createChatRoom(channel);break;
   	        case Protocol_Config.ROOM_LIST:
   	        	roomList(channel);break;
   	        case Protocol_Config.INTO_ROOM:
   	        	intoRoom(channel);break;
   	        case Protocol_Config.CHAT_MESS:
   	        	chatMess(channel);break;
   	     /*   case Protocol_Config.HOME_USER_LIST:
   	        	userList(channel);break;*/
   	        case Protocol_Config.EXIT_ROOM:
   	        	exitRoom(channel);break;
   	        case  Protocol_Config.EXIT_CLIENT:
   	        	exitClient(channel);break;
   	        case Protocol_Config.KICK_USER:
   	        	kickUser(channel);break;
   	        case Protocol_Config.CHANGE_INFO:
   	        	changeUser(channel);break;
   	        case Protocol_Config.REGISTER_ACCOUNT:
   	        	registerAccount(channel);break;
   	        case Protocol_Config.LOGIN:
   	        	login(channel);break;
   	        	default:
   	        		other(channel);break;
   	    }
    } 
    
    //TODO 聊天室相关
    //TODO 注册账号
    public void registerAccount(SocketChannel channel) throws IOException{
    	String userId = getString(channel);
    	String password = getString(channel);
    	String nickname = getString(channel);
    	for(int i=0;i<userId.length();i++){
    		char c = userId.charAt(i);
    		if(!((c>='0'&&c<='9')||(c>='a'&&c<='z')||(c>='A'&&c<='Z'))){
    			registerFeedback(channel,"账号只能由字母和数字构成");
    			return;
    		}
    	}
    	List<String[]> list = getAccountList();
    	for(String[] acc:list){
    		if(acc[0].equals(userId)){
    			registerFeedback(channel,"账号："+userId+" 已被注册");
    			return;
    		}
    	}
    	addAccount(userId, password, nickname);
    	registerFeedback(channel,"success");
    }
    //注册反馈
    public void registerFeedback(SocketChannel channel,String mess){
    	int length = 4 + 4 + mess.getBytes().length;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.REGISTER_ACCOUNT);
    	put(mess);
    	writeBuffer(channel);
    }
    //登陆
    public void login(SocketChannel channel) throws IOException{
    	System.out.println("用户登录");
    	String userId = getString(channel);
    	String password = getString(channel);
    	List<String[]> list = getAccountList();
    	for(String[] acc:list){
    		if(acc[0].equals(userId)){
    			//登陆成功
    			if(acc[1].equals(password)){
    				if(userMap.get(userId)!=null){
    					loginFail(channel,"该账号已登录");
    					return;
    				}
    				int length = 4 + 4 + acc[0].getBytes().length + 4 + acc[2].getBytes().length;
    				buffer = ByteBuffer.allocateDirect(length);
    				buffer.putInt(Protocol_Config.LOGIN_SUCCESS);
    				put(acc[0]);
    				put(acc[2]);
    				writeBuffer(channel);
    			}else{
    				loginFail(channel,"密码不正确");
    			}
    			return;
    		}
    	}
    	loginFail(channel,"账户不存在");
    }
    //登陆失败
    private void loginFail(SocketChannel channel,String mess){
    	int length = 4 + 4 + mess.getBytes().length;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.LOGIN_FAIL);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO 注册管道
    public void register(SocketChannel channel){
    	String id = getString(channel);
    	User user = (User) getObject(channel);
    	channelMap.put(id, channel);
    	userMap.put(id, user);
    	sendGroundUserList();
    	System.out.println("在服务器中注册管道");
    	sendWarningWord(channel);
    }
    //TODO　用户更改信息
    public void changeUser(SocketChannel channel) throws IOException{
    	User user = (User) getObject(channel);
    	String id = user.getId();
    	userMap.put(id, user);
    	changeAccount(id,user.getAbsoluteNickname());
    	sendGroundUserList();
    	for(ChatRoom room:crList){
    		if(room.isMember(channel)){
        		for(SocketChannel chan:room.getUsersList()){
	    	    	sendUserList(room.getChatRoomNum());
        		}
        	}
    	}
    }
    //TODO　返回敏感词集合
    public void sendWarningWord(SocketChannel channel){
    	try {
			String[] list = getWarningWord();
			byte[] bytes = getBytes(list);
			int length = 4 + bytes.length + 4;
			buffer = ByteBuffer.allocateDirect(length);
			buffer.putInt(Protocol_Config.GIVE_WARNING_WORD);
			putObject(bytes);
			writeBuffer(channel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //TODO　建房
    public void createChatRoom(SocketChannel channel){
    	String roomName = getString(channel);
    	ChatRoom room = new ChatRoom(++maxRoomNum,roomName);
    	room.addUser(channel);
    	crList.add(room);
    	buffer = ByteBuffer.allocateDirect(8);
    	buffer.putInt(Protocol_Config.CREATE_ROOM_SUCCESS);
    	buffer.putInt(room.getChatRoomNum());
    	writeBuffer(channel);
    	setRoomTitle(channel,room);
    	roomList(channel);
    	sendUserList(room.getChatRoomNum());//发送用户列表
    	//更新所有用户的房间列表
    	updateRoomList();
    	//设置为管理员
    	sendSysMess(room.getChatRoomNum(),"你创建了聊天室："+room.getChatRoomNum()+",你是管理员。", channel);
    	buffer = ByteBuffer.allocateDirect(8);
		buffer.putInt(Protocol_Config.BE_ADMIN);
		buffer.putInt(room.getChatRoomNum());
		writeBuffer(channel);
    }
    //TODO　聊天室列表
    public void roomList(SocketChannel channel){
    	//System.out.println("发送房间列表");
    	String[] str = new String[crList.size()];
    	for(int i=0;i<crList.size();i++){
    		str[i] = "["+crList.get(i).getChatRoomNum()+"]  "+crList.get(i).getRoomName()
    				+"  ("+crList.get(i).getPartNum()+" 人在线)";
    		if(crList.get(i).isMember(channel))
    			str[i] = str[i] + "(已进入)";
    	}
    	byte[] bytes;
		try {
			bytes = getBytes(str);
			int length = 4 + 4 + bytes.length;
			buffer = ByteBuffer.allocateDirect(length);
			buffer.putInt(Protocol_Config.ROOM_LIST);
			putObject(bytes);
			writeBuffer(channel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //TODO　进入聊天室
    public void intoRoom(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			ChatRoom room = getRoomByNum(roomNum);
			if(room==null)
				sysError(channel, "该聊天室不存在！");
			room.addUser(channel);
			buffer = ByteBuffer.allocateDirect(8);
	    	buffer.putInt(Protocol_Config.CREATE_ROOM_SUCCESS);
	    	buffer.putInt(room.getChatRoomNum());
	    	writeBuffer(channel);
			setRoomTitle(channel, room);
			//更新所有用户的房间列表
	    	updateRoomList();
			sendUserList(roomNum);//发送用户列表
			sendSysMessWithoutSelf(roomNum,getUserNameByChannel(channel)+"进入了房间",channel);
			sendSysMess(roomNum,"你进入了房间：["+roomNum+"]"+room.getRoomName(),channel);
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void setRoomTitle(SocketChannel channel,ChatRoom room){
    	String title = "聊天室：["+room.getChatRoomNum()+"]   "+room.getRoomName();
    	int length  = 4 + 4 + 4 + title.getBytes().length;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SET_ROOM_TITLE);
    	buffer.putInt(room.getChatRoomNum());
    	put(title);
    	writeBuffer(channel);
    }
    //TODO 发送私聊消息
    public void privateChat(int roomNum,SocketChannel channel){
    	String id = getString(channel); //此时是id
		String mess = getString(channel);
		SocketChannel chater = channelMap.get(id);
		//对方不在线
		if(chater==null){
			sendPrivateSysMess(id,"对方不在线，无法接收你的消息", channel);
		}
		String me = getUserNameByChannel(channel);
		mess = warningChecked(roomNum, mess, channel,id);
		int length = 4 + 4 + me.getBytes().length + 4 + mess.getBytes().length + 4;
		buffer = ByteBuffer.allocateDirect(length);
		buffer.putInt(Protocol_Config.CHAT_MESS);
		buffer.putInt(roomNum);
		put(me);
		put(mess);
		writeBuffer(chater);
		
    }
    //TODO　发送群聊和广场消息
    public void chatMess(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			//私聊转到另一方法处理
			if(roomNum==Protocol_Config.ROOMNUM_PRIVATE_CHAT){
				privateChat(roomNum,channel);
				return;
			}
			String nickname = getString(channel);
			String mess = getString(channel);
			mess = warningChecked(roomNum, mess, channel,null);
			int length = 4 + 4 + nickname.getBytes().length + 4 + mess.getBytes().length + 4;
			//聊天广场的消息
			if(roomNum==Protocol_Config.ROOMNUM_GROUND){
				for(String id:channelMap.keySet()){
					SocketChannel chan = channelMap.get(id);
					if(chan.equals(channel))//不发送给自己
						continue;
					buffer = ByteBuffer.allocateDirect(length);
					buffer.putInt(Protocol_Config.CHAT_MESS);
					buffer.putInt(roomNum);
					put(nickname);
					put(mess);
					writeBuffer(chan);
				}
			//聊天室的消息
			}else{
				ChatRoom room = getRoomByNum(roomNum);
				for(SocketChannel chan:room.getUsersList()){
					if(chan.equals(channel))
						continue;
					buffer = ByteBuffer.allocateDirect(length);
					buffer.putInt(Protocol_Config.CHAT_MESS);
					buffer.putInt(roomNum);
					put(nickname);
					put(mess);
					writeBuffer(chan);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
    //请求房间列表
/*    private void userList(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			sendUserList(roomNum,channel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }*/
    //TODO　发送广场的用户列表
    private void sendGroundUserList(){
    	String[] list = getGroundUserList();
    	for(String id:channelMap.keySet()){
    		SocketChannel channel = channelMap.get(id);
	    	try {
				byte[] bytes =  getBytes(list);
				int length = 4 + 4 + bytes.length + 4;
				buffer = ByteBuffer.allocateDirect(length);
				buffer.putInt(Protocol_Config.HOME_USER_LIST);
				buffer.putInt(Protocol_Config.ROOMNUM_GROUND);
				putObject(bytes);
				writeBuffer(channel);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    //TODO　发送房间的用户列表
    private void sendUserList(int roomNum){
    	String[] list = getUserListInRoom(getRoomByNum(roomNum));
    	for(SocketChannel channel:getRoomByNum(roomNum).getUsersList()){
	    	try {
				byte[] bytes =  getBytes(list);
				int length = 4 + 4 + bytes.length + 4;
				buffer = ByteBuffer.allocateDirect(length);
				buffer.putInt(Protocol_Config.HOME_USER_LIST);
				buffer.putInt(roomNum);
				putObject(bytes);
				writeBuffer(channel);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    }
    //TODO　返回房间的用户昵称数组
    private String[] getUserListInRoom(ChatRoom room){
    	List<SocketChannel> userList = room.getUsersList();
    	String[] list = new String[userList.size()];
    	for(int i = 0;i<list.length;i++){
    		String id = getUserIdByChannel(userList.get(i));
    		list[i] = userMap.get(id).getNickName();
    	}
    	return list;
    }
    //TODO　返回20个以内的所有用户数组
    private String[] getGroundUserList(){
    	int num = userMap.size();
    	if(num>20)
    		num = 20;
    	String[] list = new String[num];
    	int i = 0;
    	for(String id:userMap.keySet()){
    		if(i==20)
    			break;
    		list[i] = userMap.get(id).getNickName();
    		i++;
    	}
    	return list;
    }
  //TODO 通过通道获取用户id
    private String getUserIdByChannel(SocketChannel channel){
    	//确定channelMap中是一一对应的，可由value获取key
    	for(String id:channelMap.keySet()){
    		if(channelMap.get(id).equals(channel)){
    			return id;
    		}
    	}
    	return null;
    }
    //TODO 通过通道获取用户名
    private String getUserNameByChannel(SocketChannel channel){
    	return userMap.get(getUserIdByChannel(channel)).getNickName();
    }
    //TODO　对房间发送系统消息
    private void sendSysMessToRoom(int homeNum,String mess){
    	ChatRoom  room = getRoomByNum(homeNum);
    	for(SocketChannel channel:room.getUsersList()){
    		sendSysMess(homeNum,mess,channel);
    	}
    }
    //TODO　发送除本人外的系统消息
    private void sendSysMessWithoutSelf(int homeNum,String mess,SocketChannel channel){
    	ChatRoom  room = getRoomByNum(homeNum);
    	for(SocketChannel chan:room.getUsersList()){
    		if(channel.equals(chan))
    			continue;
    		sendSysMess(homeNum,mess,chan);
    	}
    }
    //TODO 发送系统消息
    private void sendSysMess(int homeNum,String mess,SocketChannel channel){
    	int length = 4 + 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SYS_MESS);
    	buffer.putInt(homeNum);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO 发送私聊界面的系统消息
    private void sendPrivateSysMess(String id,String mess,SocketChannel channel){
    	int length = 4 + id.getBytes().length + 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SYS_MESS_PRIVATE_CHAT);
    	put(id);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO 踢出用户
    private void kickUser(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			String id = getString(channel);
			ChatRoom room = getRoomByNum(roomNum);
			SocketChannel userBeKicked = channelMap.get(id);
			room.removeUser(userBeKicked);
			sendUserList(room.getChatRoomNum());
			sendSysMessToRoom(room.getChatRoomNum(),getUserNameByChannel(channel)+"离开了房间");
			updateRoomList();
			buffer = ByteBuffer.allocateDirect(8);
			buffer.putInt(Protocol_Config.KICK_USER);
			buffer.putInt(roomNum);
			writeBuffer(userBeKicked);
			/*sysError(userBeKicked,"你被管理员踢出聊天室：["+roomNum+"]"+room.getRoomName());*/
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //TODO　退出聊天室
    private void exitRoom(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			ChatRoom room = getRoomByNum(roomNum);
			checkAdminChange(channel, room);
			room.removeUser(channel);
			if(room.getPartNum()==0)
				crList.remove(room);
			else{
				sendUserList(room.getChatRoomNum());
				sendSysMessToRoom(room.getChatRoomNum(),getUserNameByChannel(channel)+"离开了房间");
			}
			updateRoomList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //TODO 退出客户端
    private void exitClient(SocketChannel channel){
    	closeClient(channel);
    	try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //TODO 检查管理员是否改变
    private void checkAdminChange(SocketChannel channel,ChatRoom room){
    	//要退出房间的人是管理员
    	if(room.isAdmin(channel)){
    		//设置下一顺位的用户为管理员
    		if(room.getPartNum()>1){
    			SocketChannel admin = room.getUsersList().get(1);
    			buffer = ByteBuffer.allocateDirect(8);
    			buffer.putInt(Protocol_Config.BE_ADMIN);
    			buffer.putInt(room.getChatRoomNum());
    			writeBuffer(admin);
    			sendSysMess(room.getChatRoomNum(),"你成为了管理员",admin);
    		}
    	}
    }
    //TODO 关闭客户端
    public void closeClient(SocketChannel channel){
    	List<ChatRoom> removeRoom = new ArrayList<ChatRoom>();
    	for(ChatRoom room:crList){
    		if(room.isMember(channel)){
    			checkAdminChange(channel, room);
    			room.removeUser(channel);
    			if(room.getPartNum()==0)
    				removeRoom.add(room);
    			sendUserList(room.getChatRoomNum());
    			sendSysMessToRoom(room.getChatRoomNum(),getUserNameByChannel(channel)+"离开了房间");
    		}
    	}
    	for(ChatRoom room:removeRoom){
    		System.out.println("关闭房间");
    		crList.remove(room);
    	}
    	//移除服务器中注册的信息
    	String id = getUserIdByChannel(channel);
    	userMap.remove(id);
    	channelMap.remove(id);
    	//发送广场用户列表
    	sendGroundUserList();
    }
    //TODO 敏感词过滤(id：私聊消息时才需用到，群聊和广场时id无效）
    private String warningChecked(int homeNum,String mess,SocketChannel channel,String id){
    	int num = 0;
    	try {
			String[] list = getWarningWord();
			for(String str:list){
				if(str.replace(" ","").length()==0){
					continue;
				}
				if(mess.indexOf(str)!=-1){
					num++;
				}
				mess = mess.replaceAll(str,getStarChar(str));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(num>0){
    		//更新客户端保持的敏感词列表
    		sendWarningWord(channel);
    		//私聊
    		if(homeNum==Protocol_Config.ROOMNUM_PRIVATE_CHAT&&id!=null){
    			sendPrivateSysMess(id, "构建和谐社会，你的信息中"+num+"个敏感词汇被屏蔽！", channel);
    		//群聊或广场
    		}else{
    			sendSysMess(homeNum,"构建和谐社会，你的信息中"+num+"个敏感词汇被屏蔽！", channel);
    		}
    	}
    	return mess;
    }
    //TODO 返回与敏感词字数相同的*号
    private String getStarChar(String str){
    	String s = "";
    	for(int i = 0;i<str.length();i++){
    		s = s+"*";
    	}
    	return s;
    }
    //TODO 更新所有用户的房间列表
    private void updateRoomList(){
    	for(String str:channelMap.keySet()){
    		SocketChannel chan = channelMap.get(str);
    		roomList(chan);
    	}
    }
    /** 
     * 根据聊天室的号码来获取聊天室
     * @param roomNum
     * @return
     */
    private ChatRoom getRoomByNum(int roomNum){
    	for(ChatRoom room:crList){
    		if(room.getChatRoomNum()==roomNum)
    			return room;
    	}
    	return null;
    }
    
    //TODO 发送错误报告
    private void sysError(SocketChannel channel,String mess){
    	int length = 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SYS_ERROR);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO 假定有未实现的功能
    public void other(SocketChannel channel){
    	//TODO 以下是一次传数据的过程
        int length = 4 //Config里动作（int）的长度
       		 + 4 + "未开发，敬请期待".getBytes().length;  //占据长度
        buffer = ByteBuffer.allocateDirect(length);
        //把刚刚计算长度的那些put进去
    	buffer.putInt(Protocol_Config.SHOW_ME);
    	put("未开发，敬请期待");
    	writeBuffer(channel);//写入到管道里，等于发出去了
    }
  /*  public void showMess(SocketChannel channel){
    	String mess = getString(channel);
    	System.out.println(mess);
    }*/
    //TODO 给客户端发消息
    public void addMess(SocketChannel channel,String mess){
    	int length = 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SHOW_ME);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO 发送错误报告
    public void addError(SocketChannel channel,String mess){
    	int length = 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.ERROR);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO 获取敏感词（从txt文件中获取）
    public String[] getWarningWord() throws IOException{
    	File file = new File(warningWordPath);
    	if(!file.exists()){
			file.createNewFile();
		}
    	CharBuffer cbuf = null;
		String text = null;
		FileReader fReader = new FileReader(file);
        cbuf = CharBuffer.allocate((int) file.length());
        fReader.read(cbuf);
        text = new String(cbuf.array());
        if(text.length()==0){
        	System.out.println("没有设定敏感词！！！！！！！");
        }
        String[] list = text.split("\r\n");
        fReader.close();
    	return list;
    }
  //TODO 获取账号列表
    public List<String[]> getAccountList() throws IOException{
    	List<String[]> accountList = new ArrayList<String[]>();
    	File file = new File(accountPath);
    	if(file.exists()){
    		FileReader reader = new FileReader(file);
    		CharBuffer cb = CharBuffer.allocate((int)file.length());
    		reader.read(cb);
    		String text = new String(cb.array());
    		String[] element = text.split("\r\n");
    		for(String s:element){
    			String[] oneAccount = s.split("\\*#");
    			accountList.add(oneAccount);
    		}
    		reader.close();
    	}
    	return accountList;
    }
    public void addAccount(String userId,String password,String nickname) throws IOException{
    	File file = new File(accountPath);
    	if(!file.exists()){
    		file.createNewFile();
    	}
    	FileReader reader = new FileReader(file);
    	CharBuffer cb = CharBuffer.allocate((int)file.length());
    	reader.read(cb);
    	String text = new String(cb.array());
    	if(text.length()>0)
    		text += "\r\n";
    	text += userId + "*#" + password + "*#" + nickname;
    	FileWriter writer = new FileWriter(file);
    	writer.write(text);
    	writer.flush();
    	writer.close();
    	reader.close();
    }
    public void changeAccount(String userId,String nickname) throws IOException{
    	List<String[]> list = getAccountList();
    	String password = null;
    	String lastNickname = null;
    	for(String[] acc:list){
    		if(acc[0].equals(userId)){
    			password = acc[1];
    			lastNickname = acc[2];
    		}
    	}
    	if(password==null){
    		return;
    	}
    	File file = new File(accountPath);
    	if(!file.exists()){
    		return;
    	}
    	FileReader reader = new FileReader(file);
    	CharBuffer cb = CharBuffer.allocate((int)file.length());
    	reader.read(cb);
    	String text = new String(cb.array());
    	String oldAccount = userId + "*#" + password + "*#" + lastNickname;
    	String newAccount = userId + "*#" + password + "*#" + nickname;
    	text = text.replace(oldAccount, newAccount);
    	FileWriter writer = new FileWriter(file);
    	writer.write(text);
    	writer.flush();
    	writer.close();
    	reader.close();
    }
}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             