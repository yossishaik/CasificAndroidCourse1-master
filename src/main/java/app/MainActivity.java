package app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import app.Control.DrawerAdapter;
import app.Control.LocalStorageAccess;
import app.Control.MessageHandler;
import app.Control.RoundedTransformation;
import app.Control.Utils;
import app.Fragments.AboutFragment;

public class MainActivity extends AppCompatActivity {

    // Members
    private Activity mActivity;
    Context mContext = null;
    DrawerAdapter mDrawerAdapter = null;
    public DrawerLayout mDrawerLayout = null;
    public RelativeLayout mSideMenuLayout = null;
    ListView mDrawerList = null;
    ActionBarDrawerToggle mDrawerToggle = null;
    ViewPager mViewPager;
    Toolbar mToolbar;
    View header;
    ImageView headerIcon;
    TextView headerTitle;

    // Facebook stuff
    CallbackManager facebookCallbackManager;
    AccessTokenTracker facebookAccessTokenTracker;
    AccessToken facebookAccessToken = null;

    // Static constants
    public static final int ABOUT_INDEX = 0;
    public static final int GALLEY_INDEX = 1;
    public static final int RADIO_INDEX = 2;
    public static final int NAVIGATE_INDEX = 3;
    public static final int BONUS_INDEX = 4;
    public static final int TOTAL_NUMBER_OF_TABS = 5;
    public static final int DEFAULT_TAB_INDEX = ABOUT_INDEX;

    // Buttons holder
    Button[] navigationButtons = new Button[TOTAL_NUMBER_OF_TABS];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Set context
        mContext = getApplicationContext();
        mActivity = this;

        // Set actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title));
        }

        // Init NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSideMenuLayout = (RelativeLayout) findViewById(R.id.side_menu_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerAdapter = new DrawerAdapter(mContext);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new SlideitemListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // enabling action bar app headerIcon and using it as toggle button
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        addHeaderToDrawer();

        // Poweredby button
        ImageView poweredBy = (ImageView) findViewById(R.id.powered_by);
        poweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBrowserWithURL(GlobalConstants.CASIFIC_WEBSITE);
            }
        });

        // Set view pager
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mViewPager.setAdapter(new MainActivityPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mDrawerLayout != null && mDrawerList != null && mDrawerLayout.isDrawerOpen(mSideMenuLayout))
                    mDrawerLayout.closeDrawers();

                navigationButtons[position].callOnClick();

                Utils.hideKeyboard(mActivity);
            }
        });
        // Loads all tabs on startup - this setting is bad for memory
        mViewPager.setOffscreenPageLimit(navigationButtons.length);

        // Set navigation buttons
        navigationButtons[ABOUT_INDEX] = (Button) findViewById(R.id.main_menu_about_button);
        navigationButtons[GALLEY_INDEX] = (Button) findViewById(R.id.main_menu_gallery_button);
        navigationButtons[RADIO_INDEX] = (Button) findViewById(R.id.main_menu_schedule_button);
        navigationButtons[NAVIGATE_INDEX] = (Button) findViewById(R.id.main_menu_navigate_button);
        navigationButtons[BONUS_INDEX] = (Button) findViewById(R.id.main_menu_sales_button);

        for (int i = 0; i < navigationButtons.length; i++) {
            final int final_i = i;
            navigationButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(final_i, true);
                    resetButtonsBackground();
                    setButtonBackground(final_i, true);
                }
            });
        }

        // Set facebook authentication
        LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));
        loginButton.registerCallback(facebookCallbackManager,
                new CustomFacebookCallback<LoginResult>());
        facebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    // Logout
                    LocalStorageAccess.getInstance().processFacebookMeRequestResult(null);
                    fillHeaderData();
                }
            }
        };
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("MainActivity", "facebook already logged in");
            makeFacebookRequsts();
        }

        // Navigate to default tab
        navigationButtons[DEFAULT_TAB_INDEX].callOnClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_contact:
                openDialer();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openDialer() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + GlobalConstants.STUDENTS_PHONE_NUMBER));
            startActivity(intent);
        } catch (ActivityNotFoundException activityNotFound) {
            MessageHandler.getInstance(this).putSimpleInfoMsg(getString(R.string.could_not_dial));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // If side drawer is open - close it
        // Otherwise - perform back as usual
        if (mDrawerLayout != null && mDrawerList != null && mDrawerLayout.isDrawerOpen(mSideMenuLayout))
            mDrawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    // Sets all buttons to inactive mode
    private void resetButtonsBackground() {
        for (int i = 0; i < TOTAL_NUMBER_OF_TABS; i++) {
            setButtonBackground(i, false);
        }
    }

    // Set button background to either active or inactive
    private void setButtonBackground(int buttonIndex, boolean isActive) {
        if (isActive)
            navigationButtons[buttonIndex].setBackgroundResource(R.drawable.navigation_button_background_active);
        else
            navigationButtons[buttonIndex].setBackgroundResource(R.drawable.navigation_button_background_inactive);
    }

    class SlideitemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            // Handle press
            switch (position) {
                // Header
                case 0:
                    break;
                // Facebook
                case 1:
                    startBrowserWithURL(GlobalConstants.FACEBOOK_LINK);
                    break;
                // LinkedIn
                case 2:
                    startBrowserWithURL(GlobalConstants.EMAIL_LINK);
                    break;
                case 3:
                    startBrowserWithURL(GlobalConstants.LINKEDIN_LINK);
                    break;
                // CV
                case 4:
                    startBrowserWithURL(GlobalConstants.CV_LINK);
                    break;
                //ebay
                case 5:
                    startBrowserWithURL(GlobalConstants.EBAY_LINK);
                    break;
                case 6:
                    startBrowserWithURL(GlobalConstants.YOUTUBE_LINK);
                    break;


                default:

            }

            // Close Drawer
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawers();
            }
        }
    }

    // Navigate to url using default browser
    private void startBrowserWithURL(String stringURL) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringURL));
        startActivity(browserIntent);
    }

    /**
     * When using the ActionBarDrawerToggle, must be called during
     * onPostCreate() and onConfigurationChanged()
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // Add login header to side menu top
    public void addHeaderToDrawer() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        header = inflater.inflate(R.layout.drawer_header, null, false);
        headerIcon = (ImageView) header.findViewById(R.id.icon);
        headerTitle = (TextView) header.findViewById(R.id.title);
        fillHeaderData(); // Fill in guest data

        mDrawerList.addHeaderView(header);
    }

    // Fill login header with data
    private void fillHeaderData() {
        if (!LocalStorageAccess.getInstance().isUserDataLoaded()) {
            headerTitle.setText(getString(R.string.guest));
            headerIcon.setImageResource(R.drawable.guest);
        } else {
            headerTitle.setText(LocalStorageAccess.getInstance().getUserName());
            Picasso.with(getApplicationContext()).load(LocalStorageAccess.getInstance().getUserImageURL()).transform(new RoundedTransformation(200, 0)).placeholder(R.drawable.guest).into(headerIcon);
        }
        header.invalidate();
    }

    // Main view pager adapter
    private class MainActivityPagerAdapter extends FragmentStatePagerAdapter {

        public MainActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            Fragment returnFragment = null;

            switch (i) {
                case ABOUT_INDEX:
                    returnFragment = AboutFragment.newInstance();
                    break;
                case GALLEY_INDEX:
                    //returnFragment = GalleryFragment.newInstance();
                    returnFragment = AboutFragment.newInstance();
                    break;
                case RADIO_INDEX:
                    //returnFragment = RadioFragment.newInstance();
                    returnFragment = AboutFragment.newInstance();
                    break;
                case NAVIGATE_INDEX:
                    //returnFragment = NavigateFragment.newInstance();
                    returnFragment = AboutFragment.newInstance();
                    break;
                case BONUS_INDEX:
                    //returnFragment = BonusFragment.newInstance();
                    returnFragment = AboutFragment.newInstance();
                    break;
            }

            return returnFragment;
        }

        @Override
        public int getCount() {
            return TOTAL_NUMBER_OF_TABS;
        }

    }


    /**
     * Facebook service methods
     */

    private class CustomFacebookCallback<LoginResult> implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i("MainActivity", "Facebook login result - onSuccess: " + loginResult.toString());
            makeFacebookRequsts();
        }

        @Override
        public void onCancel() {
            Log.i("MainActivity", "Facebook login result - onCancel");
        }

        @Override
        public void onError(FacebookException exception) {
            try {
                Log.i("MainActivity", "Facebook login result - onError: " + exception.getCause().toString());
            }catch (Exception e){

            }
        }
    }

    private void makeFacebookRequsts() {
        if (AccessToken.getCurrentAccessToken() != null) {
            facebookAccessToken = AccessToken.getCurrentAccessToken();
            facebookAccessTokenTracker.startTracking();
            makeMeRequest();
            makeFriendsRequest();
        }
    }

    private void makeMeRequest() {

        if (facebookAccessToken != null) {

            GraphRequest meRequest = GraphRequest.newMeRequest(
                    facebookAccessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.v("MainActivity", "Facebook meRequest successful");
                            if (object != null) {
                                LocalStorageAccess.getInstance().processFacebookMeRequestResult(object.toString());
                                fillHeaderData();
                            }
                        }
                    });
            Bundle meRequestParameters = new Bundle();
            meRequestParameters.putString("fields", "id,name,email,gender,age_range,friends, picture");
            meRequest.setParameters(meRequestParameters);
            meRequest.executeAsync();
        }
    }

    private void makeFriendsRequest() {

        if (facebookAccessToken != null) {

            GraphRequest friendsRequest = GraphRequest.newMyFriendsRequest(
                    facebookAccessToken,
                    new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(
                                JSONArray object,
                                GraphResponse response) {
                            // Application code
                            Log.v("MainActivity", "Facebook friendsRequest");
                        }
                    });
            Bundle friendsRequestParameters = new Bundle();
            friendsRequestParameters.putString("fields", "id,name, picture");
            friendsRequest.setParameters(friendsRequestParameters);
            friendsRequest.executeAsync();
        }
    }
}
