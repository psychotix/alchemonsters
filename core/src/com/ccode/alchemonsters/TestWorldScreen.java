package com.ccode.alchemonsters;

import java.util.Stack;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ccode.alchemonsters.entity.BodyComponent;
import com.ccode.alchemonsters.entity.CollisionComponent;
import com.ccode.alchemonsters.entity.CollisionSystem;
import com.ccode.alchemonsters.entity.Mappers;
import com.ccode.alchemonsters.entity.PhysicsSystem;
import com.ccode.alchemonsters.entity.TransformComponent;
import com.ccode.alchemonsters.entity.TypeComponent;
import com.ccode.alchemonsters.entity.TypeComponent.Type;
import com.ccode.alchemonsters.entity.WarpComponent;

public class TestWorldScreen implements Screen, InputProcessor, ContactListener {
	
	private AlchemonstersGame game;
	
	private TmxMapLoader mapLoader = new TmxMapLoader();
	
	private Stack<MapInstance> instanceStack = new Stack<>();
	private MapInstance activeInstance;
	
	//Active instance references
	private Body pBody;
	private int mapWidth;
	private int mapHeight;
	private int tileWidth;
	private int tileHeight;

	private Box2DDebugRenderer collisionDebug = new Box2DDebugRenderer();
	
	//Camera control
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector3 current = new Vector3();
	private Vector3 last = new Vector3(-1, -1, -1);
	private Vector3 delta = new Vector3();
	
	private boolean renderDebug = false;
	private boolean followCamera = true;
	
	//Rendering
	private ShapeRenderer shapes = new ShapeRenderer();
	private BitmapFont font = new BitmapFont();
	private Vector3 textPos = new Vector3();
	private Sprite player;
	
	//Player movement
	private float pv = 128;
	
	//Mouse tracking
	private Vector2 mouse = new Vector2();
	private Vector3 mouseWorld = new Vector3();
	
	//fps display
	float fps = 0;
	float fpsTime = 0.25f;
	float fpsTimer = fpsTime;
	
	public TestWorldScreen(AlchemonstersGame game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		camera.update();
		
		viewport = new FitViewport(640, 640 * (h / w), camera);
		
		Gdx.input.setInputProcessor(this);
	
		player = game.assetManager.get("sprites_packed/packed.atlas", TextureAtlas.class).createSprite("player");
		font.setColor(Color.YELLOW);
		
		switchToMap("city");
		
	}

	@Override
	public void render(float delta) {
		
		float vy = 0;
		float vx = 0;
		
		//Update
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			vy += pv;
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			vy -= pv;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			vx -= pv;
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			vx += pv;
		}
		
		pBody.setLinearVelocity(vx, vy);
		
		activeInstance.entityEngine.update(delta);
		
		if(followCamera) {
			camera.position.set(pBody.getPosition(), 0);
			correctCamera();
		}
		
		if((fpsTimer -= delta) < 0) {
			fps = (float) (1.0 / Gdx.graphics.getDeltaTime());
			fpsTimer += fpsTime;
		}
		
		//Draw
		Gdx.gl.glClearColor(0f, 0.2f, 0.5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		activeInstance.renderer.setView(camera);
		activeInstance.renderer.render();
		
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();		
		
		game.batch.draw(player, pBody.getPosition().x, pBody.getPosition().y);
		
		camera.unproject(textPos.set(5f, 5f, 0f));		
		font.draw(game.batch, String.format("%.1f", fps), textPos.x, textPos.y);
		
		game.batch.end();
		
		if(renderDebug) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapes.setProjectionMatrix(camera.combined);
			for(MapLayer l : activeInstance.map.getLayers()) {
				for(MapObject o : l.getObjects()) {
					if(o instanceof RectangleMapObject) {
						RectangleMapObject rectObj = (RectangleMapObject) o;
						Rectangle rect = rectObj.getRectangle();
						
						if(rect.contains(mouseWorld.x, mouseWorld.y)) {
							shapes.setColor(0, 0, 0.5f, 0.25f);
							shapes.begin(ShapeType.Filled);
							shapes.rect(rect.x, rect.y, rect.width, rect.height);
							shapes.end();
						}					
					}
				}
			}
			Gdx.gl.glDisable(GL20.GL_BLEND);
			
			collisionDebug.render(activeInstance.boxWorld, camera.combined);
		}
		
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		
	}

	/**
	 * Input processing
	 */
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode) {
		
		case Keys.F11:
			if(Gdx.graphics.isFullscreen()) {
				DisplayMode mode = Gdx.graphics.getDisplayMode();
				Gdx.graphics.setWindowedMode(mode.width, mode.height);
			}
			else {
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			}
			break;
			
		case Keys.F1:
			renderDebug = !renderDebug;
			break;
			
		case Keys.F2:
			followCamera = !followCamera;
			break;
		
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		last.set(-1, -1, -1);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!followCamera) {
			camera.unproject(current.set(screenX, screenY, 0));
			if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
				camera.unproject(delta.set(last.x, last.y, 0));
				delta.sub(current);
				camera.position.add(delta.x, delta.y, 0);
			}
			last.set(screenX, screenY, 0);
			
			correctCamera();
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouse.set(screenX, screenY);
		camera.unproject(mouseWorld.set(screenX, screenY, 0));
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	/**
	 * Camera methods
	 */
	private void correctCamera() {
		//Correct camera position so that it is locked within the map bounds.
		//If the map is smaller than the camera size, correct it so that none of the
		//map is ever off the camera.
		Rectangle cameraBounds = new Rectangle(camera.position.x - camera.viewportWidth / 2, 
											   camera.position.y - camera.viewportHeight / 2, 
											   camera.viewportWidth, 
											   camera.viewportHeight);
		Rectangle mapBounds = new Rectangle(0, 0, mapWidth * tileWidth, mapHeight * tileHeight);
		if(!mapBounds.contains(cameraBounds)) {
			float tx = 0;
			float ty = 0;
			
			if(cameraBounds.width > mapBounds.width) {
				if(mapBounds.x < cameraBounds.x) {
					tx = mapBounds.x - cameraBounds.x;
				}
				else if(mapBounds.width > cameraBounds.x + cameraBounds.width) {
					tx = mapBounds.width - (cameraBounds.x + cameraBounds.width);
				}
			}
			else if(cameraBounds.x < 0) { 
				tx = -cameraBounds.x;
			}
			else if(cameraBounds.x + cameraBounds.width > mapBounds.width) {
				tx = -((cameraBounds.x + cameraBounds.width) - mapBounds.width);
			}
			
			if(cameraBounds.height > mapBounds.height) {
				if(mapBounds.y < cameraBounds.y) {
					ty = mapBounds.y - cameraBounds.y;
				}
				else if(mapBounds.height > cameraBounds.y + cameraBounds.height) {
					ty = mapBounds.width - (cameraBounds.y + cameraBounds.height);
				}
			}
			else if(cameraBounds.y < 0) {
				ty = -cameraBounds.y;
			}
			else if(cameraBounds.y + cameraBounds.height > mapBounds.height) {
				ty = -((cameraBounds.y + cameraBounds.height) - mapBounds.height);
			}
			
			camera.translate(tx, ty);
		}
	}
	
	/**
	 * World loading functions
	 */
	public void switchToMap(String mapName) {
		switchToMap(mapName, "main");
	}
	
	public void switchToMap(String mapName, String spawnId) {
		//Check if the new instance is just moving back up the chain
		if(instanceStack.size() > 0 && instanceStack.peek().mapName.equals(mapName)) {
			setActiveInstance(instanceStack.pop());
			return;
		}
		
		//Otherwise, load the new instance
		MapInstance newInstance = loadMapInstance(mapName, spawnId);
		Object isRootObj = newInstance.map.getProperties().get("isRoot");
		if(isRootObj != null && isRootObj instanceof Boolean) {
			boolean isRoot = (boolean) isRootObj;
			if(isRoot) {
				
				//New map is a root map
				setActiveInstance(newInstance);
				instanceStack.clear();
				
			}
			else {
			
				//New map is not a root map
				if(activeInstance != null) {
					instanceStack.push(activeInstance);
				}
				setActiveInstance(newInstance);
				
			}
		}
		else {
			//TODO: isRoot variable does not exist in the map or not not of boolean type
		}
	}
	
	private void setActiveInstance(MapInstance instance) {
		activeInstance = instance;
		MapProperties props = activeInstance.map.getProperties();
		
		//Update map property values
		mapWidth = props.get("width", Integer.class);
		mapHeight = props.get("height", Integer.class);
		tileWidth = props.get("tilewidth", Integer.class);
		tileHeight = props.get("tileheight", Integer.class);
		
		//Update our reference to the player body
		pBody = activeInstance.playerBody;
	}
	
	private MapInstance loadMapInstance(String mapName, String spawnId) {
		//Load the map
		TiledMap map = mapLoader.load(String.format("maps/%s.tmx", mapName));
		OrthoCachedTiledMapRenderer renderer = new OrthoCachedTiledMapSpriteRenderer(map);
		
		//Create box2d world
		World boxWorld = new World(new Vector2(0, 0), true);
		boxWorld.setContactListener(this);
		
		//Create the entity engine
		Engine entityEngine = new Engine();
		entityEngine.addSystem(new PhysicsSystem(boxWorld));
		entityEngine.addSystem(new CollisionSystem(this));
		
		//Add the collision boxes to the world/entity system
		if(map.getLayers().get("collision") != null) {
			for(MapObject o : map.getLayers().get("collision").getObjects()) {
				//TODO: handle other shape collision boxes
				if(o instanceof RectangleMapObject) {
					
					Entity collisionEntity = new Entity();
					
					RectangleMapObject col = (RectangleMapObject) o;
					Rectangle colRect = col.getRectangle();
							
					BodyDef colBodyDef = new BodyDef();
					colBodyDef.position.set(colRect.x + colRect.width / 2, colRect.y + colRect.height / 2);
					
					Body colBody = boxWorld.createBody(colBodyDef);
					PolygonShape colBox = new PolygonShape();
					colBox.setAsBox(colRect.width / 2, colRect.height / 2);
					colBody.createFixture(colBox, 0.0f);
					colBox.dispose();
					
					collisionEntity.add(new BodyComponent(colBody));
					collisionEntity.add(new TransformComponent());
					collisionEntity.add(new TypeComponent(Type.COLLISION_BOX));
					entityEngine.addEntity(collisionEntity);
					
					colBody.setUserData(collisionEntity);
					
				}
			}
		}
		
		//Add warp area boxes to the world/entity system
		if(map.getLayers().get("warps") != null) {
			for(MapObject o : map.getLayers().get("warps").getObjects()) {
				//TODO: handle other shape collision boxes
				if(o instanceof RectangleMapObject) {
					
					MapProperties props = o.getProperties();
					if(!props.containsKey("connectedMap")) {
						System.err.printf("[Error] Unable to load warp area in %s (no connectedMap property.)\n", mapName);
					}
					
					String connectedMap = (String) props.get("connectedMap");
					String connectedSpawn = props.containsKey("connectedSpawn") ? (String) props.get("connectedSpawn") : "main";
					
					Entity warpEntity = new Entity();
					
					RectangleMapObject warp = (RectangleMapObject) o;
					Rectangle warpRect = warp.getRectangle();
					
					BodyDef warpBodyDef = new BodyDef();
					warpBodyDef.position.set(warpRect.x + warpRect.width / 2, warpRect.y + warpRect.height / 2);
					
					Body warpBody = boxWorld.createBody(warpBodyDef);
					PolygonShape warpBox = new PolygonShape();
					warpBox.setAsBox(warpRect.width / 2, warpRect.height / 2);
					FixtureDef warpFixDef = new FixtureDef();
					warpFixDef.isSensor = true;
					warpFixDef.shape = warpBox;
					warpBody.createFixture(warpFixDef);
					
					warpEntity.add(new BodyComponent(warpBody));
					warpEntity.add(new TransformComponent());
					warpEntity.add(new TypeComponent(Type.WARP_AREA));
					warpEntity.add(new WarpComponent(connectedMap, connectedSpawn));
					entityEngine.addEntity(warpEntity);
					
					warpBody.setUserData(warpEntity);
					
				}
			}
		}
		
		//Add player at spawn (to box2d world/entity engine)		
		Vector2 playerSpawn = new Vector2();
		boolean spawnFound = false;
		if(map.getLayers().get("spawn") != null) {
			MapLayer spawns = map.getLayers().get("spawn");
			for(MapObject spawn : spawns.getObjects()) {
				if(spawn instanceof RectangleMapObject && spawn.getProperties().containsKey("id") && spawn.getProperties().get("id").equals(spawnId)) {
					Rectangle spawnRect = ((RectangleMapObject) spawn).getRectangle();
					playerSpawn.set(spawnRect.x, spawnRect.y);
					spawnFound = true;
					break;
				}
			}
		}
		if(!spawnFound) {
			System.err.printf("[Warning] No spawn point found for map %s.\n", mapName);
		}
		
		Entity playerEntity = new Entity();
		
		BodyDef pBodyDef = new BodyDef();
		pBodyDef.type = BodyType.DynamicBody;
		pBodyDef.position.set(playerSpawn);
		
		Body pBody = boxWorld.createBody(pBodyDef);
		CircleShape pShape = new CircleShape();
		pShape.setRadius(16);
		pShape.setPosition(new Vector2(16, 16));
		pBody.createFixture(pShape, 0.0f);
		pShape.dispose();
		
		playerEntity.add(new BodyComponent(pBody));
		playerEntity.add(new TransformComponent());
		playerEntity.add(new CollisionComponent());
		playerEntity.add(new TypeComponent(Type.UNIT));
		entityEngine.addEntity(playerEntity);
		
		pBody.setUserData(playerEntity);
		
		//Combine into map instance
		return new MapInstance(mapName, map, renderer, boxWorld, pBody, entityEngine);
	}
	
	/**
	 * Contact listener methods for CollisionComponents
	 */
	@Override
	public void beginContact(Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();	
		
		if(a.getBody().getUserData() instanceof Entity && b.getBody().getUserData() instanceof Entity) {
			Entity ea = (Entity) a.getBody().getUserData();
			Entity eb = (Entity) b.getBody().getUserData();
			
			//Look for collision components
			CollisionComponent colA = Mappers.collisionComponent.get(ea);
			CollisionComponent colB = Mappers.collisionComponent.get(eb);
			
			//If the components were present, update the collided entity.
			if(colA != null) {
				colA.collisions.add(eb);
			}
			if(colB != null) {
				colB.collisions.add(ea);
			}
		}
	}

	@Override public void endContact(Contact contact) {}
	@Override public void preSolve(Contact contact, Manifold oldManifold) {}
	@Override public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	private class MapInstance {
		
		String mapName;
		TiledMap map;
		TiledMapRenderer renderer;
		World boxWorld;
		Body playerBody;
		Engine entityEngine;
		
		MapInstance(String mapName, TiledMap map, TiledMapRenderer renderer, World boxWorld, Body playerBody, Engine entityEngine) {
			this.mapName = mapName;
			this.map = map;
			this.renderer = renderer;
			this.boxWorld = boxWorld;
			this.playerBody = playerBody;
			this.entityEngine = entityEngine;
		}
		
	}

}
