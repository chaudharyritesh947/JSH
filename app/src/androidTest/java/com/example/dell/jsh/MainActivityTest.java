package com.example.dell.jsh;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity>obj = new ActivityTestRule<>(MainActivity.class);

    public MainActivity activity=null;

    @Before
    public void setUp()throws Exception
    {
        activity=obj.getActivity();
    }
    @Test
    public void TestLaunch()throws Exception{
        View v = activity.findViewById(R.id.navigation_view);
        assertNotNull(v);
    }
    @After
    public void tearDown ()throws Exception{
        activity=null;
    }
}