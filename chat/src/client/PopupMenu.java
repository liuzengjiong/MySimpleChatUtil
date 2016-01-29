package client;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * @TODO：右击菜单
 * @fileName : client.PopMenu.java
 * date | author | version |   
 * 2015年11月14日 | Jiong | 1.0 |
 */
public class PopupMenu extends JPopupMenu{
	private String _aimUser;
	private boolean isAdmin = false;
	private JMenuItem _privateChat;
	private JMenuItem _kickUser;
	private JMenuItem _lookUserMess;
	private int roomNum;
	
	private Base base;
	public PopupMenu(Base base){
		this.base = base;
		setSize(200, 200);
		setVisible(true);
		_privateChat = new JMenuItem("私聊");
		//_lookUserMess = new JMenuItem("查看信息");
		_privateChat.addActionListener(new ActionDeal());
		//_lookUserMess.addActionListener(new ActionDeal());
		add(_privateChat);
		//add(_lookUserMess);
	}
	
	public void setAdmin(int roomNum){
		_kickUser = new JMenuItem("踢出房间");
		_kickUser.addActionListener(new ActionDeal());
		add(_kickUser);
		this.roomNum = roomNum;
	}
	public String get_aimUser() {
		return _aimUser;
	}
	public void set_aimUser(String _aimUser) {
		this._aimUser = _aimUser;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	/**
	 * 事件处理
	 * @author Jiong
	 *
	 */
	class ActionDeal implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource()==_privateChat){
				base.gotoPrivateChat(get_aimUser());
			}
			if(e.getSource()==_lookUserMess){
				
			}
			if(e.getSource()==_kickUser){
				int n = JOptionPane.showConfirmDialog(null, "确定踢出"+get_aimUser()+"吗？", "提示",JOptionPane.YES_NO_OPTION);
				if(n==0){
					String id = _aimUser.substring(_aimUser.lastIndexOf("/"),_aimUser.lastIndexOf(")"));
					base.kickUser(roomNum,id);
				}
			}
		}
		
	}
}

