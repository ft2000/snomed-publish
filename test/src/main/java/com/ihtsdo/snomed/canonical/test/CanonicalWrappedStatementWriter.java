package com.ihtsdo.snomed.canonical.test;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.canonical.test.model.RelationshipStatementForCompareWrapper;
import com.ihtsdo.snomed.service.CanonicalOutputWriter;

public class CanonicalWrappedStatementWriter extends CanonicalOutputWriter {
    private static final Logger LOG = LoggerFactory.getLogger( CanonicalWrappedStatementWriter.class );

    public void writeCompareStatements (Writer w, Collection<RelationshipStatementForCompareWrapper> statements) throws IOException{
        printHeading(w);
        Iterator<RelationshipStatementForCompareWrapper> rIt = statements.iterator();
        int counter = 1;
        while (rIt.hasNext()){
            w.write("\r\n");
            printRelationship(w, rIt.next().getRelationshipStatement());
            counter++;
        }
        LOG.info("Wrote " + counter + " lines");
    }
}