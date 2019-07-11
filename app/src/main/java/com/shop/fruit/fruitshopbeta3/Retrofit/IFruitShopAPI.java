package com.shop.fruit.fruitshopbeta3.Retrofit;

import com.shop.fruit.fruitshopbeta3.Modul.Banner;
import com.shop.fruit.fruitshopbeta3.Modul.CheckUserResponse;
import com.shop.fruit.fruitshopbeta3.Modul.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import io.reactivex.*;

public interface IFruitShopAPI {
    // Fruit Shop API
    @FormUrlEncoded
    @POST("checkExistsUserFile.php")
    Call<CheckUserResponse> checkUserExists(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("registerUserFile.php")
    Call<User> registerNewUser(@Field("phone") String phone, @Field("name") String name, @Field("lastname") String lastname, @Field("birthdate") String birthdate, @Field("address") String address, @Field("email") String email);

    @FormUrlEncoded
    @POST("getUser.php")
    Call<User> getInformationUser(@Field("phone") String phone);

    @GET("getBanner.php")
    Observable<List<Banner>> getBanner();

}

// Interface Fruit-Shop
