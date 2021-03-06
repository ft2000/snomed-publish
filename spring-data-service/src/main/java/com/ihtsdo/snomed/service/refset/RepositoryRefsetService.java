package com.ihtsdo.snomed.service.refset;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.InvalidSnomedDateFormatException;
import com.ihtsdo.snomed.exception.MemberNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.OntologyFlavourNotFoundException;
import com.ihtsdo.snomed.exception.OntologyNotFoundException;
import com.ihtsdo.snomed.exception.OntologyVersionNotFoundException;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.model.SnomedFlavours.SnomedFlavour;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.model.refset.Plan;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Status;
import com.ihtsdo.snomed.repository.ConceptRepository;
import com.ihtsdo.snomed.repository.refset.RefsetRepository;
import com.ihtsdo.snomed.repository.refset.SnapshotRepository;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.OntologyVersionService;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
@Transactional (value = "transactionManager", readOnly = false)
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class RepositoryRefsetService implements RefsetService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryRefsetService.class);

    protected static final int NUMBER_OF_REFSETS_PER_PAGE = 5;

    @Inject
    RefsetRepository refsetRepository;
    
    @Inject
    OntologyVersionService ontologyVersionService;    
    
    @Inject
    PlanService planService;
    
    @Inject
    ConceptRepository conceptRepository; 
    
    @Inject
    SnapshotService snapshotService;
    
    @Inject
    MemberService memberService;
    
    @Inject
    protected ConceptService conceptService;   

    @Inject
    protected SnapshotRepository snapshotRepository;   
    
    @PersistenceContext(unitName="hibernatePersistenceUnit") 
    private EntityManager em;
    
    @PostConstruct
    public void init(){}
    
//    @Override
//    @Transactional(readOnly = true)
//    public List<Refset> findAll(int pageIndex){
//        LOG.debug("Retrieving all refsets");
////        System.out.println("\n\n\n\n\n\nFUCK!!!!!!!\n\n\n\n\n\n\n");
////        Pageable pageSpecification = new PageRequest(pageIndex, NUMBER_OF_SNAPSHOTS_PER_PAGE, sortByAscendingTitle());
////        Page requestedPage = snapshotRepository.findAll(constructPageSpecification(pageIndex));
////        return requestedPage.getContent();
//        throw new UnsupportedOperationException();
//    }
//    
    @Override
    @Transactional(readOnly = true)
    public com.ihtsdo.snomed.service.Page<Refset> findAll(String sortBy, SortOrder sortOrder, String searchTerm, int page, int pageSize){
        LOG.debug("Retrieving all active refsets, sorted by {} {} with title like {}", sortBy, sortOrder, searchTerm); 
        Page<Refset> pageResult = refsetRepository.findAllActiveRefsetsWithTitleLike(
                searchTerm, new PageRequest(page, pageSize, new Sort(RepositoryRefsetService.sortDirection(sortOrder), sortBy)));
        return new com.ihtsdo.snomed.service.Page<Refset>(pageResult.getContent(), pageResult.getTotalElements());
    }
    
    public static Sort.Direction sortDirection(SortOrder sortOrder){
        return (sortOrder == SortOrder.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
    
    @Override
    @Transactional(readOnly=true)
    public Refset findByPublicId(String publicId) throws RefsetNotFoundException{
        LOG.debug("Getting active refset with publicId=" + publicId);
        Refset refset = refsetRepository.findByPublicIdAndStatus(publicId, Status.ACTIVE);
        if (refset == null){
            throw new RefsetNotFoundException(publicId);
        }
        return refset;
    }    

    @Override
    @Transactional    
    public Refset update(Refset refset){
        Refset newRefset = refsetRepository.save(refset);
        newRefset.setMemberSize(getMemberSize(newRefset.getPublicId()));
        return newRefset;
    }
    
    @Override
    @Transactional(rollbackFor = {
            RefsetNotFoundException.class, 
            RefsetConceptNotFoundException.class, 
            ValidationException.class, 
            RefsetPlanNotFoundException.class, 
            RefsetTerminalRuleNotFoundException.class,
            NonUniquePublicIdException.class, 
            OntologyNotFoundException.class, 
            InvalidSnomedDateFormatException.class, 
            OntologyVersionNotFoundException.class, 
            OntologyFlavourNotFoundException.class
    })
    public Refset update(RefsetDto updated) throws RefsetNotFoundException, RefsetConceptNotFoundException, 
            ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException, 
            NonUniquePublicIdException, OntologyNotFoundException, InvalidSnomedDateFormatException, 
            OntologyVersionNotFoundException, OntologyFlavourNotFoundException{
        LOG.debug("Updating refset with information: " + updated);
        
        SnomedFlavour flavour;
        try {
            flavour = SnomedFlavours.getFlavour(updated.getSnomedExtension());
        } catch (IllegalArgumentException e) {
            throw new OntologyFlavourNotFoundException(updated.getSnomedExtension());
        }
        
        Refset refset = refsetRepository.findByPublicIdAndStatus(updated.getPublicId(), Status.ACTIVE);
        if (refset == null) {
            throw new RefsetNotFoundException("No refset found with public id: " + updated.getPublicId());
        }
        
        Concept refsetConcept = conceptRepository.findByOntologyVersionAndSerialisedId(refset.getOntologyVersion(), updated.getRefsetConcept().getIdAsLong());
        if (refsetConcept == null){
            throw new RefsetConceptNotFoundException(new ConceptDto(updated.getRefsetConcept().getIdAsLong()), 
                    "No concept found with id: " + updated.getRefsetConcept().getIdAsLong());
        }
        
        Concept moduleConcept = conceptRepository.findByOntologyVersionAndSerialisedId(refset.getOntologyVersion(), updated.getModuleConcept().getIdAsLong());
        if (moduleConcept == null){
            throw new RefsetConceptNotFoundException(new ConceptDto(updated.getModuleConcept().getIdAsLong()), 
                    "No concept found with id: " + updated.getModuleConcept().getIdAsLong());
        }

        Plan plan = planService.findById(updated.getPlan().getId());
        if (plan == null){
            planService.create(updated.getPlan());
        }else{
            planService.update(updated.getPlan());
        }
        
        OntologyVersion ontologyVersion = null;
        try {
            ontologyVersion = ontologyVersionService.findByFlavourAndTaggedOn(
                    flavour.getPublicIdString(), updated.getSnomedReleaseDateAsDate());
            if (ontologyVersion == null){
                throw new OntologyVersionNotFoundException(flavour, updated.getSnomedReleaseDateAsDate());
            }            
        } catch (ParseException e1) {
            throw new InvalidSnomedDateFormatException(updated.getSnomedReleaseDate(), e1);
        }
//
//        refset.update(
//                updated.getSource(), 
//                updated.getType(), 
//                updated.gets
//                ontologyVersion,
//                refsetConcept, 
//                moduleConcept, 
//                updated.getTitle(), 
//                updated.getDescription(), 
//                updated.getPublicId(), 
//                plan);
        
        try {
            Refset updatedRefset = refsetRepository.save(refset);
            updatedRefset.setMemberSize(getMemberSize(updatedRefset.getPublicId()));
            return updatedRefset;
        } catch (DataIntegrityViolationException e) {
            throw new NonUniquePublicIdException(e.getMessage(), e);
        }
    }   

    @Override
    @Transactional(rollbackFor={
            RefsetConceptNotFoundException.class, 
            ValidationException.class, 
            OntologyNotFoundException.class,
            NonUniquePublicIdException.class,
            InvalidSnomedDateFormatException.class,
            OntologyVersionNotFoundException.class,
            OntologyFlavourNotFoundException.class})
    public Refset create(RefsetDto created) throws RefsetConceptNotFoundException, OntologyNotFoundException,
        ValidationException, NonUniquePublicIdException, InvalidSnomedDateFormatException, 
        OntologyVersionNotFoundException, OntologyFlavourNotFoundException
    {
        LOG.debug("Creating new refset [{}]", created.toString());
        
        SnomedFlavour flavour;
        try {
            flavour = SnomedFlavours.getFlavour(created.getSnomedExtension());
        } catch (IllegalArgumentException e) {
            throw new OntologyFlavourNotFoundException(created.getSnomedExtension());
        }

        OntologyVersion ontologyVersion = null;
        try {
            ontologyVersion = ontologyVersionService.findByFlavourAndTaggedOn(
                    flavour.getPublicIdString(), created.getSnomedReleaseDateAsDate());
            
            if (ontologyVersion == null){
                throw new OntologyVersionNotFoundException(flavour, created.getSnomedReleaseDateAsDate());
            }
        } catch (ParseException e1) {
            throw new InvalidSnomedDateFormatException(created.getSnomedReleaseDate(), e1);
        }

        Concept refsetConcept = conceptRepository.findByOntologyVersionAndSerialisedId(ontologyVersion, created.getRefsetConcept().getIdAsLong());
        if (refsetConcept == null){
            throw new RefsetConceptNotFoundException(new ConceptDto(created.getRefsetConcept().getIdAsLong()), 
                    "No concept found with id: " + created.getRefsetConcept().getIdAsLong());
        }
        
        Concept moduleConcept = conceptRepository.findByOntologyVersionAndSerialisedId(ontologyVersion, created.getModuleConcept().getIdAsLong());
        if (moduleConcept == null){
            throw new RefsetConceptNotFoundException(new ConceptDto(created.getModuleConcept().getIdAsLong()), 
                    "No concept found with id: " + created.getModuleConcept().getIdAsLong());
        }

        if (created.getPlan() == null){
            created.setPlan(new PlanDto());
        }

        Plan plan = planService.create(created.getPlan());

        Refset refset = Refset.getBuilder(
                created.getSource(), 
                created.getType(), 
                ontologyVersion,
                refsetConcept, 
                moduleConcept, 
                created.getTitle(), 
                created.getDescription(), 
                created.getPublicId(), 
                plan).build();

        try {
            Refset newRefset = refsetRepository.save(refset);
            newRefset.setMemberSize(getMemberSize(newRefset.getPublicId()));
            return newRefset;
        } catch (DataIntegrityViolationException e) {
            throw new NonUniquePublicIdException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)
    public Refset delete(String publicId) throws RefsetNotFoundException {
        LOG.debug("Inactivating (deleting) refset with public id: " + publicId);
        Refset inactivated = refsetRepository.findByPublicIdAndStatus(publicId, Status.ACTIVE);
        if (inactivated == null) {
            throw new RefsetNotFoundException(publicId, "No refset found with public id: " + publicId);
        }
        inactivated.setStatus(Status.INACTIVE);
        refsetRepository.save(inactivated);
        return inactivated;
    }    
    
    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)
    public Refset resurect(String publicId) throws RefsetNotFoundException {
        LOG.debug("Resurecting refset with public id: " + publicId);
        Refset resurected = refsetRepository.findByPublicIdAndStatus(publicId, Status.INACTIVE);
        if (resurected == null) {
            throw new RefsetNotFoundException(publicId, "No inactive refset found with public id: " + publicId);
        }
        resurected.setStatus(Status.ACTIVE);
        refsetRepository.save(resurected);
        return resurected;
    }
    
    @Override
    public Refset addMembers(Set<MemberDto> members, String publicId)
            throws RefsetNotFoundException, ConceptIdNotFoundException {
        LOG.debug("Adding {} new members to refset {}", members.size(), publicId);
        
        Refset refset = findByPublicId(publicId);
        refset.addMembers(fillMembers(members, refset.getModuleConcept(), conceptService));
        refset.setMemberSize(getMemberSize(publicId));
        return refset;
    }
    
    @Override
    public Member deleteMembership(String refsetId, String memberId) throws MemberNotFoundException, RefsetNotFoundException{
        LOG.debug("Deleting membership with public id {} for refset with name {}", refsetId, memberId);
        Refset refset = findByPublicId(refsetId);        
        Member member = memberService.findByMemberPublicIdAndRefsetPublicId(memberId, refsetId);
        refset.removeMember(member);
        refset.setMemberSize(refset.getMemberSize() - 1);
        return member;
    }    
    
    /*package scope*/ static Set<Member> fillMembers(Set<MemberDto> memberDtos, 
            Concept defaultModule, ConceptService conceptService) throws ConceptIdNotFoundException  {
        Set<Member> members = new HashSet<Member>();
        if ((memberDtos == null) || (memberDtos.isEmpty())){
            return members;
        }
        for (MemberDto memberDto : memberDtos){

        	Concept component = null;
			try {
				component = conceptService.findBySerialisedId(memberDto.getComponent().getIdAsLong());
	            if (component == null){
	                throw new ConceptIdNotFoundException(memberDto.getComponent().getId(), 
	                		"Did not find component concept with serialisedId " + memberDto.getComponent().getId());
	            }
			} catch (NumberFormatException e) {
                throw new ConceptIdNotFoundException(null, 
                		"Did not find component concept with serialisedId " + memberDto.getComponent().getId(), e);
			}

            Concept module = null;
            if (memberDto.getModule() != null){
                try {
					module = conceptService.findBySerialisedId(memberDto.getModule().getIdAsLong());
	                if (module == null){
	                    throw new ConceptIdNotFoundException(memberDto.getModule().getId(), 
	                    		"Did not find module concept with serialisedId " + memberDto.getModule().getId());
	                }
                } catch (NumberFormatException e) {
                    throw new ConceptIdNotFoundException(null, 
                    		"Did not find module concept with serialisedId " + memberDto.getModule().getId(), e);
				}

            }
            members.add(Member.
                    getBuilder(module == null ? defaultModule : module, component).
                    publicId(memberDto.getPublicId() == null ? generatePublicId() : memberDto.getPublicId()).
                    build());
        }
        return members;
    }

    @Override
    public int getMemberSize(String publicId) {
        LOG.debug("Calculating number of members for refset " + publicId);
        return refsetRepository.memberSize(publicId);
    }
    
    
    private static String generatePublicId(){
        return UUID.randomUUID().toString();
    }



//  @Override
//  @Transactional(rollbackFor = {
//          RefsetNotFoundException.class, 
//          NonUniquePublicIdException.class,
//          ConceptIdNotFoundException.class})
//  public SnapshotDto importSnapshot(String refsetPublicId, SnapshotDto snapshotDto) 
//          throws RefsetNotFoundException, NonUniquePublicIdException, ConceptIdNotFoundException {
//      Refset refset = findByPublicId(refsetPublicId);
//      if (refset == null){
//          throw new RefsetNotFoundException(refsetPublicId);
//      }
//      
//      Snapshot snapshot = refset.getSnapshot(snapshotDto.getPublicId());
//      
//      if (snapshot != null){
//          throw new NonUniquePublicIdException("Snapshot with public id {} allready exists");
//      }
//      
//      snapshot = Snapshot.getBuilder(
//              snapshotDto.getPublicId(), 
//              snapshotDto.getTitle(), 
//              snapshotDto.getDescription(), 
//              fillMembers(snapshotDto.getMemberDtos(), refset.getModuleConcept()),
//              refset.getPlan().getTerminal() != null ? refset.getPlan().getTerminal().clone() : null).build();
//      
//      refset.addSnapshot(snapshot);
//      refset = refsetRepository.save(refset);
//      return SnapshotDto.parse(snapshot);
//  }        
    


//    private Pageable constructPageSpecification(int pageIndex) {
//        Pageable pageSpecification = new PageRequest(pageIndex, NUMBER_OF_SNAPSHOTS_PER_PAGE, sortByAscendingTitle());
//        return pageSpecification;
//    }    

    /*
     * @Query("SELECT p FROM Person p WHERE LOWER(p.lastName) = LOWER(:lastName)")
    public List<Person> find(@Param("lastName") String lastName);
     */
    
}
