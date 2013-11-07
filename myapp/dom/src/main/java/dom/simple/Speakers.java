package dom.simple;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.HomePage;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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

    @Hidden
    public List<Speaker> findByGivenOrFamilyName(String search) {
        return getContainer().allMatches(
                new QueryDefault<Speaker>(Speaker.class, 
                        "findByGivenOrFamilyName", 
                        "givenOrFamilyName", ".*"+search+".*"));
    }

    
    // //////////////////////////////////////
    // view models
    // //////////////////////////////////////

    @HomePage
    @ActionSemantics(Of.SAFE)
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


    // //////////////////////////////////////
    // Injected
    // //////////////////////////////////////
    
    private BookmarkService bookmarkService;
    
    public final void injectBookmarkService(final BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

}
