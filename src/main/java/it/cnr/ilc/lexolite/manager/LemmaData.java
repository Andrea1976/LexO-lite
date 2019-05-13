/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andreabellandi
 */
public class LemmaData implements Serializable {

    private boolean saveButtonDisabled;
    private boolean deleteButtonDisabled;
    private String formWrittenRepr;
//    private String info;
    private String PoS;
    private String language;
    private String gender;
    private String number;
    private String person;
    private String mood;
    private String voice;
    private String individual;
    private String type;
    private String note;
//    private String usedIn;
//    private String correspondence;

//    private LemmaData.Word linguisticTypeEntry = new Word();

//    private String transliteration;
//    private String transilterationType;
//    private String alphabet;

//    private boolean linguisticType;

//    private ArrayList<Word> documentedIn = new ArrayList();

//    private boolean bilingualVariant = false;
    private boolean verified = false;
    private ArrayList<Word> seeAlso = new ArrayList();
//    private ArrayList<Word> bilingual = new ArrayList();
    private ArrayList<Word> multiword = new ArrayList();
//    private ArrayList<Word> sublemma = new ArrayList();
//    private ArrayList<Word> collocation = new ArrayList();
//    private ArrayList<Word> otherLanguage = new ArrayList();
//    private ArrayList<ReferenceData> reference = new ArrayList();

//    private ArrayList<ExternalAttestationData> externalAttestation = new ArrayList();
//    private ArrayList<InternalAttestationData> internalAttestation = new ArrayList();

    public LemmaData() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.verified = false;
//        this.transilterationType = "none";
//        this.transliteration = "";
//        this.alphabet = "";
//        this.correspondence = "false";
//        this.linguisticType = false;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

//    public String getCorrespondence() {
//        return correspondence;
//    }
//
//    public void setCorrespondence(String correspondence) {
//        this.correspondence = correspondence;
//    }
//
//    public Word getLinguisticTypeEntry() {
//        return linguisticTypeEntry;
//    }
//
//    public void setLinguisticTypeEntry(Word linguisticTypeEntry) {
//        this.linguisticTypeEntry = linguisticTypeEntry;
//    }
//
//    public boolean isLinguisticType() {
//        return linguisticType;
//    }
//
//    public void setLinguisticType(boolean linguisticType) {
//        this.linguisticType = linguisticType;
//    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIndividual() {
        return individual;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }

//    public ArrayList<Word> getDocumentedIn() {
//        return documentedIn;
//    }
//
//    public void setDocumentedIn(ArrayList<Word> documentedIn) {
//        this.documentedIn = documentedIn;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public String getTransliteration() {
//        return transliteration;
//    }
//
//    public void setTransliteration(String transliteration) {
//        this.transliteration = transliteration;
//    }
//
//    public String getTransilterationType() {
//        return transilterationType;
//    }
//
//    public void setTransilterationType(String transilterationType) {
//        this.transilterationType = transilterationType;
//    }
//
//    public String getAlphabet() {
//        return alphabet;
//    }
//
//    public void setAlphabet(String alphabet) {
//        this.alphabet = alphabet;
//    }
//
//    public ArrayList<Word> getOtherLanguage() {
//        return otherLanguage;
//    }
//
//    public void setOtherLanguage(ArrayList<Word> otherLanguage) {
//        this.otherLanguage = otherLanguage;
//    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getNote() {
        return note;
    }

//    public boolean isBilingualVariant() {
//        return bilingualVariant;
//    }
//
//    public void setBilingualVariant(boolean bilingualVariant) {
//        this.bilingualVariant = bilingualVariant;
//    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<Word> getMultiword() {
        return multiword;
    }

    public void setMultiword(ArrayList<Word> multiword) {
        this.multiword = multiword;
    }

//    public ArrayList<Word> getSublemma() {
//        return sublemma;
//    }
//
//    public void setSublemma(ArrayList<Word> sublemma) {
//        this.sublemma = sublemma;
//    }
//
//    public ArrayList<Word> getCollocation() {
//        return collocation;
//    }
//
//    public void setCollocation(ArrayList<Word> collocation) {
//        this.collocation = collocation;
//    }
//
//    public ArrayList<ReferenceData> getReference() {
//        return reference;
//    }
//
//    public void setReference(ArrayList<ReferenceData> reference) {
//        this.reference = reference;
//    }

    public ArrayList<Word> getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(ArrayList<Word> seeAlso) {
        this.seeAlso = seeAlso;
    }

//    public ArrayList<Word> getBilingual() {
//        return bilingual;
//    }
//
//    public void setBilingual(ArrayList<Word> bilingual) {
//        this.bilingual = bilingual;
//    }

    public boolean isSaveButtonDisabled() {
        return saveButtonDisabled;
    }

    public void setSaveButtonDisabled(boolean saveButtonDisabled) {
        this.saveButtonDisabled = saveButtonDisabled;
    }

    public boolean isDeleteButtonDisabled() {
        return deleteButtonDisabled;
    }

    public void setDeleteButtonDisabled(boolean deleteButtonDisabled) {
        this.deleteButtonDisabled = deleteButtonDisabled;
    }

    public String getFormWrittenRepr() {
        return formWrittenRepr;
    }

    public void setFormWrittenRepr(String formWrittenRepr) {
        this.formWrittenRepr = formWrittenRepr;
    }

    public String getPoS() {
        return PoS;
    }

    public void setPoS(String PoS) {
        this.PoS = PoS;
    }

//    public String getInfo() {
//        return info;
//    }
//
//    public void setInfo(String info) {
//        this.info = info;
//    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

//    public String getUsedIn() {
//        return usedIn;
//    }
//
//    public void setUsedIn(String usedIn) {
//        this.usedIn = usedIn;
//    }
//
//    public ArrayList<ExternalAttestationData> getExternalAttestation() {
//        return externalAttestation;
//    }
//
//    public void setExternalAttestation(ArrayList<ExternalAttestationData> externalAttestation) {
//        this.externalAttestation = externalAttestation;
//    }
//
//    public ArrayList<InternalAttestationData> getInternalAttestation() {
//        return internalAttestation;
//    }
//
//    public void setInternalAttestation(ArrayList<InternalAttestationData> internalAttestation) {
//        this.internalAttestation = internalAttestation;
//    }

    public void clear() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.formWrittenRepr = "";
        this.PoS = "";
//        this.info = "";
        this.language = "";
        this.number = "";
        this.gender = "";
        this.person = "";
        this.mood = "";
        this.voice = "";
        this.type = "";
        this.individual = "";
        this.note = "";
//        this.usedIn = "";
//        this.correspondence = "";
//        this.transilterationType = "none";
//        this.transliteration = "";
//        this.alphabet = "";
//        this.documentedIn.clear();
//        this.bilingualVariant = false;
        this.verified = false;
        this.multiword.clear();
        this.seeAlso.clear();
//        this.collocation.clear();
//        this.sublemma.clear();
//        this.bilingual.clear();
//        this.reference.clear();
//        this.otherLanguage.clear();
//        this.externalAttestation.clear();
//        this.internalAttestation.clear();
    }

    public static class Word {

        private boolean viewButtonDisabled;
        private boolean deleteButtonDisabled;
        private String writtenRep;
        private String OWLName;
        private String language;
        private String OWLComp;
        private String label;
        private ArrayList<CandidateWord> candidates = new ArrayList();

        public Word() {
            this.viewButtonDisabled = false;
            this.deleteButtonDisabled = false;
            this.writtenRep = "";
            this.OWLName = "";
            this.language = "";
            this.OWLComp = "";
            this.label = "";
        }

        public boolean isViewButtonDisabled() {
            return viewButtonDisabled;
        }

        public void setViewButtonDisabled(boolean viewButtonDisabled) {
            this.viewButtonDisabled = viewButtonDisabled;
        }

        public boolean isDeleteButtonDisabled() {
            return deleteButtonDisabled;
        }

        public void setDeleteButtonDisabled(boolean deleteButtonDisabled) {
            this.deleteButtonDisabled = deleteButtonDisabled;
        }

        public String getWrittenRep() {
            return writtenRep;
        }

        public void setWrittenRep(String writtenRep) {
            this.writtenRep = writtenRep;
        }

        public String getOWLName() {
            return OWLName;
        }

        public void setOWLName(String OWLName) {
            this.OWLName = OWLName;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getOWLComp() {
            return OWLComp;
        }

        public void setOWLComp(String OWLComp) {
            this.OWLComp = OWLComp;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public ArrayList<CandidateWord> getCandidates() {
            return candidates;
        }

        public void setCandidates(ArrayList<CandidateWord> candidates) {
            this.candidates = candidates;
        }

    }

    public static class CandidateWord {

        private String writtenRep;
        private String OWLName;
        private String language;

        public CandidateWord() {
            this.writtenRep = "";
            this.OWLName = "";
            this.language = "";
        }

        public String getWrittenRep() {
            return writtenRep;
        }

        public void setWrittenRep(String writtenRep) {
            this.writtenRep = writtenRep;
        }

        public String getOWLName() {
            return OWLName;
        }

        public void setOWLName(String OWLName) {
            this.OWLName = OWLName;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
