package com.tonin.animaltrack.views;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

public final class FilterableComboBoxSupport {

    private FilterableComboBoxSupport() {
    }

    public static <T> void decorate(JComboBox<T> combo) {
        combo.setEditable(true);

        GenericToStringConverter converter = new GenericToStringConverter();
        List<T> allItems = new ArrayList<T>();
        boolean[] filtering = new boolean[] { false };
        boolean[] applyingText = new boolean[] { false };

        loadItems(combo, allItems);
        combo.addPropertyChangeListener("model", e -> {
            if (!filtering[0]) {
                loadItems(combo, allItems);
            }
        });
        combo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                filterModel(combo, allItems, converter, filtering, applyingText, false);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        Component editorComponent = combo.getEditor().getEditorComponent();
        if (!(editorComponent instanceof JTextComponent)) {
            return;
        }

        JTextComponent textComponent = (JTextComponent) editorComponent;
        textComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scheduleFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scheduleFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scheduleFilter();
            }

            private void scheduleFilter() {
                if (applyingText[0]) {
                    return;
                }
                SwingUtilities.invokeLater(() -> filterModel(combo, allItems, converter, filtering, applyingText, true));
            }
        });
    }

    private static <T> void loadItems(JComboBox<T> combo, List<T> allItems) {
        allItems.clear();
        ComboBoxModel<T> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            allItems.add(model.getElementAt(i));
        }
    }

    private static <T> void filterModel(JComboBox<T> combo, List<T> allItems, GenericToStringConverter converter,
            boolean[] filtering, boolean[] applyingText, boolean updatePopupVisibility) {
        Object editorItem = combo.getEditor().getItem();
        String text = editorItem == null ? "" : editorItem.toString();
        Object selectedItem = combo.getSelectedItem();
        String normalizedText = shouldIgnoreAsFilter(selectedItem, text, converter) ? "" : text.toLowerCase();

        DefaultComboBoxModel<T> filteredModel = new DefaultComboBoxModel<T>();
        for (T item : allItems) {
            String itemText = converter.getPreferredStringForItem(item);
            if (normalizedText.isEmpty() || itemText.toLowerCase().contains(normalizedText)) {
                filteredModel.addElement(item);
            }
        }

        filtering[0] = true;
        applyingText[0] = true;
        try {
            combo.setModel(filteredModel);
            if (containsItem(filteredModel, selectedItem)
                    && converter.getPreferredStringForItem(selectedItem).equals(text)) {
                combo.setSelectedItem(selectedItem);
            } else {
                combo.getEditor().setItem(text);
            }
            if (updatePopupVisibility && combo.isShowing()) {
                combo.setPopupVisible(filteredModel.getSize() > 0);
            }
        } finally {
            applyingText[0] = false;
            filtering[0] = false;
        }
    }

    private static <T> boolean containsItem(DefaultComboBoxModel<T> model, Object selectedItem) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i) == selectedItem) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldIgnoreAsFilter(Object selectedItem, String text, GenericToStringConverter converter) {
        String normalizedText = text == null ? "" : text.trim().toLowerCase();
        if ("todos".equals(normalizedText) || "todas".equals(normalizedText)
                || "sin seleccionar".equals(normalizedText)) {
            return true;
        }
        if (selectedItem == null) {
            return false;
        }
        String selectedText = converter.getPreferredStringForItem(selectedItem);
        if (!selectedText.equals(text)) {
            return false;
        }
        if (hasNullValue(selectedItem)) {
            return true;
        }
        return false;
    }

    private static boolean hasNullValue(Object item) {
        try {
            java.lang.reflect.Method method = item.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return method.invoke(item) == null;
        } catch (Exception ex) {
            return false;
        }
    }
}
