package EspressoTests;

import android.os.Build;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.main.donation.CheckPurchaseActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static EspressoTests.CustomMatchers.withDrawable;
import static EspressoTests.CustomMatchers.withIndex;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.release;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withParentIndex;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UiTests {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    private void openMenu() {
        onView(withId(R.id.drawer)).perform(open());
    }

    @Test
    public void openTrending() {
        openMenu();
        onView(withId(R.id.mainNav)).perform(navigateTo(R.id.trending));
        onView(withId(R.id.trendingFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void changeTheme() {
        openMenu();
        onView(withId(R.id.mainNav)).perform(navigateTo(R.id.settings));
        onData(anything()).inAdapterView(withId(R.id.settingsList)).atPosition(0).perform(click());
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withIndex(withId(R.id.apply), 1)).perform(click());
        assertEquals(ViewHelper.getPrimaryColor(App.getInstance().getBaseContext()), -3355444);
    }

    @Test
    public void restorePurchases() {
        openMenu();
        Intents.init();
        onView(withId(R.id.mainNav)).perform(navigateTo(R.id.restorePurchase));
        intended(hasComponent(CheckPurchaseActivity.class.getName()));
        release();
    }

    @Test
    public void checkToast() {
        openMenu();
        onView((withText(R.string.send_feedback))).perform(click());
        pressBack();
        onView(instanceOf(android.widget.EditText.class))
                .perform(ViewActions.click())
                .perform(ViewActions.typeText("hello"));
        onView((withId(R.id.description))).perform(click());
        onView(instanceOf(android.widget.EditText.class))
                .check(matches(withText(containsString(Build.MODEL))));
        onView((withId(R.id.submit))).perform(click());
        onView((withId(R.id.submit))).perform(click());
        onView(withText("Message was sent"))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkMarkup() {
        openMenu();
        onView(withId(R.id.mainNav)).perform(navigateTo(R.id.about));
        onView(allOf(
                withParent(withParent(withParent(withChild(withText("About"))))),
                withParent(withParentIndex(1)),
                withChild(withText("Changelog")))
        ).check(matches(isDisplayed()));
        onView(withDrawable(R.drawable.ic_track_changes)).check(matches(isDisplayed()));
    }

}

