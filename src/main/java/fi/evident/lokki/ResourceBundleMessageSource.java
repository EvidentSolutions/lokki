/*
 * Copyright (c) 2011 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fi.evident.lokki;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static fi.evident.lokki.Utils.requireNonNull;

/**
 * A MessageSource which uses Java's normal {@link ResourceBundle}-mechanism
 * for loading the localization messages.
 */
public final class ResourceBundleMessageSource implements MessageSource {

    private final String baseName;
    private final LocaleProvider localeProvider;

    public ResourceBundleMessageSource(String baseName, Locale locale) {
        this(baseName, new FixedLocaleProvider(locale));
    }

    public ResourceBundleMessageSource(String baseName, LocaleProvider localeProvider) {
        this.baseName = requireNonNull(baseName);
        this.localeProvider = requireNonNull(localeProvider);
    }

    @Override
    public String getMessage(String key) {
        Locale locale = localeProvider.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);

        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }
}
