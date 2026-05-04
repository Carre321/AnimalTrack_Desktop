package com.tonin.animaltrack.views.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.tonin.animaltrack.model.dto.AnimalDTO;

public class AnimalTableRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof AnimalDTO) {
            AnimalDTO animal = (AnimalDTO) value;
            setText(getColumnValue(animal, column));
        } else {
            setText("");
        }

        return this;
    }

    private String getColumnValue(AnimalDTO animal, int column) {
        switch (column) {
        case 0:
            return defaultString(animal.getCrotal());
        case 1:
            return defaultString(animal.getNombre());
        case 2:
            return defaultString(animal.getRazaNombre());
        case 3:
            return defaultString(animal.getSexoNombre());
        case 4:
            return defaultString(animal.getGranjaNombre());
        default:
            return "";
        }
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
