/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.impl.xs.traversers;

import java.util.ArrayList;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.w3c.dom.Element;

/**
 * @xerces.internal  
 * 
 * @author Elena Litani, IBM
 * @author Sandy Gao, IBM
 * @version $Id$
 */
abstract class XSDAbstractParticleTraverser extends XSDAbstractTraverser {
    
    XSDAbstractParticleTraverser (XSDHandler handler,
            XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }
    
    /**
     *
     * Traverse the "All" declaration
     *
     * &lt;all
     *   id = ID
     *   maxOccurs = 1 : 1
     *   minOccurs = (0 | 1) : 1&gt;
     *   Content: (annotation? , element*)
     * &lt;/all&gt;
     **/
    XSParticleDecl traverseAll(Element allDecl,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar,
            int allContextFlags,
            XSObject parent) {
        
        // General Attribute Checking
        
        Object[] attrValues = fAttrChecker.checkAttributes(allDecl, false, schemaDoc);
        
        Element child = DOMUtil.getFirstChildElement(allDecl);
        
        XSAnnotationImpl annotation = null;
        if (child !=null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            child = DOMUtil.getNextSiblingElement(child);
        }
        else {
            String text = DOMUtil.getSyntheticAnnotation(allDecl);
            if (text != null) {
                annotation = traverseSyntheticAnnotation(allDecl, text, attrValues, false, schemaDoc);
            }
        }
        String childName = null;
        XSParticleDecl particle;
        fPArray.pushContext();
        
        for (; child != null; child = DOMUtil.getNextSiblingElement(child)) {
            
            particle = null;
            childName = DOMUtil.getLocalName(child);
            
            // Only elements are allowed in <all>
            if (childName.equals(SchemaSymbols.ELT_ELEMENT)) {
                particle = fSchemaHandler.fElementTraverser.traverseLocal(child, schemaDoc, grammar, PROCESSING_ALL_EL, parent);
            }
            else {
                Object[] args = {"all", "(annotation?, element*)", DOMUtil.getLocalName(child)};
                reportSchemaError("s4s-elt-must-match.1", args, child);
            }
            
            if (particle != null)
                fPArray.addParticle(particle);
        }
        
        particle = null;
        XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
        XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
        Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];
        
        XSModelGroupImpl group = new XSModelGroupImpl();
        group.fCompositor = XSModelGroupImpl.MODELGROUP_ALL;
        group.fParticleCount = fPArray.getParticleCount();
        group.fParticles = fPArray.popContext();
        XSObjectList annotations;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject (annotation);
        } else {
            annotations = XSObjectListImpl.EMPTY_LIST;
        }
        group.fAnnotations = annotations;
        particle = new XSParticleDecl();
        particle.fType = XSParticleDecl.PARTICLE_MODELGROUP;
        particle.fMinOccurs = minAtt.intValue();
        particle.fMaxOccurs = maxAtt.intValue();
        particle.fValue = group;
        particle.fAnnotations = annotations;
        
        particle = checkOccurrences(particle,
                SchemaSymbols.ELT_ALL,
                (Element)allDecl.getParentNode(),
                allContextFlags,
                defaultVals.longValue());
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        
        return particle;
    }
    
    /**
     * Traverse the Sequence declaration
     *
     * <sequence
     *   id = ID
     *   maxOccurs = string
     *   minOccurs = nonNegativeInteger>
     *   Content: (annotation? , (element | group | choice | sequence | any)*)
     * </sequence>
     *
     * @param seqDecl
     * @param schemaDoc
     * @param grammar
     * @return
     */
    XSParticleDecl traverseSequence(Element seqDecl,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar,
            int allContextFlags,
            XSObject parent) {
        
        return traverseSeqChoice(seqDecl, schemaDoc, grammar, allContextFlags, false, parent);
    }
    
    /**
     * Traverse the Choice declaration
     *
     * <choice
     *   id = ID
     *   maxOccurs = string
     *   minOccurs = nonNegativeInteger>
     *   Content: (annotation? , (element | group | choice | sequence | any)*)
     * </choice>
     *
     * @param choiceDecl
     * @param schemaDoc
     * @param grammar
     * @return
     */
    XSParticleDecl traverseChoice(Element choiceDecl,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar,
            int allContextFlags,
            XSObject parent) {
        
        return traverseSeqChoice (choiceDecl, schemaDoc, grammar, allContextFlags, true, parent);
    }
    
    /**
     * Common traversal for <choice> and <sequence>
     *
     * @param decl
     * @param schemaDoc
     * @param grammar
     * @param choice    If traversing <choice> this parameter is true.
     * @return
     */
    private XSParticleDecl traverseSeqChoice(Element decl,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar,
            int allContextFlags,
            boolean choice,
            XSObject parent) {

        //
        //  Traverse nested model groups with an explicit stack rather than
        //  by recursing, so that deeply nested sequences and choices cannot
        //  exhaust the Java stack.
        //
        final ArrayList<ParticleFrame> frames = new ArrayList<ParticleFrame>();
        frames.add(new ParticleFrame(decl, schemaDoc, grammar, allContextFlags,
                choice, parent));

        while (true) {
            final int last = frames.size() - 1;
            final ParticleFrame frame = frames.get(last);
            final Element child = frame.fNextChild;

            if (child != null) {
                frame.fNextChild = DOMUtil.getNextSiblingElement(child);

                final String childName = DOMUtil.getLocalName(child);
                XSParticleDecl particle = null;
                if (childName.equals(SchemaSymbols.ELT_ELEMENT)) {
                    particle = fSchemaHandler.fElementTraverser.traverseLocal(child,
                            frame.fSchemaDoc, frame.fGrammar, NOT_ALL_CONTEXT, frame.fParent);
                }
                else if (childName.equals(SchemaSymbols.ELT_GROUP)) {
                    particle = fSchemaHandler.fGroupTraverser.traverseLocal(child,
                            frame.fSchemaDoc, frame.fGrammar);

                    // A content type of all can only appear
                    // as the content type of a complex type definition.
                    if (hasAllContent(particle)) {
                        // don't insert the "all" particle, otherwise we won't be
                        // able to create DFA from this content model
                        particle = null;
                        reportSchemaError("cos-all-limited.1.2", null, child);
                    }
                }
                else if (childName.equals(SchemaSymbols.ELT_CHOICE)) {
                    frames.add(new ParticleFrame(child, frame.fSchemaDoc,
                            frame.fGrammar, NOT_ALL_CONTEXT, true, frame.fParent));
                    continue;
                }
                else if (childName.equals(SchemaSymbols.ELT_SEQUENCE)) {
                    frames.add(new ParticleFrame(child, frame.fSchemaDoc,
                            frame.fGrammar, NOT_ALL_CONTEXT, false, frame.fParent));
                    continue;
                }
                else if (childName.equals(SchemaSymbols.ELT_ANY)) {
                    particle = fSchemaHandler.fWildCardTraverser.traverseAny(child,
                            frame.fSchemaDoc, frame.fGrammar);
                }
                else {
                    Object [] args;
                    if (frame.fChoice) {
                        args = new Object[]{"choice", "(annotation?, (element | group | choice | sequence | any)*)", childName};
                    }
                    else {
                        args = new Object[]{"sequence", "(annotation?, (element | group | choice | sequence | any)*)", childName};
                    }
                    reportSchemaError("s4s-elt-must-match.1", args, child);
                }

                if (particle != null) {
                    fPArray.addParticle(particle);
                }
            }
            else {
                final XSParticleDecl particle = frame.finish();
                frames.remove(last);
                if (last == 0) {
                    return particle;
                }
                if (particle != null) {
                    fPArray.addParticle(particle);
                }
            }
        }
    }

    /**
     * Holds the in-progress traversal state for one model group.
     */
    private final class ParticleFrame {
        final Element fDecl;
        final XSDocumentInfo fSchemaDoc;
        final SchemaGrammar fGrammar;
        final int fAllContextFlags;
        final boolean fChoice;
        final XSObject fParent;
        final Object[] fAttrValues;
        final XSAnnotationImpl fAnnotation;
        Element fNextChild;

        ParticleFrame(Element decl, XSDocumentInfo schemaDoc,
                SchemaGrammar grammar, int allContextFlags, boolean choice,
                XSObject parent) {
            fDecl = decl;
            fSchemaDoc = schemaDoc;
            fGrammar = grammar;
            fAllContextFlags = allContextFlags;
            fChoice = choice;
            fParent = parent;
            fAttrValues = fAttrChecker.checkAttributes(decl, false, schemaDoc);

            Element child = DOMUtil.getFirstChildElement(decl);
            XSAnnotationImpl annotation = null;
            if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                annotation = traverseAnnotationDecl(child, fAttrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            }
            else {
                String text = DOMUtil.getSyntheticAnnotation(decl);
                if (text != null) {
                    annotation = traverseSyntheticAnnotation(decl, text, fAttrValues, false, schemaDoc);
                }
            }
            fAnnotation = annotation;
            fNextChild = child;
            fPArray.pushContext();
        }

        XSParticleDecl finish() {
            XInt minAtt = (XInt)fAttrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
            XInt maxAtt = (XInt)fAttrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
            Long defaultVals = (Long)fAttrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];

            XSModelGroupImpl group = new XSModelGroupImpl();
            group.fCompositor = fChoice ? XSModelGroupImpl.MODELGROUP_CHOICE : XSModelGroupImpl.MODELGROUP_SEQUENCE;
            group.fParticleCount = fPArray.getParticleCount();
            group.fParticles = fPArray.popContext();
            XSObjectList annotations;
            if (fAnnotation != null) {
                annotations = new XSObjectListImpl();
                ((XSObjectListImpl)annotations).addXSObject(fAnnotation);
            } else {
                annotations = XSObjectListImpl.EMPTY_LIST;
            }
            group.fAnnotations = annotations;
            XSParticleDecl particle = new XSParticleDecl();
            particle.fType = XSParticleDecl.PARTICLE_MODELGROUP;
            particle.fMinOccurs = minAtt.intValue();
            particle.fMaxOccurs = maxAtt.intValue();
            particle.fValue = group;
            particle.fAnnotations = annotations;

            particle = checkOccurrences(particle,
                    fChoice ? SchemaSymbols.ELT_CHOICE : SchemaSymbols.ELT_SEQUENCE,
                            (Element)fDecl.getParentNode(),
                            fAllContextFlags,
                            defaultVals.longValue());
            fAttrChecker.returnAttrArray(fAttrValues, fSchemaDoc);

            return particle;
        }
    }

    // Determines whether a content spec tree represents an "all" content model
    protected boolean hasAllContent(XSParticleDecl particle) {
        // If the content is not empty, is the top node ALL?
        if (particle != null && particle.fType == XSParticleDecl.PARTICLE_MODELGROUP) {
            return ((XSModelGroupImpl)particle.fValue).fCompositor == XSModelGroupImpl.MODELGROUP_ALL;
        }
        
        return false;
    }
    
    // the inner class: used to store particles for model groups
    // to avoid creating a new Vector in each model group, or when traversing
    // each model group, we use this one big array to store all particles
    // for model groups. when the traversal finishes, this class returns an
    // XSParticleDecl[] containing all particles for the current model group.
    // it's possible that we need to traverse another model group while
    // traversing one (one inside another one; referring to a global group,
    // etc.), so we have push/pos context methods to save the same of the
    // current traversal before starting the traversal of another model group.
    static class ParticleArray {
        // big array to contain all particles
        XSParticleDecl[] fParticles = new XSParticleDecl[10];
        // the ending position of particles in the array for each context
        // index 0 is reserved, with value 0. index 1 is used for the fist
        // context. so that the number of particles for context 'i' can be
        // computed simply by fPos[i] - fPos[i-1].
        int[] fPos = new int[5];
        // number of contexts
        int fContextCount = 0;
        
        // start a new context (start traversing a new model group)
        void pushContext() {
            fContextCount++;
            // resize position array if necessary
            if (fContextCount == fPos.length) {
                int newSize = fContextCount * 2;
                int[] newArray = new int[newSize];
                System.arraycopy(fPos, 0, newArray, 0, fContextCount);
                fPos = newArray;
            }
            // the initial ending position of the current context is the
            // ending position of the previsous context. which means there is
            // no particle for the current context yet.
            fPos[fContextCount] = fPos[fContextCount-1];
        }
        
        // get the number of particles of this context (model group)
        int getParticleCount() {
            return fPos[fContextCount] - fPos[fContextCount-1];
        }
        
        // add a particle to the current context
        void addParticle(XSParticleDecl particle) {
            // resize the particle array if necessary
            if (fPos[fContextCount] == fParticles.length) {
                int newSize = fPos[fContextCount] * 2;
                XSParticleDecl[] newArray = new XSParticleDecl[newSize];
                System.arraycopy(fParticles, 0, newArray, 0, fPos[fContextCount]);
                fParticles = newArray;
            }
            fParticles[fPos[fContextCount]++] = particle;
        }
        
        // end the current context, and return an array of particles
        XSParticleDecl[] popContext() {
            int count = fPos[fContextCount] - fPos[fContextCount-1];
            XSParticleDecl[] array = null;
            if (count != 0) {
                array = new XSParticleDecl[count];
                System.arraycopy(fParticles, fPos[fContextCount-1], array, 0, count);
                // clear the particle array, to release memory
                for (int i = fPos[fContextCount-1]; i < fPos[fContextCount]; i++)
                    fParticles[i] = null;
            }
            fContextCount--;
            return array;
        }
        
    }
    
    // the big particle array to hold all particles in model groups
    ParticleArray fPArray = new ParticleArray();
}
