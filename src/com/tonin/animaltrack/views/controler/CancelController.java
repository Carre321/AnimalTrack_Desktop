package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.AbstractView;

public class CancelController extends Controller {

	public CancelController(AbstractView view) {
		super(view, "Cancelar",
				new ImageIcon(CancelController.class.getResource("/nuvola/32x32/1250_delete_delete.png")));
	}
	
	public void doAction() {
		AbstractView view = getView();
		JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, view);
		if (tabbedPane != null) {
			tabbedPane.remove(view);
			tabbedPane.revalidate();
			tabbedPane.repaint();
			return;
		}
		MainWindow.getInstance().remove(view);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		doAction();
	}
	
}
