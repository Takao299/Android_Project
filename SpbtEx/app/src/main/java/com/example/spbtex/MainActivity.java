package com.example.spbtex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spbtex.dataload.AttachedFile;
import com.example.spbtex.dataload.DownloadAsyncTask;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.FilesModel;
import com.example.spbtex.ui.facility.FacilityActivity;
import com.example.spbtex.ui.member.MemberActivity;
import com.example.spbtex.ui.history.HistoryActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends MenuActivity {

    TextView textViewSQLite;
    Button move_list_button;
    Button move_member_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewSQLite = findViewById(R.id.textViewSQLite);
        textViewSQLite.setText("stand-alone："+DbOpenHelper.STAND_ALONE.toString());

        move_list_button = findViewById(R.id.button_move_list);
        move_member_button = findViewById(R.id.button_move_member);
        if(!isLogin()){
            move_list_button.setEnabled(false);
            move_member_button.setEnabled(false);
        }

        //アクションバーの「戻るボタン」を消去
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        if(!DbOpenHelper.STAND_ALONE){
            //サーバーから画像情報を取得（画像は次の施設選択画面で使う）
            loadImagesData();
        }
    }

    //施設選択画面へ遷移
    public void moveFacility(View v){
        // ボタン押下時の動作
        Intent intent = new Intent(MainActivity.this, FacilityActivity.class);
        startActivity(intent);
    }

    //予約一覧画面へ遷移
    public void moveList(View v){
        // ボタン押下時の動作
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class); //RemainListActivity
        startActivity(intent);
    }

    //会員情報画面へ遷移
    public void moveMember(View v){
        // ボタン押下時の動作
        Intent intent = new Intent(MainActivity.this, MemberActivity.class);
        startActivity(intent);
    }


    protected void loadImagesData(){
        List<AttachedFile> attachedFileList = null;

        //サーバー側に登録されている画像の情報をDBから取得し、androidのSQLiteと同期する
        MyAsyncTask task = new MyAsyncTask();
        task.setUrl(Urls.URL_ATTACHED_FILE_GET);
        task.setMethod(MyAsyncTask.GET);
        task.setaClass(AttachedFile.class);
        task.execute();
        attachedFileList = (List<AttachedFile>) task.getReceiveObject();

        String f_name; //実際に保存されるファイル名 createTime + fileNameで合わせたもの (20250126214837118RWGnGs.jpg)
        String fileName; //サーバーにアップロードされた画像のオリジナルファイル名 (RWGnGs.jpg)
        String createTime; //サーバーにアップロードされた日時 (20250126214837118)

        FilesModel filesModel = new FilesModel(this);
        filesModel.deleteAllFiles(); //初期化

        //androidアプリの内部ストレージに存在するファイル名のリスト
        List<String> androidFiles = Arrays.asList(new File(String.valueOf(getFilesDir())).list());
        //サーバーに存在するファイル名のリスト
        List<String> serverFiles = new ArrayList<>();

        for (AttachedFile af : attachedFileList) {

            f_name = af.getCreateTime() + af.getFileName();
            createTime = af.getCreateTime();
            fileName = af.getFileName();

            serverFiles.add(f_name);

            File file = new File(getFilesDir(), f_name);

            //サーバー側で未削除の画像
            if (af.getDeleteDateTime() == null) {
                //SQLiteにデータが存在しなければファイル情報を挿入する
                if(!filesModel.searchData(fileName,createTime)){
                    filesModel.insertData(
                            af.getAfId().toString(),
                            af.getForeignId().toString(),
                            af.getFileName(),
                            af.getCreateTime(),
                            af.getDelete_pic(),
                            null
                    );
                }
                //ファイルが無い場合ファイルダウンロード
                if(!file.exists())
                    downloadImage(f_name);

            //サーバー側で削除済の画像
            } else if (af.getDeleteDateTime() != null) {
                //SQLiteからファイル情報を物理削除
                if(filesModel.searchData(fileName,createTime)){
                    filesModel.deleteFile(fileName,createTime);
                }
                //ファイルを削除
                if(file.exists())
                    file.delete();
            }
        }

        //System.out.println(androidFiles);
        //System.out.println(serverFiles);
        for(String a:androidFiles){
            //サーバーに存在しない画像ファイルは消去
            if(!serverFiles.contains(a))
                new File(getFilesDir(), a).delete();
        }

    }

    protected void downloadImage(String fileName){
        //サーバーから画像を1枚ダウンロード
        DownloadAsyncTask task = new DownloadAsyncTask();
        task.setUrl(Urls.URL_DOWNLOAD_IMAGE_POST+"/"+fileName);
        task.setContext(this);
        task.setFileName(fileName);
        task.setWait(false); //ダウンロード処理は個別に投げっぱなしにする
        task.execute();
    }

}