package config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import client.Base;

/**
 * @TODO�������࣬IP��ַ���˿ڵȡ���ȡ���Ӻ���ŵ�ѡ�������ŵ�
 * @fileName : config.Config.java
 * date | author | version |   
 * 2015��11��11�� | Jiong | 1.0 |
 */
public class Config {
	static Selector selector;
    // �������ͨ�ŵ��ŵ�
 	static SocketChannel socketChannel;
    // Ҫ���ӵķ�����IP��ַ
   static String hostIp = "127.0.0.1";
    // Ҫ���ӵ�Զ�̷������ڼ����Ķ˿�
   static int hostListenningPort = 12000;
   
   static Base base;
   public static void setBase(Base b){
	   base = b;
   }
   public static Base getBase(){
	   return base;
   }
   
    
   //TODO ��ȡ�˿ͻ��˵�selector
   public static Selector linkServer(){
	   if(selector==null){
		   try {
				socketChannel = SocketChannel.open(new InetSocketAddress(hostIp,
				            hostListenningPort));
				  socketChannel.configureBlocking(false);
			      // �򿪲�ע��ѡ�������ŵ�
			      selector = Selector.open();
			      socketChannel.register(selector, SelectionKey.OP_READ);
			} catch (IOException e) {
				//throw new RuntimeException("����"+e.getMessage());
				return null;
			}
	   }
	   return selector;
   }
   //TODO ��ȡ�˿ͻ��˵�socketChannel
  public static SocketChannel getSocketChannel(){
	  int num = 0;
	  while(socketChannel==null && num<2){
		  linkServer();
		  num++;
	  }
	  if(socketChannel==null){
		  //throw new RuntimeException("����SocketChannel��ȡ����");
	  }
	  return socketChannel;
  }
}

