package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.messages.MCombatWeatherChanged;

public class MoveActionSetWeather implements MoveAction {

	/**
	 * The weather type that should be set to. 
	 */
	public WeatherType weather;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target,
			CreatureTeam targetTeam) {
		WeatherType old = context.battleground.weather;
		context.battleground.weather = weather;
		publish(new MCombatWeatherChanged(context, source, move.name, old, weather));
	}
	
}