package com.shop.fruit.fruitshopbeta3.Modul;

public class CheckUserResponse {

    // Variable
    private boolean exists;
    private String error_msg;

    // Construct
    public CheckUserResponse()
    {

    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
// Synchrone file PHP checkuser.php Done!
