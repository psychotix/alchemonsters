package com.ccode.alchemonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ccode.alchemonsters.creature.CreatureDictionary;

public class AlchemonstersGame extends Game {
	
	//Game screens
	public final MainMenuScreen mainMenu;
	private boolean isExitRequested = false;
	
	//Rendering
	public SpriteBatch batch;
	
	public AlchemonstersGame() {
		
		//Initialize Game Screens
		mainMenu = new MainMenuScreen(this);
		
	}
	
	@Override
	public void create () {
		
		//Load Creatures
		CreatureDictionary.initAndLoad();
		
		//Rendering setup
		batch = new SpriteBatch();
		UI.initAndLoad();
		
		//Set initial screen
		//setScreen(mainMenu);
		
		setScreen(new TestWorldScreen());
		
	}
	
	@Override
	public void render() {
		if(isExitRequested) {
			
			setScreen(null);
			dispose();
			
			if(UI.isInitialized()) {
				UI.dispose();
			}
			
			System.exit(0);
			
		}
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
	
	public void requestExit() {
		isExitRequested = true;
	}
	
}
