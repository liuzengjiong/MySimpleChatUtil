package model;


import java.io.Serializable;
/**
 * TODO£∫ ”√ªßmodel
 * @author Jiong
 *
 */
public class User implements Serializable {	
	private static final long serialVersionUID = 1L;
	private String id = "";// IP+port
	private String ip = "";
	private int localPort = -1;	
	private String nickName = "";// Í«≥∆	
	private String headImg = "";// Õ∑œÒ
	
	public User(){super();}
	public User(String ip,int port, String nickName, String headImg) {
		super();
		this.ip = ip;
		this.localPort = port;
		this.nickName = nickName;
		this.headImg = headImg;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getLocalPort() {
		return localPort;
	}
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	public String getNickName() {
		return nickName+"("+getId()+")";
	}
	public String getAbsoluteNickname(){
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	
	
}
