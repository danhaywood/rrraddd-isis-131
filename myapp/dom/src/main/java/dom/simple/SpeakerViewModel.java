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
        return container.allMatches(ConferenceSession.class,
                new Predicate<ConferenceSession>() {
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
        return Lists.newArrayList(Iterables.concat(Iterables.transform(
                getPresenting(), new Function<ConferenceSession, Set<Tag>>() {
                    @Override
                    public Set<Tag> apply(ConferenceSession input) {
                        return input.getTags();
                    }
                })));
    }

    // //////////////////////////////////////
    // Injected
    // //////////////////////////////////////

    private DomainObjectContainer container;

    public final void injectDomainObjectContainer(
            final DomainObjectContainer container) {
        this.container = container;
    }

    private BookmarkService bookmarkService;

    public final void injectBookmarkService(
            final BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }
}
