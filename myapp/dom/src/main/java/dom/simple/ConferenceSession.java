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

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.ObjectContracts;
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

    // //////////////////////////////////////
    // sessionTitle (property)
    // //////////////////////////////////////
    
    private String sessionTitle;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title
    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(final String name) {
        this.sessionTitle = name;
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
    
}
