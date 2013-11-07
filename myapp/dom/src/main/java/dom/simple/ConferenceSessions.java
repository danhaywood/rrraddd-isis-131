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

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.HomePage;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ConferenceSessions extends AbstractFactoryAndRepository {

    // //////////////////////////////////////
    // Identification in the UI
    // //////////////////////////////////////

    @Override
    public String getId() {
        return "conferenceSession";
    }

    public String iconName() {
        return "ConferenceSession";
    }

    // //////////////////////////////////////
    // List (action)
    // //////////////////////////////////////
    
    @Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<ConferenceSession> listAll() {
        return allInstances(ConferenceSession.class);
    }


    // //////////////////////////////////////
    // Create (action)
    // //////////////////////////////////////
    
    @Bookmarkable
    @MemberOrder(sequence = "2")
    public ConferenceSession create(
            final @Named("Name") String name,
            final @Named("Type") ConferenceSession.Type type) {
        final ConferenceSession obj = newTransientInstance(ConferenceSession.class);
        obj.setSessionTitle(name);
        obj.setType(type);
        persistIfNotAlready(obj);
        return obj;
    }


    // //////////////////////////////////////
    // Contributions
    // //////////////////////////////////////

    // @NotContributed
    @NotInServiceMenu
    public List<ConferenceSession> findByTag(Tag tag) {
        return allMatches(new QueryDefault<ConferenceSession>(
                ConferenceSession.class, "findByTag", "tag", tag));
    }



    // //////////////////////////////////////
    // Prototype actions
    // //////////////////////////////////////


    @Bookmarkable
    @Prototype
    @ActionSemantics(Of.SAFE)
    public ConferenceSession firstOne() {
        return listAll().get(0);
    }


}
