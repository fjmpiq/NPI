package conversandroid.viewer3d.controller;

import org.andresoviedo.android_3d_model_engine.model.Camera;

import conversandroid.viewer3d.ModelRenderer;
import conversandroid.viewer3d.ModelSurfaceView;
import conversandroid.viewer3d.demo.SceneLoader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;

/**
 * @author @mx_psi, @fjmpiq, @jojelupipa
 * Implements the rotation controller
 */
public class RotationController implements SensorEventListener{

	private static final String TAG = RotationController.class.getName();

	private final ModelSurfaceView view;
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;
    private boolean initializedRot = false;

    private final float[] initialRotMatrix = new float[16];
    private final float[] initialTransRotMatrix   = new float[16];
	private final float[] mRotationMatrix  = new float[16];

	public RotationController(ModelSurfaceView view, ModelRenderer renderer, SensorManager sensorManager) {
		super();
		this.view = view;
		this.mSensorManager = sensorManager;
        // find the rotation-vector sensor
        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
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
        initializedRot = false;
    }
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            if(!initializedRot){
                SensorManager.getRotationMatrixFromVector(
                        initialTransRotMatrix, event.values);
                Matrix.transposeM(initialRotMatrix, 0, initialTransRotMatrix, 0);
                initializedRot = true;
            } else{
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix , event.values);
                 Matrix.multiplyMM(mRotationMatrix, 0, initialRotMatrix, 0, mRotationMatrix, 0);
                SceneLoader scene = view.getModelActivity().getScene();
                Camera camera = scene.getCamera();
                camera.RotateM(mRotationMatrix);
                view.requestRender();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
