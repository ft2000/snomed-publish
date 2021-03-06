package com.ihtsdo.snomed.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiser;

public class TransitiveClosureAlgorithm {
    
    public void runAlgorithm(Collection<Concept> concepts, SnomedSerialiser serialiser) throws IOException, ParseException{
        Concept kindOfConcept = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        for (Concept c : concepts){
            for (Concept p : c.getAllActiveKindOfConcepts(true)){
                serialiser.write(new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c, kindOfConcept, p));
            }
        }
    }
}
