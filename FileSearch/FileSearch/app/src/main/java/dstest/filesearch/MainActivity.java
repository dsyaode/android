package dstest.filesearch;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements  View.OnClickListener,
        LoaderManager.LoaderCallbacks<List<AppFileChangeData> >,
        FileObsReceiver.FileObsListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btn;
    private Button btn2;
    private Button btn3;
    private ListView listView;
    private ItemAdapter adapter;


    private MyHandler handler;

    private FileObsReceiver receiver;

    @Override
    public Loader<List<AppFileChangeData>> onCreateLoader(int i, Bundle bundle) {
        Log.i(TAG, "onCreateLoader");
        return new AsyncTaskLoader<List<AppFileChangeData>>(this) {
            @Override
            public List<AppFileChangeData> loadInBackground() {
                Log.i(TAG, "loadInBackground");
                return DBAccessor.queryAll(AppFileChangeData.class, new String[]{"time"}, new boolean[]{false});
            }

            @Override
            protected void onStartLoading() {
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<AppFileChangeData>> loader, List<AppFileChangeData> appFileChangeDatas) {
        Log.i(TAG, "onLoadFinished");
        adapter.setItems(appFileChangeDatas);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<AppFileChangeData>> loader) {
        Log.i(TAG, "onLoaderReset");
    }

    @Override
    public void fileChange() {
        Log.i(TAG, "fileChange");
        this.getLoaderManager().restartLoader(0, null, this);
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            AppFileChangeData data = (AppFileChangeData) msg.getData().get("data");
            addAppFileChangeData(data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) this.findViewById(R.id.button);
        listView = (ListView) this.findViewById(R.id.listView);
        btn.setOnClickListener(this);

        btn2 = (Button) this.findViewById(R.id.button2);
        btn2.setOnClickListener(this);

        btn3 = (Button) this.findViewById(R.id.button3);
        btn3.setOnClickListener(this);

        adapter = new ItemAdapter(this, new ArrayList<AppFileChangeData>());
        listView.setAdapter(adapter);

        handler = new MyHandler();

        this.getLoaderManager().initLoader(0,null, this);

        receiver = new FileObsReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileObsReceiver.ACTION_FILE_OBS);
        this.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button){
            onBtnClick();
        }else if(view.getId() == R.id.button2){
            onBtnClick2();
        }else if(view.getId() == R.id.button3){
            onBtnClick3();
        }
    }

    private void addAppFileChangeData(AppFileChangeData data){
        DBAccessor.createOrUpdate(data);
        this.getLoaderManager().restartLoader(0, null, this);
    }

    private void onBtnClick3(){
        this.getLoaderManager().restartLoader(0, null, this);
    }

    private void onBtnClick2(){
        String str = Environment.getExternalStorageDirectory().getPath();
        final File file = new File(str);
        if(file.isDirectory()){
            File[] files = file.listFiles();
            while(true){
                final Random random = new Random();
                int index = random.nextInt(files.length);
                if(files[index].isDirectory()){
                    Log.i(TAG, "create file " + files[index].getAbsolutePath());
//                    File newFile = new File(file.getAbsolutePath() + "/test_ds" + random.nextInt(100));
//                    newFile.mkdirs();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                File newFile2 = new File(file.getAbsolutePath() + "/test_txt"+random.nextInt(1000)+".txt");
                                Log.i(TAG, "create file " + newFile2.getAbsolutePath());
                                try {
                                    if(!newFile2.createNewFile()) {
                                    }
                                } catch (IOException ex) {
                                }
                                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile2));
                                stream.write(1000);
                                Thread.sleep(3000);
                                stream.flush();
                                stream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                    break;
                }
            }
        }
    }

    private void onBtnClick(){
        Intent intent = new Intent(this,FileObsService.class);
        intent.setAction(FileObsService.ACTION_START_OBS_FILE);
        this.startService(intent);

        btn.setText("正在监听");
    }

    public void onDestroy(){
        super.onDestroy();
    }



}
