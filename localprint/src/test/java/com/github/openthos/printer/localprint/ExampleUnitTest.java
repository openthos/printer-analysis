package com.github.openthos.printer.localprint;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() throws Exception{
        String testLine = "600x600dpi";
        if(!testLine.matches("^(\\d+)(.*)"))
            System.out.println("The first word is not num");
        else
            System.out.println("is num");

    }



}