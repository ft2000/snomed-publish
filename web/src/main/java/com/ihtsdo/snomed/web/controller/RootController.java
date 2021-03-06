package com.ihtsdo.snomed.web.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ihtsdo.snomed.web.service.ConceptService;
import com.ihtsdo.snomed.web.service.OntologyService;

@Controller
@RequestMapping("/")
@Transactional (value = "transactionManager", readOnly = true)
public class RootController {    

    private static final Logger LOG = LoggerFactory.getLogger( RootController.class );

    @Autowired OntologyService ontologyService;
    @Autowired ConceptService conceptService;

    @PersistenceContext(unitName="hibernatePersistenceUnit")
    EntityManager em;    
    
    @PostConstruct
    public void init(){
       
    }
    
    @RequestMapping(value="/", method = RequestMethod.GET)
    public void landingPage(HttpServletResponse response, HttpServletRequest request) throws IOException{
        LOG.debug("Redirecting root to '/version/1/search'");
        response.sendRedirect("/version/1/search");
    }
}
