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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessagesProviderTest {

    private final MessagesProvider messagesProvider = MessagesProvider.forDefaultLocale();

    @Test
    public void byDefaultTheNameOfMethodIsUsedAsMessageKey() {
        assertThat(messages().foo(), is("The Foo Message"));
    }

    @Test
    public void keyCanBeOverriddenWithAnnotation() {
        assertThat(messages().messageWithKey(), is("The Bar Message"));
    }

    @Test
    public void defaultMessagesCanBeSpecified() {
        assertThat(messages().defaultMessage(), is("my default message"));
    }

    @Test
    public void messageSourceIsConsultedBeforeUsingDefaultMessage() {
        assertThat(messages().bar(), is("The Bar Message"));
    }

    @Test
    public void ifMessageIsNotFoundErrorStringIsReturned() {
        assertThat(messages().unknown(), is("???unknown???"));
    }

    @Test
    public void messagesSupportParameters() {
        assertThat(messages().messageWithParameters("foo", 42), is("str: foo, x: 42"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void methodsWithVoidReturnTypeAreNotAllowed() {
        messagesProvider.create(MessagesWithVoidReturnType.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void methodsWithNonStringReturnTypeAreNotAllowed() {
        messagesProvider.create(MessagesWithListReturnType.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void methodsWithNonStringReturnTypeAreNotAllowedEvenWhenInherited() {
        messagesProvider.create(MessagesWithInheritedInvalidMethods.class);
    }

    private TestMessages messages() {
        return messagesProvider.create(TestMessages.class);
    }

    interface MessagesWithVoidReturnType extends Messages {
        void foo();
    }

    interface MessagesWithListReturnType extends Messages {
        List<String> foo();
    }

    interface MessagesWithInheritedInvalidMethods extends MessagesWithListReturnType {
    }
}
