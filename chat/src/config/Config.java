package config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import client.Base;

/**
 * @TODO：配置类，IP地址、端口等。获取连接后的信道选择器与信道
 * @fileName : config.Config.java
 * date | author | version |   
 * 2015年11月11日 | Jiong | 1.0 |
 */
public class Config {
	static Selector selector;
    // 与服务器通信的信道
 	static SocketChannel socketChannel;
    // 要连接的服务器IP地址
   static String hostIp = "127.0.0.1";
    // 要连接的远程服务器在监听的端口
   static int hostListenningPort = 12000;
   
   static Base base;
   public static void setBase(Base b){
	   base = b;
   }
   public static Base getBase(){
	   return base;
   }
   
    
   //TODO 获取此客户端的selector
   public static Selector linkServer(){
	   if(selector==null){
		   try {
				socketChannel = SocketChannel.open(new InetSocketAddress(hostIp,
				            hostListenningPort));
				  socketChannel.configureBlocking(false);
			      // 打开并注册选择器到信道
			      selector = Selector.open();
			      socketChannel.register(selector, SelectionKey.OP_READ);
			} catch (IOException e) {
				//throw new RuntimeException("错误："+e.getMessage());
				return null;
			}
	   }
	   return selector;
   }
   //TODO 获取此客户端的socketChannel
  public static SocketChannel getSocketChannel(){
	  int num = 0;
	  while(socketChannel==null && num<2){
		  linkServer();
		  num++;
	  }
	  if(socketChannel==null){
		  //throw new RuntimeException("错误：SocketChannel获取错误！");
	  }
	  return socketChannel;
  }
}

