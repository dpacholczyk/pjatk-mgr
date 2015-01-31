package com.fixus.towerdefense.tools;
import javax.microedition.khronos.opengles.GL10;

import rajawali.Object3D;
import rajawali.lights.DirectionalLight;
import rajawali.math.vector.Vector3;
import rajawali.parser.LoaderOBJ;
import rajawali.parser.ParsingException;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;

import android.util.Log;

import com.fixus.towerdefense.R;

public class TestRenderer extends RajawaliRenderer {
	
	
	private DirectionalLight mLight;
	private Object3D m3DObject;
	private static final boolean DEBUG = true;
	private static final String TAG = "OpenGLRenderer";
	private static final double rtod = 180 / Math.PI;
	
	public TestRenderer(Context context) {
		super(context);
	    setFrameRate(60);
	}
	
//	public TestRenderer(Context context, )
	
	public void initScene() {
		mLight = new DirectionalLight(5f, 0.2f, -1.0f);
		mLight.setColor(1.0f, 10.0f, 10.0f);
		mLight.setPower(1);

		LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.f16);
		
		try {
			objParser.parse();
		} catch (ParsingException e) {
			e.printStackTrace();
		}

		m3DObject = objParser.getParsedObject();
		m3DObject.setScale(3.0);
		//m3DObject.setMaterial(material);
		//m3DObject.setPosition(x, y, z);
		m3DObject.setPosition(0, 0, 0);
//		getCurrentScene().addLight(mLight);
		
		/*Cube mCube = new Cube(1);
		    Material diffuse = new Material();
		    diffuse.enableLighting(true);
		    diffuse.setDiffuseMethod(new DiffuseMethod.Lambert());
		    mCube.setColor(Color.BLUE);
		    mCube.setMaterial(diffuse);
		    mCube.setPosition(-1, 1, 1);
		    getCurrentScene().addChild(mCube);*/

		
		
		getCurrentScene().addChild(m3DObject);
//		
//		addChild(m3DObject);
		
//		RotateAnimation3D mAnim = new RotateAnimation3D(50f, 0, 100f);
//		mAnim.setDurationMilliseconds(2000);
//		mAnim.setRepeatMode(RepeatMode.INFINITE);
//		getCurrentScene().registerAnimation(mAnim);
//		mAnim.setTransformable3D(sphere);
//		mAnim.play();	
		
	}

    @Override
    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        // ta linia automatycznie rotuje sfere
//        sphere.setRotY(sphere.getRotY() + 1);
    }

	public void set3DObjectPosition(double x, double y, double z) {
		if (m3DObject != null){
			m3DObject.setPosition(x, y, z);
		}		
	}
	
	public void set3DObjectPosition(Vector3 newPosition) {
		if (m3DObject != null){
			m3DObject.setPosition(newPosition);
		}		
	}
	
	public void set3DObjectRotate(double rotX, double rotY, double rotZ) {
		if (m3DObject != null){
			m3DObject.setRotation(rotX, rotY, rotZ);
		}
			
	}
	
	public void doAnimation(){
		//m3DObject.rotateAround(new Vector3(1, 0, 0), 30, true);
	}
	
	public void rotateCamer(double rotX, double rotY, double rotZ) {
		getCurrentCamera().setRotation(rotX, rotY, rotZ);
	}
	
	public void rotate3DObject(double rotX, double rotY, double rotZ) {
		m3DObject.setRotation(
				rotX,	
				rotY,	
				rotZ	
		);
	}
	
	public Vector3 get3DObjectPosition() {
		return m3DObject.getPosition();
	}

	public void setCameraPosition(double x, double y, double z) {
		getCurrentCamera().setX(x);
		getCurrentCamera().setY(y);
		getCurrentCamera().setZ(z);

	}
	
	public void setCameraRotation(double x, double y, double z) {
		getCurrentCamera().setRotX(x);
		getCurrentCamera().setRotY(y);
		getCurrentCamera().setRotZ(z);
	}

	public void setCamLRTilt(double lrTiltAngleInRadians) {
		getCurrentCamera().setRotZ(-lrTiltAngleInRadians * rtod);

	}

	public void setCamFBTilt(double fbTiltAngleInRadians) {
		getCurrentCamera().setRotX(-fbTiltAngleInRadians * rtod);

	}

	public void setCubeSize(double d) {
		m3DObject.setScale(d);
	}

	public Vector3 getCubeSize() {
		return m3DObject.getScale();
	}

	public void setLRTilt(double lrTiltAngleInRadians) {
		m3DObject.setRotZ(-lrTiltAngleInRadians * rtod);

	}

	public void setFBTilt(double fbTiltAngleInRadians) {
		m3DObject.setRotX(-fbTiltAngleInRadians * rtod);

	}

	public void setSpin(double spinAngleInDegrees) {

		m3DObject.setRotY(m3DObject.getRotY() + spinAngleInDegrees);

		if (DEBUG)
			Log.d(TAG, "getRotY: " + m3DObject.getRotY());

	}

	public boolean isReady() {
		return (m3DObject != null);
	}
    
}