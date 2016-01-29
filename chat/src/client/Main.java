package client;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Base();
					//Main frame = new Main();
					UIManager.LookAndFeelInfo  []info=UIManager.getInstalledLookAndFeels() ;  
					try {
						UIManager.setLookAndFeel(info[3].getClassName());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//SwingUtilities.updateComponentTreeUI(frame);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
