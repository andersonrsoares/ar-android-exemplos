package com.beyondar.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.opengl.util.LowPassFilter;
import com.beyondar.android.util.ImageUtils;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.BeyondarObjectList;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import org.json.JSONObject;

public class StaticViewGeoObjectActivity extends FragmentActivity implements
		OnClickBeyondarObjectListener {

	private static final String TMP_IMAGE_PREFIX = "viewImage_";

	private BeyondarFragmentSupport mBeyondarFragment;
	private World mWorld;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The first thing that we do is to remove all the generated temporal
		// images. Remember that the application needs external storage write
		// permission.
		cleanTempFolder();
		LowPassFilter.ALPHA = 0.0090f;
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.simple_camera);

		mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
				R.id.beyondarFragment);

		mBeyondarFragment.setOnClickBeyondarObjectListener(this);

		// We create the world and fill it ...
		mWorld =   init();//CustomWorldHelper.generateObjects(this);

		// .. and send it to the fragment
		mBeyondarFragment.setWorld(mWorld);

		// We also can see the Frames per seconds
		mBeyondarFragment.showFPS(true);

		mBeyondarFragment.setMaxDistanceToRender(20000);
		mBeyondarFragment.setPushAwayDistance(27);
		mBeyondarFragment.setPullCloserDistance(1000);
		mBeyondarFragment.setDistanceFactor(1000);;
		// This method will replace all GeoObjects the images with a simple
		// static view
		replaceImagesByStaticViews(mWorld);

	}

	public World init(){
		try {
			JSONObject jsonObject = new JSONObject(openfile());
			ImovelRequest imovelRequest = new ImovelRequest(jsonObject,"locacao");

			World sharedWorld = new World(this);

			// The user can set the default bitmap. This is useful if you are
			// loading images form Internet and the connection get lost
			sharedWorld.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

			// User position (you can change it using the GPS listeners form Android
			// API)
			sharedWorld.setGeoPosition(-25.540293, -49.282856);

			for(int i=0;i<imovelRequest.getDados().size();i++){
				try {
					// Create an object with an image in the app resources.
					GeoObject go1 = new GeoObject(i+1);
					go1.setGeoPosition(Double.parseDouble(imovelRequest.getDados().get(i).getLat()),
							Double.parseDouble(imovelRequest.getDados().get(i).getLng()));
					go1.setImageResource(R.drawable.creature_1);
					go1.setName(imovelRequest.getDados().get(i).getValorFormat());

					sharedWorld.addBeyondarObject(go1);
				}catch (Exception ex){
					Log.e("gerando", "init: ", ex);
				}

			}
			return sharedWorld;
		}catch (Exception ex){
			return  new World(this);
		}
	}

	public String openfile(){
		// To load text file
		InputStream input;
		try {
			input = getAssets().open("imovis.json");

			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			// byte buffer into a string
			String text = new String(buffer);

			return  text;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void replaceImagesByStaticViews(World world) {
		String path = getTmpPath();

		for (BeyondarObjectList beyondarList : world.getBeyondarObjectLists()) {
			for (BeyondarObject beyondarObject : beyondarList) {
				update(beyondarObject,path,0);
			}
		}
	}

	public void update(BeyondarObject beyondarObject,String path,int color){
		// First let's get the view, inflate it and change some stuff
		View view = null;
		if(color > 0)
			view = getLayoutInflater().inflate(R.layout.static_beyondar_object_view2, null);
		else
			view = getLayoutInflater().inflate(R.layout.static_beyondar_object_view, null);


		TextView textView = (TextView) view.findViewById(R.id.geoObjectName);
		textView.setText(beyondarObject.getName());
		try {
			// Now that we have it we need to store this view in the
			// storage in order to allow the framework to load it when
			// it will be need it
			String imageName = TMP_IMAGE_PREFIX + beyondarObject.getId()+ "_" + color + ".png";
			ImageUtils.storeView(view, path, imageName);

			// If there are no errors we can tell the object to use the
			// view that we just stored
			beyondarObject.setImageUri(path + imageName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the path to store temporally the images. Remember that you need to
	 * set WRITE_EXTERNAL_STORAGE permission in your manifest in order to
	 * write/read the storage
	 */
	private String getTmpPath() {
		return getExternalFilesDir(null).getAbsoluteFile() + "/tmp/";
	}

	/**
	 * Clean all the generated files
	 */
	private void cleanTempFolder() {
		File tmpFolder = new File(getTmpPath());
		if (tmpFolder.isDirectory()) {
			String[] children = tmpFolder.list();
			for (int i = 0; i < children.length; i++) {
				if (children[i].startsWith(TMP_IMAGE_PREFIX)) {
					new File(tmpFolder, children[i]).delete();
				}
			}
		}
	}

	@Override
	public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
		if (beyondarObjects.size() > 0) {
			update(beyondarObjects.get(0),getTmpPath(),1);
			Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
		}
	}
}