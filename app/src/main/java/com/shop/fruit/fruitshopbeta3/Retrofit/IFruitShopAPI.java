package com.shop.fruit.fruitshopbeta3.Retrofit;

import com.shop.fruit.fruitshopbeta3.Modul.CheckUserResponse;
import com.shop.fruit.fruitshopbeta3.Modul.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IFruitShopAPI {
    // Fruit Shop API
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkUserExists(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register.php")
    Call<User> registerNewUser(@Field("phone") String phone, @Field("name") String name, @Field("birthdate") String birthdate, @Field("address") String address);
}

// Interface Fruit-Shop
