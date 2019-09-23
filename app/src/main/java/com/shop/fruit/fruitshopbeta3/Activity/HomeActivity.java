package com.shop.fruit.fruitshopbeta3.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.shop.fruit.fruitshopbeta3.Adapter.CategoryAdapter;
import com.shop.fruit.fruitshopbeta3.Modul.Banner;
import com.shop.fruit.fruitshopbeta3.Modul.Category;
import com.shop.fruit.fruitshopbeta3.R;
import com.shop.fruit.fruitshopbeta3.Retrofit.IFruitShopAPI;
import com.shop.fruit.fruitshopbeta3.Utils.Common;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Initializable components
    TextView objTxtName, objTxtPhone;
    SliderLayout sliderLayoutHome;

    IFruitShopAPI myFruitShop;
    // Rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    // Adapter Category
    RecyclerView recyclerView_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FruitShopAPI
        myFruitShop = Common.getAPI();
        // Slider object
        sliderLayoutHome = (SliderLayout)findViewById(R.id.homeSlider);
        //
        recyclerView_menu = (RecyclerView)findViewById(R.id.top_products_views);
        recyclerView_menu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_menu.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Views
        View headerView = navigationView.getHeaderView(0);
        //
        objTxtName = (TextView)headerView.findViewById(R.id.header_txt_name);
        objTxtPhone = (TextView)headerView.findViewById(R.id.header_txt_phone);
        // Set text
        objTxtName.setText(Common.currentUser.getName());
        objTxtPhone.setText(Common.currentUser.getPhone());
        //
        // Get Banner starting
        getBannerImageView();
        //
        // Get menu starting
        getMenu();
    }

    /**
     * @getBannerImageView()
     * info: <SK>metoda ktora vola z API tretich stran<SK/>
     */
    private void getBannerImageView()
    {
        compositeDisposable.add(myFruitShop.getBanner()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Banner>>() {
            @Override
            public void accept(List<Banner> banners) throws Exception {
                displayImageView(banners);
            }
        })
        );
    }

    /**
     * getMenu
     */
    private void getMenu()
    {
        compositeDisposable.add(myFruitShop.getMenu()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Category>>() {
            @Override
            public void accept(List<Category> categories) throws Exception {
                displayMenuView(categories);
            }
        })
        );
    }

    /**
     * @displayMenuView(param)
     * @param categories
     */
    private void displayMenuView(List<Category> categories) {
        //
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categories);
        recyclerView_menu.setAdapter(categoryAdapter);
    }

    // Call destroy
    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    /**
     *
     * @displayImageView
     * info: function image view presenter
     */
    private void displayImageView(List<Banner> banners)
    {
        HashMap<String, String> bannerMaps = new HashMap<>();
        //
        for(Banner item:banners)
        {
            bannerMaps.put(item.getName_(), item.getLink());
        }
        //
        for(String name:bannerMaps.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.description(name)
                    .image(bannerMaps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            sliderLayoutHome.addSlider(textSliderView);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }  else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
