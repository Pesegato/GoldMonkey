package model.battlefield.army.components;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.abstractComps.FieldComp;
import model.battlefield.abstractComps.Hiker;
import model.battlefield.actors.ModelActor;
import model.battlefield.army.effects.EffectSource;
import model.battlefield.army.effects.EffectTarget;
import model.battlefield.army.tacticalAI.TacticalAI;
import model.battlefield.warfare.Faction;
import model.builders.MoverBuilder;
import model.builders.TurretBuilder;
import model.builders.WeaponBuilder;
import model.builders.actors.ModelActorBuilder;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A special Hicker that can move freely on the battle field and interact with other The role of this class is to put in common three subsystems : - a mover to
 * achieve motion over the battlefield and trhought other units - a tactical AI to take decisions - an arming to launch effects It uses a model actor to be
 * drawn on the view. It is defined by XML and is only instanciated by associate builder.
 */
public class Unit extends Hiker implements EffectSource, EffectTarget {

	public enum STATE {
		MOVING, AIMING, IDLING, DESTROYED, STUCK
	};

	// final data
	public final String UIName;
	public final String builderID;
	public final String race;
	public final int maxHealth;
	public final ModelActor actor;
	public final Arming arming;
	public final TacticalAI ai;

	// variables
	public Faction faction;
	public ArrayList<Unit> group = new ArrayList<>();
	public int health;
	public STATE state = STATE.IDLING;
	public boolean selected = false;

	public Unit(double radius, double speed, double mass, Point3D pos, double yaw, MoverBuilder moverBuilder, String UIName, String BuilderID, String race,
			int maxHealth, Faction faction, ModelActorBuilder actorBuilder) {
		super(radius, speed, mass, pos, yaw, moverBuilder);
		this.UIName = UIName;
		this.builderID = BuilderID;
		this.race = race;
		this.maxHealth = maxHealth;
		ai = new TacticalAI(this);
		arming = new Arming(this);
		setFaction(faction);
		health = maxHealth;
		actor = actorBuilder.build(this);
	}

	public Unit(Unit o) {
		super(o.radius, o.speed, o.mass, o.pos, o.yaw, o.mover);
		UIName = o.UIName;
		builderID = o.builderID;
		race = o.race;
		maxHealth = o.maxHealth;
		ai = new TacticalAI(this);
		arming = new Arming(this);
		setFaction(o.faction);
		health = maxHealth;
		actor = o.actor;
	}

	private void setFaction(Faction faction) {
		if (this.faction != null) {
			this.faction.getUnits().remove(this);
		}
		this.faction = faction;
		faction.getUnits().add(this);
	}

	public void update(double elapsedTime) {
		boolean aiming, moving;

		if (destroyed()) {
			return;
		}
		findNearbyMovers();
		arming.updateWeapons();

		ai.update();

		aiming = arming.isAiming();
		actor.onAim(aiming);

		mover.updatePosition(elapsedTime);

		moving = mover.hasMoved;
		actor.onMove(moving);

		arming.updateTurrets(elapsedTime, moving);

		actor.onWait(!aiming && !moving);

	}

	protected boolean isMoving() {
		return state == STATE.MOVING;
	}

	protected void setYaw(double yaw) {
		mover.desiredYaw = yaw;
	}

	public void idle() {
		state = STATE.IDLING;
	}

	private void findNearbyMovers() {
		mover.toFlockWith.clear();
		for (Unit u : group) {
			if (u != this) {
				mover.toFlockWith.add(u.getMover());
			}
		}

		mover.toLetPass.clear();
		for (Unit u : faction.getUnits()) {
			if (u != this && getBoundsDistance(u) <= 0 && u.mover.heightmap.equals(mover.heightmap)) {
				mover.toLetPass.add(u.mover);
			}
		}

		mover.toAvoid.clear();
		mover.toAvoid = getBlockers();
		mover.addTrinketsToAvoidingList();
	}

	private List<FieldComp> getBlockers() {
		List<FieldComp> res = new ArrayList<>();
		for (Faction f : faction.getEnemies()) {
			for (Unit u : f.getUnits()) {
				res.add(u);
			}
		}

		for (Unit u : faction.getUnits()) {
			if (u != this && u.mover.holdPosition) {
				res.add(u);
			}
		}
		return res;
	}

	private void destroy() {
		state = STATE.DESTROYED;
		actor.onMove(false);
		actor.onAim(false);
		actor.onWait(false);
		actor.onDestroyedEvent();
		actor.stopActing();
	}

	public void removeFromBattlefield() {
		state = STATE.DESTROYED;
		actor.stopActingAndChildren();
	}
	public void drawOnBattlefield() {
		actor.act();
	}

	public boolean destroyed() {
		return state == STATE.DESTROYED;
	}

	public double getHealthRate() {
		return (double) health / maxHealth;
	}

	public Mover getMover() {
		return mover;
	}

	public Point2D getPos2D() {
		return getPos().get2D();
	}

	public ArrayList<Turret> getTurrets() {
		return arming.turrets;
	}

	public void addWeapon(WeaponBuilder weaponBuilder, TurretBuilder turretBuilder) {
		Turret t = null;
		if (turretBuilder != null) {
			t = turretBuilder.build(this);
			arming.turrets.add(t);
		}
		arming.weapons.add(weaponBuilder.build(this, t));
	}

	@Override
	public boolean isStillActiveSource() {
		return !destroyed();
	}

	@Override
	public Point3D getDirection() {
		throw new RuntimeException("mustn't call this.");
	}

	@Override
	public void damage(EffectSource source, int amount) {
		if (destroyed()) {
			return;
		}
		health -= amount;
		if (health <= 0) {
			destroy();
		}

		ai.registerAsAttacker(source.getUnit());
	}

	@Override
	public Unit getUnit() {
		return this;
	}

	// @Override
	public Unit getNearest(Unit o1, Unit o2) {
		return (Unit) super.getNearest(o1, o2);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("UIName", UIName).toString();
	}

}
