package server;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @TODO：协议，有数据的封装和读取,//在需要数据的封装和读取的类那里继承这个类
 * 在之后所有类需要发送和接收数据的时候，都要通过这个类的方法来进行
 * @fileName : .Protocol_DataExpress.java
 * date | author | version |   
 * 2015年10月31日 | Jiong | 1.0 |
 */
public class Protocol_DataExpress {
	public static Charset charset = Charset.forName("UTF-8");
	public byte[] bytes;
	public int size;
	public ByteBuffer buffer = null;
	//用到buffer的时候：当你返回一个动作码，后面接着要有数据的时候就要将动作码和数据一并放到
	//buffer里面，最后再将buffer写入到管道（socketChannel）里面
	//注意！！buffer用之前需要指定长度来分配缓冲区，
	//例如：ByteBuffer buffer = ByteBuffer.allocateDirect(4);
	//就是分配一个4字节的缓冲区
	//然后buffer的putInt（）是默认占用4个字节的，所以动作码都要四个字节的长度
	//每次指令的长度可以归结为：每个动作码加4，每个int类型加4，每个String类型
	//要加4+String.getBytes().length:见下面定义的put()方法
	//例子可以参见客户端的login()方法
	//当你仅仅返回一个动作码(通常是反馈信息)的时候就可以直接使用下面的writeInt
	
	//TODO 只写入一个数字（占据四个字节），可用于反馈信息
	public static void writeInt(int a, SocketChannel socketChannel) {
		try {
			ByteBuffer buffer = ByteBuffer.allocateDirect(4);
			buffer.putInt(a);
			//flip()使缓冲区为一系列新的通道写入或相对获取 操作做好准备：
			//它将限制设置为当前位置，然后将位置设置为0。
			buffer.flip();
			socketChannel.write(buffer);
			buffer.clear();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("错误2:"+e.getMessage());
		}
	}

	
	
	/**
	 * TODO 向buffer获取int类型
	 * @return
	 * @throws IOException
	 */
	public int getInt(SocketChannel socketChannel) throws IOException {
		// TODO getInt
		buffer = ByteBuffer.allocateDirect(4); //整型，四个字节
		size = socketChannel.read(buffer);
		if (size >= 0) {
			buffer.flip();
			//读取四个字节（Buffer默认是四个，可以按ctrl键点下面的getInt()方法看看介绍）
			int userIdnum = buffer.getInt(); 
			//清除读取的缓冲区
			buffer.clear(); 
			return userIdnum;
		}
		return -100; //获取错误
	}

	/**
	 * 向bufferer获取String
	 * @return
	 */
	public String getString(SocketChannel socketChannel) {
		// TODO getString
		try {
			//！！！！！！getInt()方法是先获取该字符串的长度，再为其分配缓冲区
			buffer = ByteBuffer.allocateDirect(getInt(socketChannel));
			size = socketChannel.read(buffer);
			bytes = new byte[size];
			if (size >= 0) {
				buffer.flip();
				buffer.get(bytes); //调用Buffer的get方法将数据写入到字节数组
				buffer.clear();
			}
			return new String(bytes, "GBK"); //返回字符串
		} catch (Exception e) {
			return "error";
		}
	}
	//获取对象类型
	public Object getObject(SocketChannel socketChannel){
		try {
			buffer = ByteBuffer.allocateDirect(getInt(socketChannel));
			size = socketChannel.read(buffer);
			bytes = new byte[size];
			if (size >= 0) {
				buffer.flip();
				buffer.get(bytes); //调用Buffer的get方法将数据写入到字节数组
				buffer.clear();
			}
			return getObject(bytes);
		} catch (Exception e) {
			return null;
		}
		
	}
	/**
	 * 字节数组与Object的相互转换
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
   protected static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException {  
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);  
        ObjectInputStream oi = new ObjectInputStream(bi);  
        Object obj = oi.readObject();  
        bi.close();  
        oi.close();  
        return obj;  
    } 
   protected static byte[] getBytes(Object obj) throws IOException {  
       ByteArrayOutputStream bout = new ByteArrayOutputStream();  
       ObjectOutputStream out = new ObjectOutputStream(bout);  
       out.writeObject(obj);  
       out.flush();  
       byte[] bytes = bout.toByteArray();  
       bout.close();  
       out.close();  
       return bytes;  
   } 
	//TODO 清空
	public void readOver(SocketChannel socketChannel) {
		try {
			buffer = ByteBuffer.allocateDirect(1024);
			while ((size = socketChannel.read(buffer)) >= 0) {
				buffer.clear();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Buffer没有put(String)的方法，所以自己实现一个
	 * ！！！因为可能有多个String数据，所以每个String都需要在前面加一个它的长度来作为
	 * 获取这个String时候分配的缓冲区大小
	 * 在buffer中放字符串的字节数组长度以及该字节数组
	 * @param string
	 */
	public void put(String string) {
		//先put进一个占据四个字节的数字（即String的长度）
		buffer.putInt(string.getBytes().length);
		//再将String给put进去
		buffer.put(string.getBytes());
	}
	//TODO　放入对象（先转为字节数组）
	public void putObject(byte[] bytes){
		buffer.putInt(bytes.length);
		buffer.put(bytes);
	}
	
	/**
	 * 对将buffer写入socketchannel.
	 */
	public void writeBuffer(SocketChannel channel) {
		buffer.flip();// 对buffer进行转换
		int num = 0;
		if(channel==null){
			return;
		}
		while (buffer.hasRemaining()) { //还有元素则继续
			try {
				int y = channel.write(buffer);
			} catch (Exception e) {
				num++;
				if(num==5){
					break;
				}
			}
		}
		buffer.clear();
	}
	
	
	//指定socketChannel的写入
	/*public static void write(String line, SocketChannel socketChannel) {
		try {
			socketChannel.write(charset.encode(line));
		} catch (Exception e) {
			System.out.println("错误3:"+e.getMessage());
		}
	}
*/
}

