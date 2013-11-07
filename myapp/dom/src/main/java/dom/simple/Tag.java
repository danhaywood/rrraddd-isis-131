package dom.simple;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
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

    public void injectDomainObjectContainer(
            final DomainObjectContainer container) {
        this.container = container;
    }
}
