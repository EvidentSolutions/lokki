package fi.evident.lokki;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageProviderWithCustomLocaleTest {

    @Test
    public void localeCanBeSpecifiedExplicitly() {
        MessagesProvider messagesProvider = MessagesProvider.forLocale(Locale.GERMAN);
        TestMessages messages = messagesProvider.create(TestMessages.class);

        assertThat(messages.foo(), is("Die Foo Lokalisierung Nachricht"));
    }
}
