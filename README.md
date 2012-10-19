# Lokki

Lokki takes away the pain from localization. It is inspired by Google
Web Toolkit's localization mechanism, but has no external dependencies
and can be included in any program and integrated with any framework.

To define the things that can be localized, we create an interface which
extends from `Messages`. We can optionally also specify the default messages
using annotations:

    :::java
    package myapp;

    import fi.evident.lokki.Messages;

    public interface Greetings extends Messages {

        @DefaultMessage("Good morning!")
        String goodMorning();

        @DefaultMessage("Good evening!")
        String goodEvening();

        @DefaultMessage("Hello, {0}!")
        String hello(String target);
    }

Then we can create corresponding localization files based on the fully
qualified name of the class. So we could have `myapp/Greeting_fi.properties`
which would look like this:

    goodMorning   Hyvää huomenta!
    goodEvening   Hyvää iltaa!
    hello         Hei, {0}!

To actually use this in our code, we'll ask `MessagesProvider` to create
an instance of `Greetings` for us:

    :::java
    Greetings greetings = MessagesProvider.forDefaultLocale().create(Greetings.class);

    System.out.println(greetings.hello("world"));

That's it! You can further tweak the functionality by specifying some
additional annotations or by configuring the properties of the
`MessagesProvider`, but the basics remain the same.

# Using Lokki with Maven

Lokki is available on the central Maven repository, so just add the following
dependency to your pom.xml:

    <dependency>
        <groupId>fi.evident.lokki</groupId>
        <artifactId>lokki</artifactId>
        <version>0.1.1</version>
    </dependency>
