package info.redspirit.beaconinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rj on 2017/02/13.
 */

public class HttpResponsAsync extends AsyncTask<String, Integer, JSONArray> {
    HttpURLConnection con = null;
    URL url = null;
    private AsyncCallback _asyncCallback = null;


    public HttpResponsAsync(AsyncCallback _asyncCallback) {
        this._asyncCallback = _asyncCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this._asyncCallback.onPreExecute();
    }

    @Override
    protected JSONArray doInBackground(String... strUrl) {
        try {
            // URLの作成
            url = new URL(strUrl[0]);
            // 接続用HttpURLConnectionオブジェクト作成
            con = (HttpURLConnection)url.openConnection();
            // リクエストメソッドの設定
            con.setRequestMethod("GET");
            // リダイレクトを自動で許可しない設定
            con.setInstanceFollowRedirects(false);
            // URL接続からデータを読み取る場合はtrue
            con.setDoInput(true);
            // URL接続にデータを書き込む場合はtrue
            con.setDoOutput(true);


            ///////////setいるのかよくわからない
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            //////////////////////////////////

            // 接続
            con.connect();
            Log.i("CONNECTION","now");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            Log.i("StringBuilder",sb.toString());
//            JSONObject jo = new JSONObject(sb.toString());

            return new JSONArray(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray ja) {
        super.onPostExecute(ja);
        this._asyncCallback.onPostExecute(ja);
    }
}
