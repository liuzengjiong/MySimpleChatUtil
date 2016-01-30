package server;
import java.io.File;
import java.io.FileReader;
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
 * @TODO��socketChannel������
 * @fileName : .Protocol_Config.java
 * date | author | version |   
 * 2015��10��31�� | Jiong | 1.0 |
 */
public class NIOServer extends Protocol_DataExpress{
	//ͨ��������  
    private Selector selector;
    String path = System.getProperty("user.dir");
    String warningWordPath = path + "\\warningWord.txt";
    
    //TODO������Ϊ������������
    private List<ChatRoom> crList = new ArrayList<ChatRoom>();
    private Map<String,User> userMap = new HashMap<String,User>();
    private Map<String,SocketChannel> channelMap = new HashMap<String,SocketChannel>();
    int maxRoomNum = 0;
    /** 
     * ��������˲��� 
     * @throws IOException  
     */  
    public static void main(String[] args) throws IOException {  
        NIOServer server = new NIOServer();  
        server.initServer(12000);  
        server.listen();  
    }  
    
    /** 
     * ���һ��ServerSocketͨ�������Ը�ͨ����һЩ��ʼ���Ĺ��� 
     * @param port  �󶨵Ķ˿ں� 
     * @throws IOException 
     */  
    public void initServer(int port) throws IOException {  
        // ���һ��ServerSocketͨ��  
        ServerSocketChannel serverChannel = ServerSocketChannel.open();  
        // ����ͨ��Ϊ������  
        serverChannel.configureBlocking(false);  
        // ����ͨ����Ӧ��ServerSocket�󶨵�port�˿�  
        serverChannel.socket().bind(new InetSocketAddress(port));  
        // ���һ��ͨ��������  
        this.selector = Selector.open();  
        //��ͨ���������͸�ͨ���󶨣���Ϊ��ͨ��ע��SelectionKey.OP_ACCEPT�¼�,ע����¼���  
        //�����¼�����ʱ��selector.select()�᷵�أ�������¼�û����selector.select()��һֱ������  
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);  
    }  
  
    /** 
     * ������ѯ�ķ�ʽ����selector���Ƿ�����Ҫ������¼�������У�����д��� 
     * @throws IOException 
     */  
    public void listen() throws IOException {  
        System.out.println("����������ɹ���");  
        // ��ѯ����selector  
        try{
        	//SocketChannel temp = null;
	        while (true) {
	            //��ע����¼�����ʱ���������أ�����,�÷�����һֱ����  
	            selector.select();  
	            // ���selector��ѡ�е���ĵ�������ѡ�е���Ϊע����¼�  
	            Iterator ite = this.selector.selectedKeys().iterator();  
	            while (ite.hasNext()) {  
	            	
	                SelectionKey selectionKey = (SelectionKey) ite.next();
	                //TODO ������Ϊ�˲���ͻ��˵ķ������ر�
	                try{
		                // ɾ����ѡ��key,�Է��ظ�����  
		                ite.remove();  
		                // �ͻ������������¼�  
		                if (selectionKey.isAcceptable()) {  
		                    ServerSocketChannel server = (ServerSocketChannel) selectionKey  
		                            .channel();  
		                    // ��úͿͻ������ӵ�ͨ��  
		                    SocketChannel channel = server.accept(); 
		                    //temp = channel;
		                    System.out.println("ȡ�ú�һ���ͻ��˵�����");
		                    // ���óɷ�����  
		                    channel.configureBlocking(false);  
		  
		                    //��������Ը��ͻ��˷�����ϢŶ  
		                    //channel.write(ByteBuffer.wrap(new String("��ͻ��˷�����һ����Ϣ").getBytes()));  
		                    //�ںͿͻ������ӳɹ�֮��Ϊ�˿��Խ��յ��ͻ��˵���Ϣ����Ҫ��ͨ�����ö���Ȩ�ޡ�  
		                    channel.register(this.selector, SelectionKey.OP_READ);  
		                      
		                    // ����˿ɶ����¼�  
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
	            			 System.out.println("�ر�");
	            		 }
	                }
	  
	            }  
        	 }
        }  catch(Exception e){
         	e.printStackTrace();
         	System.out.println("���󣡣�"+e.getMessage());
         }
       
    }
    public void specialClose(SocketChannel channel){
    	//TODO ���������û��ķ����б�
        updateRoomList();
    	//�ر�����Ŀͻ���
    	closeClient(channel);
    }
    /** 
     * �����ȡ�ͻ��˷�������Ϣ ���¼� 
     * @param key 
     * @throws IOException  
     */  
    public void read(SelectionKey key) throws IOException{  
    	//System.out.println("����read����");
    	 // �������ɶ�ȡ��Ϣ:�õ��¼�������Socketͨ�� 
        SocketChannel channel = (SocketChannel) key.channel();  
        //�Ȼ�ȡ�����룬�ж�Ҫ����ʲô����
        int action = getInt(channel);
        //System.out.println("action:"+action);
        //������
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
   	        	default:
   	        		other(channel);break;
   	    }
    } 
    
    //TODO ���������
    //TODO ע��ܵ�
    public void register(SocketChannel channel){
    	String id = getString(channel);
    	User user = (User) getObject(channel);
    	channelMap.put(id, channel);
    	userMap.put(id, user);
    	sendGroundUserList();
    	System.out.println("�ڷ�������ע��ܵ�");
    	sendWarningWord(channel);
    }
    //TODO���û�������Ϣ
    public void changeUser(SocketChannel channel){
    	User user = (User) getObject(channel);
    	String id = user.getId();
    	userMap.put(id, user);
    	sendGroundUserList();
    	for(ChatRoom room:crList){
    		if(room.isMember(channel)){
        		for(SocketChannel chan:room.getUsersList()){
	    	    	sendUserList(room.getChatRoomNum());
        		}
        	}
    	}
    }
    //TODO���������дʼ���
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
    //TODO������
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
    	sendUserList(room.getChatRoomNum());//�����û��б�
    	//���������û��ķ����б�
    	updateRoomList();
    	//����Ϊ����Ա
    	sendSysMess(room.getChatRoomNum(),"�㴴���������ң�"+room.getChatRoomNum()+",���ǹ���Ա��", channel);
    	buffer = ByteBuffer.allocateDirect(8);
		buffer.putInt(Protocol_Config.BE_ADMIN);
		buffer.putInt(room.getChatRoomNum());
		writeBuffer(channel);
    }
    //TODO���������б�
    public void roomList(SocketChannel channel){
    	System.out.println("���ͷ����б�");
    	String[] str = new String[crList.size()];
    	for(int i=0;i<crList.size();i++){
    		str[i] = "["+crList.get(i).getChatRoomNum()+"]  "+crList.get(i).getRoomName()
    				+"  ("+crList.get(i).getPartNum()+" ������)";
    		if(crList.get(i).isMember(channel))
    			str[i] = str[i] + "(�ѽ���)";
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
    //TODO������������
    public void intoRoom(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			ChatRoom room = getRoomByNum(roomNum);
			if(room==null)
				sysError(channel, "�������Ҳ����ڣ�");
			room.addUser(channel);
			buffer = ByteBuffer.allocateDirect(8);
	    	buffer.putInt(Protocol_Config.CREATE_ROOM_SUCCESS);
	    	buffer.putInt(room.getChatRoomNum());
	    	writeBuffer(channel);
			setRoomTitle(channel, room);
			//���������û��ķ����б�
	    	updateRoomList();
			sendUserList(roomNum);//�����û��б�
			sendSysMessWithoutSelf(roomNum,getUserNameByChannel(channel)+"�����˷���",channel);
			sendSysMess(roomNum,"������˷��䣺["+roomNum+"]"+room.getRoomName(),channel);
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void setRoomTitle(SocketChannel channel,ChatRoom room){
    	String title = "�����ң�["+room.getChatRoomNum()+"]   "+room.getRoomName();
    	int length  = 4 + 4 + 4 + title.getBytes().length;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SET_ROOM_TITLE);
    	buffer.putInt(room.getChatRoomNum());
    	put(title);
    	writeBuffer(channel);
    }
    //TODO ����˽����Ϣ
    public void privateChat(int roomNum,SocketChannel channel){
    	String id = getString(channel); //��ʱ��id
		String mess = getString(channel);
		SocketChannel chater = channelMap.get(id);
		//�Է�������
		if(chater==null){
			sendPrivateSysMess(id,"�Է������ߣ��޷����������Ϣ", channel);
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
    //TODO������Ⱥ�ĺ͹㳡��Ϣ
    public void chatMess(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			//˽��ת����һ��������
			if(roomNum==Protocol_Config.ROOMNUM_PRIVATE_CHAT){
				privateChat(roomNum,channel);
				return;
			}
			String nickname = getString(channel);
			String mess = getString(channel);
			mess = warningChecked(roomNum, mess, channel,null);
			int length = 4 + 4 + nickname.getBytes().length + 4 + mess.getBytes().length + 4;
			//����㳡����Ϣ
			if(roomNum==Protocol_Config.ROOMNUM_GROUND){
				for(String id:channelMap.keySet()){
					SocketChannel chan = channelMap.get(id);
					if(chan.equals(channel))//�����͸��Լ�
						continue;
					buffer = ByteBuffer.allocateDirect(length);
					buffer.putInt(Protocol_Config.CHAT_MESS);
					buffer.putInt(roomNum);
					put(nickname);
					put(mess);
					writeBuffer(chan);
				}
			//�����ҵ���Ϣ
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
    //���󷿼��б�
/*    private void userList(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			sendUserList(roomNum,channel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }*/
    //TODO�����͹㳡���û��б�
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
    
    //TODO�����ͷ�����û��б�
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
    //TODO�����ط�����û��ǳ�����
    private String[] getUserListInRoom(ChatRoom room){
    	List<SocketChannel> userList = room.getUsersList();
    	String[] list = new String[userList.size()];
    	for(int i = 0;i<list.length;i++){
    		String id = getUserIdByChannel(userList.get(i));
    		list[i] = userMap.get(id).getNickName();
    	}
    	return list;
    }
    //TODO������20�����ڵ������û�����
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
  //TODO ͨ��ͨ����ȡ�û�id
    private String getUserIdByChannel(SocketChannel channel){
    	//ȷ��channelMap����һһ��Ӧ�ģ�����value��ȡkey
    	for(String id:channelMap.keySet()){
    		if(channelMap.get(id).equals(channel)){
    			return id;
    		}
    	}
    	return null;
    }
    //TODO ͨ��ͨ����ȡ�û���
    private String getUserNameByChannel(SocketChannel channel){
    	return userMap.get(getUserIdByChannel(channel)).getNickName();
    }
    //TODO���Է��䷢��ϵͳ��Ϣ
    private void sendSysMessToRoom(int homeNum,String mess){
    	ChatRoom  room = getRoomByNum(homeNum);
    	for(SocketChannel channel:room.getUsersList()){
    		sendSysMess(homeNum,mess,channel);
    	}
    }
    //TODO�����ͳ��������ϵͳ��Ϣ
    private void sendSysMessWithoutSelf(int homeNum,String mess,SocketChannel channel){
    	ChatRoom  room = getRoomByNum(homeNum);
    	for(SocketChannel chan:room.getUsersList()){
    		if(channel.equals(chan))
    			continue;
    		sendSysMess(homeNum,mess,chan);
    	}
    }
    //TODO ����ϵͳ��Ϣ
    private void sendSysMess(int homeNum,String mess,SocketChannel channel){
    	int length = 4 + 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SYS_MESS);
    	buffer.putInt(homeNum);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO ����˽�Ľ����ϵͳ��Ϣ
    private void sendPrivateSysMess(String id,String mess,SocketChannel channel){
    	int length = 4 + id.getBytes().length + 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SYS_MESS_PRIVATE_CHAT);
    	put(id);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO �߳��û�
    private void kickUser(SocketChannel channel){
    	try {
			int roomNum = getInt(channel);
			String id = getString(channel);
			ChatRoom room = getRoomByNum(roomNum);
			SocketChannel userBeKicked = channelMap.get(id);
			room.removeUser(userBeKicked);
			sendUserList(room.getChatRoomNum());
			sendSysMessToRoom(room.getChatRoomNum(),getUserNameByChannel(channel)+"�뿪�˷���");
			updateRoomList();
			buffer = ByteBuffer.allocateDirect(8);
			buffer.putInt(Protocol_Config.KICK_USER);
			buffer.putInt(roomNum);
			writeBuffer(userBeKicked);
			/*sysError(userBeKicked,"�㱻����Ա�߳������ң�["+roomNum+"]"+room.getRoomName());*/
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //TODO���˳�������
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
				sendSysMessToRoom(room.getChatRoomNum(),getUserNameByChannel(channel)+"�뿪�˷���");
			}
			updateRoomList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //TODO �˳��ͻ���
    private void exitClient(SocketChannel channel){
    	closeClient(channel);
    	try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //TODO ������Ա�Ƿ�ı�
    private void checkAdminChange(SocketChannel channel,ChatRoom room){
    	//Ҫ�˳���������ǹ���Ա
    	if(room.isAdmin(channel)){
    		//������һ˳λ���û�Ϊ����Ա
    		if(room.getPartNum()>1){
    			SocketChannel admin = room.getUsersList().get(1);
    			buffer = ByteBuffer.allocateDirect(8);
    			buffer.putInt(Protocol_Config.BE_ADMIN);
    			buffer.putInt(room.getChatRoomNum());
    			writeBuffer(admin);
    			sendSysMess(room.getChatRoomNum(),"���Ϊ�˹���Ա",admin);
    		}
    	}
    }
    //TODO �رտͻ���
    public void closeClient(SocketChannel channel){
    	List<ChatRoom> removeRoom = new ArrayList<ChatRoom>();
    	for(ChatRoom room:crList){
    		if(room.isMember(channel)){
    			checkAdminChange(channel, room);
    			room.removeUser(channel);
    			if(room.getPartNum()==0)
    				removeRoom.add(room);
    			sendUserList(room.getChatRoomNum());
    			sendSysMessToRoom(room.getChatRoomNum(),getUserNameByChannel(channel)+"�뿪�˷���");
    		}
    	}
    	for(ChatRoom room:removeRoom){
    		System.out.println("�رշ���");
    		crList.remove(room);
    	}
    	//�Ƴ���������ע�����Ϣ
    	String id = getUserIdByChannel(channel);
    	userMap.remove(id);
    	channelMap.remove(id);
    	//���͹㳡�û��б�
    	sendGroundUserList();
    }
    //TODO ���дʹ���(id��˽����Ϣʱ�����õ���Ⱥ�ĺ͹㳡ʱid��Ч��
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
    		//���¿ͻ��˱��ֵ����д��б�
    		sendWarningWord(channel);
    		//˽��
    		if(homeNum==Protocol_Config.ROOMNUM_PRIVATE_CHAT&&id!=null){
    			sendPrivateSysMess(id, "������г��ᣬ�����Ϣ��"+num+"�����дʻ㱻���Σ�", channel);
    		//Ⱥ�Ļ�㳡
    		}else{
    			sendSysMess(homeNum,"������г��ᣬ�����Ϣ��"+num+"�����дʻ㱻���Σ�", channel);
    		}
    	}
    	return mess;
    }
    //TODO ���������д�������ͬ��*��
    private String getStarChar(String str){
    	String s = "";
    	for(int i = 0;i<str.length();i++){
    		s = s+"*";
    	}
    	return s;
    }
    //TODO ���������û��ķ����б�
    private void updateRoomList(){
    	for(String str:channelMap.keySet()){
    		SocketChannel chan = channelMap.get(str);
    		roomList(chan);
    	}
    }
    /** 
     * ���������ҵĺ�������ȡ������
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
    
    //TODO ���ʹ��󱨸�
    private void sysError(SocketChannel channel,String mess){
    	int length = 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SYS_ERROR);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO �ٶ���δʵ�ֵĹ���
    public void other(SocketChannel channel){
    	//TODO ������һ�δ����ݵĹ���
        int length = 4 //Config�ﶯ����int���ĳ���
       		 + 4 + "δ�����������ڴ�".getBytes().length;  //ռ�ݳ���
        buffer = ByteBuffer.allocateDirect(length);
        //�Ѹոռ��㳤�ȵ���Щput��ȥ
    	buffer.putInt(Protocol_Config.SHOW_ME);
    	put("δ�����������ڴ�");
    	writeBuffer(channel);//д�뵽�ܵ�����ڷ���ȥ��
    }
  /*  public void showMess(SocketChannel channel){
    	String mess = getString(channel);
    	System.out.println(mess);
    }*/
    //TODO ���ͻ��˷���Ϣ
    public void addMess(SocketChannel channel,String mess){
    	int length = 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.SHOW_ME);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO ���ʹ��󱨸�
    public void addError(SocketChannel channel,String mess){
    	int length = 4 + mess.getBytes().length + 4;
    	buffer = ByteBuffer.allocateDirect(length);
    	buffer.putInt(Protocol_Config.ERROR);
    	put(mess);
    	writeBuffer(channel);
    }
    //TODO ��ȡ���дʣ���txt�ļ��л�ȡ��
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
        	System.out.println("û���趨���дʣ�������������");
        }
        String[] list = text.split("\r\n");
        
    	return list;
    }
  
}

