package com.ihtsdo.snomed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.OntologyVersion;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface ConceptRepository extends JpaRepository<Concept, Long>{

    public Concept findBySerialisedId(Long serialisedId);
    public Concept findByOntologyVersionAndSerialisedId(OntologyVersion ontologyVersion, Long serialisedId);

}
