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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static fi.evident.lokki.Utils.requireNonNull;

/**
 * A MessageSource which uses Java's normal {@link ResourceBundle}-mechanism
 * for loading the localization messages.
 */
final class ResourceBundleMessageSource implements MessageSource {

    @Nonnull
    private final List<String> baseNames;

    @Nonnull
    private final LocaleProvider localeProvider;

    ResourceBundleMessageSource(@Nonnull List<String> baseNames, @Nonnull LocaleProvider localeProvider) {
        this.baseNames = new ArrayList<String>(baseNames);
        this.localeProvider = requireNonNull(localeProvider);
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key) {
        Locale locale = localeProvider.getLocale();

        for (String baseName : baseNames) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
                return bundle.getString(key);
            } catch (MissingResourceException e) {
                // Ok, try next bundle
            }
        }

        return null;
    }
}
