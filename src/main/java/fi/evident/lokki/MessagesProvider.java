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

import fi.evident.lokki.Messages.DefaultMessage;
import fi.evident.lokki.Messages.Key;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static fi.evident.lokki.Utils.proxy;
import static fi.evident.lokki.Utils.requireNonNull;
import static java.text.MessageFormat.format;

/**
 * Provides various ways of constructing Messages-objects that can be
 * queried to provide localized messages.
 */
public final class MessagesProvider {

    private final LocaleProvider localeProvider;
    private final MessageSource messageSource;

    public static MessagesProvider forDefaultLocale() {
        return new MessagesProvider(DefaultLocaleProvider.INSTANCE);
    }

    public static MessagesProvider forLocale(Locale locale) {
        return new MessagesProvider(new FixedLocaleProvider(locale));
    }

    public MessagesProvider(LocaleProvider localeProvider) {
        this.localeProvider = requireNonNull(localeProvider);
        this.messageSource = null;
    }

    public MessagesProvider(MessageSource messageSource) {
        this.messageSource = requireNonNull(messageSource);
        this.localeProvider = null;
    }

    /**
     * Creates a new Messages object based on given class.
     */
    public <T extends Messages> T create(Class<T> messagesClass) {
        verifyClass(messagesClass);

        MessageSource source = (messageSource != null)
            ? messageSource
            : new ResourceBundleMessageSource(bundleNamesFor(messagesClass), localeProvider);

        return proxy(messagesClass, new MyInvocationHandler(messagesClass, source));
    }

    private static List<String> bundleNamesFor(Class<? extends Messages> messagesClass) {
        List<String> bundles = new ArrayList<String>();
        addBundleNames(messagesClass, bundles);
        return bundles;
    }

    private static void addBundleNames(Class<?> messagesClass, List<String> bundles) {
        bundles.add(messagesClass.getName());

        for (Class<?> parent : messagesClass.getInterfaces())
            if (parent != Messages.class)
                addBundleNames(parent, bundles);
    }

    private static <T extends Messages> void verifyClass(Class<T> messagesClass) {
        if (!messagesClass.isInterface())
            throw new IllegalArgumentException("class is not an interface: " + messagesClass.getName());
        
        for (Method method : messagesClass.getMethods())
            if (method.getReturnType() != String.class)
                throw new IllegalArgumentException("invalid method definition: " + method);
    }

    private static final class MyInvocationHandler implements InvocationHandler {

        private final Class<?> messagesClass;
        private final MessageSource messageSource;

        public MyInvocationHandler(Class<?> messagesClass, MessageSource messageSource) {
            this.messagesClass = requireNonNull(messagesClass);
            this.messageSource = requireNonNull(messageSource);
        }

        @Override
        public Object invoke(Object target, Method method, Object[] args) throws Throwable {
            String key = resolveMessageKey(method);
            String pattern = resolveMessagePattern(key, method);

            if (pattern != null)
                return (args != null && args.length != 0) ? format(pattern, args) : pattern;
            else
                return "???" + key + "???";
        }

        private static String resolveMessageKey(Method method) {
            Key keyAnnotation = method.getAnnotation(Key.class);
            return (keyAnnotation != null) ? keyAnnotation.value() : method.getName();
        }

        private String resolveMessagePattern(String key, Method method) {
            String message = messageSource.getMessage(key);
            if (message != null) {
                return message;
            } else {
                DefaultMessage defaultMessage = method.getAnnotation(DefaultMessage.class);
                return defaultMessage != null ? defaultMessage.value() : null;
            }
        }
    }
}
