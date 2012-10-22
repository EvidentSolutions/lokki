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

import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessagesProviderWithMessageSourceTest {

    private final MockMessageSource messageSource = new MockMessageSource();
    private final MessagesProvider messagesProvider = new MessagesProvider(messageSource);

    @Test
    public void byDefaultTheNameOfMethodIsUsedAsMessageKey() {
        messageSource.setMessage("foo", "My message");
        assertThat(messages().foo(), is("My message"));
    }

    @Test
    public void keyCanBeOverriddenWithAnnotation() {
        messageSource.setMessage("bar", "My other message");
        assertThat(messages().messageWithKey(), is("My other message"));
    }

    @Test
    public void defaultMessagesCanBeSpecified() {
        assertThat(messages().defaultMessage(), is("my default message"));
    }

    @Test
    public void messageSourceIsConsultedBeforeUsingDefaultMessage() {
        messageSource.setMessage("bar", "My message");
        assertThat(messages().bar(), is("My message"));
    }

    @Test
    public void ifMessageIsNotFoundErrorStringIsReturned() {
        assertThat(messages().unknown(), is("???unknown???"));
    }

    @Test
    public void messagesSupportParameters() {
        assertThat(messages().messageWithParameters("foo", 42), is("str: foo, x: 42"));
    }

    private TestMessages messages() {
        return messagesProvider.create(TestMessages.class);
    }

    private static class MockMessageSource implements MessageSource {

        private final Map<String,String> messages = new HashMap<String, String>();

        @Override
        @Nullable
        public String getMessage(@Nonnull String key) {
            return messages.get(key);
        }

        public void setMessage(@Nonnull String key, String message) {
            messages.put(key, message);
        }
    }
}
