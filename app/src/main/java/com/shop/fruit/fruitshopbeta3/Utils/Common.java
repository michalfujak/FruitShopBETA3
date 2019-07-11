package com.shop.fruit.fruitshopbeta3.Utils;

import com.shop.fruit.fruitshopbeta3.Modul.User;
import com.shop.fruit.fruitshopbeta3.Retrofit.IFruitShopAPI;
import com.shop.fruit.fruitshopbeta3.Retrofit.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    //
    private static final String BASE_URL = "http://fruit-shop.dev-droid.sk/android/";

    // User.class
    public static User currentUser = null;

    public static IFruitShopAPI getAPI()
    {
        return RetrofitClient.getRetrofit(BASE_URL).create(IFruitShopAPI.class);
    }
}
