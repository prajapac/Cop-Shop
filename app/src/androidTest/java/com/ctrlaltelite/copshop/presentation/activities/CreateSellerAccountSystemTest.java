package com.ctrlaltelite.copshop.presentation.activities;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.ctrlaltelite.copshop.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateSellerAccountSystemTest {

    @Rule
    public ActivityTestRule<ListingListActivity> mActivityTestRule = new ActivityTestRule<>(ListingListActivity.class);

    @Test
    public void createSellerAccountSystemTest() {

        String newEmail = createSellerAccount();

        // We are on listing list page
        ViewInteraction drawerButton = onView(withContentDescription("Open navigation drawer"));
        drawerButton.perform(click());

        // Click on the first drawer item (Account Details)
        ViewInteraction button = onView(
                allOf(SystemTestUtils.childAtPosition(allOf(withId(R.id.design_navigation_view),
                        SystemTestUtils.childAtPosition(withId(R.id.nav_view), 0)), 1),
                        isDisplayed()));
        button.perform(click());

        onView(withId(R.id.editTextOrganizationName)).check(matches(withText("The Continental")));
        onView(withId(R.id.editTextEmail)).check(matches(withText(newEmail)));
    }

    public static String createSellerAccount() {
        // Generate an email address
        Random rand = new Random();
        String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        char char1 = candidateChars.charAt(rand.nextInt(candidateChars.length()));
        char char2 = candidateChars.charAt(rand.nextInt(candidateChars.length()));
        char char3 = candidateChars.charAt(rand.nextInt(candidateChars.length()));
        String newEmail = char1 + char2 + char3 + "@email.com";

        // Log out
        SystemTestUtils.logout();

        // Create Account
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.register_seller_btn)).perform(click());

        onView(withId(R.id.editTextOrganizationName)).perform(replaceText("The Continental"), closeSoftKeyboard());
        onView(withId(R.id.editTextStreetAddress)).perform(replaceText("Portage and Main"), closeSoftKeyboard());
        onView(withId(R.id.editTextPostalCode)).perform(replaceText("A1A 1A1"), closeSoftKeyboard());
        onView(withId(R.id.editTextProvince)).perform(replaceText("MB"), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail)).perform(replaceText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(replaceText("12345"), closeSoftKeyboard());

        onView(withId(R.id.btnCreateSellerAccount)).perform(scrollTo()).perform(click());

        SystemTestUtils.loginAsSeller(newEmail, "12345");

        return newEmail;
    }

}
