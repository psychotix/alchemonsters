package com.ccode.alchemonsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.Move;
import com.ccode.alchemonsters.combat.MoveDictionary;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.CreatureBase;
import com.ccode.alchemonsters.creature.CreatureDictionary;
import com.ccode.alchemonsters.creature.CreatureNature;
import com.ccode.alchemonsters.creature.CreatureStats;
import com.ccode.alchemonsters.creature.StatType;

public class TestCombatScreen extends InputAdapter implements Screen {

	private final AlchemonstersGame game;
	
	//Overall Frame
	private Label teamATitle;
	private CreatureTeam teamA;
	private Label teamA1;
	private TextButton teamA1Edit;
	private Label teamA2;
	private TextButton teamA2Edit;
	private Label teamA3;
	private TextButton teamA3Edit;
	private Label teamA4;
	private TextButton teamA4Edit;
	
	private Label teamBTitle;
	private CreatureTeam teamB;
	private Label teamB1;
	private TextButton teamB1Edit;
	private Label teamB2;
	private TextButton teamB2Edit;
	private Label teamB3;
	private TextButton teamB3Edit;
	private Label teamB4;
	private TextButton teamB4Edit;
	
	//Creature Selector/Editor Frame
	private Window teamEditWindow;
	
	private SelectBox<String> creatureSelectBox;
	private Slider baseHealthSlider;
	private Label baseHealthDisplay;
	private Slider baseManaSlider;
	private Label baseManaDisplay;
	
	private TextField vitaeEdit;
	private TextField focusEdit;
	private TextField magicATKEdit;
	private TextField magicDEFEdit;
	private TextField physATKEdit;
	private TextField physDEFEdit;
	private TextField speedEdit;
	
	private SelectBox<StatType> positiveNature;
	private SelectBox<StatType> negativeNature;
	
	private List<String> movesActiveList;
	
	private CreatureTeam currentTeam;
	private int currentId;
	
	//Moves Selection Window
	private Window moveSelectWindow;
	private List<String> movesAvailableList;
	
	//TODO: Combat window UI
	
	//Scene2d ui
	private Stage ui;
	private Table table;
	
	public TestCombatScreen(AlchemonstersGame game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		teamA = new CreatureTeam();
		teamB = new CreatureTeam();
		
		ui = new Stage(new ScreenViewport(), game.batch);
		table = new Table(UI.DEFAULT_SKIN);
		table.setFillParent(true);
		ui.addActor(table);
		table.bottom();
		
		Window teamBuilder = new Window("Team Setup", UI.DEFAULT_SKIN);
		teamBuilder.top();
		teamBuilder.setMovable(false);
		table.add(teamBuilder).expand().left().top().fillY().prefWidth(300);
		
		teamATitle = new Label("Team A", UI.DEFAULT_SKIN);
		teamBuilder.add(teamATitle).center().padTop(10).left();
		teamBuilder.row();
		
		teamA1 = new CreatureNameLabel(teamA, 0);
		teamA1Edit = new EditButton(teamA, 0);
		teamBuilder.add(teamA1).expandX().left();
		teamBuilder.add(teamA1Edit).right().fillX();
		teamBuilder.row();
		
		teamA2 = new CreatureNameLabel(teamA, 1);
		teamA2Edit = new EditButton(teamA, 1);
		teamBuilder.add(teamA2).left();
		teamBuilder.add(teamA2Edit).right().fillX();
		teamBuilder.row();
		
		teamA3 = new CreatureNameLabel(teamA, 2);
		teamA3Edit = new EditButton(teamA, 2);
		teamBuilder.add(teamA3).expandX().left();
		teamBuilder.add(teamA3Edit).right().fillX();
		teamBuilder.row();
		
		teamA4 = new CreatureNameLabel(teamA, 3);
		teamA4Edit = new EditButton(teamA, 3);
		teamBuilder.add(teamA4).expandX().left();
		teamBuilder.add(teamA4Edit).right().fillX();
		teamBuilder.row();
		
		teamBuilder.row();
		
		teamBTitle = new Label("Team B", UI.DEFAULT_SKIN);
		teamBuilder.add(teamBTitle).center().padTop(30).left();
		teamBuilder.row();
		
		teamB1 = new CreatureNameLabel(teamB, 0);
		teamB1Edit = new EditButton(teamB, 0);
		teamBuilder.add(teamB1).expandX().left();
		teamBuilder.add(teamB1Edit).right().fillX();
		teamBuilder.row();
		
		teamB2 = new CreatureNameLabel(teamB, 1);
		teamB2Edit = new EditButton(teamB, 1);
		teamBuilder.add(teamB2).left();
		teamBuilder.add(teamB2Edit).right().fillX();
		teamBuilder.row();
		
		teamB3 = new CreatureNameLabel(teamB, 2);
		teamB3Edit = new EditButton(teamB, 2);
		teamBuilder.add(teamB3).expandX().left();
		teamBuilder.add(teamB3Edit).right().fillX();
		teamBuilder.row();
		
		teamB4 = new CreatureNameLabel(teamB, 3);
		teamB4Edit = new EditButton(teamB, 3);
		teamBuilder.add(teamB4).expandX().left();
		teamBuilder.add(teamB4Edit).right().fillX();
		teamBuilder.row();
		
		//TEAM CREATURE EDIT WINDOW
		teamEditWindow = new Window("Edit Team", UI.DEFAULT_SKIN);
		teamEditWindow.setVisible(false);
		
		Label creatureSelectText = new Label("Creature:", UI.DEFAULT_SKIN);
		creatureSelectBox = new SelectBox<>(UI.DEFAULT_SKIN);
		Array<String> items = new Array<>();
		for(String id : CreatureDictionary.getAvailableCreatureIDs()) {
			items.add(id);
		}
		creatureSelectBox.setItems(items);
		creatureSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				CreatureBase b = CreatureDictionary.getBase(creatureSelectBox.getSelected());
				baseHealthSlider.setRange(b.minBaseHealth, b.maxBaseHealth);
				baseManaSlider.setRange(b.minBaseMana, b.maxBaseMana);
			}
		});
		
		teamEditWindow.add(creatureSelectText, creatureSelectBox);
		teamEditWindow.row();
		
		Label baseHealthLabel = new Label("Base Health", UI.DEFAULT_SKIN);
		baseHealthSlider = new Slider(0, 10, 1, false, UI.DEFAULT_SKIN);
		baseHealthDisplay = new Label("", UI.DEFAULT_SKIN) {
			@Override
			public void act(float delta) {
				super.act(delta);
				setText((int) baseHealthSlider.getValue());
			}
		};
		
		teamEditWindow.add(baseHealthLabel, baseHealthSlider, baseHealthDisplay);
		teamEditWindow.row();
		
		Label baseManaLabel = new Label("Base Mana", UI.DEFAULT_SKIN);
		baseManaSlider = new Slider(0, 10, 1, false, UI.DEFAULT_SKIN);
		baseManaDisplay = new Label("", UI.DEFAULT_SKIN) {
			@Override
			public void act(float delta) {
				super.act(delta);
				setText((int) baseManaSlider.getValue());
			}
		};
		
		teamEditWindow.add(baseManaLabel, baseManaSlider, baseManaDisplay);
		teamEditWindow.row();
		
		Label vitaeLabel = new Label("Vitae: ", UI.DEFAULT_SKIN);
		vitaeEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label focusLabel = new Label("Focus: ", UI.DEFAULT_SKIN);
		focusEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label magicATKLabel = new Label("MagicATK: ", UI.DEFAULT_SKIN);
		magicATKEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label magicDEFLabel = new Label("MagicDEF: ", UI.DEFAULT_SKIN);
		magicDEFEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label physATKLabel = new Label("PhysATK: ", UI.DEFAULT_SKIN);
		physATKEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label physDEFLabel = new Label("PhysDEF: ", UI.DEFAULT_SKIN);
		physDEFEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label speedLabel = new Label("Speed: ", UI.DEFAULT_SKIN);
		speedEdit = new TextField("16", UI.DEFAULT_SKIN);
		
		teamEditWindow.add(vitaeLabel, vitaeEdit);
		teamEditWindow.add(focusLabel, focusEdit);
		teamEditWindow.row();
		teamEditWindow.add(magicATKLabel, magicATKEdit);
		teamEditWindow.add(magicDEFLabel, magicDEFEdit);
		teamEditWindow.row();
		teamEditWindow.add(physATKLabel, physATKEdit);
		teamEditWindow.add(physDEFLabel, physDEFEdit);
		teamEditWindow.row();
		teamEditWindow.add(speedLabel, speedEdit);
		teamEditWindow.row();
		
		Label movesLabel = new Label("Available Moves", UI.DEFAULT_SKIN);
		movesActiveList = new List<>(UI.DEFAULT_SKIN);
		movesActiveList.setItems("Move A", "Move B", "Move C", "Move D", "Move E", "Move F", "Move G");
		ScrollPane movesPane = new ScrollPane(movesActiveList, UI.DEFAULT_SKIN);
		movesPane.setScrollbarsVisible(true);
		movesPane.setFadeScrollBars(false);
		
		Label positiveNatureLabel = new Label("Positive Nature Stat: ", UI.DEFAULT_SKIN);
		positiveNature = new SelectBox<>(UI.DEFAULT_SKIN);
		positiveNature.setItems(StatType.values());
		Label negativeNatureLabel = new Label("Negative Nature Stat: ", UI.DEFAULT_SKIN);
		negativeNature = new SelectBox<>(UI.DEFAULT_SKIN);
		negativeNature.setItems(StatType.values());

		teamEditWindow.add(positiveNatureLabel, positiveNature);
		teamEditWindow.row();
		teamEditWindow.add(negativeNatureLabel, negativeNature);
		teamEditWindow.row();
		
		Table moveButtons = new Table(UI.DEFAULT_SKIN);
		TextButton removeButton = new TextButton("Remove", UI.DEFAULT_SKIN);
		removeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(movesActiveList.getSelectedIndex() != -1) {
					Array<String> items = movesActiveList.getItems();
					items.removeIndex(movesActiveList.getSelectedIndex());
					movesActiveList.setItems(items);
				}
			}
		});
		TextButton addButton = new TextButton("Add", UI.DEFAULT_SKIN);
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				moveSelectWindow.setVisible(true);
				moveSelectWindow.toFront();
			}
		});
		moveButtons.add(removeButton);
		moveButtons.row();
		moveButtons.add(addButton);
		
		teamEditWindow.add(movesLabel);
		teamEditWindow.row();
		teamEditWindow.add(movesPane).prefHeight(100).prefWidth(Value.percentWidth(1f));
		teamEditWindow.add(moveButtons);
		teamEditWindow.row();
		
		TextButton editCancel = new TextButton("Cancel", UI.DEFAULT_SKIN);
		editCancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				teamEditWindow.setVisible(false);
			}
		});
		TextButton editAccept = new TextButton("Accept", UI.DEFAULT_SKIN);
		editAccept.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				acceptCreatureAdd();
				teamEditWindow.setVisible(false);
			}
		});
		teamEditWindow.add(editCancel, editAccept);
		
		teamEditWindow.pack();
		ui.addActor(teamEditWindow);
		
		
		//MOVES SELECT WINDOW INIT
		moveSelectWindow = new Window("Move Selection", UI.DEFAULT_SKIN);
		moveSelectWindow.setVisible(false);
		
		Label loadedMoves = new Label("Loaded Moves", UI.DEFAULT_SKIN);
		movesAvailableList = new List<>(UI.DEFAULT_SKIN);
		movesAvailableList.setItems(MoveDictionary.getLoadedMoveNames().toArray(new String[]{}));
		ScrollPane movesSelect = new ScrollPane(movesAvailableList, UI.DEFAULT_SKIN);
		movesSelect.setScrollbarsVisible(true);
		movesSelect.setFadeScrollBars(false);
		
		moveSelectWindow.add(loadedMoves);
		moveSelectWindow.row();
		moveSelectWindow.add(movesSelect).prefHeight(200).prefWidth(400);
		moveSelectWindow.row();
		
		TextButton movesCancel = new TextButton("Cancel", UI.DEFAULT_SKIN);
		movesCancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				moveSelectWindow.setVisible(false);
			}
		});
		TextButton movesAccept = new TextButton("Accept", UI.DEFAULT_SKIN);
		movesAccept.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String toAdd = movesAvailableList.getSelected();
				if(!movesActiveList.getItems().contains(toAdd, true)) {
					Array<String> items = movesActiveList.getItems();
					items.add(toAdd);
					movesActiveList.setItems(items);
					moveSelectWindow.setVisible(false);
				}
			}
		});
		Table movesButtons = new Table(UI.DEFAULT_SKIN);
		movesButtons.add(movesCancel, movesAccept);
		
		moveSelectWindow.add(movesButtons);
		
		moveSelectWindow.pack();
		ui.addActor(moveSelectWindow);
		
		InputMultiplexer multi = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(multi);
		
		//Quick fix to update the default baseMana and baseHealth slider ranges
		creatureSelectBox.setSelectedIndex(1);
		creatureSelectBox.setSelectedIndex(0);
		
	}
	
	private void acceptCreatureAdd() {
		try {
			CreatureBase base = CreatureDictionary.getBase(creatureSelectBox.getSelected());
			CreatureStats stats = new CreatureStats(
					Byte.parseByte(vitaeEdit.getText()), 
					Byte.parseByte(focusEdit.getText()), 
					Byte.parseByte(magicATKEdit.getText()), 
					Byte.parseByte(magicDEFEdit.getText()), 
					Byte.parseByte(physATKEdit.getText()), 
					Byte.parseByte(physDEFEdit.getText()), 
					Byte.parseByte(speedEdit.getText())
			);
			CreatureNature nature = new CreatureNature(positiveNature.getSelected(), negativeNature.getSelected());
			Creature c = new Creature(base, nature, stats);
			
			c.baseHealth = (int) baseHealthSlider.getValue();
			c.baseMana = (int) baseManaSlider.getValue();
			
			Array<Move> moves = new Array<>();
			for(String m : movesActiveList.getItems()) {
				moves.add(MoveDictionary.getMove(m));
			}
			c.moves = moves.toArray(Move.class);
			
			currentTeam.creatures[currentId] = c;
		}
		catch (NumberFormatException nfe) {
			System.out.println("Error retrieving stat values. Make sure that the inputs are integer numbers within the allowable range (0-31).");
			nfe.printStackTrace();
		}
	}
	
	private void displayCreatureEditWindow(CreatureTeam t, int id) {
		currentTeam = t;
		currentId = id;
		
		if(t.creatures[id] != null) {
			Creature c = t.creatures[id];
			CreatureBase b = c.base;
			creatureSelectBox.setSelected(b.id);
			
			baseHealthSlider.setRange(b.minBaseHealth, b.maxBaseHealth);
			baseHealthSlider.setValue(c.baseHealth);
			
			baseManaSlider.setRange(b.minBaseMana, b.maxBaseMana);
			baseManaSlider.setValue(c.baseMana);
			
			positiveNature.setSelected(c.nature.increased);
			negativeNature.setSelected(c.nature.decreased);
			
			vitaeEdit.setText(String.valueOf(c.stats.vitae));
			focusEdit.setText(String.valueOf(c.stats.focus));
			magicATKEdit.setText(String.valueOf(c.stats.magicATK));
			magicDEFEdit.setText(String.valueOf(c.stats.magicDEF));
			physATKEdit.setText(String.valueOf(c.stats.physATK));
			physDEFEdit.setText(String.valueOf(c.stats.physDEF));
			speedEdit.setText(String.valueOf(c.stats.speed));
			
			Array<String> activeMoves = new Array<>();
			for(Move m : c.moves) {
				activeMoves.add(m.name);
			}
			movesActiveList.setItems(activeMoves);
		}
		else {
			resetCreatureStatsDisplay();
		}
		
		teamEditWindow.setVisible(true);
	}
	
	private void resetCreatureStatsDisplay() {
		creatureSelectBox.setSelectedIndex(0);
		
		vitaeEdit.setText("16");
		focusEdit.setText("16");
		magicATKEdit.setText("16");
		magicDEFEdit.setText("16");
		physATKEdit.setText("16");
		physDEFEdit.setText("16");
		speedEdit.setText("16");
		
		movesActiveList.setItems();
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		ui.act(delta);
		ui.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		ui.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		ui.dispose();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.F1) {
			table.setDebug(!table.getDebug(), true);
			teamEditWindow.setDebug(!teamEditWindow.getDebug(), true);
		}
		else if(keycode == Keys.P) {
			Json json = new Json();
			json.setOutputType(OutputType.javascript);
			System.out.println(json.prettyPrint(currentTeam.creatures[currentId]));
		}
		else {
			return false;
		}
		return true;
	}
	
	private class EditButton extends TextButton {
		
		EditButton(CreatureTeam t, int id) {
			super("Edit / Add", UI.DEFAULT_SKIN);
			addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					displayCreatureEditWindow(t, id);
				}
			});
		}
		
	}
	
	private class CreatureNameLabel extends Label {
		
		CreatureTeam t;
		int id;
		
		CreatureNameLabel(CreatureTeam t, int id) {
			super("<empty>", UI.DEFAULT_SKIN);
			this.t = t;
			this.id = id;
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			if(t.creatures[id] == null) {
				setText("<empty>");
			}
			else if(!t.creatures[id].base.name.equals(getText())) {
				setText(t.creatures[id].base.name);
			}
		}
		
	}

}
