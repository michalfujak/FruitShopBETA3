package com.shop.fruit.fruitshopbeta3.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shop.fruit.fruitshopbeta3.Modul.CheckUserResponse;
import com.shop.fruit.fruitshopbeta3.Modul.User;
import com.shop.fruit.fruitshopbeta3.R;
import com.shop.fruit.fruitshopbeta3.Retrofit.IFruitShopAPI;
import com.shop.fruit.fruitshopbeta3.Utils.Common;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

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
    Button buttonRegister;
    AlertDialog alertDialog;                                        // android.app.AlertDialog
    AlertDialog alertDialogRegisterWaiting;                         // android.app.AlertDialog
    android.support.v7.app.AlertDialog.Builder alertV7Builder;       // android.support.v7.app.AlertDialog.Builder
    MaterialEditText materialName;
    MaterialEditText materialAddress;
    MaterialEditText materialDateTime;
    MaterialEditText materialLastname;
    MaterialEditText materialEmail;

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
        // AutoLogin Facebook still :)
        if(AccountKit.getCurrentAccessToken() != null)
        {
            // SESSION Starting...
            // AutoLogin Session still
            // AlertDialog.Spots V7 < Warning not AlertDialog.V4 >
            alertDialog = new SpotsDialog.Builder().setContext(MainActivity.this).setTheme(R.style.LoadingActivityForFacebook).build();
            alertDialog.show();
            alertDialog.setMessage(getString(R.string.ActivityPleaseWaitingInitializableAlert));
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    // User phone current
                    myServiceFruit.checkUserExists(account.getPhoneNumber().toString())
                            .enqueue(new Callback<CheckUserResponse>() {
                                @Override
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse checkUserResponse = response.body();
                                    if(checkUserResponse.isExists())
                                    {
                                        // Fetch information User
                                        myServiceFruit.getInformationUser(account.getPhoneNumber().toString())
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        // If user already, just new Activity [SK] -> Ak uzivatel existuje, vytvorit novu aktivitu.
                                                        alertDialog.dismiss();
                                                        // User < not NULL >
                                                        Common.currentUser = response.body(); // fixed
                                                        Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                                                        // Login complete!
                                                        // Login complete < Blocked && integration source code >
                                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        StyleableToast.makeText(MainActivity.this, t.getMessage(), R.style.ToastRegisterErrors).show();
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        // Need register. [SK] -> Potrebna registracia.
                                        alertDialog.dismiss();
                                        // StyleableToast.makeText(MainActivity.this, "Register" , R.style.ToastRegisterInformations).show();
                                        // Create method
                                        showRegisterDialogUser(account.getPhoneNumber().toString());
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
                Toast.makeText(this, "" + result.getError().getErrorType().getMessage() + "", Toast.LENGTH_SHORT).show();
            }
            else if(result.wasCancelled())
            {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
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
                        public void onSuccess(final Account account) {
                            // User phone current
                            myServiceFruit.checkUserExists(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                            CheckUserResponse checkUserResponse = response.body();
                                            if(checkUserResponse.isExists())
                                            {
                                                // Fetch information User
                                                myServiceFruit.getInformationUser(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {
                                                                // If user already, just new Activity [SK] -> Ak uzivatel existuje, vytvorit novu aktivitu.
                                                                alertDialog.dismiss();
                                                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                                                                // Login complete!
                                                                // Login complete < Blocked && integration source code >
                                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                StyleableToast.makeText(MainActivity.this, t.getMessage(), R.style.ToastRegisterErrors).show();
                                                            }
                                                        });
                                            }
                                            else
                                            {
                                                // Need register. [SK] -> Potrebna registracia.
                                                alertDialog.dismiss();
                                                // StyleableToast.makeText(MainActivity.this, "Register" , R.style.ToastRegisterInformations).show();
                                                // Create method
                                                showRegisterDialogUser(account.getPhoneNumber().toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                            // Doprogramovat error message
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
     * @showRegisterDialogUser(phone)
     * @param +phone
     * @return false
     */
    private void showRegisterDialogUser(final String phone)
    {
        // Parameter phone prenasame k registracii k spracovaniu.
        alertV7Builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.register_card_view);
        alertV7Builder.setTitle(getString(R.string.titleRegisterAlertDialog));

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View registerAlertBuilder = layoutInflater.inflate(R.layout.register_dialog, null);
        //
        materialName = (MaterialEditText)registerAlertBuilder.findViewById(R.id.register_name);
        materialAddress = (MaterialEditText)registerAlertBuilder.findViewById(R.id.register_address);
        materialDateTime = (MaterialEditText)registerAlertBuilder.findViewById(R.id.register_datetime);
        materialLastname = (MaterialEditText)registerAlertBuilder.findViewById(R.id.register_name_last);
        materialEmail = (MaterialEditText)registerAlertBuilder.findViewById(R.id.register_email);
        // Continue

        buttonRegister = (Button)registerAlertBuilder.findViewById(R.id.button_register_continue);
        materialDateTime.addTextChangedListener(new PatternedTextWatcher("####-##-##"));
        // nastavenie View.Card
        alertV7Builder.setView(registerAlertBuilder);
        //
        // Initializable create null dialog
        // Preniest do noveho objektu pre pripad zavretie view.card po stlaceni registracie.
        final android.support.v7.app.AlertDialog dialog = alertV7Builder.create();
        // Button Register Listener
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                // dialog.dissmiss...
                dialog.dismiss();
                if(TextUtils.isEmpty(materialName.getText().toString()))
                {
                    StyleableToast.makeText(MainActivity.this, getString(R.string.registerEmptyNameToast), R.style.ToastRegisterErrors).show();
                    return;
                }
                if(TextUtils.isEmpty(materialAddress.getText().toString()))
                {
                    StyleableToast.makeText(MainActivity.this, getString(R.string.registerEmptyAddressToast), R.style.ToastRegisterErrors).show();
                    return;
                }
                if(TextUtils.isEmpty(materialDateTime.getText().toString()))
                {
                    StyleableToast.makeText(MainActivity.this, getString(R.string.registerEmptyDateTimeToast), R.style.ToastRegisterErrors).show();
                    return;
                }
                if(TextUtils.isEmpty(materialLastname.getText().toString()))
                {
                    StyleableToast.makeText(MainActivity.this, getString(R.string.registerEmptyPriezviskoToast), R.style.ToastRegisterErrors).show();
                    return;
                }
                if(TextUtils.isEmpty(materialEmail.getText().toString()))
                {
                    StyleableToast.makeText(MainActivity.this, getString(R.string.registerEmailCharsEmail), R.style.ToastRegisterErrors).show();
                    return;
                }
                alertDialogRegisterWaiting = new SpotsDialog.Builder().setContext(MainActivity.this).setTheme(R.style.LoadingActivityRegister).build();
                alertDialogRegisterWaiting.show();
                alertDialogRegisterWaiting.setMessage(getString(R.string.ActivityPleaseWaitingInitializableRegister));
                // Active Service
                myServiceFruit.registerNewUser(phone,
                                               materialName.getText().toString(),
                                               materialLastname.getText().toString(),
                                               materialDateTime.getText().toString(),
                                               materialAddress.getText().toString(),
                                               materialEmail.getText().toString()
                                               )
                                               .enqueue(new Callback<User>() {
                                                   @Override
                                                   public void onResponse(Call<User> call, Response<User> response) {
                                                       alertDialogRegisterWaiting.dismiss();
                                                       User userRegister = response.body();
                                                       if(TextUtils.isEmpty(userRegister.getError_msg()))
                                                       {
                                                           StyleableToast.makeText(MainActivity.this, getString(R.string.registerToastableUserRegisterDone) , R.style.ToastRegisterDone).show();
                                                           // < Blocked register done! && Integration source code >
                                                           Common.currentUser = response.body();
                                                           // SK, naplnenie objektu <user.class> pre spracovanie JSON body...
                                                           // registration complete!
                                                           startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                           finish();
                                                       }
                                                   }

                                                   @Override
                                                   public void onFailure(Call<User> call, Throwable t) {
                                                       alertDialogRegisterWaiting.dismiss();
                                                   }
                                               });
            }
        });

        // Starting set View
        // Zobrazenie celeho view.card aktivity.
        dialog.show();
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
