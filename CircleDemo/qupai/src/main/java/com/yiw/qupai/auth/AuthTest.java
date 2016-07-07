package com.yiw.qupai.auth;

import android.content.Context;
import android.util.Log;

import com.duanqu.qupai.auth.AuthService;
import com.duanqu.qupai.auth.QupaiAuthListener;
import com.yiw.qupai.common.Contant;
import com.yiw.qupai.utils.AppSharePreferences;

import java.util.UUID;


public class AuthTest {

    private static final String TAG = AuthTest.class.getSimpleName();
    private static AuthTest instance;

    public static AuthTest getInstance() {
        if (instance == null) {
            instance = new AuthTest();
        }
        return instance;
    }

    private static final String AUTHTAG = "QupaiAuth";

    /**
     * 鉴权 建议只调用一次,在demo里面为了测试调用了多次 得到accessToken，通常一个用户对应一个token
     * @param context
     * @param appKey    appkey
     * @param appsecret appsecret
     * @param
     */
    public void initAuth(final Context context , String appKey, String appsecret){
        Log.e("Live","accessToken" + Contant.accessToken);

        final String space = UUID.randomUUID().toString().replace("-","");
        AuthService service = AuthService.getInstance();
        service.setQupaiAuthListener(new QupaiAuthListener() {
            @Override
            public void onAuthError(int errorCode, String message) {
                Log.e(TAG, "ErrorCode" + errorCode + "message" + message);

            }

            @Override
            public void onAuthComplte(int responseCode, String responseMessage) {
                Log.e(TAG, "onAuthComplte: " + responseCode + " &message: " + responseMessage);

                Contant.accessToken = responseMessage;
                Contant.space = space;
                AppSharePreferences.getInstance(context).set(Contant.SP_ACCESSTOKEN, responseMessage);
                AppSharePreferences.getInstance(context).set(Contant.SP_SPACE, space);
            }
        });
        service.startAuth(context,appKey, appsecret, space);
    }

}
