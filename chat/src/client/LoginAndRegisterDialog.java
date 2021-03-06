package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @TODO：
 * @fileName : client.LoginAndRegisterDialog.java
 * date | author | version |   
 * 2016年1月31日 | Jiong | 1.0 |
 */
public class LoginAndRegisterDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private Base base;
	
	private JPanel nicknamePanel;
	private JRadioButton jbLogin;
	private JTextField jtfUserId;
	private JPasswordField jtfPassword;
	private JTextField jtfNickname;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginAndRegisterDialog dialog = new LoginAndRegisterDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LoginAndRegisterDialog(Base base) {
		this.base = base;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new GridLayout(3,1));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel userIdPanel = new JPanel();
			userIdPanel.setLayout(new FlowLayout());
			JPanel passwordPanel = new JPanel();
			passwordPanel.setLayout(new FlowLayout());
			JPanel optionPanel = new JPanel();
			optionPanel.setLayout(new FlowLayout());
			
			jbLogin = new JRadioButton("登陆",true);
			JRadioButton jbRegister = new JRadioButton("注册");
			ButtonGroup group = new ButtonGroup();
			group.add(jbLogin);
			group.add(jbRegister);
			optionPanel.add(jbLogin);
			optionPanel.add(jbRegister);
			contentPanel.add(optionPanel);
			jbLogin.addItemListener(new MyItemListener());
			
			JLabel labUserId = new JLabel("账号:");
			JLabel labPassword = new JLabel("密码");
			jtfUserId = new JTextField();
			jtfPassword = new JPasswordField();
			
			userIdPanel.add(labUserId);
			userIdPanel.add(jtfUserId);
			jtfUserId.setPreferredSize(new Dimension(200,30));
			contentPanel.add(userIdPanel);
			
			passwordPanel.add(labPassword);
			passwordPanel.add(jtfPassword);
			jtfPassword.setPreferredSize(new Dimension(200,30));
			contentPanel.add(passwordPanel);
			
			nicknamePanel = new JPanel();
			nicknamePanel.setLayout(new FlowLayout());
			JLabel labNickname = new JLabel("昵称");
			jtfNickname = new JTextField();
			jtfNickname.setPreferredSize(new Dimension(200,30));
			nicknamePanel.add(labNickname);
			nicknamePanel.add(jtfNickname);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new GridLayout(1,1));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("确定");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if(jbLogin.isSelected()){
							login(jtfUserId.getText(),new String(jtfPassword.getPassword()));
						}else{
							register(jtfUserId.getText(),new String(jtfPassword.getPassword()),jtfNickname.getText());
						}
					}	
				});
			}
		}
		
	}
	
	public void register(String userId,String password,String nickname){
		String mess = base.register(userId, password, nickname);
		if(mess.equals("success")){
			int i = JOptionPane.showConfirmDialog(contentPanel, "注册成功，是否立即登陆？","注册反馈",JOptionPane.YES_NO_OPTION);
			if(i==JOptionPane.YES_OPTION){
				login(userId,password);
			}else{
				//dispose();
			}
		}else{
			JOptionPane.showMessageDialog(contentPanel, mess);
		}
		
	}
	public void login(String userId,String password){
		String mess = base.login(userId, password);
		if(mess.equals("success")){
			dispose();
		}else{
			JOptionPane.showMessageDialog(contentPanel, mess);
		}
	}
	//监听
	class MyItemListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e) {
			//选择登陆
			if(e.getStateChange() == ItemEvent.SELECTED){
				contentPanel.remove(nicknamePanel);
				contentPanel.setLayout(new GridLayout(3,1));
			}else{
				contentPanel.setLayout(new GridLayout(4,1));
				contentPanel.add(nicknamePanel);
			}
			contentPanel.updateUI();
		}
		
	}

}

                                      