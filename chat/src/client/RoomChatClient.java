package client;



import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import model.User;
import util.AttributeSetUtil;
/**
 * TODO：聊天室界面窗口
 * @author Jiong
 *
 */
public class RoomChatClient extends JFrame {
	private static final long serialVersionUID = 1L;
	
	Base base = null;
	String[] roomList = null; //房间列表
	GroundChatClient roomListDialog = null; //大厅
	int roomNum = 0;
	String roomTitle = null;
	StyledDocument doc;//聊天窗口的风格
	private PopupMenu _popMenu; //右键菜单
	private JTree tree = null; //用户列表
	private DefaultMutableTreeNode root; //用户列表根节点
	private DefaultTreeModel model; //用户列表（树）的数据模型
	private JPanel jContentPane = null;
	private JSplitPane jSplitPane = null;
	private JSplitPane jSplitPane1 = null;
	private JScrollPane jScrollPane = null;
	private JPanel jPanel = null;
	private JTextPane txtContent = null;
	private JLabel lblSend = null;
	private JScrollPane txtSend = null;
	private JTextArea txtSendArea = null;
	private JButton btnSend = null;
	public  User user = new User();
	private JScrollPane jScrollPane1 = null; //右边的用户列表滚动框
	

	
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerLocation(450);
			jSplitPane.setRightComponent(getJScrollPane1());
			jSplitPane.setLeftComponent(getJSplitPane1());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jSplitPane1	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane1() {
		if (jSplitPane1 == null) {
			jSplitPane1 = new JSplitPane();
			jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane1.setTopComponent(getJScrollPane());
			jSplitPane1.setBottomComponent(getJPanel());
			jSplitPane1.setDividerLocation(400);
		}
		return jSplitPane1;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getTxtContent());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			lblSend = new JLabel();
			lblSend.setText("输入：");
			lblSend.setBounds(new Rectangle(9, 11, 39, 18));
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(lblSend, null);
			jPanel.add(getTxtSend(), null);
			jPanel.add(getBtnSend(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes txtContent	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextPane getTxtContent() {
		if (txtContent == null) {
			txtContent = new JTextPane();
			txtContent.setEditable(false);
		}
		return txtContent;
	}
	private StyledDocument getDocStyle(){
		if(doc==null){
			 doc = getTxtContent().getStyledDocument();
		}
		return doc;
	}
	
	private JScrollPane getTxtSend(){
		if(txtSend==null){
			txtSend = new JScrollPane();
			txtSend.setViewportView(getTxtSendArea());
			txtSend.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			txtSend.setLocation(new Point(51, 9));
			txtSend.setSize(new Dimension(250, 50));
		}
		return txtSend;
	}
	/**
	 * This method initializes txtSend	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextArea getTxtSendArea() {
		if (txtSendArea == null) {
			txtSendArea = new JTextArea();
			txtSendArea.setLineWrap(true);
			txtSendArea.setWrapStyleWord(true);
			txtSendArea.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e){
					if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_ENTER){
						txtSendEnter();
					}
				}
			});
		}
		return txtSendArea;
	}

	/**
	 * This method initializes btnSend	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnSend() {
		if (btnSend == null) {
			btnSend = new JButton();
			btnSend.setText("发送");
			btnSend.setLocation(new Point(366, 38));
			btnSend.setSize(new Dimension(60, 22));
			btnSend.setPreferredSize(new Dimension(60, 22));
			btnSend.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					sendMess();
				}
			});
		}
		return btnSend;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getUserTree());
		}
		return jScrollPane1;
	}
	/**
	 * @param args
	 */
/*	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ChatClient thisClass = new ChatClient();
				
			}
		});
	}*/
	public RoomChatClient(){super();};
	/**
	 * This is the default constructor
	 */
	public RoomChatClient(Base base) {
		this.base = base;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		_popMenu = new PopupMenu(base);
		setVisible(true);
		setResizable(false);
		this.setSize(700, 500);
		this.setContentPane(getJContentPane());
		getJContentPane().updateUI();
		txtSendArea.requestFocus();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//点窗口×号的事件
		addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			int n = JOptionPane.showConfirmDialog(null, "确定退出房间吗？", "提示",JOptionPane.YES_NO_OPTION);
			if(n==0){
				exitRoom();
				dispose();
			}
		}
		});
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}
	
	public void setRoomTitle(String title){
		roomTitle = title;
		setTitle(title);
		String reg = "\\[(\\d+)\\]";
		Pattern pattern = Pattern.compile(reg);//样式
		Matcher matcher = pattern.matcher(title);//匹配
		if(matcher.find()){
			int roomNum = Integer.valueOf(matcher.group(1));
			this.roomNum = roomNum;
			System.out.println("房间号："+this.roomNum);
		}
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
		String str = txtSendArea.getText();
		if(str.replaceAll(" ", "").length()==0){
			sysMess("请发送有意义的消息");
			return;
		}
		Date date=new Date(System.currentTimeMillis());
	    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");   
	    String time = df.format(date);
	    String header = base.getNickname() +"   "+time +"\r\n";
		txtSendArea.setText("");
		try {
			if(txtContent.getText().length()!=0){
		    	getDocStyle().insertString(doc.getLength(), "\r\n", null);
			}
			String mess = str;
			str = warningChecked(str);
			getDocStyle().insertString(doc.getLength(), header, AttributeSetUtil.getHeadAttr());
			getDocStyle().insertString(doc.getLength(),"  "+ str, AttributeSetUtil.getSelfAttr());
			txtContent.setCaretPosition(doc.getLength());
			base.sendMess(roomNum, mess);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		txtSendArea.requestFocus();
	}
	public void receiceMess(String nickname,String mess){
		Date date=new Date(System.currentTimeMillis());
	    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");   
	    String time = df.format(date);
	    String header = nickname +"   "+time +"\r\n";
		try {
			if(txtContent.getText().length()!=0){
		    	getDocStyle().insertString(doc.getLength(), "\r\n", null);
			}
			getDocStyle().insertString(doc.getLength(), header, AttributeSetUtil.getHeadAttr());
			getDocStyle().insertString(doc.getLength(),"  "+ mess, AttributeSetUtil.getOtherAttr());
			txtContent.setCaretPosition(doc.getLength());
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
			if(txtContent.getText().length()!=0){
		    	getDocStyle().insertString(doc.getLength(), "\r\n", null);
			}
			getDocStyle().insertString(doc.getLength(), mess, AttributeSetUtil.getSysAttr());
			txtContent.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public JTree getUserTree(){
		if(tree == null){
			tree = new JTree();
			tree.addMouseListener(new MouseAdapter(){
				 public void mousePressed(MouseEvent e) {  
				        //右键
				        if (e.getButton() == 3) { 
				        	 TreePath path = tree.getPathForLocation(e.getX(), e.getY());//得到树结点的路径 
						        if (path == null) {  
						            return;  
						        }  
						        tree.setSelectionPath(path); 
						        DefaultMutableTreeNode note = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
						        String name = note.toString();//获得这个结点的名称
						        //点自己不反应
						        if(name.equals(base.getNickname()))
						        	return;
						        _popMenu.set_aimUser(name);
				            _popMenu.show(getJScrollPane1(), e.getX(), e.getY()); 
				        }  
				    }
			});
			root = new DefaultMutableTreeNode("房间的在线用户");
			model = new DefaultTreeModel(root);
			tree.setModel(model);
			tree.updateUI();
		}
		return tree;
	}
	public void setTree(String[] list){
		root.removeAllChildren();
		for(String str:list){
			addTree(str);
		}
		tree.setModel(model);
		tree.updateUI();
	}
	public void addTree(String str){
		root.add(new DefaultMutableTreeNode(str));
	}
	//设置成为管理员
	public void setAdmin(){
		_popMenu.setAdmin(roomNum);
	}
	public void exitRoom(){
		base.exitRoom(roomNum);
	}
	public void setRoomNum(int roomNum){
		this.roomNum = roomNum;
	}
	public int getRoomNum(){
		return this.roomNum;
	}
	public String getTitle(){
		return roomTitle;
	}
	
}
                                                                                                                                                   