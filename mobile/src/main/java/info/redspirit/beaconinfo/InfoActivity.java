package info.redspirit.beaconinfo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Locale;


public class InfoActivity extends AppCompatActivity implements LocationListener {

    ImageView iv;
    TextView placeNameTxt;
    TextView infoTxt;
    Button bt;
    Spinner spinner;
    LocationManager locationManager;
    private String process;
    private String[] WORDS;
    private String id;
    private String latitude;
    private String longitude;
    private Double nowLat;
    private Double nowLng;
    private String nowLatitude;
    private String nowLongitude;
    private String imageUrl;
    private String name;
    private String info;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        iv = (ImageView) findViewById(R.id.InfoMainImage);
        placeNameTxt = (TextView) findViewById(R.id.placeNameTxt);
        infoTxt = (TextView) findViewById(R.id.infoTxt);

        Intent intent = getIntent();
        int getId = intent.getIntExtra("id", 1);

        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String url;

        if(language.equals("ja")){
            url = "http://sample-env-2.3p4ikwvwvd.us-west-2.elasticbeanstalk.com/ja/infoprocess.php?id=" + getId;
        }else{
            url = "http://sample-env-2.3p4ikwvwvd.us-west-2.elasticbeanstalk.com/en/infoprocess.php?id=" + getId;
        }


        HttpResponsAsync hra = new HttpResponsAsync(new AsyncCallback() {
            @Override
            public void onPreExecute() {
                // プログレスダイアログの設定
                waitDialog = new ProgressDialog(InfoActivity.this);
                // プログレスダイアログのメッセージを設定します
                waitDialog.setMessage("NOW LOADING...");
                // 円スタイル（くるくる回るタイプ）に設定します
                waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                waitDialog.setIndeterminate(true);
                // プログレスダイアログを表示
                waitDialog.show();
            }

            @Override
            public void onPostExecute(JSONArray ja) {
                try {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject eventObj = ja.getJSONObject(i);
                        id = eventObj.getString("spot_id");
                        latitude = eventObj.getString("latitude");
                        longitude = eventObj.getString("longitude");
                        imageUrl = eventObj.getString("image_url");
                        name = eventObj.getString("spot_name");
                        info = eventObj.getString("spot_info");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (imageUrl.equals("sample")) {

                } else {

                }
                placeNameTxt.setText(name);
                infoTxt.setText(info);

                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }

            }

            @Override
            public void onProgressUpdate(int progress) {

            }

            @Override
            public void onCancelled() {

            }
        });
        hra.execute(url);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //スピナー選択
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                process = (String) spinner.getSelectedItem();
                Log.i("spinner", process);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bt = (Button) findViewById(R.id.visitBtn);
        //ボタン
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test0();
            }
        });

        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            /** fine location のリクエストコード（値は他のパーミッションと被らなければ、なんでも良い）*/
            final int requestCode = 1;

            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
            return;
        }

        // 位置情報を管理している LocationManager のインスタンスを生成する
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = null;
//        Criteria criteria = new Criteria();
//        locationProvider = locationManager.getBestProvider(criteria, true);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPSが利用可能になっているかどうかをチェック
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっているかをチェック
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            // いずれも利用可能でない場合は、GPSを設定する画面に遷移する
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            return;
        }

        /** 位置情報の通知するための最小時間間隔（ミリ秒） */
        final long minTime = 500;
        /** 位置情報を通知するための最小距離間隔（メートル）*/
        final long minDistance = 1;

        // 利用可能なロケーションプロバイダによる位置情報の取得の開始
        // FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        // 最新の位置情報
        Location location = locationManager.getLastKnownLocation(locationProvider);

//        nowLat = location.getLatitude();
//        nowLng = location.getLongitude();

        try {
            Log.i("LocationProvider", String.valueOf(locationProvider));
//            Log.i("GPS", BigDecimal.valueOf(nowLat).toPlainString() + ":" + BigDecimal.valueOf(nowLng).toPlainString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //位置情報が通知されるたびにコールバックされるメソッド
    @Override
    public void onLocationChanged(Location location) {
        nowLat = location.getLatitude();
        nowLng = location.getLongitude();
        Log.d("GPS", String.valueOf(nowLat) +":"+ String.valueOf(nowLng));
    }

    //ロケーションプロバイダが利用不可能になるとコールバックされるメソッド
    @Override
    public void onProviderDisabled(String provider) {
        //ロケーションプロバイダーが使われなくなったらリムーブする必要がある
    }

    //ロケーションプロバイダが利用可能になるとコールバックされるメソッド
    @Override
    public void onProviderEnabled(String provider) {
        //プロバイダが利用可能になったら呼ばれる
    }

    //ロケーションステータスが変わるとコールバックされるメソッド
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // 利用可能なプロバイダの利用状態が変化したときに呼ばれる
    }

    //TODO:自前マップ実装
    protected void goMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    private void test0() {
        String srcLatitude = BigDecimal.valueOf(nowLat).toPlainString();
        String srcLongitude = BigDecimal.valueOf(nowLng).toPlainString();
        String desLatitude = latitude;
        String desLongitude = longitude;

//        String start = "新宿駅";
//        String destination = "鶴岡八幡宮";
        String dir;

        // 電車:r
        //String dir = "r";
        // 車:d
        //String dir = "d";
        // 歩き:w
        //String dir = "w";
        if (process.equals("Car") || process.equals("車")) {
            dir = "d";
        } else if (process.equals("Train") || process.equals("電車")) {
            dir = "r";
        } else {
            dir = "w";
        }

        Log.i("dir", dir);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//        intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + start + "&daddr=" + destination + "&dirflg=" + dir));
        intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + srcLatitude + "," + srcLongitude + "&daddr=" + desLatitude + "," + desLongitude + "&dirflg=" + dir));
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
