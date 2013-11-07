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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.CssClass;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy=VersionStrategy.VERSION_NUMBER, 
        column="version")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name="findByTag", language="JDOQL",
            value="SELECT "
                    + "FROM dom.simple.ConferenceSession "
                    + "WHERE tags.contains(:tag)")
})
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

    @javax.jdo.annotations.Column(allowsNull="false",length=40)
    @RegEx(validation="[^%]+")
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
    // tags (collection)
    // //////////////////////////////////////

    @javax.jdo.annotations.Join
    @javax.jdo.annotations.Element(dependent = "false")
    private SortedSet<Tag> tags = new TreeSet<Tag>();

    @Render(Render.Type.EAGERLY)
    @Disabled
    @MemberOrder(sequence = "1")
    public SortedSet<Tag> getTags() {
        return tags;
    }

    public void setTags(final SortedSet<Tag> tags) {
        this.tags = tags;
    }
    
    
    // //////////////////////////////////////
    // addTag (action)
    // //////////////////////////////////////
    
    @CssClass("x-highlight")
    @MemberOrder(sequence = "1")
    public ConferenceSession addTag(final Tag tag) {
        getTags().add(tag);
        return this;
    }

    public String disableAddTag(Tag tag) {
        return getTags().size() >= 4? "Cannot add more than 4 tags": null;
    }

    public Collection<Tag> choices0AddTag() {
        List<Tag> tags = Lists.newArrayList(tagRepo.listAll());
        tags.removeAll(getTags());
        return tags;
    }

    // //////////////////////////////////////
    // removeTag (action)
    // //////////////////////////////////////
    
    @CssClass("x-caution")
    @MemberOrder(sequence = "2")
    public ConferenceSession removeTag(final Tag tag) {
        getTags().remove(tag);
        return this;
    }

    public Collection<Tag> choices0RemoveTag() {
        return getTags();
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

    private Tags tagRepo;
    
    public final void injectTags(final Tags tags) {
        this.tagRepo = tags;
    }

}
