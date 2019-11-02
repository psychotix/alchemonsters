package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.DynamicVariables;

public class BattleTeam {

	private int actives[];
	public final CreatureTeam team;
	public DynamicVariables variables = new DynamicVariables();
	
	public BattleTeam(CreatureTeam team, int activePositions) {
		this.team = team;
		actives = new int[activePositions];
		for(int i = 0; i < actives.length; ++i) {
			actives[i] = i;
		}
	}
	
	public Creature active(int position) {
		return team.creatures[actives[position]];
	}
	
	public int activeId(int position) {
		return actives[position];
	}
	
	public void setActive(int position, int id) {
		for(int i = 0; i < actives.length; ++i) {
			if(actives[i] == id) {
				//TODO: throw an error or something here - we should never switch to an already active mon
				System.err.println("Switched to already active mon!!!! This is bad :O");
				return;
			}
		}
		actives[position] = id;
	}
	
	public Creature get(int index) {
		return team.creatures[index];
	}
	
	public Creature[] creatures() {
		return team.creatures;
	}
	
	public void startCombat() {
		variables.clear();
		for(Creature c : team.creatures) {
			if(c != null) {
				c.startCombat();
			}
		}
	}
	
	public boolean isDefeated() {
		for(Creature c : team.creatures) {
			if(c != null && !c.isDead()) {
				return false;
			}
		}
		return true;
	}
	
}
