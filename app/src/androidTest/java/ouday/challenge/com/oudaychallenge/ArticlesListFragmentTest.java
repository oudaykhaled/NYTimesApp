package ouday.challenge.com.oudaychallenge;

import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ouday.challenge.com.oudaychallenge.area.main.view.ArticlesAdapter;
import ouday.challenge.com.oudaychallenge.area.main.view.ArticlesListFragment;
import ouday.challenge.com.oudaychallenge.entities.Result;
import ouday.challenge.com.oudaychallenge.network_layer.news_requester.response.NewsResponse;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class ArticlesListFragmentTest {

    ArticlesListFragment articlesListFragment;

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<MainActivity>(MainActivity.class);
    private List<Result> data;

    @Before
    public void setup() {
        articlesListFragment = new ArticlesListFragment();
        intentsTestRule.getActivity().openFragment(articlesListFragment);
        data = new Gson().fromJson(MockRowResponse.data, NewsResponse.class).getResults();
        intentsTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                articlesListFragment.drawList(data);

            }
        });
    }

    @Test
    public void testToolbarTitleText_shouldHaveCorrectText() {
        onView(allOf(withParent(isAssignableFrom(Toolbar.class)), isAssignableFrom(AppCompatTextView.class)))
                .check(matches(withText(R.string.app_name)));
    }


    private static Matcher<View> hasNewsDataForPosition(final int position,
                                                            @NonNull final Result beverage) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Item has news data at position: " + position + " : ");
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                if (null == recyclerView) {
                    return false;
                }

                ArticlesAdapter.ViewHolder viewHolder = (ArticlesAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);

                if (null == viewHolder) {
                    return false;
                }

                return withChild(withText(beverage.getTitle())).matches(viewHolder.txtHeader);
            }
        };
    }

}
