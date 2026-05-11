package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.dao.criteria.EventoCriteria;
import com.tonin.animaltrack.model.dto.EventoDTO;
import com.tonin.animaltrack.service.EventoService;
import com.tonin.animaltrack.service.impl.EventoServiceImpl;
import com.tonin.animaltrack.views.EventoSearchView;

public class EventoSearchController extends Controller implements KeyListener {

	private EventoSearchView view = null;
	private EventoService service = null;

	public EventoSearchController(EventoSearchView view) {
		super(view, "Buscar",
				new ImageIcon(EventoSearchController.class.getResource("/animaltrack/icons/32/search.png")));
		this.view = view;
		this.service = new EventoServiceImpl();
	}

	public void doAction() {
		EventoCriteria criteria = view.getCriteria();
		List<EventoDTO> results = service.findByCriteria(criteria);
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
