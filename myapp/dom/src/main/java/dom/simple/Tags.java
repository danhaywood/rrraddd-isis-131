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