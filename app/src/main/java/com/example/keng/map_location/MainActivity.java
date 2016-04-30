package com.example.keng.map_location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private AMap aMap;
    private LocationManager locationManager;
    private Button Lng_Lat_Location;
    private Button Analy_Location;
    private boolean Address_Search;
    private double Double_lng;
    private double Double_lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapView = (MapView) findViewById(R.id.map);
        //回调mapView的onCreate()方法
        mapView.onCreate(savedInstanceState);
        init();
        ToggleButton tb = (ToggleButton) findViewById(R.id.tb);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //使用卫星地图
                    aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                } else {
                    //设置使用普通地图
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                }
            }
        });

        Lng_Lat_Location = (Button) findViewById(R.id.Lng_Lat_Location);
        final TextView latTv = (TextView) findViewById(R.id.latitude);
        final TextView lngTv = (TextView) findViewById(R.id.longitude);
        //为经纬度定位按钮添加点击响应时间
        Lng_Lat_Location.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的经纬度
                String lng = lngTv.getEditableText().toString().trim();
                String lat = latTv.getEditableText().toString().trim();
                if (lng.equals("") || lat.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入有效的经度、纬度！", Toast.LENGTH_SHORT).show();
                } else {
                    //将获得的经纬度转为浮点型
                    Double_lng = Double.parseDouble(lng);
                    Double_lat = Double.parseDouble(lat);
                    Address_Search = true;

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updatePosition(location);

                }
            }
        });

        Analy_Location = (Button) findViewById(R.id.Analy_Location);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 8, new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                updatePosition(loc);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                updatePosition(locationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });



    }

    private void updatePosition(Location location){
        LatLng GPS_pos = new LatLng(location.getLatitude(),location.getLongitude());
        //创建一个用于显示GPS位置的CameraUpdate
        CameraUpdate GPS_cu = CameraUpdateFactory.changeLatLng(GPS_pos);
        //更新地图的显示区域
        aMap.moveCamera(GPS_cu);
        //清除所有的Marker覆盖物
        aMap.clear();

        //使用集合封装多个MarkerOptions对象，实现一次性添加多个Marker
        ArrayList<MarkerOptions> optionlist = new ArrayList<>();

        //创建一个MarkerOptions对象,用于标志拐杖所在的位置
        MarkerOptions markerOptions2 = new MarkerOptions();
        //设置markerOptions的各种属性
        //将拐杖所在位置的经纬度封装成为LatLng
        LatLng Cane_pos = new LatLng(23.16628, 113.3399);
        markerOptions2.position(Cane_pos).title("拐杖所在的位置").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).draggable(true);
        optionlist.add(markerOptions2);

        //创建一个MarkerOptions对象,用于标志用户输入的经纬度对应的位置
        MarkerOptions markerOptions = new MarkerOptions();
        if(Address_Search){
            //设置markerOptions的各种属性
            //将用户输入的经纬度封装成为LatLng
            LatLng Input_pos = new LatLng(Double_lat, Double_lng);
            markerOptions.position(Input_pos).title("输入经纬度对应的位置").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true);
            optionlist.add(markerOptions);
            Toast.makeText(MainActivity.this,Double_lat+""+Double_lng,Toast.LENGTH_SHORT).show();
        }

        //创建一个MarkerOptions1对象,用于标志手机GPS对应的位置
        MarkerOptions markerOptions1 = new MarkerOptions();
        //使用集合封装多个图标，可为MarkerOptions设置多个图标
        ArrayList<BitmapDescriptor> giflist = new ArrayList<>();
        giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerOptions1.position(GPS_pos).icons(giflist).title("手机GPS的当前位置").draggable(true).period(10);

        optionlist.add(markerOptions1);
        //批量添加多个Marker
        Marker marker = aMap.addMarker(markerOptions1);
        marker.showInfoWindow();
        aMap.addMarkers(optionlist, true);
    }

    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    protected void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    //初始化地图以及CameraUpdate
    private void init() {
        if(aMap==null)
        {
            aMap=mapView.getMap();
            //利用CameraUpdateFactory创建一个放大级别的CameraUpdate
            CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
            aMap.moveCamera(cu);
            //利用CameraUpdateFactory创建一个更改地图倾斜度的CameraUpdate
            CameraUpdate tiltUpdate = CameraUpdateFactory.changeTilt(30);
            aMap.moveCamera(tiltUpdate);
        }
    }

}
