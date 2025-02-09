package com.example.spbtex.ui.login.data;

import android.content.Context;
import com.example.spbtex.MyAsyncTask;
import com.example.spbtex.Urls;
import com.example.spbtex.ui.login.data.model.LoggedInUser;
import com.example.spbtex.sqlite.MemberModel;
import com.example.spbtex.ui.member.Member;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    //外部DB用
    public Result<LoggedInUser> login(String username, String password) {
        try {
            MyAsyncTask task = new MyAsyncTask(new Member(username, password));
            task.setUrl(Urls.URL_LOGIN_POST);
            task.setMethod(MyAsyncTask.POST);
            task.setaClass(LoggedInUser.class);

            task.execute();
            LoggedInUser loggedInUser = (LoggedInUser) task.getReceiveObject();

            if(loggedInUser == null) {
                throw new Exception("loggedInUser:null");
            } else if ( loggedInUser.getUserId().equals("-1") ) {
                throw new Exception("loggedInUser:error");
            } else{
                System.out.println("loggedInUser:OK");
            }
            return new Result.Success<>(loggedInUser);
                    // TODO: handle loggedInUser authentication
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }


    //SQLite用
    public Result<LoggedInUser> login(String username, String password, Context context) {

        LoggedInUser loggedInUser = new MemberModel(context).searchData(username, password);
        try{
            if(loggedInUser == null){
                throw new Exception("loggedInUser:null");
            }else{
                loggedInUser.setSessionId("dummySession");
                return new Result.Success<>(loggedInUser);
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }

    }


}