package main.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import main.gui.tooloption.BrushToolOption;
import main.gui.tooloption.FillAllToolOption;
import main.map.Level;

public class Toolbar extends JToolBar implements ActionListener {

	private static final long serialVersionUID = 9175136347431142440L;
	private Level lvl;
	public static String CURRENT_TOOL = "";
	
	public Toolbar(Level lvl) {
		this.lvl = lvl;
		add(new JLabel("Tools    "));
		addSeparator();
		item("Brush", "brush");
		item("Fill all", "fillall");
//		item("Line", "line");
	}
	
	public void actionPerformed(ActionEvent e) {
		String x = e.getActionCommand();
		if(x.equals("brush")) {
			lvl.getTod().setOptionPanel(new BrushToolOption(), "Brush");
		} else if(x.equals("fillall")) {
			lvl.getTod().setOptionPanel(new FillAllToolOption(lvl), "Fill all");
//		} else if(x.equals("line")) {
//			lvl.getTod().setOptionPanel(new JPanel(), "Line tool");
		}
		CURRENT_TOOL = x;
	}
	
	private void item(String name, String action) {
		JButton item = new JButton(name);
		item.setActionCommand(action);
		item.addActionListener(this);
		add(item);
	}

	public Level getLvl() {
		return lvl;
	}

	public void setLvl(Level lvl) {
		this.lvl = lvl;
	}

}
