package com.tonin.animaltrack.views;

import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

public class GenericToStringConverter extends ObjectToStringConverter {

    @Override
    public String getPreferredStringForItem(Object item) {
        return item == null ? "" : item.toString();
    }

}
