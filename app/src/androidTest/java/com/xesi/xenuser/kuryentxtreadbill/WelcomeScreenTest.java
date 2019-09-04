package com.xesi.xenuser.kuryentxtreadbill;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WelcomeScreenTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void welcomeScreenTest() {
    }

}
