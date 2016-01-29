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
 * @TODO�����ӷ����������������󵽷����������շ���������Ϣ
 * @fileName : client.ChatHandler.java
 * date | author | version |   
 * 2015��11��5�� | Jiong | 1.0 |
 */
public class ChatHandler extends Protocol_DataExpress{
	protected static final long serialVersionUID = 1L;
	private Base ui;
	public ChatHandler(Base clazz){
		this.ui = clazz;
	}
	// �ŵ�ѡ����
    private Selector selector;
    // �������ͨ�ŵ��ŵ�
    static SocketChannel socketChannel;
    //���ӷ�����
	public boolean linkServer(){
		 selector = Config.linkServer();
		 socketChannel = Config.getSocketChannel();
		 if(selector==null)
			 return false;
		 ListenThread lt = new ListenThread(selector);
	     new Thread(lt).start();
	     return true;
	}
	//���������ע��ܵ�
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
	//�ı��û���Ϣ
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
	//����
	public void createRoom(String roomName){
		int length = 4 + 4 + roomName.getBytes().length;
		buffer = ByteBuffer.allocateDirect(length);
		buffer.putInt(Protocol_Config.CREATE_CHATROOM);
		put(roomName);
		writeBuffer(socketChannel);
	}
	//��ȡ�����б�
	public void requestList(){
		writeInt(Protocol_Config.ROOM_LIST,socketChannel);
	}
	//����������
	public void intoRoom(int roomNum){
		buffer = ByteBuffer.allocateDirect(8);
		buffer.putInt(Protocol_Config.INTO_ROOM);
		buffer.putInt(roomNum);
		writeBuffer(socketChannel);
	}
	//������Ϣ
	public void sendMess(int roomNum,String nickname,String mess){
		int length = 4 + 4 + nickname.getBytes().length + 4 + mess.getBytes().length + 4;
		buffer = ByteBuffer.allocateDirect(length);
		buffer.putInt(Protocol_Config.CHAT_MESS);
		buffer.putInt(roomNum);
		put(nickname);
		put(mess);
		writeBuffer(socketChannel);
	}
	//�˳�������
	public void exitRoom(int roomNum){
		buffer = ByteBuffer.allocateDirect(8);
		buffer.putInt(Protocol_Config.EXIT_ROOM);
		buffer.putInt(roomNum);
		writeBuffer(socketChannel);
	}
	//�˳��ͻ���
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
	//TODO��
	class ListenThread extends Protocol_DataExpress implements Runnable{
		Selector selector;
		public ListenThread(Selector selector){
			this.selector = selector;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("�ͻ��˼��������ɹ�");
		try{
	 	 while (true) {  
	          //��ע����¼�����ʱ���������أ�����,�÷�����һֱ����  
	          selector.select();  
	          // ���selector��ѡ�е���ĵ�������ѡ�е���Ϊע����¼�  
	          Iterator ite = selector.selectedKeys().iterator();  
	          while (ite.hasNext()) {  
	              SelectionKey key = (SelectionKey) ite.next();  
	              // ɾ����ѡ��key,�Է��ظ�����  
	              ite.remove();  
	              if (key.isConnectable()) {  
	                  SocketChannel channel = (SocketChannel) key  
	                          .channel();  
	                  // ����������ӣ����������  
	                  if(channel.isConnectionPending()){  
	                      channel.finishConnect();  
	                        
	                  }  
	                  // ���óɷ�����  
	                  channel.configureBlocking(false);  

	                  //��������Ը�����˷�����ϢŶ  
	                  //channel.write(ByteBuffer.wrap(new String("�����˷�����һ����Ϣ").getBytes()));  
	                  //�ںͷ�������ӳɹ�֮��Ϊ�˿��Խ��յ�����˵���Ϣ����Ҫ��ͨ�����ö���Ȩ�ޡ�  
	                  channel.register(selector, SelectionKey.OP_READ);  
	                    
	                  // ����˿ɶ����¼�  
	              } else if (key.isReadable()) {  
	                      read(key);  
	              }  
	          }
	 	 	}
	 	 }catch(IOException e){
	 		 System.out.println("����:"+e.getMessage());
	 	 }
		
		}
		/** 
		   * �����ȡ��������Ϣ ���¼� 
		   * @param key 
		   * @throws IOException  
		   */  
		  public void read(SelectionKey key) throws IOException{  
		  	
		  	 // �������ɶ�ȡ��Ϣ:�õ��¼�������Socketͨ�� 
		      SocketChannel channel = (SocketChannel) key.channel();  
		      
		      //�Ȼ�ȡ�����룬�ж�Ҫ����ʲô����
		      int action = getInt(channel);
		     // System.out.println("��ȡaction��"+action);
		      //�������action�ķ�Χȥ����������һ��switch
		      //����action�����÷�Χ���Էֱ���벻ͬ����תվ(switch)ȥѡ��ִ����Ӧ�Ķ��� 
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
		  //���շ���������Ϣ���Կͻ��˽�����Ӧ����
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
				String nickname = getString(channel); //ȡһ��String
				String mess = getString(channel);//��ȡһ��
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


