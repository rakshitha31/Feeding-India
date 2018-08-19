package com.example.navada.feedingindia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.navada.feedingindia.R;

public class HomeFragment extends Fragment {

    private ActionBar toolbar;
    private BottomNavigationView mBottomNavigationView;
    private SharedPreferences mSharedPreferences;
    private Handler mHandler;
    private static final int MENU_ITEM_ID_ONE =1;
    private static final int MENU_ITEM_ID_TWO =2;
    private static final int MENU_ITEM_ID_THREE =3;
    private static final int MENU_ITEM_ID_FOUR =4;
    private static String TAG_DONATE = "Donate";
    private static String TAG_FEED = "Feed";
    private static String TAG_FORM = "Form";
    private static String TAG_VALIDATE = "Validate";
    private static String CURRENT_TAG = TAG_DONATE;
    private static int navItemId = 1;
    private String [] mBottomNavFragmentTitles;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mBottomNavFragmentTitles = getResources().getStringArray(R.array.bottom_nav_fragment_titles);
        mHandler = new Handler();
        mSharedPreferences = this.getActivity().getSharedPreferences("com.example.navada.feedingindia", Context.MODE_PRIVATE);

        navItemId = 1;
        CURRENT_TAG = TAG_DONATE;

        if(mSharedPreferences.getBoolean("clear",false))
            mSharedPreferences.edit().remove("clear").apply();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBottomNavigationView = view.findViewById(R.id.bottom_nav_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBottomNavBar();

        if(savedInstanceState == null)
            loadFragment(new DonateFragment());
    }


    @Override
    public void onStart() {
        super.onStart();

        if(mSharedPreferences.getBoolean("clear",false)) {
            getActivity().getSupportFragmentManager().popBackStack();
            mSharedPreferences.edit().remove("clear").apply();
            setBottomNavBar();
            navItemId = 1;
            CURRENT_TAG = TAG_DONATE;
            loadFragment(new DonateFragment());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId()){

                    case MENU_ITEM_ID_ONE:
                        CURRENT_TAG = TAG_DONATE;
                        navItemId = 1;
                        loadFragment(new DonateFragment());
                        break;

                    case MENU_ITEM_ID_TWO:
                        CURRENT_TAG = TAG_FEED;
                        navItemId = 2;
                        loadFragment(new FeedFragment());
                        break;

                    case MENU_ITEM_ID_THREE:
                        CURRENT_TAG = TAG_FORM;
                        navItemId = 3;
                        loadFragment(new FormFragment());
                        break;

                    case MENU_ITEM_ID_FOUR:
                        CURRENT_TAG = TAG_VALIDATE;
                        navItemId = 4;
                        loadFragment(new ValidateFragment());
                        break;

                    default:
                        CURRENT_TAG = TAG_DONATE;
                        navItemId = 1;
                        loadFragment(new DonateFragment());

                }

                return false;
            }
        });

    }

    private void setBottomNavBar(){
        
        String userType =  mSharedPreferences.getString("userType","normal");

        Menu mBottomNavMenu = mBottomNavigationView.getMenu();
        mBottomNavMenu.clear();

        switch(userType){

            case "normal":
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_ONE,Menu.NONE,"Donate").setIcon(R.drawable.ic_donate);
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_TWO,Menu.NONE,"Feed").setIcon(R.drawable.ic_feed);
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_THREE,Menu.NONE,"Form").setIcon(R.drawable.ic_form);
                break;

            case "hungerhero":
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_ONE,Menu.NONE,"Donate").setIcon(R.drawable.ic_donate);
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_TWO,Menu.NONE,"Feed").setIcon(R.drawable.ic_feed);
                break;

            case "admin":
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_ONE,Menu.NONE,"Donate").setIcon(R.drawable.ic_donate);
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_TWO,Menu.NONE,"Feed").setIcon(R.drawable.ic_feed);
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_FOUR,Menu.NONE,"Validate").setIcon(R.drawable.ic_validate);
                break;

            default:
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_ONE,Menu.NONE,"Donate").setIcon(R.drawable.ic_donate);
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_TWO,Menu.NONE,"Feed").setIcon(R.drawable.ic_feed);
                mBottomNavMenu.add(Menu.NONE,MENU_ITEM_ID_THREE,Menu.NONE,"Form").setIcon(R.drawable.ic_form);

        }

    }

    private void loadFragment(final Fragment fragment) {


        if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>0)
            getActivity().getSupportFragmentManager().popBackStack();

        setToolBarTitle();
        selNavMenuItem();

        Runnable mPendingRunnable = new Runnable() {

            public void run() {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                transaction.replace(R.id.frame_container, fragment ,CURRENT_TAG);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };

        mHandler.post(mPendingRunnable);
    }

    private void setToolBarTitle(){
        toolbar.setTitle(mBottomNavFragmentTitles[navItemId-1]);
    }

    private void selNavMenuItem(){
        mBottomNavigationView.getMenu().findItem(navItemId).setChecked(true);
    }

}
