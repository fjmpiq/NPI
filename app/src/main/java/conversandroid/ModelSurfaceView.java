package conversandroid;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import conversandroid.controller.RotationController;
import conversandroid.controller.TouchController;
import android.hardware.SensorEventListener;

/**
 * This is the actual opengl view. From here we can detect touch gestures for example
 * 
 * @author andresoviedo
 *
 */
public class ModelSurfaceView extends GLSurfaceView implements SensorEventListener {

	private ModelActivity parent;
	private ModelRenderer mRenderer;
	private TouchController touchHandler;
	private RotationController rotHandler;

	public ModelSurfaceView(ModelActivity parent) {
		super(parent);

		// parent component
		this.parent = parent;

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		// This is the actual renderer of the 3D space
		mRenderer = new ModelRenderer(this);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		// TODO: enable this?
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		touchHandler = new TouchController(this, mRenderer);
		rotHandler = new RotationController(this, mRenderer,parent.getSensorManager());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return touchHandler.onTouchEvent(event);
	}

    public void onSensorChanged(SensorEvent event) {
	    rotHandler.onSensorChanged(event);
    }

	public ModelActivity getModelActivity() {
		return parent;
	}

	public ModelRenderer getModelRenderer(){
		return mRenderer;
	}

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void start(){
	    rotHandler.start();
    }

    public void stop(){
	    rotHandler.stop();
    }

}