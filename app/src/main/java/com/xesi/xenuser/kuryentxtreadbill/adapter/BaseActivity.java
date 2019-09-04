package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.xesi.xenuser.kuryentxtreadbill.Homepage;
import com.xesi.xenuser.kuryentxtreadbill.R;

/**
 * Created by xenuser on 3/27/2017.
 */
public class BaseActivity extends Activity {

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */

    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_DEL:
                return true;
//            case KeyEvent.KEYCODE_BACK:
//                String appName = this.getClass().getSimpleName();
//                if (!appName.equals("Homepage") && !appName.equals("Logout") && !appName.equals("IPServerSetting") && !appName.equals("Authenticate") && !appName.equals("PasswordCreation")) {
//                    Log.d("onBackPressed", "OnBackPressed " + appName);
//                    Intent i = new Intent(getApplicationContext(), Homepage.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                    finish();
//                    return true;
//                }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}