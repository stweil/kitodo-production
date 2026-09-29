/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.editor;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.kitodo.config.ConfigCore;
import org.kitodo.production.helper.Helper;
import org.kitodo.production.helper.LegalTexts;
import org.kitodo.production.helper.LocaleHelper;

@Named("LegalTextEditor")
@ViewScoped
public class LegalTextEditor implements Serializable {

    private static List<String> legalTextTitles;
    private String currentLegalTextTitle;
    private String currentLegalTextContent;
    private String currentLanguage;

    /**
     * Default constructor.
     */
    public LegalTextEditor() {
        legalTextTitles = new LinkedList<>();

        legalTextTitles.add(LegalTexts.TERMS_OF_USE);
        legalTextTitles.add(LegalTexts.DATA_PRIVACY);
        legalTextTitles.add(LegalTexts.IMPRINT);

        currentLegalTextTitle = legalTextTitles.getFirst();

        Locale defaultLocale = FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
        currentLanguage = defaultLocale.toString();
        loadText();
    }

    /**
     * Load currently selected legal file and set it's text content to
     * 'currentLegalTextContent'.
     */
    private void loadText() {
        currentLegalTextContent = LegalTexts.loadText(this.currentLegalTextTitle, this.currentLanguage);
    }

    /**
     * Save text of currently selected legal text to file.
     */
    public void saveText() {
        String filePath = ConfigCore.getKitodoConfigDirectory() + "legal_" + this.currentLegalTextTitle + "_"
                + this.currentLanguage + ".html";
        try {
            Files.write(Paths.get(filePath), this.currentLegalTextContent.getBytes(StandardCharsets.UTF_8));
            LegalTexts.updateTexts(this.currentLanguage);
        } catch (IOException e) {
            Helper.setErrorMessage("ERROR: unable to save file '" + filePath + "'!");
        }
    }

    /**
     * Return list of legal texts.
     *
     * @return list of legal texts
     */
    public List<String> getLegalTextTitles() {
        return legalTextTitles;
    }

    /**
     * Return current legal text.
     *
     * @return current legal text
     */
    public String getCurrentLegalTextTitle() {
        return this.currentLegalTextTitle;
    }

    /**
     * Set current legal text.
     *
     * @param text
     *            current legal text
     */
    public void setCurrentLegalTextTitle(String text) {
        if (!Objects.equals(text, this.currentLegalTextTitle)) {
            this.currentLegalTextTitle = text;
            loadText();
        }
    }

    /**
     * Return current legal text content as String.
     *
     * @return current legal text content as String
     */
    public String getCurrentLegalTextContent() {
        return currentLegalTextContent;
    }

    /**
     * Set current legal text content to given String 'textString'.
     *
     * @param textString
     *            current legal text String
     */
    public void setCurrentLegalTextContent(String textString) {
        this.currentLegalTextContent = textString;
    }

    /**
     * Get the locales for which a legal text can be maintained.
     *
     * <p>The label of each option is rendered in the <em>current UI language</em> (so the names
     * follow the user's language setting) and includes the region for region-specific locales
     * (so de and de_CH are distinguishable). The value is the locale tag, which is also the
     * suffix of the corresponding legal_&lt;text&gt;_&lt;lang&gt;.html file.</p>
     *
     * @return list of locale options for the language menu
     */
    public List<LocaleOption> getAvailableLocales() {
        Locale uiLanguage = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        List<LocaleOption> options = new LinkedList<>();
        FacesContext.getCurrentInstance().getApplication().getSupportedLocales().forEachRemaining(locale -> {
            if (!locale.getLanguage().isEmpty()) {
                options.add(new LocaleOption(locale, uiLanguage));
            }
        });
        return options;
    }

    /**
     * Get language currently selected in the editor.
     *
     * @return language currently selected in the editor
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Set current language.
     *
     * @param language
     *            new current language
     */
    public void setCurrentLanguage(String language) {
        if (!Objects.equals(language, this.currentLanguage)) {
            this.currentLanguage = language;
            loadText();
        }
    }

    /**
     * A selectable legal-text language: the underlying locale (its string form is the value and
     * the suffix of the legal_&lt;text&gt;_&lt;lang&gt;.html file) and a display name rendered in
     * the given UI language.
     */
    public static class LocaleOption {

        private final Locale locale;
        private final String language;
        private final String displayLanguage;

        public LocaleOption(Locale locale, Locale uiLanguage) {
            this.locale = locale;
            this.language = locale.toString();
            this.displayLanguage = LocaleHelper.displayLanguage(locale, uiLanguage);
        }

        public Locale getLocale() {
            return locale;
        }

        public String getLanguage() {
            return language;
        }

        public String getDisplayLanguage() {
            return displayLanguage;
        }
    }
}
