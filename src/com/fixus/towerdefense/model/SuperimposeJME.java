/* SuperimposeJME - SuperimposeJME Example
 * 
 * Example Chapter 3
 * accompanying the book
 * "Augmented Reality for Android Application Development", Packt Publishing, 2013.
 * 
 * Copyright � 2013 Jens Grubert, Raphael Grasset / Packt Publishing.
 */

package com.fixus.towerdefense.model;

import android.location.Location;
import android.util.Log;

import com.fixus.towerdefense.RadarActivity;
import com.fixus.towerdefense.game.GameStatus;
import com.fixus.towerdefense.library.configuration.Configuration;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.collision.CollisionResults;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

public class SuperimposeJME extends SimpleApplication  implements AnimEventListener { 

	private static final String TAG = "TD_SuperimposeJME";
	
	// The geometry which will represent the video background
	private Geometry mVideoBGGeom;
	// The material which will be applied to the video background geometry.
	private Material mvideoBGMat;
	// The texture displaying the Android camera preview frames.
	private Texture2D mCameraTexture;
	// the JME image which serves as intermediate storage place for the Android
	// camera frame before the pixels get uploaded into the texture.
	private Image mCameraImage;
	// A flag indicating if the scene has been already initialized.
	private boolean mSceneInitialized = false;
	// A flag indicating if a new Android camera image is available.
	boolean mNewCameraFrameAvailable = false;
	
	private Vector3f mUserPosition;
	private Vector3f mNinjaPosition;
	Location locationNinja;
	public static boolean firstTimeLocation = true;
	

	private float mForegroundCamFOVY = 50; // for a Samsung Galaxy SII
	
	// for animation	
	// The controller allows access to the animation sequences of the model
	private AnimControl mAniControl;
	// the channel is used to run one animation sequence at a time
	private AnimChannel mAniChannel;
	
	private boolean newPosition =false;
	
	public static final int MOVE_ROTATION = 0;
	public static final int GPS_ROTATION = 1;
	
	public float oldX = 0;

	
  public Spatial ninja;
	
	public static void main(String[] args) {
		SuperimposeJME app = new SuperimposeJME();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		//dodanie listenera
		addTouchListener();
		// Do not display statistics
		setDisplayStatView(true);
		setDisplayFps(true);
		// we use our custom viewports - so the main viewport does not need the  rootNode
		viewPort.detachScene(rootNode);
		initVideoBackground(settings.getWidth(), settings.getHeight());
		initForegroundScene();	
		initBackgroundCamera();		
		initForegroundCamera(mForegroundCamFOVY);
		
    }
	
	private void addTouchListener(){
		//inputManager.addListener(new TouchTrigger(0),"Click");
		String sTouchId = "tap";
		inputManager.addMapping(sTouchId, new TouchTrigger(TouchInput.ALL));
		inputManager.addListener(oTouch, sTouchId);
	}
	
	private TouchListener oTouch = new TouchListener() {
		public void onTouch(String name, TouchEvent event, float tpf) {			
			if (event.getType() == TouchEvent.Type.TAP) {
				
		        CollisionResults results = new CollisionResults();
		        // Convert screen click to 3d position
		        Vector2f click2d = new Vector2f(event.getX(), event.getY());
		        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
		        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
		        // Aim the ray from the clicked spot forwards.
		        Ray ray = new Ray(click3d, dir);
		        // Collect intersections between ray and all nodes in results list.
		        rootNode.collideWith(ray, results);
		        
		        if (results.size() > 0) {
		        	if (results.getClosestCollision()!=null) {
						RadarActivity.messageDialog(results.getClosestCollision().getGeometry().getName());
						toogleObject(false);
						RadarActivity.blockShow = true;
					}
		        }
		        
		        /*Ray oray = new Ray(new Vector3f(event.getX()-1, event.getY()-1, 0.0f),new Vector3f(event.getX(), event.getY(), ninja.getWorldScale().z));
				CollisionResults results = new CollisionResults();
				int collisionSphere = ninja.collideWith(oray, results);
				Log.d(TAG, "z: " + ninja.getWorldScale().z);
				Log.d(TAG, "x: " + ninja.getWorldScale().x + "  " + event.getX());
				Log.d(TAG, "y: " + ninja.getWorldScale().y+ "  " + event.getY());
				
				String name2 ="";
	
				if (results.getClosestCollision()!=null) {
					name2 = results.getClosestCollision().getGeometry().getName();
					Log.d(TAG, name2 + "   " + collisionSphere);
				}else{
					Log.d(TAG, ":(");
				}*/
			}
	
			if (event.getType() == TouchEvent.Type.SCROLL) {
			}
	
			if (event.getType() == TouchEvent.Type.DOWN) {
			}
			
			}
		};
	
	// This function creates the geometry, the viewport and the virtual camera
	// needed for rendering the incoming Android camera frames in the scene
	// graph
	public void initVideoBackground(int screenWidth, int screenHeight) {
		// Create a Quad shape.
		Quad videoBGQuad = new Quad(1, 1, true);
		// Create a Geometry with the Quad shape
		mVideoBGGeom = new Geometry("quad", videoBGQuad);
		float newWidth = 1.f * screenWidth / screenHeight;
		// Center the Geometry in the middle of the screen.
		mVideoBGGeom.setLocalTranslation(-0.5f * newWidth, -0.5f, 0.f);//
		// Scale (stretch) the width of the Geometry to cover the whole screen
		// width.
		mVideoBGGeom.setLocalScale(1.f * newWidth, 1.f, 1);
		// Apply a unshaded material which we will use for texturing.
		mvideoBGMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mVideoBGGeom.setMaterial(mvideoBGMat);
		// Create a new texture which will hold the Android camera preview frame
		// pixels.
		mCameraTexture = new Texture2D();

		mUserPosition=new Vector3f();
		mNinjaPosition=new Vector3f();
		locationNinja = new Location("");
		
		mSceneInitialized = true;
	}
	
	public void initBackgroundCamera() {
		// Create a custom virtual camera with orthographic projection
		Camera videoBGCam = cam.clone();		
		videoBGCam.setParallelProjection(true);
		// Also create a custom viewport.
		ViewPort videoBGVP = renderManager.createMainView("VideoBGView", videoBGCam);
		// Attach the geometry representing the video background to the
		// viewport.
		videoBGVP.attachScene(mVideoBGGeom);
	}
	public void initForegroundScene() {
		rootNode.detachAllChildren();
		// Load a model from test_data (OgreXML + material + texture)
//        ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja = assetManager.loadModel(Configuration.modelPath);
        
//        ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.j3o");
        ninja.scale(0.025f, 0.025f, 0.025f);
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md"); // default material
      Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // default material
//        Material mat = assetManager.loadMaterial("Materials/Ninja/Ninja.j3m");
//        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Ninja/Ninja.jpg"));
      mat.setTexture("ColorMap", assetManager.loadTexture(Configuration.texturePath));
      
        ninja.setMaterial(mat);               
        
        // Math.toRadians przelicza kąt na radiany które podawane są do metody w celu rotacji.
        // rotacja odbywa się tak, że 0 to znaczy skierowane na wprost zgodnie z tym jak sie patrzy przez kamere
        // obrot np. 90 stopni oznacza obrot w lewo
        ninja.rotate(0.5f, (float)Math.toRadians(0.0), 0.0f);
        ninja.setLocalTranslation(0.0f, -2.5f, -35.0f);
        
        rootNode.attachChild(ninja);
        
        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
	
//        mAniControl = ninja.getControl(AnimControl.class);
//        mAniControl.addListener(this);
//        mAniChannel = mAniControl.createChannel();
        // show animation from beginning
//        mAniChannel.setAnim("Walk");
//        mAniChannel.setLoopMode(LoopMode.Loop);
//        mAniChannel.setSpeed(1f);
        
        ninja.setCullHint(CullHint.Always);
	}
	
	private float newX, newY, newZ;
	private float mX, mY, mZ;
	private boolean rotationMove = false;
	private boolean gpsMove = false;
	public static int mode;
	
	MotionPath path = new MotionPath();
	MotionEvent motionControl;
	float newXAnimation = 0.0f;
	
	public void rotate(float x, float y, float z) {
		newX = x;
		newY = y;
		newZ = z;
		newPosition = true;
	}
	
	public void move(float x, float y, float z) {
		mX = x;
		mY = y;
		mZ = z;
		
		rotationMove = true;
	}
		
	public void moveByPart(float x, boolean add) {
		if(ninja != null) {
			Vector3f currentTranslation = ninja.getLocalTranslation();
			Log.d("ANIM", "old: " + currentTranslation.x);
			
			if(add) {
				if(currentTranslation.x < 0 && x < 0) {
					mX = currentTranslation.x - x;
				} else if(currentTranslation.x < 0 && x > 0) {
					mX = currentTranslation.x + x;
				} else if(currentTranslation.x > 0 && x < 0) {
					mX = currentTranslation.x - x;
				} else if(currentTranslation.x > 0 && x > 0) {
					mX = currentTranslation.x + x;
				} else if(currentTranslation.x == 0 && x < 0) {
					mX = currentTranslation.x - x;
				} else if(currentTranslation.x == 0 && x > 0) {
					mX = currentTranslation.x + x;
				}
			} else {
				if(currentTranslation.x < 0 && x < 0) {
					mX = currentTranslation.x + x;
				} else if(currentTranslation.x < 0 && x > 0) {
					mX = currentTranslation.x - x;
				} else if(currentTranslation.x > 0 && x < 0) {
					mX = currentTranslation.x + x;
				} else if(currentTranslation.x > 0 && x > 0) {
					mX = currentTranslation.x - x;
				} else if(currentTranslation.x == 0 && x < 0) {
					mX = currentTranslation.x + x;
				} else if(currentTranslation.x == 0 && x > 0) {
					mX = currentTranslation.x - x;
				}
			}
			
			Log.d("ANIM", "new: " + mX + " | " + x);
			Log.d("ANIM", "add: " + add);
			rotationMove = true;
		}
	}
	
	public void startCinematic(float x) {
		if(ninja != null) {
			Vector3f currentTranslation = ninja.getLocalTranslation();
			newXAnimation = x;
			path = new MotionPath();
			path.addWayPoint(new Vector3f(currentTranslation.x, -2.5f, currentTranslation.z));
			path.addWayPoint(new Vector3f(x, -2.5f, currentTranslation.z));
			
			motionControl = new MotionEvent(ninja, path);
			motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
	        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
	        motionControl.setInitialDuration(4f);
	        motionControl.setSpeed(2f);    
	        
	        path.addListener(new MotionPathListener() {

	            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
	                if (path.getNbWayPoints() == wayPointIndex + 1) {
	                    Log.d("MOTION", control.getSpatial().getName() + "Finished!!! ");
	                    try {
		                    path.removeWayPoint(wayPointIndex);
	                    } catch(ArrayIndexOutOfBoundsException ex) {
	                    	Log.d("MOTION", ex.getMessage());
	                    }
	            		moveX(newXAnimation);
	                } else {
	                    Log.d("MOTION", control.getSpatial().getName() + " Reached way point " + wayPointIndex);
	                }
	            }
	        });
	        
	        motionControl.play();
		}
	}
	
	public void startCinematic(float x, float initialDuration, float speed) {
		if(ninja != null) {
			Vector3f currentTranslation = ninja.getLocalTranslation();
			newXAnimation = x;
			path = new MotionPath();
			if(GameStatus.useDistance) {
				path.addWayPoint(new Vector3f(currentTranslation.x, -2.5f, (mNinjaPosition.z) * -3));
				path.addWayPoint(new Vector3f(x, -2.5f, (mNinjaPosition.z) * -3));
			} else {
				path.addWayPoint(new Vector3f(currentTranslation.x, -2.5f, 0));
				path.addWayPoint(new Vector3f(x, -2.5f, 0));
			}
			
			motionControl = new MotionEvent(ninja, path);
			motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
	        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
	        motionControl.setInitialDuration(initialDuration);
	        motionControl.setSpeed(speed);    
	        
	        path.addListener(new MotionPathListener() {

	            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
	                if (path.getNbWayPoints() == wayPointIndex + 1) {
	                    Log.d("MOTION", control.getSpatial().getName() + "Finished!!! ");
	                    try {
		                    path.removeWayPoint(wayPointIndex);
	                    } catch(ArrayIndexOutOfBoundsException ex) {
	                    	Log.d("MOTION", ex.getMessage());
	                    }
	            		moveX(newXAnimation);
	                } else {
	                    Log.d("MOTION", control.getSpatial().getName() + " Reached way point " + wayPointIndex);
	                }
	            }
	        });
	        
	        motionControl.play();
		}
	}
	
	public void moveX(float x) {
		mX = x;
		
		rotationMove = true;
	}
	
	public void moveOneAxis(float value, String axis) {
		if(axis.equals("X")) {
			moveOnlyX(value);
		} else if(axis.equals("Y")) {
			moveOnlyY(value);
		} else if(axis.equals("Z")) {
			moveOnlyZ(value);
		}
		
		rotationMove = true;
	}
	
	public void moveOnlyX(float x) {
		Vector3f currentTranslation = ninja.getLocalTranslation();
		mX = x;
		mY = currentTranslation.y;
		mZ = currentTranslation.z;
	}
	
	public void moveOnlyY(float y) {
		Vector3f currentTranslation = ninja.getLocalTranslation();
		mX = currentTranslation.x;
		mY = y;
		mZ = currentTranslation.z;
	}
	
	public void moveOnlyZ(float z) {
		Vector3f currentTranslation = ninja.getLocalTranslation();
		mX = currentTranslation.x;
		mY = currentTranslation.y;
		mZ = z;
	}

	
	public void rotateCamera(float x, float y, float z) {
		ninja.rotate(x, y, z);
	}
	
	public void initForegroundCamera(float fovY) {

		Camera fgCam = new Camera(settings.getWidth(), settings.getHeight());
		fgCam.setLocation(new Vector3f(0f, 0f, 10f));
		fgCam.setAxes(new Vector3f(-1f,0f,0f), new Vector3f(0f,1f,0f), new Vector3f(0f,0f,-1f));
		
		float phiDeg = 10;
		float phi = phiDeg / 180 * FastMath.PI; 
		Quaternion mRotQ = new Quaternion();
		
		fgCam.setFrustumPerspective(fovY,  settings.getWidth()/settings.getHeight(), 1, 1000);
		ViewPort fgVP = renderManager.createMainView("ForegroundView", fgCam);
		fgVP.attachScene(rootNode);
		fgVP.setClearFlags(false, true, false);
		fgVP.setBackgroundColor(ColorRGBA.Blue);
	}

	 public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
		 // unused
	  }

	 public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
	    // unused
	  }
	 
	// This method retrieves the preview images from the Android world and puts
	// them into a JME image.
	public void setVideoBGTexture(final Image image) {
		if (!mSceneInitialized) {
			return;
		}
		mCameraImage = image;
		mNewCameraFrameAvailable = true;
	}
	

	
    private void WSG84toECEF(Location loc, Vector3f position) {
    	
    	double WGS84_A=6378137.0;           // WGS 84 semi-major axis constant in meters

    	double WGS84_E=0.081819190842622;   // WGS 84 eccentricity

    	double lat=(float) Math.toRadians(loc.getLatitude());
    	double lon=(float) Math.toRadians(loc.getLongitude());
    	
    	double clat=Math.cos(lat);
    	double slat=Math.sin(lat);
    	double clon=Math.cos(lon);
    	double slon=Math.sin(lon);

    	double N=WGS84_A / Math.sqrt(1.0 - WGS84_E * WGS84_E * slat * slat);

    	double x = N*clat*clon;
    	double y = N*clat*slon;
    	double z = (N * (1.0 - WGS84_E * WGS84_E)) * slat;
        
        position.set((float)x,(float)y,(float)z);
    }
    
    private void ECEFtoENU(Location loc, Vector3f cameraPosition, Vector3f poiPosition, Vector3f enuPOIPosition) {
    	double lat=(float) Math.toRadians(loc.getLatitude());
    	double lon=(float) Math.toRadians(loc.getLongitude());
    	
    	double clat=Math.cos(lat);
    	double slat=Math.sin(lat);
    	double clon=Math.cos(lon);
    	double slon=Math.sin(lon);

        double dx = cameraPosition.x - poiPosition.x;

        double dy = cameraPosition.y - poiPosition.y;

        double dz = cameraPosition.z - poiPosition.z;

        double e = -slon*dx  + clon*dy;

        double n = -slat*clon*dx - slat*slon*dy + clat*dz;

        double u = clat*clon*dx + clat*slon*dy + slat*dz;
        
        enuPOIPosition.set((float)e,(float)n,(float)u);
    }
    
	public String setUserLocation(Location location, Location locationNinja) {
		if (!mSceneInitialized) {
			return null;
		}
		WSG84toECEF(location, mUserPosition);
		
		Vector3f ECEFNinja=new Vector3f();
		Vector3f ENUNinja=new Vector3f();
		
		if(location == null) {
			return null;
		}

		WSG84toECEF(locationNinja, ECEFNinja);

		ECEFtoENU(locationNinja,mUserPosition,ECEFNinja,ENUNinja);
		//x=east,y=north,z=up
		mNinjaPosition.set(ENUNinja.x,0,ENUNinja.y);
		
		gpsMove = true;
		mode = 1;
		
		return mNinjaPosition.toString();
	}

	@Override
	public void simpleUpdate(float tpf) {
		if (mNewCameraFrameAvailable) {
			mCameraTexture.setImage(mCameraImage);
			mvideoBGMat.setTexture("ColorMap", mCameraTexture);
		}

//		if(newPosition && ninja != null) {
//			ninja.rotate((float)Math.toRadians(newX), (float)Math.toRadians(newY), (float)Math.toRadians(newZ));
//			newPosition = false;
//		}

		if((rotationMove && ninja != null) || (gpsMove && ninja != null)) {
			float newX = 0f;
			if(mNinjaPosition.x > this.oldX) {
				newX = this.oldX + mNinjaPosition.x;
			} else if(mNinjaPosition.x == this.oldX) {
				newX = mNinjaPosition.x;
			} else {
				newX = this.oldX - mNinjaPosition.x;
			}
			this.oldX = mNinjaPosition.x;
			if(GameStatus.useDistance) {
				ninja.setLocalTranslation(mX, -2.5f,(mNinjaPosition.z) * -3);
			} else {
				ninja.setLocalTranslation(mX, -2.5f, -35.0f);
			}

			if(gpsMove) {
				gpsMove = false;
			}
			if(rotationMove) {
				rotationMove = false;
 			}
		}
				
		mVideoBGGeom.updateLogicalState(tpf);
		mVideoBGGeom.updateGeometricState();
	}

	@Override
	public void simpleRender(RenderManager rm) {
	    // unused
	}
	
	public void toogleAnimation(boolean switcher) {
		if(this.mAniControl != null) {
			this.mAniControl.setEnabled(switcher);
		}
	}
	
	public void toogleObject(boolean show) {
		if(ninja != null) {
			if(show) {
				ninja.setCullHint(CullHint.Never);
			} else {
				ninja.setCullHint(CullHint.Always);
			}
		}
	}
}
