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

package org.kitodo.production.helper;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import org.apache.commons.lang3.LocaleUtils;
import org.kitodo.config.ConfigCore;
import org.omnifaces.util.Ajax;
import org.omnifaces.util.Faces;

@Named("LegalTexts")
@ApplicationScoped
public class LegalTexts implements Serializable {

    public static final String TERMS_OF_USE = "termsOfUse";
    public static final String DATA_PRIVACY = "dataPrivacy";
    public static final String IMPRINT = "imprint";

    private static String termsOfUseText = "";
    private static String dataPrivacyText = "";
    private static String imprintText = "";

    private static final Locale ENGLISH = Locale.ENGLISH;
    private static final String DEFAULT_LANGUAGE = Locale.GERMAN.getLanguage();

    /**
     * Get terms of use text.
     *
     * @return terms of use text
     */
    public String getTermsOfUseText() {
        return termsOfUseText;
    }

    /**
     * Set terms of use texts.
     *
     * @param newText
     *            new terms of use text
     */
    public void setTermsOfUseText(String newText) {
        termsOfUseText = newText;
    }

    /**
     * Get data privacy text.
     *
     * @return data privacy text
     */
    public String getDataPrivacyText() {
        return dataPrivacyText;
    }

    /**
     * Set data privacy text.
     *
     * @param newText
     *            new data privacy text
     */
    public void setDataPrivacyText(String newText) {
        dataPrivacyText = newText;
    }

    /**
     * Get imprint text.
     *
     * @return imprint text
     */
    public String getImprintText() {
        return imprintText;
    }

    /**
     * Set imprint text.
     *
     * @param newText
     *            new imprint text
     */
    public void setImprintText(String newText) {
        imprintText = newText;
    }

    /**
     * Initialize legal texts.
     */
    @PostConstruct
    public static void initializeTexts() {
        updateTexts(DEFAULT_LANGUAGE);
    }

    /**
     * Reload all legal texts in the given language.
     *
     * @param language
     *            language in which legal texts are loaded
     */
    public static void updateTexts(String language) {
        termsOfUseText = loadText(TERMS_OF_USE, language);
        dataPrivacyText = loadText(DATA_PRIVACY, language);
        imprintText = loadText(IMPRINT, language);
        if (Objects.nonNull(Faces.getContext())) {
            Ajax.update("imprintDialog", "dataPrivacyDialog", "termsOfUseDialog");
        }
    }

    /**
     * Load legal text file identified by given String 'legalTextName' and in the
     * given language and return its text content. If no file for the given
     * parameters can be found, a corresponding default text from the message
     * properties files will be used.
     *
     * <p>If a region-specific language is given (e.g. "de_CH") and no file for
     * it exists, the file of the base language (e.g. "de") is loaded instead.
     * If the file of the given language is only the default text, the file of
     * the English fallback language is used when it contains a customized
     * text, so that a customized legal text is always preferred over the
     * placeholder. The placeholder itself is only used when none of the
     * candidates has been customized.</p>
     *
     * @param legalTextName
     *            String identifying the legal text file to load
     * @param language
     *            String specifying in which language the legal text is to be loaded
     * @return the text content of the legal text
     */
    public static String loadText(String legalTextName, String language) {
        for (String localeTag : candidateLocales(language)) {
            String filePath = ConfigCore.getKitodoConfigDirectory() + "legal_" + legalTextName + "_" + localeTag + ".html";
            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
                if (!isDefaultText(legalTextName, localeTag, content)) {
                    return content;
                }
            } catch (IOException e) {
                // no file in this language, continue with the next candidate
            }
        }
        return getDefaultText(legalTextName, language);
    }

    /**
     * Get the languages in which a legal text is to be searched for, most
     * specific first: the given language, its base language when it is
     * region-specific, and the English fallback language as a last resort.
     *
     * @param language
     *            language requested for the legal text
     * @return the languages to try, in order
     */
    private static List<String> candidateLocales(String language) {
        List<String> candidates = new LinkedList<>();
        Locale locale = LocaleUtils.toLocale(language);
        candidates.add(locale.toString());
        if (Objects.nonNull(locale.getCountry()) && !locale.getCountry().isEmpty()) {
            candidates.add(LocaleUtils.toLocale(locale.getLanguage()).toString());
        }
        if (!candidates.contains(ENGLISH.getLanguage())) {
            candidates.add(ENGLISH.getLanguage());
        }
        return candidates;
    }

    /**
     * Check whether the given text is only the default text of the message
     * properties files, i.e. not a customized legal text.
     *
     * @param legalTextName
     *            String identifying the legal text
     * @param language
     *            language of the default text to compare with
     * @param content
     *            text content to check
     * @return true if the content equals the default text of the given language
     */
    private static boolean isDefaultText(String legalTextName, String language, String content) {
        return content.strip().equals(getDefaultText(legalTextName, language).strip());
    }

    private static String getDefaultText(String legalText, String language) {
        Locale locale = LocaleUtils.toLocale(language);
        return "<p>" + Helper.getString(locale, legalText + "DefaultText") + "</p><br/>"
                + "<p>" + Helper.getString(locale, "adjustSettingText") + "</p>";
    }
}
