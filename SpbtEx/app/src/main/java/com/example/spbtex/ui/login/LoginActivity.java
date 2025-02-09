package com.example.spbtex.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.spbtex.MainActivity;
import com.example.spbtex.R;
import com.example.spbtex.databinding.ActivityLoginBinding;
import com.example.spbtex.ui.register.MailAddressActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        loginViewModel.setContext(this); //後付け。ログイン情報保持用プリファレンスにリポジトリで書き込むため、
                                        // contextをこのActivity→ViewModel→Repositoryで渡す

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                //テキストが入力完了したらボタンが活性化
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        //ログイン処理が終わったらプログレスバーを終了させる
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                    return;
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                //Complete and destroy login activity once successful
                //finish();
                setResult(Activity.RESULT_OK);
                //遷移
                loginFinish(getCurrentFocus());
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        /*
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        //キーボード（のエンター）入力でもログイン処理ができる機能
        //ちゃんとボタン押下でログイン→画面遷移したいので無効化
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });
         */

        //ログインボタンが押下された
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //プログレスバーが活性化
                loadingProgressBar.setVisibility(View.VISIBLE);
                //ビューモデルのloginメソッドに入力したメアドとパスワードを入れて託す
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }//LoginViewModelのloginメソッドで割り当てるエラーメッセージを決めている

    //会員登録するボタン MailAddressActivityへ遷移
    public void moveMail(View v){
        // ボタン押下時の動作
        Intent intent = new Intent(LoginActivity.this, MailAddressActivity.class);
        startActivity(intent);
    }

    //閉じるボタン
    public void buttonFinish(View v){
        // ボタン押下時の動作
        finish();
    }

    public void loginFinish(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /*
    //自動ログイン用コンストラクタ
    //Contextの扱いが難しいため実装しない
    public LoginActivity(String email,String password){
        System.out.println("自動ログイン");
        loginViewModel.setContext(this);
        loginViewModel.login(email,password);
    }
    public LoginActivity(){
        System.out.println("LoginActivity!");
    }
     */

}