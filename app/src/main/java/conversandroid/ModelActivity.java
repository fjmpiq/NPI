package conversandroid;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import conversandroid.demo.ExampleSceneLoader;
import conversandroid.demo.SceneLoader;
import org.andresoviedo.util.android.ContentUtils;

import java.io.IOException;
import android.hardware.SensorManager;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class ModelActivity extends Activity {

    private static final int REQUEST_CODE_LOAD_TEXTURE = 1000;

    /**
     * Type of model if file name has no extension (provided though content provider)
     */
    private int paramType;
    /**
     * The file to load. Passed as input parameter
     */
    private Uri paramUri;
    /**
     * Background GL clear color. Default is light gray
     */
    private float[] backgroundColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};

    private ModelSurfaceView gLView;

    private SceneLoader scene;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Try to get input parameters
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getString("uri") != null) {
                this.paramUri = Uri.parse(b.getString("uri"));
            }
            this.paramType = b.getString("type") != null ? Integer.parseInt(b.getString("type")) : -1;
            try {
                String[] backgroundColors = b.getString("backgroundColor").split(" ");
                backgroundColor[0] = Float.parseFloat(backgroundColors[0]);
                backgroundColor[1] = Float.parseFloat(backgroundColors[1]);
                backgroundColor[2] = Float.parseFloat(backgroundColors[2]);
                backgroundColor[3] = Float.parseFloat(backgroundColors[3]);
            } catch (Exception ex) {
                // Assuming default background color
            }
        }
        Log.i("Renderer", "Params: uri '" + paramUri + "'");

        // Create our 3D scenario
        if (paramUri == null) {
            scene = new ExampleSceneLoader(this);
        } else {
            scene = new SceneLoader(this);
        }
        scene.init();


        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gLView = new ModelSurfaceView(this);
        setContentView(gLView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.model, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.model_toggle_wireframe:
                scene.toggleWireframe();
                break;
            case R.id.model_toggle_textures:
                scene.toggleTextures();
                break;
            case R.id.model_toggle_lights:
                scene.toggleLighting();
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public Uri getParamUri() {
        return paramUri;
    }

    public int getParamType() {
        return paramType;
    }

    public float[] getBackgroundColor() {
        return backgroundColor;
    }

    public SceneLoader getScene() {
        return scene;
    }

    public ModelSurfaceView getGLView() {
        return gLView;
    }
    public SensorManager getSensorManager() {return sensorManager;}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {return;}
        switch (requestCode) {
            case REQUEST_CODE_LOAD_TEXTURE:
                // The URI of the selected file
                final Uri uri = data.getData();
                if (uri != null) {
                    Log.i("ModelActivity", "Loading texture '" + uri + "'");
                    try {
                        ContentUtils.setThreadActivity(this);
                        scene.loadTexture(null, uri);
                    } catch (IOException ex) {
                        Log.e("ModelActivity", "Error loading texture: " + ex.getMessage(), ex);
                        Toast.makeText(this, "Error loading texture '" + uri + "'. " + ex
                                .getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        ContentUtils.setThreadActivity(null);
                    }
                }
        }
    }

    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        gLView.start();
    }

    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        gLView.stop();
    }

}
