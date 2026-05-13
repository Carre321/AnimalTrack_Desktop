package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tonin.animaltrack.dao.Results;
import com.tonin.animaltrack.dao.criteria.EventoCriteria;
import com.tonin.animaltrack.model.dto.EventoDTO;
import com.tonin.animaltrack.service.EventoService;
import com.tonin.animaltrack.service.impl.EventoServiceImpl;
import com.tonin.animaltrack.views.EventoSearchView;

public class EventoSearchController extends Controller implements KeyListener, ItemListener {

	private static Logger logger = LogManager.getLogger(EventoSearchController.class.getName());

	private EventoSearchView view = null;
	private EventoService service = null;

	public EventoSearchController(EventoSearchView view) {
		super(view, "Buscar",
				new ImageIcon(EventoSearchController.class.getResource("/animaltrack/icons/32/search.png")));
		this.view = view;
		this.service = new EventoServiceImpl();
	}

	public void doAction() {
		buscarPagina(1);
	}

	public void buscarPagina(int pagina) {
		if (pagina < 1) {
			pagina = 1;
		}
		EventoCriteria criteria = view.getCriteria();
		try {
			int pageSize = view.getPageSize();
			int from = ((pagina - 1) * pageSize) + 1;
			Results<EventoDTO> results = service.findByCriteria(criteria, from, pageSize);
			view.setResults(results, pagina);
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
		buscarPagina(1);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			buscarPagina(1);
		}
	}
}
