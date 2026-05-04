package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.dao.criteria.AnimalCriteria;
import com.tonin.animaltrack.model.dto.AnimalDTO;
import com.tonin.animaltrack.service.AnimalService;
import com.tonin.animaltrack.service.impl.AnimalServiceImpl;
import com.tonin.animaltrack.views.AnimalSearchView;

public class AnimalSearchController extends Controller 
implements KeyListener, ItemListener {

	private AnimalSearchView view = null;
	private AnimalService service = null;

	public AnimalSearchController(AnimalSearchView view) {
		super(view, "Buscar",
				new ImageIcon(AnimalSearchController.class.getResource("/nuvola/32x32/1746_find_find.png")));
		this.view = view;
		this.service = new AnimalServiceImpl();
	}

	public void doAction () {
		AnimalCriteria criteria = view.getCriteria();
		List<AnimalDTO> results = service.findByCriteria(criteria);
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
		AnimalCriteria criteria = view.getCriteria();
		if (criteria.getCrotalLike()!= null && criteria.getCrotalLike().length()>=4) {
			doAction();	
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		doAction();
		
	}

}
