package com.yuanopen.erweima;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView tvShow;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

public  void Search(View v)
{
    tvShow= (TextView) findViewById(R.id.textView);
    handler=new Handler();
    customScan();
}
    // 你也可以使用简单的扫描功能，但是一般扫描的样式和行为都是可以自定义的，这里就写关于自定义的代码了
// 你可以把这个方法作为一个点击事件
    public void customScan(){
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

    private  void  getText(String code){

        BaseRequest request=new BaseRequest() {
            @Override
            protected void onFail(IOException e) {

            }

            @Override
            protected void onResponseFail(int code) {

            }

            @Override
            protected void onResponseSuccess(final String body) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("aaa",body);
                        try {
                            String str="";
                            JSONObject ob1=new JSONObject(body);
                            JSONObject result=ob1.getJSONObject("result");
                            JSONObject ob2=result.getJSONObject("summary");
                            String name=ob2.getString("name");
                            String price=ob2.getString("interval");
                            JSONArray ob3=result.getJSONArray("eshop");
                            str+=name+":"+price+"\n";
                            Log.i("aaa",name);
                            Log.i("aaa",str);
                            for (int i = 0; i < ob3.length(); i++) {
                                JSONObject ob= (JSONObject) ob3.get(i);
                                str+=ob.getString("shopname")+": "+ob.getString("price")+" 元\n";
                            }
                            Log.i("aaa",str);
                            tvShow.setText(str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        };

        request.request("http://api.juheapi.com/jhbar/bar?" +
                "pkg=&barcode="+code+"&cityid=1&appkey=df9f7e1dc5e983a86ced925ad2072d75&key=df9f7e1dc5e983a86ced925ad2072d75");
//        request.request("http://api.juheapi.com/jhbar/bar?pkg=&barcode=6901294174115&cityid=1&appkey=df9f7e1dc5e983a86ced925ad2072d75&key=df9f7e1dc5e983a86ced925ad2072d75");
    }

    @Override
// 通过 onActivityResult的方法获取 扫描回来的 值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
                Toast.makeText(this,"内容为空",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"扫描成功",Toast.LENGTH_LONG).show();
                // ScanResult 为 获取到的字符串
                final  String ScanResult = intentResult.getContents();
                Toast.makeText(this,"结果："+ScanResult,Toast.LENGTH_LONG).show();
                new  Thread(new Runnable() {
                    @Override
                    public void run() {
                        getText(ScanResult);
                    }
                }).start();

            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }


    Bitmap encodeAsBitmap(String str){
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 200, 200);
            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e){
            e.printStackTrace();
        } catch (IllegalArgumentException iae){ // ?
            return null;
        }

        // 如果不使用 ZXing Android Embedded 的话，要写的代码

//        int w = result.getWidth();
//        int h = result.getHeight();
//        int[] pixels = new int[w * h];
//        for (int y = 0; y < h; y++) {
//            int offset = y * w;
//            for (int x = 0; x < w; x++) {
//                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
//            }
//        }
//        bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels,0,100,0,0,w,h);

        return bitmap;
    }
}
