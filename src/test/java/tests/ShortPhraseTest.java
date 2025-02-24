package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShortPhraseTest {

    @ParameterizedTest
    @ValueSource(strings = {"Hello, world", "this is too long a phrase for the test", ""})
    public void testLengthOfPhrase(String phrase){
        assertTrue(phrase.length()>15, "The phrase should be greater then 15, length phrase - " + phrase.length());
    }
}
