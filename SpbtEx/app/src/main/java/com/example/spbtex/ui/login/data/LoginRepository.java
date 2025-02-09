package com.example.spbtex.ui.login.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.spbtex.ui.login.data.model.LoggedInUser;
import com.example.spbtex.sqlite.DbOpenHelper;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    private Context context; //後付け。区別するためコンストラクタでセットしたくない
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> login(String username, String password) {
        // handle login
        Result<LoggedInUser> result = null;
        if(DbOpenHelper.STAND_ALONE){
            //SQLite用
            result = dataSource.login(username, password, context);
        }else{
            //外部DB用
            result = dataSource.login(username, password);
        }

        if (result instanceof Result.Success) {

            // ログイン情報の保持をプリファレンスにて行う
            // AsyncTask, LoginDataSource, LoginRepository, LoginViewModel, LoginActivity
            // 以上５箇所のどこでプリファレンスへの保存を行うのが適切なのか
            // getSharedPreferences()はContextが必要なのでActivityで行いたいがタイミングが悪いのか保存されない
            // このRepositoryにlocal storageの文言があるため、ここでローカルに保存するのが適切かもしれない
            SharedPreferences pref = context.getSharedPreferences("PreferencesEx", MODE_PRIVATE);
            //プリファレンスへの書き込み
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("text_userId", (((Result.Success<LoggedInUser>) result).getData()).getUserId());
            editor.putString("text_displayName", (((Result.Success<LoggedInUser>) result).getData()).getDisplayName());
            editor.putString("text_sessionId", (((Result.Success<LoggedInUser>) result).getData()).getSessionId());
            editor.apply();

            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }
}