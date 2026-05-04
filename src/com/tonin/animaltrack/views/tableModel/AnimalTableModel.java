package com.tonin.animaltrack.views.tableModel;

import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.tonin.animaltrack.model.dto.AnimalDTO;

public class AnimalTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNS = { "Crotal", "Nombre", "Raza", "Sexo", "Granja" };

    private List<AnimalDTO> model;

    public AnimalTableModel(List<AnimalDTO> model) {
        this.model = model == null ? Collections.<AnimalDTO>emptyList() : model;
    }

    @Override
    public int getRowCount() {
        return model.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return model.get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return AnimalDTO.class;
    }
}
