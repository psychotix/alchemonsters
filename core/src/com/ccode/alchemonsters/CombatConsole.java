package com.ccode.alchemonsters;

import java.util.LinkedList;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Subscriber;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentApplied;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentRemoved;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatStatBuffApplied;
import com.ccode.alchemonsters.engine.event.messages.MCombatStateChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;

public class CombatConsole extends ScrollPane implements Subscriber {

	private LinkedList<Message> messageQueue = new LinkedList<>();
	private TextArea consoleArea;
	
	public CombatConsole() {
		super(new TextArea("", UI.DEFAULT_SKIN));
		consoleArea = (TextArea) getActor();
		consoleArea.setDisabled(true);
		
		setScrollbarsVisible(true);   
		setFadeScrollBars(false);     
		setFlickScroll(false);        
		setForceScroll(false, true);  
		
		subscribe(MCombatDamageDealt.ID);
		subscribe(MCombatFinished.ID);
		subscribe(MCombatStarted.ID);
		subscribe(MCombatStateChanged.ID);
		subscribe(MCombatTeamActiveChanged.ID);
		subscribe(MCombatStatBuffApplied.ID);
		subscribe(MCombatAilmentApplied.ID);
		subscribe(MCombatAilmentRemoved.ID);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		Message m;
		while((m = messageQueue.poll()) != null) {
			 if(m instanceof MCombatDamageDealt) {
				 MCombatDamageDealt damage = (MCombatDamageDealt) m;
					String damageText;
					if(damage.isHit) {
						damageText = String.format("%s dealt %s %s damage to %s%s", 
								damage.source.personalName, 
								damage.amount, 
								damage.elementType, 
								damage.target.personalName, 
								damage.isCrit ? " (CRIT!)" : ".");
					}
					else {
						damageText = String.format("%s attacked with %s, but missed!", damage.source.personalName, damage.cause);
					}
					println(damageText);
			 }
			 else if(m instanceof MCombatStarted) {
				 println("Combat started!");
			 }
			 else if(m instanceof MCombatFinished) {
				 println("Combat completed.");
			 }
			 else if(m instanceof MCombatStateChanged) {
				 MCombatStateChanged full = (MCombatStateChanged) m;
				 switch(full.next) {
				 
				case BATTLE_PHASE_1:
					println("Entering Battle Phase 1...");
					break;
					
				case BATTLE_PHASE_2:
					break;
					
				case END_PHASE:
					println("Entering End Phase...");
					break;
					
				case MAIN_PHASE_1:
					println("Entering Main Phase 1...");
					break;
					
				case MAIN_PHASE_2:
					break;
					
				case ACTIVE_DEATH_SWAP:
					println("Entering Death Swap Phase...");
					break;
				 
				 }
			 }
			 else if(m instanceof MCombatTeamActiveChanged) {
				 MCombatTeamActiveChanged full = (MCombatTeamActiveChanged) m;
				 println(String.format("Switched from %s to %s.", 
						 full.team.creatures[full.prevActive].personalName, 
						 full.team.creatures[full.nextActive].personalName));
			 }
			 else if(m instanceof MCombatStatBuffApplied) {
				 MCombatStatBuffApplied full = (MCombatStatBuffApplied) m;
				 println(String.format("%s buffed %s's %s stat by %s.", 
						 full.source.personalName,
						 full.target.personalName,
						 full.statBuffed,
						 full.buffAmt));
			 }
			 else if(m instanceof MCombatAilmentApplied) {
				 MCombatAilmentApplied full = (MCombatAilmentApplied) m;
				 println(String.format("%s's %s applied %s to %s.", 
						 full.source.personalName,
						 full.cause,
						 full.ailmentName,
						 full.target.personalName));
			 }
			 else if(m instanceof MCombatAilmentRemoved) {
				 MCombatAilmentRemoved full = (MCombatAilmentRemoved) m;
				 println(String.format("%s's %s (caused by %s) was removed from %s.",
						 full.source.personalName,
						 full.ailmentName,
						 full.cause,
						 full.target.personalName));
			 }
		}
	}
	
	private void println(String s) {
		consoleArea.setText(s + "\n" + consoleArea.getText());
		consoleArea.setPrefRows(consoleArea.getLines());
		layout();
	}

	@Override
	public void handleMessage(Message currentMessage) {
		messageQueue.add(currentMessage);
	}
	
	public void clear() {
		consoleArea.setText("");
		consoleArea.setPrefRows(consoleArea.getLines());
		layout();
	}
	
}
