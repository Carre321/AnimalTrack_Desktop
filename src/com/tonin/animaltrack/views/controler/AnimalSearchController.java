package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tonin.animaltrack.dao.Results;
import com.tonin.animaltrack.dao.criteria.AnimalCriteria;
import com.tonin.animaltrack.model.dto.AnimalDTO;
import com.tonin.animaltrack.service.AnimalService;
import com.tonin.animaltrack.service.impl.AnimalServiceImpl;
import com.tonin.animaltrack.views.AnimalSearchView;

public class AnimalSearchController extends Controller 
implements KeyListener, ItemListener {

	private static Logger logger = LogManager.getLogger(AnimalSearchController.class.getName());

	private AnimalSearchView view = null;
	private AnimalService service = null;

	public AnimalSearchController(AnimalSearchView view) {
		super(view, "Buscar",
				new ImageIcon(AnimalSearchController.class.getResource("/animaltrack/icons/32/search.png")));
		this.view = view;
		this.service = new AnimalServiceImpl();
	}

	public void doAction () {
		buscarPagina(1);
	}

	public void buscarPagina(int pagina) {
		if (pagina < 1) {
			pagina = 1;
		}
		AnimalCriteria criteria = view.getCriteria();
		try {
			int pageSize = view.getPageSize();
			int from = ((pagina - 1) * pageSize) + 1;
			Results<AnimalDTO> results = service.findByCriteria(criteria, from, pageSize);
			view.setResults(results, pagina);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(view, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		AnimalCriteria criteria = view.getCriteria();
		if (criteria.getCrotalLike()!= null && criteria.getCrotalLike().length()>=4) {
			buscarPagina(1);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			buscarPagina(1);
		}
		
	}

}
