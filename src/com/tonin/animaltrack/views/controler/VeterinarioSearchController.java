package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.dao.criteria.VeterinarioCriteria;
import com.tonin.animaltrack.model.dto.VeterinarioDTO;
import com.tonin.animaltrack.service.VeterinarioService;
import com.tonin.animaltrack.service.impl.VeterinarioServiceImpl;
import com.tonin.animaltrack.views.VeterinarioSearchView;

public class VeterinarioSearchController extends Controller implements KeyListener {

	private VeterinarioSearchView view = null;
	private VeterinarioService service = null;

	public VeterinarioSearchController(VeterinarioSearchView view) {
		super(view, "Buscar",
				new ImageIcon(VeterinarioSearchController.class.getResource("/nuvola/32x32/1746_find_find.png")));
		this.view = view;
		this.service = new VeterinarioServiceImpl();
	}

	public void doAction() {
		VeterinarioCriteria criteria = view.getCriteria();
		List<VeterinarioDTO> results = service.findByCriteria(criteria);
		view.setModel(results);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		doAction();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		doAction();
	}
}
