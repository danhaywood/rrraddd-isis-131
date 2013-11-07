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