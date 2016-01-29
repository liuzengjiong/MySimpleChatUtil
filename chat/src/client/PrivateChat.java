package client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import server.Protocol_Config;
import util.AttributeSetUtil;

/**
 * @TODO��
 * @fileName : client.PrivateChat.java
 * date | author | version |   
 * 2015��11��14�� | Jiong | 1.0 |
 */
public class PrivateChat extends JFrame {
	private JSplitPane chatGroundPane = null;
	private JScrollPane messagePane = null;
	private JTextPane messageText = null;
	
	private JPanel bottomPane = null;
	private JScrollPane inputPane = null;
	private JTextArea inputArea = null;
	private JButton btnOk = null;
	StyledDocument doc;//���촰�ڵķ��(JTextPane)
	
	private Base base;
	private String _chaterName;
	private String _chaterId;
	
	private StyledDocument getDocStyle(){
		if(doc==null){
			 doc = getMsgContent().getStyledDocument();
		}
		return doc;
	}
	
	private JPanel getBottomPane(){
		if (bottomPane == null) {
			JLabel lblSend = new JLabel();
			lblSend.setText("���룺");
			lblSend.setBounds(new Rectangle(9, 11, 39, 18));
			bottomPane = new JPanel();
			bottomPane.setLayout(null);
			bottomPane.add(lblSend, null);
			bottomPane.add(getInputPane(), null);
			bottomPane.add(getBtnOk(), null);
		}
		return bottomPane;
	}
	private JButton getBtnOk(){
		if (btnOk == null) {
			btnOk = new JButton();
			btnOk.setText("����");
			btnOk.setLocation(new Point(420, 28));
			btnOk.setSize(new Dimension(60, 22));
			btnOk.setPreferredSize(new Dimension(60, 22));
			btnOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					sendMess();
				}
			});
		}
		return btnOk;
	}
	private JScrollPane getInputPane(){
		if(inputPane==null){
			inputPane = new JScrollPane();
			inputPane.setViewportView(getInputArea());
			inputPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			inputPane.setLocation(new Point(51, 9));
			inputPane.setSize(new Dimension(322, 40));
		}
		return inputPane;
	}
	private JTextArea getInputArea(){
		if (inputArea == null) {
			inputArea = new JTextArea();
			inputArea.setLineWrap(true);
			inputArea.setWrapStyleWord(true);
			inputArea.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e){
					if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_ENTER){
						txtSendEnter();
					}
				}
			});
		}
		return inputArea;
	}
	//��Ϣ������
		private JScrollPane getMsgPane(){
			if(messagePane==null){
				messagePane = new JScrollPane();
				messagePane.setViewportView(getMsgContent());
			}
			return messagePane;
		}
		//��Ϣ������
		private JTextPane getMsgContent(){
			if(messageText==null){
				messageText = new JTextPane();
				messageText.setEditable(false);
			}
			return messageText;
		}
		//���첿��
		private JSplitPane getChatGroundPane(){
			if(chatGroundPane==null){
				chatGroundPane = new JSplitPane();
				chatGroundPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
				chatGroundPane.setDividerLocation(400);
				chatGroundPane.setTopComponent(getMsgPane());
				chatGroundPane.setBottomComponent(getBottomPane());
			}
			return chatGroundPane;
		}
		private String getStarChar(String str){
	    	String s = "";
	    	for(int i = 0;i<str.length();i++){
	    		s = s+"*";
	    	}
	    	return s;
	    }
		//���дʹ���
		 private String warningChecked(String mess){
		    	String[] list = base.getWarningWord();
				if(list==null){
					return mess;
				}
				for(String str:list){
					if(str.replace(" ","").length()==0){
						continue;
					}
					mess = mess.replaceAll(str,getStarChar(str));
				}
		    	return mess;
		    }
		public void sendMess(){
			String str = inputArea.getText();
			if(str.replaceAll(" ", "").length()==0){
				sysMess("�뷢�����������Ϣ");
				return;
			}
			Date date=new Date(System.currentTimeMillis());
		    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");   
		    String time = df.format(date);
		    String header = base.getNickname() +"   "+time +"\r\n";
			inputArea.setText("");
			try {
				if(messageText.getText().length()!=0){
			    	getDocStyle().insertString(doc.getLength(), "\r\n", null);
				}
				String mess = str;
				str = warningChecked(str);
				getDocStyle().insertString(doc.getLength(), header, AttributeSetUtil.getHeadAttr());
				getDocStyle().insertString(doc.getLength(),"  "+ str, AttributeSetUtil.getSelfAttr());
				messageText.setCaretPosition(doc.getLength());
				base.sendPrivateMess(Protocol_Config.ROOMNUM_PRIVATE_CHAT,_chaterId,mess);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inputArea.requestFocus();
		}
		public void receiceMess(String nickname,String mess){
			Date date=new Date(System.currentTimeMillis());
		    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");   
		    String time = df.format(date);
		    String header = nickname +"   "+time +"\r\n";
			try {
				if(messageText.getText().length()!=0){
			    	getDocStyle().insertString(doc.getLength(), "\r\n", null);
				}
				getDocStyle().insertString(doc.getLength(), header, AttributeSetUtil.getHeadAttr());
				getDocStyle().insertString(doc.getLength(),"  "+ mess, AttributeSetUtil.getOtherAttr());
				messageText.setCaretPosition(doc.getLength());
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void txtSendEnter(){
			sendMess();
		}
		public void sysMess(String mess){
			try {
				mess = "ϵͳ��ʾ��"+mess;
				if(messageText.getText().length()!=0){
			    	getDocStyle().insertString(doc.getLength(), "\r\n", null);
				}
				getDocStyle().insertString(doc.getLength(), mess, AttributeSetUtil.getSysAttr());
				messageText.setCaretPosition(doc.getLength());
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public PrivateChat(Base base,String nickName,boolean noVisible){
			this._chaterName = nickName;
			this._chaterId = nickName.substring(nickName.lastIndexOf("/"),nickName.lastIndexOf(")"));
			this.base = base;
			setVisible(false);
			init();
		}
		public PrivateChat(Base base,String nickName){
			this._chaterName = nickName;
			this._chaterId = nickName.substring(nickName.lastIndexOf("/"),nickName.lastIndexOf(")"));
			this.base = base;
			setVisible(true);
			init();	
		}
		private void init(){
			setTitle("˽��----"+_chaterName);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			//�㴰�ڡ��ŵ��¼�
			/*addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "ȷ���ر����촰����", "��ʾ",JOptionPane.YES_NO_OPTION);
				if(n==0){
					dispose();
					//base.closePrivateChat(_chaterId);
				}
			}
			});*/
			setResizable(false);
			System.out.println("�����б�");
			Container container = getContentPane();
			container.add(getChatGroundPane());
			this.setSize(500,500);
			inputArea.requestFocus();
		}
		public String getChaterName(){
			return _chaterName;
		}
		public String getChaterId(){
			return _chaterId;
		}
		public void setChaterName(String name){
			this._chaterName = name;
			setTitle(name);
		}
		public void setFocu(){
			inputArea.requestFocus();
		}
		
}

