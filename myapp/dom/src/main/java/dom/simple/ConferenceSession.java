/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package dom.simple;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;
import org.joda.time.LocalDate;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy=VersionStrategy.VERSION_NUMBER, 
        column="version")
@ObjectType("SESSION")
@Bookmarkable
public class ConferenceSession implements Comparable<ConferenceSession> {

    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        if(getSessionTitle().length()>20) {
            buf.append(getSessionTitle().substring(0, 20)).append("...");
        } else {
            buf.append(getSessionTitle());
        }
        if(getSpeaker() != null) {
            buf.append("(").append(container.titleOf(getSpeaker())).append(")");
        }
        return buf.toString();
    }

    
    // //////////////////////////////////////
    // sessionTitle (property)
    // //////////////////////////////////////
    
    private String sessionTitle;

    @javax.jdo.annotations.Column(allowsNull="false")
    @MemberOrder(sequence="1")
    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(final String name) {
        this.sessionTitle = name;
    }

    
    // //////////////////////////////////////
    // date (property)
    // //////////////////////////////////////

    private LocalDate date;

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Hidden(where=Where.ALL_TABLES)
    @MemberOrder(name="Scheduling", sequence = "3")
    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }


    
    // //////////////////////////////////////
    // Type (property)
    // //////////////////////////////////////

    public enum Type {
        KEYNOTE, ALL_DAY_TUTORIAL, SESSION, OTHER
    }

    private Type Type;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "2")
    public Type getType() {
        return Type;
    }

    public void setType(final Type Type) {
        this.Type = Type;
    }

    
    // //////////////////////////////////////
    // speaker (property)
    // //////////////////////////////////////

    private Speaker speaker;

    @javax.jdo.annotations.Column(allowsNull="true")
    @MemberOrder(sequence = "3")
    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(final Speaker speaker) {
        this.speaker = speaker;
    }
    
    public List<Speaker> autoCompleteSpeaker(String search) {
        return speakers.findByGivenOrFamilyName(search);
    }

    // //////////////////////////////////////
    // compareTo
    // //////////////////////////////////////

    @Override
    public int compareTo(ConferenceSession other) {
        return ObjectContracts.compare(this, other, "sessionTitle");
    }

    
    // //////////////////////////////////////
    // Injected
    // //////////////////////////////////////

    @SuppressWarnings("unused")
    private DomainObjectContainer container;
    public void injectDomainObjectContainer(final DomainObjectContainer container) {
        this.container = container;
    }

    private Speakers speakers;
    
    public final void injectSpeakers(final Speakers speakers) {
        this.speakers = speakers;
    }

}
