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
 * @TODO��Э�飬�����ݵķ�װ�Ͷ�ȡ,//����Ҫ���ݵķ�װ�Ͷ�ȡ��������̳������
 * ��֮����������Ҫ���ͺͽ������ݵ�ʱ�򣬶�Ҫͨ�������ķ���������
 * @fileName : .Protocol_DataExpress.java
 * date | author | version |   
 * 2015��10��31�� | Jiong | 1.0 |
 */
public class Protocol_DataExpress {
	public static Charset charset = Charset.forName("UTF-8");
	public byte[] bytes;
	public int size;
	public ByteBuffer buffer = null;
	//�õ�buffer��ʱ�򣺵��㷵��һ�������룬�������Ҫ�����ݵ�ʱ���Ҫ�������������һ���ŵ�
	//buffer���棬����ٽ�bufferд�뵽�ܵ���socketChannel������
	//ע�⣡��buffer��֮ǰ��Ҫָ�����������仺������
	//���磺ByteBuffer buffer = ByteBuffer.allocateDirect(4);
	//���Ƿ���һ��4�ֽڵĻ�����
	//Ȼ��buffer��putInt������Ĭ��ռ��4���ֽڵģ����Զ����붼Ҫ�ĸ��ֽڵĳ���
	//ÿ��ָ��ĳ��ȿ��Թ��Ϊ��ÿ���������4��ÿ��int���ͼ�4��ÿ��String����
	//Ҫ��4+String.getBytes().length:�����涨���put()����
	//���ӿ��Բμ��ͻ��˵�login()����
	//�����������һ��������(ͨ���Ƿ�����Ϣ)��ʱ��Ϳ���ֱ��ʹ�������writeInt
	
	//TODO ֻд��һ�����֣�ռ���ĸ��ֽڣ��������ڷ�����Ϣ
	public static void writeInt(int a, SocketChannel socketChannel) {
		try {
			ByteBuffer buffer = ByteBuffer.allocateDirect(4);
			buffer.putInt(a);
			//flip()ʹ������Ϊһϵ���µ�ͨ��д�����Ի�ȡ ��������׼����
			//������������Ϊ��ǰλ�ã�Ȼ��λ������Ϊ0��
			buffer.flip();
			socketChannel.write(buffer);
			buffer.clear();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("����2:"+e.getMessage());
		}
	}

	
	
	/**
	 * TODO ��buffer��ȡint����
	 * @return
	 * @throws IOException
	 */
	public int getInt(SocketChannel socketChannel) throws IOException {
		// TODO getInt
		buffer = ByteBuffer.allocateDirect(4); //���ͣ��ĸ��ֽ�
		size = socketChannel.read(buffer);
		if (size >= 0) {
			buffer.flip();
			//��ȡ�ĸ��ֽڣ�BufferĬ�����ĸ������԰�ctrl���������getInt()�����������ܣ�
			int userIdnum = buffer.getInt(); 
			//�����ȡ�Ļ�����
			buffer.clear(); 
			return userIdnum;
		}
		return -100; //��ȡ����
	}

	/**
	 * ��bufferer��ȡString
	 * @return
	 */
	public String getString(SocketChannel socketChannel) {
		// TODO getString
		try {
			//������������getInt()�������Ȼ�ȡ���ַ����ĳ��ȣ���Ϊ����仺����
			buffer = ByteBuffer.allocateDirect(getInt(socketChannel));
			size = socketChannel.read(buffer);
			bytes = new byte[size];
			if (size >= 0) {
				buffer.flip();
				buffer.get(bytes); //����Buffer��get����������д�뵽�ֽ�����
				buffer.clear();
			}
			return new String(bytes, "GBK"); //�����ַ���
		} catch (Exception e) {
			return "error";
		}
	}
	//��ȡ��������
	public Object getObject(SocketChannel socketChannel){
		try {
			buffer = ByteBuffer.allocateDirect(getInt(socketChannel));
			size = socketChannel.read(buffer);
			bytes = new byte[size];
			if (size >= 0) {
				buffer.flip();
				buffer.get(bytes); //����Buffer��get����������д�뵽�ֽ�����
				buffer.clear();
			}
			return getObject(bytes);
		} catch (Exception e) {
			return null;
		}
		
	}
	/**
	 * �ֽ�������Object���໥ת��
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
	//TODO ���
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
	 * Bufferû��put(String)�ķ����������Լ�ʵ��һ��
	 * ��������Ϊ�����ж��String���ݣ�����ÿ��String����Ҫ��ǰ���һ�����ĳ�������Ϊ
	 * ��ȡ���Stringʱ�����Ļ�������С
	 * ��buffer�з��ַ������ֽ����鳤���Լ����ֽ�����
	 * @param string
	 */
	public void put(String string) {
		//��put��һ��ռ���ĸ��ֽڵ����֣���String�ĳ��ȣ�
		buffer.putInt(string.getBytes().length);
		//�ٽ�String��put��ȥ
		buffer.put(string.getBytes());
	}
	//TODO�����������תΪ�ֽ����飩
	public void putObject(byte[] bytes){
		buffer.putInt(bytes.length);
		buffer.put(bytes);
	}
	
	/**
	 * �Խ�bufferд��socketchannel.
	 */
	public void writeBuffer(SocketChannel channel) {
		buffer.flip();// ��buffer����ת��
		int num = 0;
		if(channel==null){
			return;
		}
		while (buffer.hasRemaining()) { //����Ԫ�������
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
	
	
	//ָ��socketChannel��д��
	/*public static void write(String line, SocketChannel socketChannel) {
		try {
			socketChannel.write(charset.encode(line));
		} catch (Exception e) {
			System.out.println("����3:"+e.getMessage());
		}
	}
*/
}

