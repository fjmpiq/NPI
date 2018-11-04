package conversandroid.controller;

import org.andresoviedo.android_3d_model_engine.model.Camera;

import conversandroid.ModelRenderer;
import conversandroid.ModelSurfaceView;
import conversandroid.demo.SceneLoader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RotationController implements SensorEventListener{

	private static final String TAG = RotationController.class.getName();

	private final ModelSurfaceView view;
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;
	private final float[] mRotationMatrix = new float[16];

	public RotationController(ModelSurfaceView view, ModelRenderer renderer, SensorManager sensorManager) {
		super();
		this.view = view;
		this.mSensorManager = sensorManager;
        // find the rotation-vector sensor
        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);

		mRotationMatrix[ 0] = 1;
		mRotationMatrix[ 4] = 1;
		mRotationMatrix[ 8] = 1;
		mRotationMatrix[12] = 1;
	}

    public void start() {
        /*
        enable our sensor when the activity is resumed, ask for
        10 ms updates.
        */
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
    }
    public void stop() {
        // make sure to turn our sensor off when the activity is paused
        mSensorManager.unregisterListener(this);
    }
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix , event.values);
            SceneLoader scene = view.getModelActivity().getScene();
            Camera camera = scene.getCamera();
            camera.RotateM(mRotationMatrix);
            view.requestRender();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
