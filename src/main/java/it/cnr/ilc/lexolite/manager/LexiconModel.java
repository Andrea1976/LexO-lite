/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.LexOliteProperties;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.Namespace;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.controller.BaseController;
import it.cnr.ilc.lexolite.controller.LoginController;
import it.cnr.ilc.lexolite.manager.LemmaData.Word;
import it.cnr.ilc.lexolite.manager.SenseData.Openable;
import it.cnr.ilc.lexolite.util.LexiconUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.singleton;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

/**
 *
 * @author andreabellandi
 */
public class LexiconModel extends BaseController {

    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private LoginController loginController;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory factory;
    private PrefixManager pm;

    private static final String ONTOLOGY_FOLDER = System.getProperty("user.home") + LexOliteProperties.getProperty("lexiconFolder");
    private static final String DEFAULT_ONTOLOGY = ONTOLOGY_FOLDER + LexOliteProperties.getProperty("lexiconFileName");

    public LexiconModel(FileUploadEvent f) {
        manager = OWLManager.createOWLOntologyManager();
        try (InputStream inputStream = f.getFile().getInputstream()) {
            ontology = manager.loadOntologyFromOntologyDocument(inputStream);
            factory = manager.getOWLDataFactory();
            setPrefixes();
        } catch (OWLOntologyCreationException | IOException ex) {
            log(org.apache.log4j.Level.ERROR, loginController.getAccount(), "LOADING lexicon ", ex);
        }
    }

    public LexiconModel() {
        manager = OWLManager.createOWLOntologyManager();
        try (InputStream inputStream = new FileInputStream(DEFAULT_ONTOLOGY)) {
            ontology = manager.loadOntologyFromOntologyDocument(inputStream);
            factory = manager.getOWLDataFactory();
            setPrefixes();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LexiconModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | OWLOntologyCreationException ex) {
            Logger.getLogger(LexiconModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setPrefixes() {
        pm = new DefaultPrefixManager();
        pm.setPrefix("lexicon", Namespace.LEXICON);
        pm.setPrefix("lemon", Namespace.LEMON);
        pm.setPrefix("lexinfo", Namespace.LEXINFO);
        pm.setPrefix("rdfs", Namespace.RDFS);
        pm.setPrefix("skos", Namespace.SKOS);
        pm.setPrefix("ditmaolemon", Namespace.DITMAO_LEMON_NS);
        pm.setPrefix("ontolex", Namespace.ONTOLEX);
        pm.setPrefix("lime", Namespace.LIME);
        pm.setPrefix("dct", Namespace.DCT);
        pm.setPrefix("decomp", Namespace.DECOMP);
        pm.setPrefix("rdf", Namespace.RDF);
    }

    // params: langName, uriLang, lingCat, descritpion, creator
    public void addNewLangLexicon(String... params) {
        OWLClass lexiconClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("lime:"), "Lexicon");
        OWLNamedIndividual lexiconEntry = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), params[0] + "_lexicon");
        addIndividualAxiom(lexiconClass, lexiconEntry);
        addDataPropertyAxiom("language", lexiconEntry, params[0], pm.getPrefixName2PrefixMap().get("lime:"));
        addDataPropertyAxiom("language", lexiconEntry, params[1], pm.getPrefixName2PrefixMap().get("dct:"));
        addDataPropertyAxiom("linguisticCatalog", lexiconEntry, params[2], pm.getPrefixName2PrefixMap().get("lime:"));
        addDataPropertyAxiom("description", lexiconEntry, params[3], pm.getPrefixName2PrefixMap().get("dct:"));
        addDataPropertyAxiom("creator", lexiconEntry, params[4], pm.getPrefixName2PrefixMap().get("dct:"));
    }

    // NEW LEMMA ACTION: write all the triples about the new lemma entry
    public void addLemma(LemmaData ld, String lex) {
        String lemmaInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getLanguage(), "lemma");
        String entryInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getLanguage(), "entry");
        ld.setIndividual(lemmaInstance);
        OWLNamedIndividual lexicon = getIndividual(lex);
        OWLNamedIndividual le = getEntry(entryInstance, ld.getType());
        OWLNamedIndividual cf = getForm(lemmaInstance);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.ENTRY.getLabel(), lexicon, le, pm.getPrefixName2PrefixMap().get("lime:"));
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.CANONICALFORM.getLabel(), le, cf, pm.getPrefixName2PrefixMap().get("ontolex:"));
        setMoprhology(le, cf, ld);
    }

    private void setMoprhology(OWLNamedIndividual le, OWLNamedIndividual cf, LemmaData ld) {
        addDataPropertyAxiom(OntoLexEntity.DataProperty.WRITTENREP.getLabel(), cf, ld.getFormWrittenRepr(), pm.getPrefixName2PrefixMap().get("ontolex:"));
        if (ld.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            addObjectPropertyAxiom("lexinfo", cf, "partOfSpeech", ld.getPoS());
        } else {
            addObjectPropertyAxiom("lexinfo", cf, "partOfSpeech", getMultiwordPoS(ld.getPoS()));
        }
        addObjectPropertyAxiom("lexinfo", cf, "gender", ld.getGender());
        addObjectPropertyAxiom("lexinfo", cf, "mood", ld.getMood());
        addObjectPropertyAxiom("lexinfo", cf, "person", ld.getPerson());
        addObjectPropertyAxiom("lexinfo", cf, "voice", ld.getVoice());
        addObjectPropertyAxiom("lexinfo", cf, "number", ld.getNumber());
        addDataPropertyAxiom("verified", le, "false", pm.getPrefixName2PrefixMap().get("ditmaolemon:"));
    }

    private String getMultiwordPoS(String pos) {
        switch (pos) {
            case "nounPhrase":
                return "noun";
            case "verbPhrase":
                return "verb";
            case "adjectivePhrase":
                return "adjective";
        }
        return null;
    }

    // write the entry as individual of the related class and returns it 
    private OWLNamedIndividual getEntry(String uri, String clazz) {
        OWLClass lexicalEntryClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("ontolex:"), clazz);
        OWLNamedIndividual lexicalEntry = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), uri);
        addIndividualAxiom(lexicalEntryClass, lexicalEntry);
        return lexicalEntry;
    }

    // write the form as individual of the related class and returns it 
    private OWLNamedIndividual getForm(String uri) {
        OWLClass lexicalFormClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.Class.FORM.getLabel());
        OWLNamedIndividual form = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), uri);
        addIndividualAxiom(lexicalFormClass, form);
        return form;
    }

    // write the sense as individual of the related class and returns it 
    private OWLNamedIndividual getSense(String senseName, int n) {
        OWLClass lexicalSenseClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("lemon:"), "LexicalSense");
        OWLNamedIndividual sense = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), senseName + n);
        addIndividualAxiom(lexicalSenseClass, sense);
        return sense;
    }

    // returns the ontological individual of a given uri string
    private OWLNamedIndividual getIndividual(String uri) {
        return factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), uri);
    }

    // Save a new lemma note
    public void saveLemmaNote(LemmaData ld, String oldNote) {
        OWLNamedIndividual lemma = getIndividual(ld.getIndividual());
        if (oldNote.isEmpty()) {
            addDataPropertyAxiom("comment", lemma, ld.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        } else {
            updateDataPropertyAxiom(lemma, "comment", oldNote, ld.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        }
    }

    // write all triples about lemma entry with RENAMING
    public void updateLemmaWithRenaming(LemmaData oldLemma, LemmaData newLemma) {
        String oldLemmaInstance = oldLemma.getIndividual();
        String newLemmaInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(), newLemma.getLanguage(), "lemma");
        String oldEntryInstance = oldLemmaInstance.replace("_lemma", "_entry");
        String newEntryInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(), newLemma.getLanguage(), "entry");
        newLemma.setIndividual(newLemmaInstance);
        updateLemma(oldLemma, newLemma);
        IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldLemmaInstance), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newLemmaInstance));
        OWLNamedIndividual le = getIndividual(oldEntryInstance);
        // form individuals renaming
        formRenaming(oldLemma, newLemma, le);
        // sense individuals renaming
        senseRenaming(oldLemma, newLemma, le);
        IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldEntryInstance), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newEntryInstance));
    }

    private void formRenaming(LemmaData oldLemma, LemmaData newLemma, OWLNamedIndividual le) {
        OWLObjectProperty otherForm = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.ObjectProperty.OTHERFORM.getLabel());
        for (OWLIndividual i : EntitySearcher.getObjectPropertyValues(le, otherForm, ontology).collect(Collectors.toList())) {
            String formInstance = i.toStringID().replace(pm.getPrefixName2PrefixMap().get("lexicon:"), "");
            formInstance = formInstance.replace(LexiconUtil.sanitize(oldLemma.getFormWrittenRepr()), LexiconUtil.sanitize(newLemma.getFormWrittenRepr()));
            IRIrenaming(IRI.create(i.toStringID()), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + formInstance));
        }
    }

    private void senseRenaming(LemmaData oldLemma, LemmaData newLemma, OWLNamedIndividual le) {
        OWLObjectProperty sense = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.ObjectProperty.SENSE.getLabel());
        int senseNumb = 1;
        for (OWLIndividual i : EntitySearcher.getObjectPropertyValues(le, sense, ontology).collect(Collectors.toList())) {
            String senseInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(), oldLemma.getLanguage(), "sense");
            IRIrenaming(IRI.create(i.toStringID()), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + senseInstance + senseNumb));
            senseNumb++;
        }
    }

    private void IRIrenaming(IRI oldIRI, IRI newIRI) {
        OWLEntityRenamer ren = new OWLEntityRenamer(manager, singleton(ontology));
        List<OWLOntologyChange> changes = ren.changeIRI(oldIRI, newIRI);
        ontology.getOWLOntologyManager().applyChanges(changes);
    }

    public void updateLemma(LemmaData oldLemma, LemmaData newLemma) {
        String _subject = oldLemma.getIndividual();
        OWLNamedIndividual subject = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + _subject));
        OWLNamedIndividual entrySubject = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + _subject.replace("_lemma", "_entry")));
        updateMorphology(subject, oldLemma, newLemma);
        updateDataPropertyAxiom(entrySubject, "verified", oldLemma.isVerified(), newLemma.isVerified(), pm.getPrefixName2PrefixMap().get("ditmaolemon:"));
        updateLinkingRelation(oldLemma.getIndividual(), oldLemma.getSeeAlso(), newLemma.getSeeAlso(), "seeAlso");
    }

    private void updateMorphology(OWLNamedIndividual subject, LemmaData oldLemma, LemmaData newLemma) {
        updateDataPropertyAxiom(subject, OntoLexEntity.DataProperty.WRITTENREP.getLabel(), oldLemma.getFormWrittenRepr(), newLemma.getFormWrittenRepr(), pm.getPrefixName2PrefixMap().get("ontolex:"));
        if (oldLemma.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            updateObjectPropertyAxiom(subject, "partOfSpeech", oldLemma.getPoS(), newLemma.getPoS(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        } else {
            updateObjectPropertyAxiom(subject, "partOfSpeech", getMultiwordPoS(oldLemma.getPoS()), getMultiwordPoS(newLemma.getPoS()), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        }
        updateObjectPropertyAxiom(subject, "gender", oldLemma.getGender(), newLemma.getGender(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        updateObjectPropertyAxiom(subject, "person", oldLemma.getPerson(), newLemma.getPerson(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        updateObjectPropertyAxiom(subject, "mood", oldLemma.getMood(), newLemma.getMood(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        updateObjectPropertyAxiom(subject, "voice", oldLemma.getVoice(), newLemma.getVoice(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        updateObjectPropertyAxiom(subject, "number", oldLemma.getNumber(), newLemma.getNumber(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
    }

    private void updateLinkingRelation(String sbj, ArrayList<Word> oldWords, ArrayList<Word> newWords, String rel) {
        // for each old linking relation, if it is not in new linking relation then remove old linking relation
        for (Word w : oldWords) {
            if ((!contains(newWords, w)) && (!w.getWrittenRep().isEmpty())) {
                if (rel.equals("seeAlso")) {
                    removeObjectPropertyAxiom("rdf", getIndividual(sbj.replace("_lemma", "_entry")), rel, getIndividual(w.getOWLName().replace("_lemma", "_entry")));
                }
            }
        }
        // for each new linking relation, if it is not in old linking relation then add new linking relation
        for (Word w : newWords) {
            if ((!contains(oldWords, w)) && (!w.getWrittenRep().isEmpty())) {
                if (rel.equals("seeAlso")) {
                    addObjectPropertyAxiom(rel, getIndividual(w.getOWLName().replace("_lemma", "_entry")), getIndividual(sbj.replace("_lemma", "_entry")), pm.getPrefixName2PrefixMap().get("rdf:"));
                }
            }
        }
    }

    private boolean contains(ArrayList<Word> alw, Word w) {
        for (Word _w : alw) {
            if (w.getOWLName().equals(_w.getOWLName())) {
                return true;
            }
        }
        return false;
    }

    public void addLexicalRelation(String ns, OWLNamedIndividual sbj, String rel, String obj) {
        addObjectPropertyAxiom(rel, sbj, getIndividual(obj), pm.getPrefixName2PrefixMap().get("ditmaolemon:"));
    }

    // NEW LEMMA MULTIWORD ACTION: write all the triples about the new lemma entry
    public void addMultiwordLemma(LemmaData ld, String lex) {
        addLemma(ld, lex);
        createMultiwordDecomposition(ld);
    }

    private void createMultiwordDecomposition(LemmaData ld) {
        OWLNamedIndividual entry = getIndividual(ld.getIndividual().replace("_lemma", "_entry"));
        for (int i = 0; i < ld.getMultiword().size(); i++) {
            String position = Integer.toString(i);
            // write the component individual
            OWLNamedIndividual componentIndividual = getComponent(LexiconUtil.getIRI(ld.getIndividual().replace("_lemma", "_entry"), "comp", position));
            addObjectPropertyAxiom("constituent", entry, componentIndividual, pm.getPrefixName2PrefixMap().get("decomp:"));
            // write its position
            addDataPropertyAxiom("comment", componentIndividual, position, pm.getPrefixName2PrefixMap().get("rdfs:"));
            // write its correspondence if the word exists as lexical entry
            String lexicalEntryOfWord = ld.getMultiword().get(i).getOWLName();
            if (!lexicalEntryOfWord.isEmpty()) {
                OWLNamedIndividual le = getIndividual(lexicalEntryOfWord.replace("_lemma", "_entry"));
                addObjectPropertyAxiom("correspondsTo", componentIndividual, le, pm.getPrefixName2PrefixMap().get("decomp:"));
            }
        }
    }

    // write the comp of the component class and returns it
    private OWLNamedIndividual getComponent(String uri) {
        OWLClass ComponentClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("decomp:"), "Component");
        OWLNamedIndividual c = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), uri);
        addIndividualAxiom(ComponentClass, c);
        return c;
    }

    // UPDATE multiwordLemma with no renaming
    public void updateMultiwordLemma(LemmaData oldLemma, LemmaData newLemma) {
        // it verifies if some component has been changed
        updateComponentsOfLemma(oldLemma.getMultiword(), newLemma.getMultiword());
        // update the oppurtune lemma fields
        updateLemma(oldLemma, newLemma);
    }

    // A component can be changed but NOT the written representation of the multiword 
    private void updateComponentsOfLemma(ArrayList<Word> oldComponents, ArrayList<Word> newComponents) {
        for (int i = 0; i < oldComponents.size(); i++) {
            if (!oldComponents.get(i).getLabel().equals(newComponents.get(i).getLabel())) {
                Word oldComp = oldComponents.get(i);
                Word newComp = newComponents.get(i);
                // write the new component (only the modification of the correspondsTo property is required)
                if (!oldComp.getWrittenRep().contains("not found")) {
                    // the related word is associated with a lexical entry, so deletion it is required
                    removeObjectPropertyAxiom(pm.getPrefixName2PrefixMap().get("decomp:"), getIndividual(oldComp.getOWLComp()), "correspondsTo", getIndividual(oldComp.getOWLName()));
                }
                addObjectPropertyAxiom("correspondsTo", getIndividual(oldComp.getOWLComp()), getIndividual(newComp.getOWLName().replace("_lemma", "_entry")), pm.getPrefixName2PrefixMap().get("decomp:"));
            }
        }
    }

    // UPDATE multiwordLemma with renaming
    public void updateMultiwordLemmaWithRenaming(LemmaData oldLemma, LemmaData newLemma) {
        // delete the old constituents
        deleteMultiwordDecomposition(oldLemma);
        // update the oppurtune lemma fields
        updateLemmaWithRenaming(oldLemma, newLemma);
        // create the new constituents
        createMultiwordDecomposition(newLemma);
    }

    private void deleteMultiwordDecomposition(LemmaData oldLemma) {
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        for (Word comp : oldLemma.getMultiword()) {
            remover.visit(getIndividual(comp.getOWLComp()));
            manager.applyChanges(remover.getChanges());
        }
    }

    // Save a new form note
    public void saveFormNote(FormData fd, String oldNote) {
        OWLNamedIndividual form = getIndividual(fd.getIndividual());
        if (oldNote.isEmpty()) {
            // it needs to create the instance of the property note
            addDataPropertyAxiom("comment", form, fd.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        } else {
            // an instance of the property already exists and we have to modify its value
            updateDataPropertyAxiom(form, "comment", oldNote, fd.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        }
    }

    // NEW FORM ACTION: write all triples about the form
    public void addForm(FormData fd, LemmaData ld) {
        String formInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getLanguage(), fd.getFormWrittenRepr(), "form");
        String entryInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getLanguage(), "entry");
        OWLNamedIndividual le = getIndividual(entryInstance);
        OWLNamedIndividual of = getForm(formInstance);
        fd.setIndividual(formInstance);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.OTHERFORM.getLabel(), le, of, pm.getPrefixName2PrefixMap().get("ontolex:"));
        setMorphology(of, fd);
    }

    private void setMorphology(OWLNamedIndividual of, FormData fd) {
        addDataPropertyAxiom(OntoLexEntity.DataProperty.WRITTENREP.getLabel(), of, fd.getFormWrittenRepr(), pm.getPrefixName2PrefixMap().get("ontolex:"));
        addObjectPropertyAxiom("lexinfo", of, "gender", fd.getGender());
        addObjectPropertyAxiom("lexinfo", of, "number", fd.getNumber());
        addObjectPropertyAxiom("lexinfo", of, "person", fd.getPerson());
        addObjectPropertyAxiom("lexinfo", of, "mood", fd.getMood());
        addObjectPropertyAxiom("lexinfo", of, "voice", fd.getVoice());
    }

    // write all triples about a form with RENAMING
    public void addFormWithRenaming(FormData oldForm, FormData newForm, LemmaData ld) {
        String oldFormInstance = oldForm.getIndividual();
        String newFormInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getLanguage(), newForm.getFormWrittenRepr(), "form");
        updateForm(oldForm, newForm, ld);
        IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldFormInstance), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newFormInstance));
    }

    public void updateForm(FormData oldForm, FormData newForm, LemmaData ld) {
        String _subject = oldForm.getIndividual();
        OWLNamedIndividual subject = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + _subject));
        updateMorphology(subject, oldForm, newForm);
    }

    private void updateMorphology(OWLNamedIndividual subject, FormData oldForm, FormData newForm) {
        updateDataPropertyAxiom(subject, OntoLexEntity.DataProperty.WRITTENREP.getLabel(), oldForm.getFormWrittenRepr(), newForm.getFormWrittenRepr(), pm.getPrefixName2PrefixMap().get("ontolex:"));
        updateObjectPropertyAxiom(subject, "gender", oldForm.getGender(), newForm.getGender(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        updateObjectPropertyAxiom(subject, "number", oldForm.getNumber(), newForm.getNumber(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        updateObjectPropertyAxiom(subject, "person", oldForm.getPerson(), newForm.getPerson(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        updateObjectPropertyAxiom(subject, "mood", oldForm.getMood(), newForm.getMood(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        updateObjectPropertyAxiom(subject, "voice", oldForm.getVoice(), newForm.getVoice(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
    }

    private void updateDataPropertyAxiom(OWLNamedIndividual subject, String dataProperty, String oldValue, String newValue, String ns) {
        OWLDataProperty p = factory.getOWLDataProperty(IRI.create(ns + dataProperty));
        OWLDataPropertyAssertionAxiom oldDataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, subject, oldValue);
        ontology.removeAxiom(oldDataPropertyAssertion);
        OWLDataPropertyAssertionAxiom newDataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, subject, newValue);
        manager.addAxiom(ontology, newDataPropertyAssertion);
    }

    private void updateDataPropertyAxiom(OWLNamedIndividual subject, String dataProperty, boolean oldValue, boolean newValue, String ns) {
        OWLDataProperty p = factory.getOWLDataProperty(IRI.create(ns + dataProperty));
        OWLDataPropertyAssertionAxiom oldDataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, subject, String.valueOf(oldValue));
        ontology.removeAxiom(oldDataPropertyAssertion);
        OWLDataPropertyAssertionAxiom newDataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, subject, String.valueOf(newValue));
        manager.addAxiom(ontology, newDataPropertyAssertion);
    }

    private void updateObjectPropertyAxiom(OWLNamedIndividual subject, String objProperty, String oldObj, String newObj, String ns) {
        oldObj = (oldObj.equals(Label.NO_ENTRY_FOUND) || oldObj.isEmpty()) ? Label.NO_ENTRY_FOUND : oldObj;
        newObj = (newObj.equals(Label.NO_ENTRY_FOUND) || newObj.isEmpty()) ? Label.NO_ENTRY_FOUND : newObj;
        if (!oldObj.equals(newObj)) {
            OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create(ns + objProperty));
            if (oldObj.equals(Label.NO_ENTRY_FOUND)) {
                addAxiom(ns, newObj, p, subject);
            } else {
                if (newObj.equals(Label.NO_ENTRY_FOUND)) {
                    removeAxiom(ns, oldObj, p, subject);
                } else {
                    addAxiom(ns, newObj, p, subject);
                    removeAxiom(ns, oldObj, p, subject);
                }
            }
        }
    }

    private void addAxiom(String ns, String obj, OWLObjectProperty p, OWLNamedIndividual subject) {
        OWLNamedIndividual newObject = factory.getOWLNamedIndividual(ns, obj);
        OWLObjectPropertyAssertionAxiom newObjPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(p, subject, newObject);
        manager.addAxiom(ontology, newObjPropertyAssertion);
    }

    private void removeAxiom(String ns, String obj, OWLObjectProperty p, OWLNamedIndividual subject) {
        OWLNamedIndividual oldObject = factory.getOWLNamedIndividual(ns, obj);
        OWLObjectPropertyAssertionAxiom oldObjPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(p, subject, oldObject);
        ontology.removeAxiom(oldObjPropertyAssertion);
    }

    private void addObjectPropertyAxiom(String ns, OWLNamedIndividual subject, String predicate, String object) {
        if (!object.equals(Label.NO_ENTRY_FOUND) && !object.isEmpty()) {
            OWLNamedIndividual i = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get(ns + ":"), object);
            addObjectPropertyAxiom(predicate, subject, i, pm.getPrefixName2PrefixMap().get(ns + ":"));
        }
    }

    // remove a specific triple
    private void removeObjectPropertyAxiom(String ns, OWLNamedIndividual subject, String predicate, OWLNamedIndividual object) {
        OWLObjectProperty prop = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get(ns + ":"), predicate);
        OWLObjectPropertyAssertionAxiom propertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(prop, subject, object);
        ontology.removeAxiom(propertyAssertion);
    }

    private void removeDataPropertyAxiom(String ns, OWLNamedIndividual subject, String predicate, String value) {
        OWLDataProperty prop = factory.getOWLDataProperty(pm.getPrefixName2PrefixMap().get(ns + ":"), predicate);
        OWLDataPropertyAssertionAxiom propertyAssertion = factory.getOWLDataPropertyAssertionAxiom(prop, subject, value);
        ontology.removeAxiom(propertyAssertion);
    }

    // Save a new sense note
    public void saveSenseNote(SenseData sd, String oldNote) {
        OWLNamedIndividual sense = getIndividual(sd.getName());
        if (oldNote.isEmpty()) {
            // it needs to create the instance of the property note
            addDataPropertyAxiom("comment", sense, sd.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        } else {
            // an instance of the property already exists and we have to modify its value
            updateDataPropertyAxiom(sense, "comment", oldNote, sd.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        }
    }

    // NEW ACTION: write all triples about sense entry
    public void addSense(SenseData sd, LemmaData ld) {
        String entryInstance = ld.getIndividual().replace("_lemma", "_entry");
        String senseInstance = ld.getIndividual().replace("_lemma", "_sense");
        OWLNamedIndividual le = getIndividual(entryInstance);
        OWLObjectProperty sense = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.ObjectProperty.SENSE.getLabel());
        int senseNumber = EntitySearcher.getObjectPropertyValues(le, sense, ontology).collect(Collectors.toList()).size();
        OWLNamedIndividual s = getSense(senseInstance, senseNumber + 1);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.SENSE.getLabel(), le, s, pm.getPrefixName2PrefixMap().get("ontolex:"));
        sd.setName(s.getIRI().getShortForm());
    }

    public void deleteLemma(LemmaData ld) {
        String entryInstance = ld.getIndividual().replace("_lemma", "_entry");
        OWLNamedIndividual lemma = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + ld.getIndividual()));
        OWLNamedIndividual lexicalEntry = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + entryInstance));
        if (!ld.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            deleteMultiwordDecomposition(ld);
        }
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        remover.visit(lemma);
        manager.applyChanges(remover.getChanges());
        remover.visit(lexicalEntry);
        manager.applyChanges(remover.getChanges());

    }

    public void deleteSense(SenseData sd) {
        OWLNamedIndividual i = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + sd.getName()));
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        remover.visit(i);
        manager.applyChanges(remover.getChanges());
    }

    public void deleteForm(FormData fd) {
        OWLNamedIndividual i = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + fd.getIndividual()));
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        remover.visit(i);
        manager.applyChanges(remover.getChanges());
    }

    // NEW ACTION: write and delete all triples about new sense relations
    public void addSenseRelation(SenseData oldSense, SenseData newSense) {
        OWLNamedIndividual sbj = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldSense.getName()));
        if (!newSense.getDefinition().equals(oldSense.getDefinition())) {
            removeDataPropertyAxiom(pm.getPrefixName2PrefixMap().get("skos:"), sbj, "definition", oldSense.getDefinition());
            if (!newSense.getDefinition().isEmpty() && !newSense.getDefinition().equals(Label.NO_ENTRY_FOUND)) {
                addDataPropertyAxiom("definition", sbj, newSense.getDefinition(), pm.getPrefixName2PrefixMap().get("skos:"));
            }
        }
        saveRelations(sbj, oldSense.getSynonym(), newSense.getSynonym(), "synonym", "lexinfo");
        saveRelations(sbj, oldSense.getApproximateSynonym(), newSense.getApproximateSynonym(), "approximateSynonym", "lexinfo");
        saveRelations(sbj, oldSense.getAntonym(), newSense.getAntonym(), "antonym", "lexinfo");
        saveRelations(sbj, oldSense.getHypernym(), newSense.getHypernym(), "hypernym", "lexinfo");
        saveRelations(sbj, oldSense.getHyponym(), newSense.getHyponym(), "hyponym", "lexinfo");
        saveRelations(sbj, oldSense.getTranslation(), newSense.getTranslation(), "translation", "lexinfo");
        saveOntologyReference(sbj, oldSense.getOWLClass(), newSense.getOWLClass());
    }

    private void saveOntologyReference(OWLNamedIndividual sbj, Openable oldR, Openable newR) {
        if (newR.isViewButtonDisabled()) {
            if (oldR.isViewButtonDisabled()) {
                if (!oldR.getName().equals(newR.getName())) {
                    removeObjectPropertyAxiom("ontolex", sbj, OntoLexEntity.ObjectProperty.REFERENCE.getLabel(), factory.getOWLNamedIndividual(Namespace.DOMAIN_ONTOLOGY, oldR.getName()));
                    addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.REFERENCE.getLabel(), sbj, factory.getOWLNamedIndividual(Namespace.DOMAIN_ONTOLOGY, newR.getName()), pm.getPrefixName2PrefixMap().get("ontolex:"));
                }
            } else {
                addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.REFERENCE.getLabel(), sbj, factory.getOWLNamedIndividual(Namespace.DOMAIN_ONTOLOGY, newR.getName()), pm.getPrefixName2PrefixMap().get("ontolex:"));
            }
        } else {
            if (oldR.isViewButtonDisabled()) {
                removeObjectPropertyAxiom("ontolex", sbj, OntoLexEntity.ObjectProperty.REFERENCE.getLabel(), factory.getOWLNamedIndividual(Namespace.DOMAIN_ONTOLOGY, oldR.getName()));
            }
        }
    }

    private void saveRelations(OWLNamedIndividual sbj, ArrayList<Openable> oldR, ArrayList<Openable> newR, String rel, String ns) {
        // for each old sense relation, if it is not in new sense relation then remove old sense relation
        for (Openable o : oldR) {
            if ((!contains(newR, o)) && (!o.getName().isEmpty())) {
                String name = getNormalizedName(o.getName());
                o.setName(name);
                removeSenseRelation(ns, sbj, rel, getIndividual(name));
                // symmetric or inverse relation is removed
                if (rel.equals("synonym") || rel.equals("antonym") || rel.equals("approximateSynonym") || rel.equals("translation")) {
                    removeSenseRelation(ns, getIndividual(name), rel, sbj);
                } else {
                    if (rel.equals("hypernym")) {
                        removeSenseRelation(ns, getIndividual(name), "hyponym", sbj);
                    } else {
                        if (rel.equals("hyponym")) {
                            removeSenseRelation(ns, getIndividual(name), "hypernym", sbj);
                        }
                    }
                }
            }
        }
        // for each new sense relation, if it is not in old sense relation then add new sense relation
        for (Openable o : newR) {
            if ((!contains(oldR, o)) && (!o.getName().isEmpty())) {
                String name = getNormalizedName(o.getName());
                addSenseRelation(ns, sbj, rel, getIndividual(name));
                o.setName(name);
                o.setDeleteButtonDisabled(false);
                o.setViewButtonDisabled(false);
                // symmetric or inverse relation is added
                if (rel.equals("synonym") || rel.equals("antonym") || rel.equals("approximateSynonym") || rel.equals("translation")) {
                    addSenseRelation(ns, getIndividual(name), rel, sbj);
                } else {
                    if (rel.equals("hypernym")) {
                        addSenseRelation(ns, getIndividual(name), "hyponym", sbj);
                    } else {
                        if (rel.equals("hyponym")) {
                            addSenseRelation(ns, getIndividual(name), "hypernym", sbj);
                        }
                    }
                }
            }
        }
    }

    private boolean contains(ArrayList<Openable> alo, Openable o) {
        for (Openable _o : alo) {
            if (o.getName().equals(_o.getName())) {
                return true;
            }
        }
        return false;
    }

    // write a specific triple
    private void addSenseRelation(String ns, OWLNamedIndividual subject, String relType, OWLNamedIndividual object) {
        addObjectPropertyAxiom(relType, subject, object, pm.getPrefixName2PrefixMap().get(ns + ":"));
    }

    private void removeSenseRelation(String ns, OWLNamedIndividual subject, String relType, OWLNamedIndividual object) {
        removeObjectPropertyAxiom(ns, subject, relType, object);
    }

    private String getNormalizedName(String t) {
        if (t.contains("@")) {
            return t.split("@")[0];
        } else {
            return t;
        }
    }

    public StreamedContent export(String format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String fileName = LexOliteProperties.getProperty("lexiconFileName") == null ? "lexicon" : LexOliteProperties.getProperty("lexiconFileName");
        try {
            if (format.equals("rdf")) {
                manager.saveOntology(ontology, baos);
                fileName = fileName + ".owl";
            } else {
                if (format.equals("turtle")) {
                    manager.saveOntology(ontology, new TurtleOntologyFormat(), baos);
                    fileName = fileName + ".ttl";
                } else {
                    manager.saveOntology(ontology, new OWLXMLOntologyFormat(), baos);
                    fileName = fileName + ".owl";
                }
            }
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(LexiconModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());
        return new DefaultStreamedContent(in, "application/txt", fileName);
    }

    public synchronized void persist() throws IOException, OWLOntologyStorageException {
        System.out.println("[" + getTimestamp() + "] LexO-lite : persist start");
        File f = new File(DEFAULT_ONTOLOGY);
        File bkp = new File(DEFAULT_ONTOLOGY + "." + getTimestamp());
        if (!f.renameTo(bkp)) {
            throw new IOException("unable to rename " + bkp.getName());
        }
        try (FileOutputStream fos = new FileOutputStream(DEFAULT_ONTOLOGY)) {
            manager.saveOntology(ontology, fos);
            Runtime.getRuntime().exec("gzip " + bkp.getAbsolutePath());
        }
        System.out.println("[" + getTimestamp() + "] LexO-lite : persist end");
    }

    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void addIndividualAxiom(OWLClass c, OWLNamedIndividual i) {
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(c, i);
        manager.addAxiom(ontology, classAssertion);
    }

    private void removeIndividualAxiom(OWLClass c, OWLNamedIndividual i) {
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(c, i);
        ontology.remove(classAssertion);
    }

    private void addObjectPropertyAxiom(String objProp, OWLNamedIndividual src, OWLNamedIndividual trg, String ns) {
        OWLObjectProperty p = factory.getOWLObjectProperty(ns, objProp);
        OWLObjectPropertyAssertionAxiom propertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(p, src, trg);
        manager.addAxiom(ontology, propertyAssertion);
    }

    private void addDataPropertyAxiom(String dataProp, OWLNamedIndividual src, String trg, String ns) {
        if (!trg.isEmpty()) {
            OWLDataProperty p = factory.getOWLDataProperty(ns, dataProp);
            OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, src, trg);
            manager.addAxiom(ontology, dataPropertyAssertion);
        }
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLDataFactory getFactory() {
        return factory;
    }

}
