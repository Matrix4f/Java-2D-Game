package main.gui.tooloption;

import javax.swing.JDialog;
import javax.swing.JPanel;

import main.DisplayFrame;

@SuppressWarnings("serial")
public class ToolOptionDialog extends JDialog {
	
	public ToolOptionDialog() {
		setTitle("Tool Options- No tool selected");
		setSize(250, 300);
		setLocation(DisplayFrame.getWidth() - getWidth() - 10, 250);
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public void setOptionPanel(JPanel panel, String toolName) {
		setContentPane(panel);
		setTitle("Tool Options- " + toolName);
		invalidate();
		validate();
		repaint();
	}
	
	public JPanel getOptionPanel() {
		return (JPanel) getContentPane();
	}
}
