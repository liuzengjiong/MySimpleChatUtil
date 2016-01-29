package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import model.User;
import server.Protocol_Config;
import server.Protocol_DataExpress;
import config.Config;

/**
 * @TODO：连接服务器，负责发送请求到服务器及接收服务器的消息
 * @fileName : client.ChatHandler.java
 * date | author | version |   
 * 2015年11月5日 | Jiong | 1.0 |
 */
public class ChatHandler extends Protocol_DataExpress{
	protected static final long serialVersionUID = 1L;
	private Base ui;
	public ChatHandler(Base clazz){
		this.ui = clazz;
	}
	// 信道选择器
    private Selector selector;
    // 与服务器通信的信道
    static SocketChannel socketChannel;
    //链接服务器
	public boolean linkServer(){
		 selector = Config.linkServer();
		 socketChannel = Config.getSocketChannel();
		 if(selector==null)
			 return false;
		 ListenThread lt = new ListenThread(selector);
	     new Thread(lt).start();
	     return true;
	}
	//向服务器中注册管道
	public void register(String id,User user){
		try {
			byte[] bytes = getBytes(user);
			int length = 4 + id.getBytes().length + 4 + bytes.length + 4;
			buffer = ByteBuffer.allocateDirect(length);
			buffer.putInt(Protocol_Config.REGISTER_CHANNEL);
			put(id);
			putObject(bytes);
			writeBuffer(socketChannel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//改变用户信息
	public void changeUser(User user){
		try {
			byte[] bytes = getBytes(user);
			int length = 4 + bytes.length + 4;
			buffer = ByteBuffer.allocateDirect(length);
			buffer.putInt(Protocol_Config.CHANGE_INFO);
			putObject(bytes);
			writeBuffer(socketChannel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//建房
	public void createRoom(String roomName){
		int length = 4 + 4 + roomName.getBytes().length;
		buffer = ByteBuffer.allocateDirect(length);
		buffer.putInt(Protocol_Config.CREATE_CHATROOM);
		put(roomName);
		writeBuffer(socketChannel);
	}
	//获取房间列表
	public void requestList(){
		writeInt(Protocol_Config.ROOM_LIST,socketChannel);
	}
	//进入聊天室
	public void intoRoom(int roomNum){
		buffer = ByteBuffer.allocateDirect(8);
		buffer.putInt(Protocol_Config.INTO_ROOM);
		buffer.putInt(roomNum);
		writeBuffer(socketChannel);
	}
	//发送消息
	public void sendMess(int roomNum,String nickname,String mess){
		int length = 4 + 4 + nickname.getBytes().length + 4 + mess.getBytes().length + 4;
		buffer = ByteBuffer.allocateDirect(length);
		buffer.putInt(Protocol_Config.CHAT_MESS);
		buffer.putInt(roomNum);
		put(nickname);
		put(mess);
		writeBuffer(socketChannel);
	}
	//退出聊天室
	public void exitRoom(int roomNum){
		buffer = ByteBuffer.allocateDirect(8);
		buffer.putInt(Protocol_Config.EXIT_ROOM);
		buffer.putInt(roomNum);
		writeBuffer(socketChannel);
	}
	//退出客户端
	public void exitClient(){
		writeInt(Protocol_Config.EXIT_CLIENT,socketChannel);
	}
	public void kickUser(int roomNum,String id){
		int length = 4 + 4 + id.getBytes().length + 4;
		buffer = ByteBuffer.allocateDirect(length);
		buffer.putInt(Protocol_Config.KICK_USER);
		buffer.putInt(roomNum);
		put(id);
		writeBuffer(socketChannel);
	}
	public String getID(){
		try {
			return socketChannel.getLocalAddress().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public String getIp(){
		String s = getID();
		return s.substring(s.indexOf("/")+1,s.indexOf(":"));
	}
	public int getPort(){
		String s = getID();
		try{
			return Integer.valueOf(s.substring(s.indexOf(":")+1,s.length()));
		}catch(Exception e){
			return -1;
		}
	}
	//TODO　
	class ListenThread extends Protocol_DataExpress implements Runnable{
		Selector selector;
		public ListenThread(Selector selector){
			this.selector = selector;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("客户端监听开启成功");
		try{
	 	 while (true) {  
	          //当注册的事件到达时，方法返回；否则,该方法会一直阻塞  
	          selector.select();  
	          // 获得selector中选中的项的迭代器，选中的项为注册的事件  
	          Iterator ite = selector.selectedKeys().iterator();  
	          while (ite.hasNext()) {  
	              SelectionKey key = (SelectionKey) ite.next();  
	              // 删除已选的key,以防重复处理  
	              ite.remove();  
	              if (key.isConnectable()) {  
	                  SocketChannel channel = (SocketChannel) key  
	                          .channel();  
	                  // 如果正在连接，则完成连接  
	                  if(channel.isConnectionPending()){  
	                      channel.finishConnect();  
	                        
	                  }  
	                  // 设置成非阻塞  
	                  channel.configureBlocking(false);  

	                  //在这里可以给服务端发送信息哦  
	                  //channel.write(ByteBuffer.wrap(new String("向服务端发送了一条信息").getBytes()));  
	                  //在和服务端连接成功之后，为了可以接收到服务端的信息，需要给通道设置读的权限。  
	                  channel.register(selector, SelectionKey.OP_READ);  
	                    
	                  // 获得了可读的事件  
	              } else if (key.isReadable()) {  
	                      read(key);  
	              }  
	          }
	 	 	}
	 	 }catch(IOException e){
	 		 System.out.println("错误:"+e.getMessage());
	 	 }
		
		}
		/** 
		   * 处理读取发来的信息 的事件 
		   * @param key 
		   * @throws IOException  
		   */  
		  public void read(SelectionKey key) throws IOException{  
		  	
		  	 // 服务器可读取消息:得到事件发生的Socket通道 
		      SocketChannel channel = (SocketChannel) key.channel();  
		      
		      //先获取动作码，判断要进行什么动作
		      int action = getInt(channel);
		     // System.out.println("获取action："+action);
		      //这里根据action的范围去决定进入哪一个switch
		      //所以action划定好范围可以分别进入不同的中转站(switch)去选择执行相应的动作 
		      if(action>=2000&&action<=2999){
		      	switch(action){
		      	case Protocol_Config.ROOM_LIST:
		      		setHomeList(channel);break;
		      	case Protocol_Config.SET_ROOM_TITLE:
		      		setRoomTitle(channel);break;
		      	case Protocol_Config.CREATE_ROOM_SUCCESS:
		      		ui.createRoomSuccess(getInt(channel));break;
		      	case Protocol_Config.SYS_ERROR:
		      		ui.showError(getString(channel));break;
		      	case Protocol_Config.CHAT_MESS:
		      		receiveMess(channel);break;
		      	case Protocol_Config.HOME_USER_LIST:
		      		setTree(channel);break;
		      	case Protocol_Config.SYS_MESS:
		      		sysMess(channel);break;
		      	case Protocol_Config.GIVE_WARNING_WORD:
		      		setWarning(channel);break;
		      	case Protocol_Config.BE_ADMIN:
		      		setAdmin(channel);break;
		      	case Protocol_Config.SYS_MESS_PRIVATE_CHAT:
		      		sysMess_privateChat(channel);break;
		      	case Protocol_Config.KICK_USER:
		      		beKicked(channel);break;
		      	}
		      }
		  }  
		  //接收服务器的消息，对客户端进行相应处理
		  void setHomeList(SocketChannel channel){
			  Object obj = getObject(socketChannel);
			  String[] str = (String[])obj;
			  ui.setRoomList(str);
		  }
		  void setRoomTitle(SocketChannel channel){
			  int roomNum;
			try {
				roomNum = getInt(channel);
				String title = getString(channel);
				 ui.setRoomTitle(roomNum,title);
			} catch (IOException e) {
				e.printStackTrace();
			}  
		  }
		  void receiveMess(SocketChannel channel){
			  try {
				int roomNum = getInt(channel);
				String nickname = getString(channel); //取一个String
				String mess = getString(channel);//再取一个
				ui.receiveMess(roomNum,nickname,mess);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		  }
		  void setTree(SocketChannel channel){
			  try {
				int roomNum = getInt(channel);
				String[] list = (String[]) getObject(channel);
				ui.setTree(roomNum, list);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		void sysMess(SocketChannel channel){
			try {
				int roomNum = getInt(channel);
				String mess = getString(channel);
				ui.sysMess(roomNum, mess);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		void sysMess_privateChat(SocketChannel channel){
			String id = getString(channel);
			String mess = getString(channel);
			ui.sysMess_privateChat(id, mess);
		}
		void setWarning(SocketChannel channel){
			String[] str = (String[]) getObject(channel);
			ui.setWarningWord(str);
		}
		void setAdmin(SocketChannel channel){
			try {
				int roomNum = getInt(channel);
				ui.setAdmin(roomNum);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		void beKicked(SocketChannel channel){
			try {
				int roomNum  = getInt(channel);
				ui.beKicked(roomNum);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
}


