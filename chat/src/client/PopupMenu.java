package client;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * @TODO���һ��˵�
 * @fileName : client.PopMenu.java
 * date | author | version |   
 * 2015��11��14�� | Jiong | 1.0 |
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
		_privateChat = new JMenuItem("˽��");
		//_lookUserMess = new JMenuItem("�鿴��Ϣ");
		_privateChat.addActionListener(new ActionDeal());
		//_lookUserMess.addActionListener(new ActionDeal());
		add(_privateChat);
		//add(_lookUserMess);
	}
	
	public void setAdmin(int roomNum){
		_kickUser = new JMenuItem("�߳�����");
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
	 * �¼�����
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
				int n = JOptionPane.showConfirmDialog(null, "ȷ���߳�"+get_aimUser()+"��", "��ʾ",JOptionPane.YES_NO_OPTION);
				if(n==0){
					String id = _aimUser.substring(_aimUser.lastIndexOf("/"),_aimUser.lastIndexOf(")"));
					base.kickUser(roomNum,id);
				}
			}
		}
		
	}
}

