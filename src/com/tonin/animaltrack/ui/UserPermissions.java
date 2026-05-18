package com.tonin.animaltrack.ui;

import com.tonin.animaltrack.model.dto.UsuarioLoginDTO;

public class UserPermissions {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String GANADERO = "GANADERO";
    private static final String VETERINARIO = "VETERINARIO";

    private final UsuarioLoginDTO user;

    public UserPermissions(UsuarioLoginDTO user) {
        this.user = user;
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public boolean isAdmin() {
        return hasRole(ADMIN);
    }

    public boolean isGanadero() {
        return hasRole(GANADERO);
    }

    public boolean isVeterinario() {
        return hasRole(VETERINARIO);
    }

    public boolean canOpenAnimals() {
        return isAdmin() || isGanadero() || isVeterinario();
    }

    public boolean canOpenHome() {
        return isAuthenticated();
    }

    public boolean canOpenEvents() {
        return isAdmin() || isGanadero() || isVeterinario();
    }

    public boolean canOpenVeterinarios() {
        return isAdmin();
    }

    public boolean canOpenAdmin() {
        return isAdmin();
    }

    public boolean canCreateAnimal() {
        return isAdmin() || isGanadero();
    }

    public boolean canEditAnimal() {
        return isAdmin() || isGanadero();
    }

    public boolean canDeleteAnimal() {
        return isAdmin() || isGanadero();
    }

    public boolean canCreateEvento() {
        return isAdmin() || isGanadero() || isVeterinario();
    }

    public boolean canEditEvento() {
        return isAdmin() || isGanadero() || isVeterinario();
    }

    public boolean canDeleteEvento() {
        return isAdmin() || isGanadero() || isVeterinario();
    }

    public boolean canOpenGanaderoViews() {
        return isAdmin() || isGanadero();
    }

    public boolean canOpenVeterinarioViews() {
        return isAdmin() || isVeterinario();
    }

    public boolean canManageOwnFarm() {
        return isAdmin() || isGanadero();
    }

    public boolean canManageAssignedFarm() {
        return isAdmin() || isVeterinario();
    }

    private boolean hasRole(String expected) {
        return user != null && user.getRol() != null && expected.equalsIgnoreCase(user.getRol());
    }
}
