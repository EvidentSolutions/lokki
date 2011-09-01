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
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Locale;

import static fi.evident.lokki.Utils.requireNonNull;

/**
 * Provides various ways of constructing Messages-objects that can be
 * queried to provide localized messages.
 */
public final class MessagesProvider {

    private MessagesProvider() { }

    /**
     * Creates a new Messages object based on given class. A ResourceBundle corresponding
     * to the class is loaded from the classpath and system's default locale is used for
     * picking the correct translations.
     */
    public static <T extends Messages> T create(Class<T> messagesClass) {
        return create(messagesClass, DefaultLocaleProvider.INSTANCE);
    }

    /**
     * Creates a new Messages object based on given class. A ResourceBundle corresponding
     * to the class is loaded from the classpath and given locale is used for
     * picking the correct translations.
     */
    public static <T extends Messages> T create(Class <T> messagesClass, Locale locale) {
        return create(messagesClass, new ConstantLocaleProvider(locale));
    }

    /**
     * Creates a new Messages object based on given class. A ResourceBundle corresponding
     * to the class is loaded from the classpath and given LocaleProvider is consulted
     * to resolve the locale used for selecting translations.
     */    
    public static <T extends Messages> T create(Class<T> messagesClass, LocaleProvider localeProvider) {
        return create(messagesClass, new ResourceBundleMessageSource(messagesClass.getName(), localeProvider));
    }

    /**
     * Creates a new Messages object based on given class. Given MessageSource functions
     * as the source of messages and is responsible for deciding what Locale to use and
     * how to load messages.
     */
    public static <T extends Messages> T create(Class<T> messagesClass, MessageSource messageSource) {
        InvocationHandler handler = new MyInvocationHandler(messagesClass, messageSource);
        Object proxy = Proxy.newProxyInstance(messagesClass.getClassLoader(), new Class<?>[] {messagesClass}, handler);
        return messagesClass.cast(proxy);
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
            Key keyAnnotation = method.getAnnotation(Key.class);
            String key = (keyAnnotation != null) ? keyAnnotation.value() : method.getName();

            String message = messageSource.getMessage(key);
            if (message != null)
                return format(message, args);

            DefaultMessage defaultMessage = method.getAnnotation(DefaultMessage.class);
            if (defaultMessage != null)
                return format(defaultMessage.value(), args);
            else
                return "???" + key + "???";
        }

        private static Object format(String pattern, Object[] args) {
            return (args != null && args.length != 0) ? MessageFormat.format(pattern, args) : pattern;
        }
    }
}
