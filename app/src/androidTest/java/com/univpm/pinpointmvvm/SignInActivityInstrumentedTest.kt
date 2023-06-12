package com.univpm.pinpointmvvm

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.univpm.pinpointmvvm.view.activities.SignInActivity
import com.univpm.pinpointmvvm.viewmodel.AccountSettingsViewModel
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SignInActivityInstrumentedTest {

    private lateinit var email1 : String
    private lateinit var password1 : String
    private lateinit var email2 : String
    private lateinit var password2 : String
    private lateinit var viewmodel : AccountSettingsViewModel

    @get:Rule
    var activityRule: ActivityScenarioRule<SignInActivity>
            = ActivityScenarioRule(SignInActivity::class.java)

    @Before
    fun setUp() {
        // Inizializza i dati
        email1 = "prova@prova.com"
        password1 = "prova123"
        email2 = "asd@asd.com"
        password2 = "asd12345"
        viewmodel = AccountSettingsViewModel()
    }

    @Test
    fun test01LoginSuccess() {
        onView(withId(R.id.email_login_edit_text))
            .perform(typeText(email1), closeSoftKeyboard())
        onView(withId(R.id.password_login_edit_text))
            .perform(typeText(password1), closeSoftKeyboard())
        onView(withId(R.id.in_login_button)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.layout_home)).check(matches(isDisplayed()))
        viewmodel.logOut()
    }

    @Test
    fun test02LoginFail() {
        onView(withId(R.id.email_login_edit_text))
            .perform(typeText(email2), closeSoftKeyboard())
        onView(withId(R.id.password_login_edit_text))
            .perform(typeText(password2), closeSoftKeyboard())
        onView(withId(R.id.in_login_button)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.layout_login)).check(matches(isDisplayed()))
    }

}
