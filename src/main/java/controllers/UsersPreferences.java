package controllers;

import java.util.prefs.Preferences;

/**
 * Classe utilitaire pour gérer les préférences utilisateur
 */
public class UsersPreferences {
    
    private static final String THEME_KEY = "app_theme";
    private static final Preferences prefs = Preferences.userNodeForPackage(UsersPreferences.class);
    
    /**
     * Enregistre la préférence de thème de l'utilisateur
     * @param theme "light" ou "dark"
     */
    public static void saveThemePreference(String theme) {
        prefs.put(THEME_KEY, theme);
    }
    
    /**
     * Récupère la préférence de thème enregistrée
     * @return le thème préféré ou null si non défini
     */
    public static String getThemePreference() {
        return prefs.get(THEME_KEY, null);
    }
    
    /**
     * Réinitialise toutes les préférences
     */
    public static void resetAllPreferences() {
        try {
            prefs.clear();
        } catch (Exception e) {
            System.err.println("Erreur lors de la réinitialisation des préférences: " + e.getMessage());
        }
    }
}