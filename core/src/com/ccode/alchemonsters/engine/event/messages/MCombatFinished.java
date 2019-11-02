package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatFinished extends Message {
	
	public static final String ID = "COMBAT_FINISHED";
	
	public final BattleContext context;
	public final BattleTeam won;
	public final BattleTeam lost;
	
	public MCombatFinished(BattleContext context, BattleTeam won, BattleTeam lost) {
		super(ID);
		this.context = context;
		this.won = won;
		this.lost = lost;
	}
	
}
