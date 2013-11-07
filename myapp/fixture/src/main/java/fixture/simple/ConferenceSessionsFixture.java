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

package fixture.simple;

import java.util.List;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import services.ClockService;

import com.google.common.collect.Lists;

import dom.simple.ConferenceSession;
import dom.simple.ConferenceSessions;
import dom.simple.Speaker;
import dom.simple.Speakers;
import dom.simple.Tag;
import dom.simple.Tags;

public class ConferenceSessionsFixture extends AbstractFixture {

    
    @Override
    public void install() {

        isisJdoSupport.executeUpdate("delete from \"ConferenceSession\"");
        isisJdoSupport.executeUpdate("delete from \"Speaker\"");

        installObjects();
        
        getContainer().flush();
    }

    private void installObjects() {
        createTag("UX");
        createTag("Mobile");
        createTag("Java");
        createTag("Agile");
        createTag(".NET");
        createTag("Test");
        createTag("Mastery");
        createTag("Web");
        createTag("Architecture");
        createTag("Dev Ops");
        createTag("Cloud");
        createTag("Languages");
        createTag("Tools");
        createTag("Team");
        createTag("Database");
        createTag("Javascript");
        createTag("Keynote");
        
        createSpeaker("Dan", "Haywood");
        createSpeaker("Misko", "Hevery");
        createSpeaker("Cyrille", "Martraire");
        createSpeaker("Svetlana", "Haywood");
        createSpeaker("James", "Ward");
        createSpeaker("Jessica", "Kerr");

        create("RRRADDD! Apache Isis", ConferenceSession.Type.SESSION);
        create("Best practices for AngularJS", ConferenceSession.Type.SESSION);
        create("Refactor your specs!", ConferenceSession.Type.SESSION);
        create("Why Kotlin?", ConferenceSession.Type.OTHER);
        create("Introduction to the Play Framework", ConferenceSession.Type.SESSION);
        create("Object Oriented Design in the Wild", ConferenceSession.Type.ALL_DAY_TUTORIAL);
    }


    // //////////////////////////////////////

    private ConferenceSession create(final String name, final ConferenceSession.Type type) {
        ConferenceSession session = conferenceSessions.create(name, type);
        session.setDate(clockService.now().plusDays((int)(Math.random()*5)));
        return session;
    }

    private Speaker createSpeaker(final String givenName, final String familyName) {
        return speakers.create(givenName, familyName);
    }
    
    private Tag createTag(final String name) {
        return tags.create(name);
    }


    // //////////////////////////////////////


    private ConferenceSessions conferenceSessions;

    public void injectConferenceSessions(final ConferenceSessions conferenceSessions) {
        this.conferenceSessions = conferenceSessions;
    }

    
    private IsisJdoSupport isisJdoSupport;
    public void injectIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }

    
    private ClockService clockService;

    public final void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }

    private Speakers speakers;
    public final void injectSpeakers(final Speakers speakers) {
        this.speakers = speakers;
    }

    private Tags tags;
    public final void injectTags(final Tags tags) {
        this.tags = tags;
    }

}
