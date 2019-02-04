package com.shop.fruit.fruitshopbeta3.Utils;

import com.shop.fruit.fruitshopbeta3.Retrofit.IFruitShopAPI;
import com.shop.fruit.fruitshopbeta3.Retrofit.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    //
    private static final String BASE_URL = "http://www.android.dev-droid.sk/android/";

    public static IFruitShopAPI getAPI()
    {
        return RetrofitClient.getRetrofit(BASE_URL).create(IFruitShopAPI.class);
    }
}
