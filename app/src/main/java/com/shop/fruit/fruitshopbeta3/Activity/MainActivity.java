package com.shop.fruit.fruitshopbeta3.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.shop.fruit.fruitshopbeta3.Modul.CheckUserResponse;
import com.shop.fruit.fruitshopbeta3.R;
import com.shop.fruit.fruitshopbeta3.Retrofit.IFruitShopAPI;
import com.shop.fruit.fruitshopbeta3.Utils.Common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    /**
     * @static variable
     */
    private static final int REQUEST_CODE = 1000;
    /**
     *
     * @Instance Component object
     */
    Button buttonContinue;
    AlertDialog alertDialog;

    /**
     * @FruitShopService interface
     */
    IFruitShopAPI myServiceFruit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Call Service
        myServiceFruit = Common.getAPI();
        //
        buttonContinue = (Button)findViewById(R.id.button_continue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartFacebookPageAutorization(LoginType.PHONE);
            }
        });
        // DISABLE  KeyHashPrint();
    }

    /**
     * @StartFacebookPageAutorization()
     * @param
     * @return false
     */
    private void StartFacebookPageAutorization(LoginType loginType)
    {
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, builder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * @onActivityResult()
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(result.getError() != null)
            {
                Toast.makeText(this, "" + result.getError().getErrorType().getMessage() + "", Toast.LENGTH_SHORT);
            }
            else if(result.wasCancelled())
            {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT);
            }
            else
            {
                if(result.getAccessToken() != null)
                {
                    alertDialog = new SpotsDialog.Builder().setContext(MainActivity.this).setTheme(R.style.LoadingActivityForFacebook).build();
                    alertDialog.show();
                    alertDialog.setMessage(getString(R.string.ActivityPleaseWaitingInitializableAlert));
                    // Get User and Phone CheckUser.php
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            // User phone current
                            myServiceFruit.checkUserExists(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                            CheckUserResponse checkUserResponse = response.body();
                                            if(checkUserResponse.isExists())
                                            {
                                                // If user already, just new Activity [SK] -> Ak uzivatel existuje, vytvorit novu aktivitu.
                                                alertDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                // Need register. [SK] -> Potrebna registracia.
                                                alertDialog.dismiss();
                                                // Create method
                                                Toast.makeText(MainActivity.this, "Need register!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            // User phone error
                            Log.d("ERROR_FB", accountKitError.getErrorType().getMessage());
                        }
                    });
                }
            }
        }
    }

    /**
     * @KeyhashPrint
     * @info Methoda zakoduje namespace pre dalsie spracovanie.
     */
    private void KeyHashPrint()
    {
        // KeyHash generate for developer.facebook.com/myApp
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo("com.shop.fruit.fruitshopbeta3", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch(PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }
}
