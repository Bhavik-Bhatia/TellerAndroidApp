package com.example.teller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.teller.Fragments.LibraryFragment;
import com.example.teller.Fragments.NotificationFragment;
import com.example.teller.Fragments.ProfileFragment;
import com.example.teller.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomePage extends AppCompatActivity implements Custom_dialog_library.Custom_Dialog_library_Listener {

    BottomNavigationView bottomAppBarview;
    Fragment sel_fragment = null;
    FloatingActionButton fab;
    MenuItem hid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_home_page);



        //Bottom Nav bar customization
        bottomAppBarview = findViewById(R.id.navview);
        bottomAppBarview.setItemBackground(null);
        bottomAppBarview.getMenu().getItem(2).setEnabled(false);
        bottomAppBarview.getMenu().getItem(2).setVisible(false);
        bottomAppBarview.getMenu().getItem(4).setChecked(true);

        //SETTING CURRENT FRAGMENT

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new ProfileFragment()).commit();


        bottomAppBarview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.account:
                        sel_fragment = new ProfileFragment();
                        fab.hide();

                        break;

                    case R.id.lib_page:
                        sel_fragment = new LibraryFragment();
                        fab.show();

                        break;

                    case R.id.searchpage:
                        sel_fragment = new SearchFragment();
                        fab.hide();

                        break;

                    case R.id.notification:
                        sel_fragment = new NotificationFragment();
                        fab.hide();

                        break;

                }
                if (sel_fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,sel_fragment).commit();
                }

                return true;
            }
        });


        //While user clicks on Floating action button to open the dialog box
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                open_library_dailog();
            }
        });
    }

    //When you CLICK ON FLOATING BTN
    private void open_library_dailog() {
        Custom_dialog_library custom_dialog_library = new Custom_dialog_library();
        custom_dialog_library.show(getSupportFragmentManager(), "Enter Library Name");
    }


    @Override
    public void onBackPressed() {
        // make sure you have this outcommented
        // super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void applyText(String input_lib_name) {

        LibraryFragment libraryFragment = new LibraryFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Bundle b = new Bundle();
        b.putString("input_lib_name_key",input_lib_name);

        libraryFragment.setArguments(b);
        fragmentTransaction.replace(R.id.framelayout,libraryFragment).commit();

    }
}