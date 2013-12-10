package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;

public class RdfSchemaSerialiser extends BaseSnomedSerialiser{
    private final Logger LOG = LoggerFactory.getLogger(RdfSchemaSerialiser.class);
    
    private final String NS_ONTOLOGY_VARIABLE = "__ONTOLOGY_ID__";
    private final String NS_CONCEPT = "http://snomedtools.info/snomed/version/" + NS_ONTOLOGY_VARIABLE + "/concept/rdfs/";
    private final String NS_TRIPLE = "http://snomedtools.info/snomed/version/" + NS_ONTOLOGY_VARIABLE + "/statement/rdfs/";
    private final String NS_DESCRIPTION = "http://snomedtools.info/snomed/version/" + NS_ONTOLOGY_VARIABLE + "/description/rdfs/";
    
    private SimpleDateFormat longTimeParser = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");
    
    
    private Set<Concept> parsedConcepts  = new HashSet<>();
    
    private boolean isParsed(Concept c){
        return parsedConcepts.contains(c);
    }
    
    private Concept parsed(Concept c){
        parsedConcepts.add(c);
        return c;
    }
    
    RdfSchemaSerialiser(Writer writer) throws IOException{
        super(writer);
    }
    
    @Override
    public void write(Ontology o, Collection<Statement> statements) throws IOException, ParseException {
        LOG.debug("Exporting to RDF/XML");
        Stopwatch stopwatch = new Stopwatch().start();

        printBody(o, statements);

        footer();
        stopwatch.stop();
        LOG.info("Completed RDF Schema export in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");          
    }

    @Override
    public void write(Statement statement) throws IOException, ParseException {
        write(statement.getOntology(), statement);
    }

    @Override
    public SnomedSerialiser header() throws IOException {
        writer.write("<rdf:RDF\n");
        writer.write(" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
        writer.write(" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n");
        writer.write(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n");
        writer.write(" xmlns:sn=\"http://snomedtools.info/snomed/term/\"\n");
        writer.write(" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n");
        writer.write(">\n");
        printStructural();
        return this;
    }

    @Override
    public SnomedSerialiser footer() throws IOException{
        writer.write("</rdf:RDF>\n");
        return this;
    }

    public void printStructural() throws IOException{
        //EffectiveTime
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/effectiveTime\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">EffectiveTime</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //Active
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/active\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">Active</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //Status
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/status\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">Status</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //Module
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/module\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">Module</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //Group
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/group\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">Group</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //CharacteristicType
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/characteristictype\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">CharacteristicType</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //Modifier
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/modifier\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">Modifier</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //Description
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/description\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">Description</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //CaseSignificance
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/casesignificance\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">CaseSignificance</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
        
        //Type
        writer.write("<rdf:Description rdf:about=\"http://snomedtools.info/snomed/term/type\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">Type</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
    }

    private void printBody(Ontology o, Collection<Statement> statements) throws IOException, ParseException{
        int counter = 1;
        
        for (Statement s : statements){
            if (o == null){
                write(s);
            }else{
                write(o, s);
            }
            counter++;
            if (counter % 10000 == 0){
                LOG.info("Processed {} statements", counter);
            }
        }
    }

    private void write(Ontology o, Statement s) throws IOException, ParseException {
        parse(o, s.getSubject());
        parse(o, s.getObject());
        parse(o, s.getPredicate());
        writeStatement(o, s);
    }

    private void parse(Ontology o, Concept c) throws IOException,
            ParseException {
        if (!isParsed(c)){
            writeConcept(o, parsed(c));
            if (c.getDescription() != null){
	            for (Description d : c.getDescription()){
	                writeDescription(o, d);
	            }
            }
                        
        }
    }

    protected void writeConcept(Ontology o, Concept c)
            throws IOException, ParseException {
        writer.write("<rdf:Description rdf:about=\"" + getConceptName(c.getSerialisedId(), o) + "\">\n");
        if (c.isPredicate()){
            writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        }
        else{
            writer.write("<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n");
        }
        writer.write("<rdfs:label xml:lang=\"en-gb\">" + StringEscapeUtils.escapeXml(c.getFullySpecifiedName()) + "</rdfs:label>\n");
        if (c.getModule() != null) writer.write("<sn:module rdf:resource=\"" + getConceptName(c.getModule().getSerialisedId(), o) + "\"/>\n");
        if (c.getStatus() != null) writer.write("<sn:status rdf:resource=\"" + getConceptName(c.getStatus().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + c.isActive() + "</sn:active>\n");
                
        if (c.getEffectiveTime() != 0) writer.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
            dateTimeFormatter.format(longTimeParser.parse(String.valueOf(c.getEffectiveTime()))) + 
            "</sn:effectiveTime>\n");
        
        if ((c.getDescription() != null) && !c.getDescription().isEmpty()){
	        for (Description d : c.getDescription()){
	            writer.write("<sn:description rdf:resource=\"" + getDescriptionName(d.getSerialisedId(), o) + "\"/>\n");
	        }
    	}
        if (c.isPredicate()){
            for (Concept p : c.getKindOfs()){
                writer.write("<rdfs:subPropertyOf rdf:resource=\"" + getConceptName(p.getSerialisedId(), o) + "\" />\n");
            }
        }else{
            for (Concept p : c.getKindOfs()){
                writer.write("<rdfs:subClassOf rdf:resource=\"" + getConceptName(p.getSerialisedId(), o) + "\" />\n");
            }            
        }
        writer.write("</rdf:Description>\n");
    }

    protected void writeDescription(Ontology o, Description d) throws IOException, ParseException {
        writer.write("<rdf:Description rdf:about=\"" + getDescriptionName(d.getSerialisedId(), o) + "\">\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n");
        writer.write("<sn:module rdf:resource=\"" + getConceptName(d.getModule().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:type rdf:resource=\"" + getConceptName(d.getType().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:casesignificance rdf:resource=\"" + getConceptName(d.getCaseSignificance().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + d.isActive() + "</sn:active>\n");
        
        writer.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(d.getEffectiveTime()))) + 
                "</sn:effectiveTime>\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">" + StringEscapeUtils.escapeXml(d.getTerm()) + "</rdfs:label>\n");
        
        writer.write("</rdf:Description>\n");
    }

    protected void writeStatement(Ontology o, Statement s) throws IOException, ParseException {
        if ( ! isTrue(OPTIONS_RDF_INCLUDE_ISA_STATEMENT)){
            //modelled as subProperty and SubClass. See writeConcept. But you do lose all the 
            //reification data for the isA statements (!)
            return;
        }
        writer.write("<rdf:Description rdf:about=\"" + getTripleName(s.getSerialisedId(), o) + "\">\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement\"/>\n");
        writer.write("<rdf:subject rdf:resource=\"" + getConceptName(s.getSubject().getSerialisedId(), o) + "\"/>\n");
        writer.write("<rdf:predicate rdf:resource=\"" + getConceptName(s.getPredicate().getSerialisedId(), o) + "\"/>\n");
        writer.write("<rdf:object rdf:resource=\"" + getConceptName(s.getObject().getSerialisedId(), o) + "\"/>\n");
        if (s.getModifier() != null) writer.write("<sn:modifier rdf:resource=\"" + getConceptName(s.getModifier().getSerialisedId(), o) + "\"/>\n");
        if (s.getModule() != null) writer.write("<sn:module rdf:resource=\"" + getConceptName(s.getModule().getSerialisedId(), o) + "\"/>\n");
        if (s.getCharacteristicType() != null) writer.write("<sn:characteristictype rdf:resource=\"" + getConceptName(s.getCharacteristicType().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:group rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">" + s.getGroupId() + "</sn:group>\n");
        writer.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + s.isActive() + "</sn:active>\n");
        
        if (s.getEffectiveTime() != 0) writer.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(s.getEffectiveTime()))) + 
                "</sn:effectiveTime>\n");
        
        writer.write("</rdf:Description>\n");
    }
        

    private String getDescriptionName(long id, Ontology o){
        return NS_DESCRIPTION.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }

    private String getConceptName(long id, Ontology o){
        return NS_CONCEPT.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }
    
    private String getTripleName(long id, Ontology o) {
        return NS_TRIPLE.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }

	@Override
	public void write(Concept c) throws IOException, ParseException {
		writeConcept(c.getOntology(), c);
		
	}

	@Override
	public void write(Description d) throws IOException, ParseException {
		writeDescription(d.getOntology(), d);
		
	}


}