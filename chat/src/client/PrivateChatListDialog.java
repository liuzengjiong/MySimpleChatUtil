package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * @TODO��
 * @fileName : client.PrivateChatListDialog.java
 * date | author | version |   
 * 2015��11��14�� | Jiong | 1.0 |
 */
public class PrivateChatListDialog extends JDialog{
	Base base;
	String[] list;
	private JList listFrame = null;
	private String[] getList(){
		return list;
	}
	private Base getBase(){
		return base;
	}
	public PrivateChatListDialog(Base base,String[] list){
		this.base = base;
		this.list = list;
		init();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//�㴰�ڡ��ŵ��¼�
		addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			int n = 0;
			if(getList().length>0){
				n = JOptionPane.showConfirmDialog(null, "����"+getList().length+"����ϵ��δ�򿪣�ȷ�Ϲرգ�", "��ʾ",JOptionPane.YES_NO_OPTION);
			}
			if(n==0){
				dispose();
				if(getList().length>0)
					//����������ʾ
					getBase().setLabChaterListVisible(false);
			}
		}
		});
	}
	public void init(){
		setResizable(false);
		Container container = getContentPane();
		setLayout(new BorderLayout());	
		listFrame = new JList(list);
		listFrame.setBorder(BorderFactory.createTitledBorder("��˽�����б�,˫����"));
		listFrame.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listFrame.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1&&e.getClickCount()==2){
					if(listFrame.getSelectedValue()==null)
						return;
					String str = listFrame.getSelectedValue().toString();
					base.gotoPrivateChat(str);
					if(list.length==0){
						dispose();
					}
				}
			}
		});
		container.add(new JScrollPane(listFrame),BorderLayout.CENTER);
		this.setSize(400,500);
	}
	public void setList(String[] list){
		this.list = list;
		listFrame.setListData(list);
	}
}

