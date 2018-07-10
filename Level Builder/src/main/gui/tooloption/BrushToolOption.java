package main.gui.tooloption;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BrushToolOption extends JPanel {

	private static final long serialVersionUID = -2065099349590232101L;
	
	private JSlider slider;
	private JLabel lblSlider;
	private JPanel pnlBrushSize;
	
	public BrushToolOption() {
		pnlBrushSize = new JPanel();
		slider = new JSlider(1, 5);
		slider.setValue(1);
		lblSlider = new JLabel("1");
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblSlider.setText(slider.getValue() + "");
			}
		});
		
		pnlBrushSize.setBorder(BorderFactory.createTitledBorder("Brush size"));
		pnlBrushSize.add(slider);
		pnlBrushSize.add(lblSlider);
		add(pnlBrushSize);
	}

	public JSlider getSlider() {
		return slider;
	}

	public JLabel getLblSlider() {
		return lblSlider;
	}

	public JPanel getPnlBrushSize() {
		return pnlBrushSize;
	}
}
