package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tonin.animaltrack.dao.criteria.VeterinarioCriteria;
import com.tonin.animaltrack.model.dto.VeterinarioDTO;
import com.tonin.animaltrack.service.VeterinarioService;
import com.tonin.animaltrack.service.impl.VeterinarioServiceImpl;
import com.tonin.animaltrack.views.VeterinarioSearchView;

public class VeterinarioSearchController extends Controller implements KeyListener {

	private static Logger logger = LogManager.getLogger(VeterinarioSearchController.class.getName());

	private VeterinarioSearchView view = null;
	private VeterinarioService service = null;

	public VeterinarioSearchController(VeterinarioSearchView view) {
		super(view, "Buscar",
				new ImageIcon(VeterinarioSearchController.class.getResource("/animaltrack/icons/32/search.png")));
		this.view = view;
		this.service = new VeterinarioServiceImpl();
	}

	public void doAction() {
		VeterinarioCriteria criteria = view.getCriteria();
		try {
			List<VeterinarioDTO> results = service.findByCriteria(criteria);
			view.setModel(results);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			view.setModel(null);
		}
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
