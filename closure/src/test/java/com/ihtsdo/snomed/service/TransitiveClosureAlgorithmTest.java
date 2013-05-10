package com.ihtsdo.snomed.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.SerialiserFactory.Form;

public class TransitiveClosureAlgorithmTest {

    private static TransitiveClosureAlgorithm algorithm;

    Set<Concept> concepts = new HashSet<Concept>();
    Concept c1,c2,c3,c4,c5,cp;
    Statement s12,s13,s14,s15,s23,s24,s25,s34,s35,s45;

    @Before
    public void setUp() throws Exception {
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);
        c5 = new Concept(5);
        cp = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);        

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        c4.addKindOf(c5);

        s12 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c1, cp, c2);
        s13 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c1, cp, c3);
        s14 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c1, cp, c4);
        s15 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c1, cp, c5);
        s23 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c2, cp, c3);
        s24 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c2, cp, c4);
        s25 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c2, cp, c5);
        s34 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c3, cp, c4);
        s35 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c3, cp, c5);
        s45 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c4, cp, c5);

        concepts.addAll(Arrays.asList(c1,c2,c3,c4,c5,cp));
        algorithm = new TransitiveClosureAlgorithm();
    }

    @Test
    public void shouldReturnAllParentsForC1() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            algorithm.runAlgorithm(Arrays.asList(c1), SerialiserFactory.getSerialiser(Form.CHILD_PARENT, pw));
        }
        baos.flush();
        assertEquals("1\t2\r\n" + 
                "1\t3\r\n" +
                "1\t4\r\n" +
                "1\t5\r\n" ,baos.toString());
    }

    @Test
    public void shouldReturnAllParentsForC1C2() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            algorithm.runAlgorithm(Arrays.asList(c1,c2), SerialiserFactory.getSerialiser(Form.CHILD_PARENT, pw));
        }
        baos.flush();
        assertEquals("1\t2\r\n" + 
                "1\t3\r\n" +
                "1\t4\r\n" +
                "1\t5\r\n" +
                "2\t3\r\n" +
                "2\t4\r\n" +
                "2\t5\r\n",
                baos.toString());
    }

    @Test
    public void shouldReturnAllParentsForC1C2C3() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            algorithm.runAlgorithm(Arrays.asList(c1,c2,c3), SerialiserFactory.getSerialiser(Form.CHILD_PARENT, pw));
        }
        baos.flush();
        assertEquals("1\t2\r\n" + 
                "1\t3\r\n" +
                "1\t4\r\n" +
                "1\t5\r\n" +
                "2\t3\r\n" +
                "2\t4\r\n" +
                "2\t5\r\n" +
                "3\t4\r\n" +
                "3\t5\r\n",
                baos.toString());
    }

    @Test
    public void shouldReturnAllParentsForC1C2C3C4() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            algorithm.runAlgorithm(Arrays.asList(c1,c2,c3,c4), SerialiserFactory.getSerialiser(Form.CHILD_PARENT, pw));
        }
        baos.flush();
        assertEquals("1\t2\r\n" + 
                "1\t3\r\n" +
                "1\t4\r\n" +
                "1\t5\r\n" +
                "2\t3\r\n" +
                "2\t4\r\n" +
                "2\t5\r\n" +
                "3\t4\r\n" +
                "3\t5\r\n" +
                "4\t5\r\n",
                baos.toString());
    }

    @Test
    public void shouldReturnAllParentsForC1C2C3C4C5() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            algorithm.runAlgorithm(Arrays.asList(c1,c2,c3,c4,c5), SerialiserFactory.getSerialiser(Form.CHILD_PARENT, pw));
        }
        baos.flush();
        assertEquals("1\t2\r\n" + 
                "1\t3\r\n" +
                "1\t4\r\n" +
                "1\t5\r\n" +
                "2\t3\r\n" +
                "2\t4\r\n" +
                "2\t5\r\n" +
                "3\t4\r\n" +
                "3\t5\r\n" +
                "4\t5\r\n",
                baos.toString());
    }
}
