package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import model.User;
import server.Protocol_Config;
import util.AttributeSetUtil;

/**
 * TODO 客户端界面（大厅）
 * @author Jiong
 *
 */
public class GroundChatClient extends JFrame{

	private final JPanel contentPanel = new JPanel();
	private JList listChat = null;
	private JSplitPane basePane = null;
	
	private JPanel roomListPane = null;
	
	private JSplitPane rightPane = null;
	private JSplitPane chatGroundPane = null;
	
	private JScrollPane userListPane = null;
	private JTree userTree = null;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;
	
	private JScrollPane messagePane = null;
	private JTextPane messageText = null;
	
	private JPanel bottomPane = null;
	private JScrollPane inputPane = null;
	private JTextPane inputArea = null;
	private JButton btnOk = null;
	private JButton btnImage = null;
	
	private JLabel labChaterList = null;
	
	private JTextField jtfName = null;
	private JLabel labName = null;
	private JLabel labId = null;
	
	
	StyledDocument doc;//聊天窗口的风格(JTextPane)
	private PopupMenu _popMenu;
	String list[];
	Base base;
	
	//增加用户信息盘
	private JSplitPane mostRightPane = null;
	private JPanel userInfoPane = null;
	private JSplitPane getMostRightPane(){
		if(mostRightPane==null){
			mostRightPane = new JSplitPane();
			mostRightPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			mostRightPane.setDividerLocation(350);
			mostRightPane.setTopComponent(getUserListPane());
			mostRightPane.setBottomComponent(getUserInfoPane());
		}
		return mostRightPane;
	}
	private JPanel getUserInfoPane(){
		if(userInfoPane==null){
			userInfoPane = new JPanel();
			userInfoPane.setLayout(null);
			userInfoPane.setBorder(BorderFactory.createTitledBorder("我的信息"));
			JLabel _labName = new JLabel("昵称：");
			_labName.setBounds(new Rectangle(5,30,40,20));
			jtfName = new JTextField();
			jtfName.setBounds(new Rectangle(50,30,100,20));
			jtfName.setEditable(false);
			jtfName.setText(base.getUser().getAbsoluteNickname());
			labName = new JLabel("修改");
			labName.setForeground(Color.BLUE);
			labName.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e){
					if(e.getButton()== 1){
						changeName();
					}	
				}
			});
			labName.setBounds(new Rectangle(160,30,50,20));
			JLabel _labId  = new JLabel("ID：");
			_labId.setBounds(new Rectangle(5,60,40,20));
			labId = new JLabel(base.getUser().getId());
			labId.setBounds(new Rectangle(50,60,100,20));
			
			userInfoPane.add(_labName);
			userInfoPane.add(jtfName);
			userInfoPane.add(labName);
			userInfoPane.add(_labId);
			userInfoPane.add(labId);
			userInfoPane.add(getLabChaterList());
		}
		return userInfoPane;
	}
	public void setLabChaterListVisible(boolean wantToShan){
		labChaterList.setVisible(true);
		if(wantToShan)
			new Thread(new ShanDong()).start();
	}
	private void changeName(){
		String s = labName.getText();
		if(s.equals("修改")){
			labName.setText("确定");
			jtfName.setEditable(true);
			jtfName.requestFocus();
		}else{
			labName.setText("修改");
			jtfName.setEditable(false);
			User user = base.getUser();
			user.setNickName(jtfName.getText());
			base.setUser(user);
			labName.requestFocus();
		}
	}
	private JLabel getLabChaterList(){
		if(labChaterList==null){
			labChaterList = new JLabel("<html><u>您有未查看的私信，点此查看</u></html>");
			labChaterList.setVisible(false);
			labChaterList.setForeground(Color.RED);
			labChaterList.setBounds(new Rectangle(5,100,200,30));
			labChaterList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e){
					if(e.getButton()== 1){
						base.setPrivateChatListDialog();
						base.setPrivateChatListDialogVisible(true);
						labChaterList.setVisible(false);
					}
					
				}
			});
		}
		return labChaterList;
	}
	private StyledDocument getDocStyle(){
		if(doc==null){
			 doc = getMsgContent().getStyledDocument();
		}
		return doc;
	}
	
	private JPanel getBottomPane(){
		if (bottomPane == null) {
			/*JLabel lblSend = new JLabel();
			lblSend.setText("输入：");
			lblSend.setBounds(new Rectangle(9, 11, 39, 18));*/
			bottomPane = new JPanel();
			bottomPane.setLayout(null);
			//bottomPane.add(lblSend, null);
			bottomPane.add(getInputPane(), null);
			bottomPane.add(getBtnOk(), null);
		}
		return bottomPane;
	}
	private JButton getBtnOk(){
		if (btnOk == null) {
			btnOk = new JButton();
			btnOk.setText("发送");
			btnOk.setLocation(new Point(356, 58));
			btnOk.setSize(new Dimension(80, 22));
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
			inputPane.setBorder(BorderFactory.createTitledBorder("输入"));
			inputPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			inputPane.setLocation(new Point(3, 5));
			inputPane.setSize(new Dimension(350, 80));
		}
		return inputPane;
	}
	private JTextPane getInputArea(){
		if (inputArea == null) {
			inputArea = new JTextPane();
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
	
	//消息滚动盘
	private JScrollPane getMsgPane(){
		if(messagePane==null){
			messagePane = new JScrollPane();
			messagePane.setViewportView(getMsgContent());
		}
		return messagePane;
	}
	//消息内容盘
	private JTextPane getMsgContent(){
		if(messageText==null){
			messageText = new JTextPane();
			messageText.setEditable(false);
		}
		return messageText;
	}
	//底盘
	private JSplitPane getBasePane(){
		if(basePane==null){
			basePane = new JSplitPane();
			basePane.setDividerLocation(300);
			basePane.setRightComponent(getRightPane());
			basePane.setLeftComponent(getRoomListPane());
		}
		return basePane;
	}
	//右部分
	private JSplitPane getRightPane(){
		if(rightPane==null){
			rightPane = new JSplitPane();
			rightPane.setDividerLocation(450);
			rightPane.setLeftComponent(getChatGroundPane());
			rightPane.setRightComponent(getMostRightPane());
		}
		return rightPane;
	}
	//聊天部分
	private JSplitPane getChatGroundPane(){
		if(chatGroundPane==null){
			chatGroundPane = new JSplitPane();
			chatGroundPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			chatGroundPane.setDividerLocation(400);
			chatGroundPane.setBorder(BorderFactory.createTitledBorder("聊天广场"));
			chatGroundPane.setTopComponent(getMsgPane());
			chatGroundPane.setBottomComponent(getBottomPane());
		}
		return chatGroundPane;
	}
	//用户列表部分
	private JScrollPane getUserListPane(){
		if(userListPane==null){
			userListPane = new JScrollPane();
			userListPane.setViewportView(getUserTree());
		}
		return userListPane;
	}
	public JTree getUserTree(){
		if(userTree == null){
			userTree = new JTree();
			userTree.addMouseListener(new MouseAdapter(){
				 public void mousePressed(MouseEvent e) {  
				        //右键
				        if (e.getButton() == 3) { 
				        	 TreePath path = userTree.getPathForLocation(e.getX(), e.getY());//得到树结点的路径 
						        if (path == null) {  
						            return;  
						        }  
						        userTree.setSelectionPath(path); 
						        DefaultMutableTreeNode note = (DefaultMutableTreeNode) userTree.getLastSelectedPathComponent();
						        String name = note.toString();//获得这个结点的名称
						        //点自己不反应
						        if(name.equals(base.getNickname()))
						        	return;
						        _popMenu.set_aimUser(name);
				            _popMenu.show(userTree, e.getX(), e.getY()); 
				        }  
				    }
			});
			root = new DefaultMutableTreeNode("世界部分在线用户(<=20)");
			model = new DefaultTreeModel(root);
			userTree.setModel(model);
			userTree.updateUI();
		}
		return userTree;
	}
	public void setTree(String[] list){
		root.removeAllChildren();
		for(String str:list){
			addTree(str);
		}
		userTree.setModel(model);
		userTree.updateUI();
	}
	public void addTree(String str){
		root.add(new DefaultMutableTreeNode(str));
	}
	//房间列表部分
	private JPanel getRoomListPane(){
		if(roomListPane==null){
			roomListPane = new JPanel();
			roomListPane.setLayout(new BorderLayout());	
			listChat = new JList(list);
			listChat.setBorder(BorderFactory.createTitledBorder("聊天室列表"));
			listChat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(1, 2));
			JButton ok = new JButton("进入");
			JButton newH = new JButton("创建聊天室");
			
			ok.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//int i = listFrame.getSelectedIndex();
					if(listChat.getSelectedValue()==null)
						return;
					String str = listChat.getSelectedValue().toString();
					if(str.endsWith("(已进入)")){
						base.showError("你已经进了该房间");
						return;
					}
					base.intoRoom(str);
					
				}	
			});
			newH.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String roomName = null;
					try{
						roomName = 
								JOptionPane.showInputDialog(null,"请输入房间名：\n","提示",JOptionPane.PLAIN_MESSAGE,null,null,"随便聊聊").toString();
					}catch(Exception ee){
						
					}
					if(roomName==null)
						return;
					if(roomName.length()==0)
						roomName = "我的房间";
					base.createRoom(roomName);
				}	
			});
			panel.add(ok);
			panel.add(newH);
			roomListPane.add(new JScrollPane(listChat),BorderLayout.CENTER);
			roomListPane.add(panel,BorderLayout.SOUTH);
		}
		return roomListPane;
	}
	public void  setList(String[] list){
		listChat.setListData(list);
	}
	private void closeClient(){
		base.exitClient();
		System.exit(0);
	}
	private String getStarChar(String str){
    	String s = "";
    	for(int i = 0;i<str.length();i++){
    		s = s+"*";
    	}
    	return s;
    }
	//敏感词过滤
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
			sysMess("请发送有意义的消息");
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
			String mess  = str;
			str = warningChecked(str);
			getDocStyle().insertString(doc.getLength(), header, AttributeSetUtil.getHeadAttr());
			getDocStyle().insertString(doc.getLength(),"  "+ str, AttributeSetUtil.getSelfAttr());
			messageText.setCaretPosition(doc.getLength());
			base.sendMess(Protocol_Config.ROOMNUM_GROUND, mess);
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
			mess = "系统提示："+mess;
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
	/**
	 * Create the dialog.
	 */
	public GroundChatClient(Base base,String[] list) {
		this.base = base;
		this.list = list;
		
		_popMenu = new PopupMenu(base);
		setTitle("聊天室客户端----"+base.getNickname());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//点窗口×号的事件
		addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			int n = JOptionPane.showConfirmDialog(null, "确定退出吗(退出关闭所有窗口)", "提示",JOptionPane.YES_NO_OPTION);
			if(n==0){
				closeClient();
			}
		}
		});
		setResizable(false);
		System.out.println("房间列表");
		Container container = getContentPane();
		container.add(getBasePane());
		this.setSize(1000,530);
		this.setVisible(true);
		inputArea.requestFocus();
	}
	boolean isShanDong = false;
	class ShanDong implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(isShanDong)
				return;
			int num = 0;
			isShanDong = true;
			while(num++<6){
				try{
					if(labChaterList.isVisible())
						Thread.sleep(500);
					else
						Thread.sleep(200);
				}catch(Exception e){	
				}
				if(labChaterList.isVisible())
					labChaterList.setVisible(false);
				else
					labChaterList.setVisible(true);
			}
			labChaterList.setVisible(true);
			isShanDong = false;
		}
		
	}
}
                                                                                                                                                                                                                           