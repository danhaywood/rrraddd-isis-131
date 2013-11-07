rrraddd-isis-131
================

For RRRADDD! conference session demo, against Isis v1.3.1.


##Run the archetype

#### DEMO

<pre>    
mvn archetype:generate                                       \
  -D archetypeGroupId=org.apache.isis.archetype              \
  -D archetypeArtifactId=simple_wicket_restful_jdo-archetype \
  -D archetypeVersion=1.3.1                                  \
  -D groupId=com.mycompany                                   \
  -D artifactId=myapp                                        \
  -D version=1.0-SNAPSHOT                                    \
  -B
</pre>    
    
#####Checkpoint:
`git reset --hard checkpoint-010`
    
##Build and run

<pre>
cd myapp
mvn clean install
</pre>    

then

<pre>    
mvn antrun:run
</pre>    

or alternatively

<pre>    
mvn jetty:run    
</pre>    
    

####DEMO

* install fixtures
* list all
* create new
* list all    

        
##Dev environment

#### DEMO (configure)

* Here's one I did earlier...
    * `File > Import > Maven > Existing Maven Projects`
    * Configure JDO/DataNucleus
    * Working sets
    * Shortcuts
* Resources
    * [http://isis.apache.org/getting-started/screencasts.html](http://isis.apache.org/getting-started/screencasts.html)
    * [http://isis.apache.org/components/objectstores/jdo/datanucleus-and-eclipse.html](http://isis.apache.org/getting-started/screencasts.html)


####DEMO (run)
* Run the app from within the IDE



##Explore codebase

####DEMO

* `myapp` : parent module
* `myapp-dom`
   - `dom.simple.SimpleObject`
   - `dom.simple.SimpleObjects`
* `myapp-fixture`
   - `fixture.simple.SimpleObjectsFixture`
* `myapp-integtests`
* `myapp-webapp`



## Testing

####DEMO

* `myapp-dom` unit tests
   - run 
   - inspect, eg
        - `SimpleObjectTest_name`
* `myapp-integtests` integration tests
   - run
   - inspect, eg: 
       - `integration.tests.smoke.SimpleObjectsTest_listAll_and_create`
       - `integration.specs.simple.SimpleObjectSpec_listAllAndCreate.feature`
   -  generated report, eg
        - `file:///C:/GITHUB/danhaywood/rrraddd-isis-131/myapp/integtests/target/cucumber-html-report/index.html`
    - change test in IDE, re-run (in Maven)   



##Remove tests (since prototyping)

####DEMO

* close `myapp-integtests`
* in `myapp-dom`
   * `src/test/java` to `javaMOVED`
   * alt-f5 (update maven projects)


#####Checkpoint:
`git reset --hard checkpoint-020`





##Rename domain class, domain service, fixtures

####SimpleObject

rename to `ConferenceSession`

####SimpleObjects

rename to `ConferenceSessions`

####SimpleObjectsFixture

rename to `ConferenceSessionsFixture`


####myapp-webapp/src/main/webapp/WEB-INF/isis.properties

<pre>
isis.services = \
                10:dom.simple.SimpleObjects,\
                ...

isis.fixtures=fixture.simple.SimpleObjectsFixture
</pre>

to

<pre>
isis.services = \
                10:dom.simple.ConferenceSessions,\
                ...

isis.fixtures=fixture.simple.ConferenceSessionsFixture
</pre>

####ConferenceSessionsFixture

<pre>
isisJdoSupport.executeUpdate("delete from \"SimpleObject\"");
</pre>

to:

<pre>
isisJdoSupport.executeUpdate("delete from \"ConferenceSession\"");
</pre>

#### icon

* add icon, `icons/OverheadProjector.png` -> `myapp/dom/src/main/resources/images/ConferenceSession.png`



####ConferenceSessions

* getId()

*a "nice to have", but has an effect on the URLs exposed in the REST API*

<pre>
public String getId() {
    return "conferenceSessions";
}
</pre>

* `iconName()`

<pre>
public String iconName() {
    return "ConferenceSession";
}
</pre>

####ConferenceSession

*a "nice to have", but has an effect on the URLs exposed in the REST API*
<pre>
@ObjectType("SESSION")
</pre>


####DEMO

* menu service changed to "Conference Sessions"
* tooltip on icon/title changed to "Conference Session"
* icon changed


#####Checkpoint:
`git reset --hard checkpoint-030`






##Refactor domain class

####ConferenceSession

* rename `name` property to `sessionTitle`

* update `compareTo()`
<pre>
public int compareTo(ConferenceSession other) {
    return ObjectContracts.compare(this, other, "sessionTitle");
}
</pre>

* review title

<pre>
@Title
public String getSessionTitle() {
    return sessionTitle;
}
</pre>

#### ConferenceSessionsFixture

* Better fixture data

<pre>
private void installObjects() {
    create("RRRADDD! Apache Isis");
    create("Best practices for AngularJS");
    create("Refactor your specs!");
    create("Why Kotlin?");
    create("Introduction to the Play Framework");
    create("Object Oriented Design in the Wild");
}
</pre>


####DEMO

* table shows sessionTitle column
* object form shows sessionTitle property
* object page labelled with title
* updated data

also

* discuss injected services into fixtures, entities, services etc.
  
#####Checkpoint:
`git reset --hard checkpoint-040`





##Add date scalar property

####ConferenceSession

<pre>
// //////////////////////////////////////
// date (property)
// //////////////////////////////////////

private LocalDate date;

@javax.jdo.annotations.Persistent
@javax.jdo.annotations.Column(allowsNull = "true")
@MemberOrder(sequence = "1")
public LocalDate getDate() {
    return date;
}

public void setDate(final LocalDate date) {
    this.date = date;
}
</pre>


####ConferenceSessionsFixture    

<pre>
private ConferenceSession create(final String name) {
    ...
    session.setDate(clockService.now().plusDays((int)(Math.random()*5)));
    ...
}
</pre>

and

<pre>
private ClockService clockService;

public final void injectClockService(final ClockService clockService) {
    this.clockService = clockService;
}
</pre>
>


####DEMO

* now has a date property in title and in form
* can edit, obviously
* is optional
 
#####Checkpoint:
`git reset --hard checkpoint-050`




## Add enum scalar property

* type property

#####ConferenceSession

<pre>
// //////////////////////////////////////
// Type (property)
// //////////////////////////////////////

public enum Type {
    KEYNOTE, ALL_DAY_TUTORIAL, SESSION, OTHER
}

private Type Type;
@javax.jdo.annotations.Column(allowsNull="false")
@MemberOrder(sequence = "1")
public Type getType() {
    return Type;
}
public void setType(final Type Type) {
    this.Type = Type;
}
</pre>

    
#####ConferenceSessions

* update the `create(...)` method

<pre>
public ConferenceSession create(
        final @Named("Name") String name,
        final @Named("Type") ConferenceSession.Type type) {
    final ConferenceSession obj = newTransientInstance(ConferenceSession.class);
    obj.setSessionTitle(name);
    obj.setType(type);
    persistIfNotAlready(obj);
    return obj;
}
</pre>

    
#####ConferenceSessionsFixture

* update the fixture data

<pre>
private void installObjects() {
    create("RRRADDD! Apache Isis", ConferenceSession.Type.SESSION);
    create("Best practices for AngularJS", ConferenceSession.Type.SESSION);
    create("Refactor your specs!", ConferenceSession.Type.SESSION);
    create("Why Kotlin?", ConferenceSession.Type.OTHER);
    create("Introduction to the Play Framework", ConferenceSession.Type.SESSION);
    create("Object Oriented Design in the Wild", ConferenceSession.Type.ALL_DAY_TUTORIAL);
}

private ConferenceSession create(final String name, final Type type) {
    return simpleObjects.create(name, type);
}
</pre>

  
####DEMO

* list all
   * table with new property
* show object form
* edit object details
    * both are mandatory
* create new object


#####Checkpoint:
`git reset --hard checkpoint-060`



##UI hints

##### `ConferenceSession
    
* sessionTitle, then type, then date

<pre>
@MemberOrder(sequence="1")
public String getSessionTitle() {
</pre>

* then type
 
<pre>
@MemberOrder(sequence = "2")
public Type getType() {
</pre>
    
* then date (hide in table)

<pre>
@Hidden(where=Where.ALL_TABLES)
@MemberOrder(name="Scheduling", sequence = "3")
public LocalDate getDate() {
</pre>


####DEMO

* table shows only title, type properties (in that order)
* order form shows
   * title, type properties grouped together in the default 'General' group
   * date property separately in a new 'Scheduling' group

#####Checkpoint:
`git reset --hard checkpoint-070`



Add another type (and domain service)
-------------------------------------

####Speaker

    package dom.simple;
    
    import javax.jdo.annotations.IdentityType;
    import javax.jdo.annotations.VersionStrategy;
    
    import org.apache.isis.applib.DomainObjectContainer;
    import org.apache.isis.applib.annotation.Bookmarkable;
    import org.apache.isis.applib.annotation.MemberOrder;
    import org.apache.isis.applib.annotation.ObjectType;
    import org.apache.isis.applib.annotation.Title;
    import org.apache.isis.applib.util.ObjectContracts;
    
    @javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
    @javax.jdo.annotations.DatastoreIdentity(
            strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
             column="id")
    @javax.jdo.annotations.Version(
            strategy=VersionStrategy.VERSION_NUMBER, 
            column="version")
    @ObjectType("SPEAKER")
    @Bookmarkable
    public class Speaker implements Comparable<Speaker> {
    
        
        // //////////////////////////////////////
        // givenName (property)
        // //////////////////////////////////////
    
        private String givenName;
    
        @MemberOrder(sequence = "1")
        @javax.jdo.annotations.Column(allowsNull="false")
        @Title(sequence="2", prepend=", ")
        public String getGivenName() {
            return givenName;
        }
    
        public void setGivenName(final String name) {
            this.givenName = name;
        }
        
        
        // //////////////////////////////////////
        // familyName (property)
        // //////////////////////////////////////
    
        private String familyName;
    
        @MemberOrder(sequence = "2")
        @javax.jdo.annotations.Column(allowsNull="false")
        @Title(sequence="1")
        public String getFamilyName() {
            return familyName;
        }
    
        public void setFamilyName(final String familyName) {
            this.familyName = familyName;
        }
    
    
        // //////////////////////////////////////
        // compareTo
        // //////////////////////////////////////
    
        @Override
        public int compareTo(Speaker other) {
            return ObjectContracts.compare(this, other, "givenName, familyName");
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


####Speakers

    package dom.simple;
    
    import java.util.List;
    
    import org.apache.isis.applib.AbstractFactoryAndRepository;
    import org.apache.isis.applib.annotation.ActionSemantics;
    import org.apache.isis.applib.annotation.ActionSemantics.Of;
    import org.apache.isis.applib.annotation.Bookmarkable;
    import org.apache.isis.applib.annotation.MemberOrder;
    import org.apache.isis.applib.annotation.Named;
    
    public class Speakers extends AbstractFactoryAndRepository {
    
        @Override
        public String getId() {
            return "speakers";
        }
    
        public String iconName() {
            return "Speaker";
        }
    
        @Bookmarkable
        @ActionSemantics(Of.SAFE)
        @MemberOrder(sequence = "1")
        public List<Speaker> listAll() {
            return allInstances(Speaker.class);
        }
    
        @MemberOrder(sequence = "2")
        public Speaker create(
                final @Named("Given name") String givenName,
                final @Named("Family name") String familyName) {
            Speaker speaker = getContainer().newTransientInstance(Speaker.class);
            speaker.setGivenName(givenName);
            speaker.setFamilyName(familyName);
            getContainer().persistIfNotAlready(speaker);
            return speaker;
        }
    }


#####ConferenceSessionsFixture

* install()

        isisJdoSupport.executeUpdate("delete from \"Speaker\"");

* installObjects()

        createSpeaker("Dan", "Haywood");
        createSpeaker("Misko", "Hevery");
        createSpeaker("Cyrille", "Martraire");
        createSpeaker("Svetlana", "Haywood");
        createSpeaker("James", "Ward");
        createSpeaker("Jessica", "Kerr");

* createSpeaker() method:

        private Speaker createSpeaker(final String givenName, final String familyName) {
            return speakers.create(givenName, familyName);
        }


* and inject new domain service:

        private Speakers speakers;
        public final void injectSpeakers(final Speakers speakers) {
            this.speakers = speakers;
        }

   
#####isis.properties

* number indicates the ordering in the menu

        isis.services = \
                        10:dom.simple.ConferenceSessions,\
                        20:dom.simple.Speakers,\

#### icon

* add icon, `icons/Customer.gif` -> `myapp/dom/src/main/resources/images/Speaker.gif`



####DEMO

* new domain service
* list, create

#####Checkpoint:
`git reset --hard checkpoint-080`


##Add reference property

####ConferenceSession

* 'speaker' property (of type 'Speaker')

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

####DEMO

* displays field, no way to associate :-(


#####Checkpoint:
`git reset --hard checkpoint-090`


##Add autoComplete (and repository queries)

####ConferenceSession

* autoComplete method:

        public List<Speaker> autoCompleteSpeaker(String search) {
            return speakers.findByGivenOrFamilyName(search);
        }

* inject service:

        private Speakers speakers;
    
        public final void injectSpeakers(final Speakers speakers) {
            this.speakers = speakers;
        }

    
#####Speakers    

* finder (hidden because only intended to be called programmatically)
    
        @Hidden
        public List<Speaker> findByGivenOrFamilyName(String search) {
            return getContainer().allMatches(
                    new QueryDefault<Speaker>(Speaker.class, 
                            "findByGivenOrFamilyName", 
                            "givenOrFamilyName", ".*"+search+".*"));
        }


#####Speaker

* Isis configured to use JDO, so add the annotation

        @javax.jdo.annotations.Queries({
            @javax.jdo.annotations.Query(
                    name="findByGivenOrFamilyName", language="JDOQL",
                    value="SELECT "
                            + "FROM dom.simple.Speaker "
                            + "WHERE givenName.matches(:givenOrFamilyName) "
                            + "   || familyName.matches(:givenOrFamilyName)")
        })

####DEMO

* can now add speaker
    * using either given or family name
* Speakers finder is not visible

#####Checkpoint:
`git reset --hard checkpoint-100`



   
##Update title (also, prototype actions)

####ConferenceSession

* title imperatively, rather than declaratively

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


####DEMO:

* metamodel validation exception



####ConferenceSession

* remove `@Title` from `#sessionTitle`

####ConferenceSessions

* Add prototyping action to domain service

        @Bookmarkable
        @Prototype
        @ActionSemantics(Of.SAFE)
        public ConferenceSession firstOne() {
            return listAll().get(0);
        }
              


####DEMO:
* now runs ok
* prototype action styled differently
* add speaker to session
* title changes
* (optional) run in SERVER mode


#####Checkpoint:
`git reset --hard checkpoint-110`




## Another new entity, Tag

*the motivation for this is that we want to add a new collection of tags from ConferenceSession*


####Tag

* new entity (note the `compareTo`, `toString`, using Isis helper methods)

        package dom.simple;
        
        import javax.jdo.annotations.IdentityType;
        import javax.jdo.annotations.VersionStrategy;
        
        import org.apache.isis.applib.DomainObjectContainer;
        import org.apache.isis.applib.annotation.Bookmarkable;
        import org.apache.isis.applib.annotation.MemberOrder;
        import org.apache.isis.applib.annotation.ObjectType;
        import org.apache.isis.applib.annotation.Title;
        import org.apache.isis.applib.util.ObjectContracts;
        
        @javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
        @javax.jdo.annotations.DatastoreIdentity(
                strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
                 column="id")
        @javax.jdo.annotations.Version(
                strategy=VersionStrategy.VERSION_NUMBER, 
                column="version")
        @ObjectType("TAG")
        @Bookmarkable
        public class Tag implements Comparable<Tag> {
        
            
            // //////////////////////////////////////
            // name (property)
            // //////////////////////////////////////
        
            private String name;
        
            @javax.jdo.annotations.Column(allowsNull = "true")
            @Title
            @MemberOrder(sequence = "1")
            public String getName() {
                return name;
            }

            public void setName(final String name) {
                this.name = name;
            }
        
        
        
            // //////////////////////////////////////
            // compareTo
            // //////////////////////////////////////
        
            @Override
            public int compareTo(Tag other) {
                return ObjectContracts.compare(this, other, "name");
            }
        
            
            // //////////////////////////////////////
            // toString
            // //////////////////////////////////////
        
            @Override
            public String toString() {
                return ObjectContracts.toString(this, "name");
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


####Tags

* new domain service; all actions are hidden

        package dom.simple;
        
        import java.util.List;
        
        import org.apache.isis.applib.AbstractFactoryAndRepository;
        import org.apache.isis.applib.annotation.Hidden;
        
        public class Tags extends AbstractFactoryAndRepository {
        
            @Override
            public String getId() {
                return "tags";
            }
            public String iconName() {
                return "Tag";
            }
        
            @Hidden
            public List<Tag> listAll() {
                return allInstances(Tag.class);
            }
            @Hidden
            public Tag create(final String name) {
                final Tag obj = newTransientInstance(Tag.class);
                obj.setName(name);
                persistIfNotAlready(obj);
                return obj;
            }
        }


####ConferenceSessionsFixture

* installObjects()
 
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


* createTag() method

        private Tag createTag(final String name) {
            return tags.create(name);
        }

* inject new service
    
        private Tags tags;
        public final void injectTags(final Tags tags) {
            this.tags = tags;
        }


####isis.properties

* no number required, since not visible in UI

        isis.services = \
                        10:dom.simple.ConferenceSessions,\
                        20:dom.simple.Speakers,\
                        dom.simple.Tags,\


#### icon

* add icon, `icons/RedFlag.png` -> `myapp/dom/src/main/resources/images/Tag.png`

####DEMO 

* (no visible change, since Tags actions are all hidden)                
       
#####Checkpoint:
`git reset --hard checkpoint-120`




         
## Add collection
              
#### ConferenceSession
              
* tags collection

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


####DEMO
* view the DDL
* open object
* can't add tags :-(

#####Checkpoint:
`git reset --hard checkpoint-130`



##Add actions

####ConferenceSession

* addTag action

        // //////////////////////////////////////
        // addTag (action)
        // //////////////////////////////////////
        
        @MemberOrder(sequence = "1")
        public ConferenceSession addTag(final Tag tag) {
            getTags().add(tag);
            return this;
        }

* removeTag action
 
        // //////////////////////////////////////
        // removeTag (action)
        // //////////////////////////////////////
        
        @MemberOrder(sequence = "2")
        public ConferenceSession removeTag(final Tag tag) {
            getTags().remove(tag);
            return this;
        }


####ConferenceSessionsFixture

* create(...) method, add three (random) tags for each session

        for (Tag tag : random(tags.listAll(), 3)) {
            session.addTag(tag);
        }

* supporting random(...) method

        private List<Tag> random(List<Tag> tags, int num) {
            List<Tag> availableTags = Lists.newArrayList(tags);
            List<Tag> selectedTags = Lists.newArrayList();
            while(selectedTags.size()<num) {
                int selected = (int)(availableTags.size() * Math.random());
                try {
                    selectedTags.add(availableTags.remove(selected));
                } catch(Exception ex) {}
            }
            return selectedTags;
        }


####DEMO:
* view
* need to get a reference to the tag again...
    
    

#####Checkpoint:
`git reset --hard checkpoint-140`



##Add choices

*an alternative to using autoComplete, useful if the list of choices is relatively short*

####ConferenceSession

* inject Tags repo

        private Tags tagRepo;
        
        public final void injectTags(final Tags tags) {
            this.tagRepo = tags;
        }

* added the "choices" supporting method for addTag action (all tags not yet added)
 
        public Collection<Tag> choices0AddTag() {
            List<Tag> tags = Lists.newArrayList(tagRepo.listAll());
            tags.removeAll(getTags());
            return tags;
        }

* added the "choices" supporting method for removeTag action (only those tags previously added)

        public Collection<Tag> choices0RemoveTag() {
            return getTags();
        }
    
####DEMO

* can now add/remove tags, yay!

#####Checkpoint:
`git reset --hard checkpoint-150`

    
    
##More UI Hints (dynamic, this time)

####DEMO:
* download layout
* edit column spans
* copy `ConferenceSession.layout.json` (below) to `myapp-dom/src/main/java/dom/simple`
* refresh layout
 
        {
          "columns": [
            {
              "span": 6,
              "memberGroups": {
                "General": {
                  "members": {
                    "sessionTitle": {},
                    "type": {},
                    "speaker": {}
                  }
                },
                "Scheduling": {
                  "members": {
                    "date": {}
                  }
                }
              }
            },
            {
              "span": 0,
              "memberGroups": {}
            },
            {
              "span": 0,
              "memberGroups": {}
            },
            {
              "span": 6,
              "collections": {
                "tags": {
                  "actions": {
                    "addTag": {},
                    "removeTag": {}
                  }
                }
              }
            }
          ],
          "actions": {
            "downloadLayout": {},
            "refreshLayout": {}
          }
        }

#####Checkpoint:
`git reset --hard checkpoint-160`




##Declarative and imperative business rules

####ConferenceSession

* add RegEx pattern and a maximum length to the 'sessionTitle' property (latter using JDO annotation)

        @javax.jdo.annotations.Column(allowsNull="false",length=40)
        @RegEx(validation="[^%]+")
        public String getSessionTitle() {


* can't add more than 4 tags

        public String disableAddTag(Tag tag) {
            return getTags().size() >= 4? "Cannot add more than 4 tags": null;
        }

####DEMO

* cannot save session whose sessionTitle property has > 40 chars
* cannot add more than 4 tags to a session
* 

#####Checkpoint:
`git reset --hard checkpoint-170`

    


##Other business rules (hiding, validation)

####ConferenceSession

* hideAddTag 

        public boolean hideAddTag() {
            return getTags().size() >= 4;
        }

####DEMO
* action becomes hidden


####ConferenceSession

* offer tags that are already in the collection, but then prevent using validation

        public Collection<Tag> choices0AddTag() {
            List<Tag> tags = Lists.newArrayList(tagRepo.listAll());
            //tags.removeAll(getTags());
            return tags;
        }
    
        public String validateAddTag(Tag tag) {
            return getTags().contains(tag)? "Already added that tag": null;
        }

####DEMO
* can attempt to add a tag, but prevented


####ConferenceSession

* backing out the above changes

        public Collection<Tag> choices0AddTag() {
            List<Tag> tags = Lists.newArrayList(tagRepo.listAll());
            tags.removeAll(getTags());
            return tags;
        }

        //    public String validateAddTag(Tag tag) {
        //        return getTags().contains(tag)? "Already added that tag": null;
        //    }
        
        //    public boolean hideAddTag() {
        //        return getTags().size() >= 4;
        //    }

        public String disableAddTag(Tag tag) {
            return getTags().size() >= 4? "Cannot add more than 4 tags": null;
        }



##Contributed Actions etc

####ConferenceSessions

* add action, will be contributed to Tag, as both action and a collection 

        // //////////////////////////////////////
        // Contributions
        // //////////////////////////////////////

        // @NotContributed
        @NotInServiceMenu
        public List<ConferenceSession> findByTag(Tag tag) {
            return allMatches(new QueryDefault<ConferenceSession>(
                    ConferenceSession.class, "findByTag", "tag", tag));
        }

####ConferenceSession

* declare JDO query
 
        @javax.jdo.annotations.Queries({
            @javax.jdo.annotations.Query(
                    name="findByTag", language="JDOQL",
                    value="SELECT "
                            + "FROM dom.simple.ConferenceSession "
                            + "WHERE tags.contains(:tag)")
        })

#### DEMO

* view Tag; shows the sessions that are associated with it

#####Checkpoint:
`git reset --hard checkpoint-180`


    

##Bookmarks
    
####DEMO
* bookmarked objects
   * eg `ConferenceSession`
* bookmarked actions
   * eg `ConferenceSessions#listAll`
    



##CSS

####myapp-webapp/src/main/webapp/css/application.css

* example custom CSS already defined for "x-highlight" and "x-caution" CSS classes 

#### ConferenceSession

* addTag, add annotation

        @CssClass("x-highlight")
        public ConferenceSession addTag(final Tag tag) {

* removeTag, add annotation
    
        @CssClass("x-caution")
        public ConferenceSession removeTag(final Tag tag) {


#### DEMO

* view session object

#####Checkpoint:
`git reset --hard checkpoint-190`



##Widgets, also @HomePage

*show Conference sessions on a calendar view.  This uses a "third-party" component, up on github*

#### myapp/pom.xml

* parent module, scope=import for dependency management

        <dependencyManagement>
            <dependencies>
                ...
                
                <dependency>
                    <groupId>com.danhaywood.isis.wicket</groupId>
                    <artifactId>danhaywood-isis-wicket-fullcalendar2</artifactId>
                    <version>1.3.0</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
        
            </dependencies>
        </dependencyManagement>
    
    
####myapp-dom/pom.xml

* dom project depends on modules 'applib' (minimal coupling, defines an interface and value type)
 
    	<dependencies>
            ...
    
            <dependency>
                <groupId>com.danhaywood.isis.wicket</groupId>
                <artifactId>danhaywood-isis-wicket-fullcalendar2-applib</artifactId>
            </dependency>
            
    	</dependencies>


####myapp-webapp/pom.xml

* webapp project references the actual widget UI implementation

        <dependencies>
            ...
    
            <dependency>
               <groupId>com.danhaywood.isis.wicket</groupId>
                <artifactId>danhaywood-isis-wicket-fullcalendar2-ui</artifactId>
            </dependency>
     
        </dependencies>
  

#####Checkpoint:
`git reset --hard checkpoint-200`



  
####ConferenceSession

* declare it implements the `CalendarEventable` interface
 
        public class ConferenceSession implements Comparable<ConferenceSession>
            , com.danhaywood.isis.wicket.fullcalendar2.applib.CalendarEventable {
        

* and implement the required methods:
            
            // //////////////////////////////////////
            // CalendarEventable impl
            // //////////////////////////////////////
        
            @Programmatic
            @Override
            public String getCalendarName() {
                return "date";
            }
        
            @Programmatic
            @Override
            public CalendarEvent toCalendarEvent() {
                return new CalendarEvent(
                        getDate().toDateTimeAtStartOfDay(), 
                        "date", 
                        container.titleOf(this));
            }


##ConferenceSessions

* indicate that the listAll action should be called for the home page
    
        @HomePage
        @Bookmarkable
        @ActionSemantics(Of.SAFE)
        @MemberOrder(sequence = "1")
        public List<ConferenceSession> listAll() {
            return allInstances(ConferenceSession.class);
        }



####DEMO
* all sessions displayed automatically on home page
* switch to calendar view (top right, new icon should appear)


#####Checkpoint:
`git reset --hard checkpoint-210`



    
##View models

####SpeakerViewModel

* shows summary info of speaker, their sessions, the tags of those sessions

        package dom.simple;
        
        import java.util.List;
        import java.util.Set;
        
        import org.apache.isis.applib.DomainObjectContainer;
        import org.apache.isis.applib.ViewModel;
        import org.apache.isis.applib.annotation.MemberOrder;
        import org.apache.isis.applib.annotation.Render;
        import org.apache.isis.applib.annotation.Render.Type;
        import org.apache.isis.applib.annotation.Title;
        import org.apache.isis.applib.services.bookmark.Bookmark;
        import org.apache.isis.applib.services.bookmark.BookmarkService;
        
        import com.google.common.base.Function;
        import com.google.common.base.Predicate;
        import com.google.common.collect.Iterables;
        import com.google.common.collect.Lists;
        import com.google.common.io.BaseEncoding;
        
        public class SpeakerViewModel implements ViewModel {
        
            
            // //////////////////////////////////////
            // ViewModel impl
            // //////////////////////////////////////
        
            @Override
            public String viewModelMemento() {
                Bookmark bookmark = bookmarkService.bookmarkFor(getSpeaker());
                return encode(bookmark);
            }
        
            @Override
            public void viewModelInit(String memento) {
                Bookmark bookmark = decode(memento);
                setSpeaker((Speaker) bookmarkService.lookup(bookmark));
            }
        
            static String encode(Bookmark bookmark) {
                return BaseEncoding.base32().encode(bookmark.toString().getBytes());
            }
        
            private static Bookmark decode(String memento) {
                return new Bookmark(new String(BaseEncoding.base32().decode(memento)));
            }
            
            
            // //////////////////////////////////////
            // speaker (property)
            // //////////////////////////////////////
        
            private Speaker speaker;
        
            @javax.jdo.annotations.Column(allowsNull = "true")
            @Title
            @MemberOrder(sequence = "1")
            public Speaker getSpeaker() {
                return speaker;
            }
        
            public void setSpeaker(final Speaker speaker) {
                this.speaker = speaker;
            }
        
        
        
            // //////////////////////////////////////
            // numberOfSessions (property)
            // //////////////////////////////////////
        
        
            @MemberOrder(sequence = "2")
            public int getNumberOfSessions() {
                return getPresenting().size();
            }
        
        
            // //////////////////////////////////////
            // presenting (collection)
            // //////////////////////////////////////
        
            @Render(Type.EAGERLY)
            @MemberOrder(sequence = "1")
            public List<ConferenceSession> getPresenting() {
                return container.allMatches(ConferenceSession.class, new Predicate<ConferenceSession>() {
                    @Override
                    public boolean apply(ConferenceSession input) {
                        return input.getSpeaker() == getSpeaker();
                    }
                });
            }
        
            // //////////////////////////////////////
            // tags (collection)
            // //////////////////////////////////////
            
            @Render(Type.EAGERLY)
            @MemberOrder(sequence = "2")
            public List<Tag> getTags() {
                return Lists.newArrayList(
                        Iterables.concat(
                            Iterables.transform(
                                getPresenting(), 
                                new Function<ConferenceSession, Set<Tag>>(){
                                    @Override
                                    public Set<Tag> apply(ConferenceSession input) {
                                        return input.getTags();
                                    }
                                })
                        ));
            }
            
        
            
            // //////////////////////////////////////
            // Injected
            // //////////////////////////////////////
        
            private DomainObjectContainer container;
            public final void injectDomainObjectContainer(final DomainObjectContainer container) {
                this.container = container;
            }
        
            private BookmarkService bookmarkService;
            public final void injectBookmarkService(final BookmarkService bookmarkService) {
                this.bookmarkService = bookmarkService;
            }  
        }
        
        
####Speakers

* new action to return all speakers as home page (should really be in its own application service...)
        
        // //////////////////////////////////////
        // view models
        // //////////////////////////////////////
    
        @ActionSemantics(Of.SAFE)
        @HomePage
        public List<SpeakerViewModel> all() {
            return Lists.newArrayList(Iterables.transform(listAll(), 
                new Function<Speaker, SpeakerViewModel>() {
                    @Override
                    public SpeakerViewModel apply(Speaker input) {
                        Bookmark bookmark = bookmarkService.bookmarkFor(input);
                        return getContainer().newViewModelInstance(
                                SpeakerViewModel.class, 
                                SpeakerViewModel.encode(bookmark));
                    }
                })
            );
        }

* and inject in the framework-provided `BookmarkService`    

        // //////////////////////////////////////
        // Injected
        // //////////////////////////////////////
        
        private BookmarkService bookmarkService;
    
        public final void injectBookmarkService(final BookmarkService bookmarkService) {
            this.bookmarkService = bookmarkService;
        }

#### ConferenceSessions

* remove @HomePage annotation from the earlier `listAll()` action

####ConferenceSessionsFixture

* create(...) set up random speaker with each session

        session.setSpeaker(randomSpeaker());

* randomSpeaker() supporting method

        private Speaker randomSpeaker() {
            List<Speaker> all = speakers.listAll();
            while(true) {
                try {
                    int selected = (int)(all.size() * Math.random());
                    return all.get(selected);
                } catch(Exception ex){}
            }
        }


#### Demo
* hit the home page
* select a speaker view model

#####Checkpoint:
`git reset --hard checkpoint-220`



## REST API

####DEMO

* [http://localhost:8080/restful](http://localhost:8080/restful)
