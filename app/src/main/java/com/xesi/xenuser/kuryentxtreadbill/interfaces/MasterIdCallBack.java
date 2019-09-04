package com.xesi.xenuser.kuryentxtreadbill.interfaces;

import com.xesi.xenuser.kuryentxtreadbill.model.message.Message;

/**
 * Created by xenuser on 3/20/2017.
 */
public interface MasterIdCallBack {
    void onSuccess(int id);
    void onSuccess(Message message);
    void onError(String message);
}
