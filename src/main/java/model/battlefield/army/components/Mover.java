package model.battlefield.army.components;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.Angle;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.abstractComps.Hiker;
import model.battlefield.army.motion.CollisionManager;
import model.battlefield.army.motion.SteeringMachine;
import model.battlefield.army.motion.pathfinding.FlowField;
import model.battlefield.map.Map;
import model.battlefield.map.Trinket;

/**
 * Mover is module to connect to hiker to create motion and placement. The mover knows the map, is supplied with neighbors at each frame and provide movement
 * methods. It computes movement accordingly to these three elements and apply it to the hiker. It is also responsible to compute hiker elevation, and to decide
 * when it has arrived to destination. There is a difference between the states "destination reached" and "post found". When a mover has reached its
 * destination, it waits for its group arrival before considering its position as a post. This way, in large groups, movers won't fight eternally to regain
 * their arrival point. One mover is associated with one hiker. It is defined by XML and is only instanciated by associate builder.
 */
public class Mover {
	public enum Heightmap {
		SKY, AIR, GROUND
	};

	public enum PathfindingMode {
		FLY, WALK
	};

	public enum StandingMode {
		STAND, PRONE
	};

	// final
	public final Heightmap heightmap;
	public final PathfindingMode pathfindingMode;
	public final StandingMode standingMode;

	public final Hiker hiker;
	final SteeringMachine sm;
	final CollisionManager cm;

	// variables
	public Point3D velocity = Point3D.ORIGIN;

	public double desiredYaw = 0;
	Point3D desiredUp = Point3D.UNIT_Z;

	public boolean hasMoved = false;

	public List<FieldComp> toAvoid = new ArrayList<>();
	public List<Mover> toFlockWith = new ArrayList<>();
	public List<Mover> toLetPass = new ArrayList<>();

	public FlowField flowfield;
	private boolean hasDestination;
	public boolean hasFoundPost;
	public boolean holdPosition = false;
	public boolean tryHold = false;

	public Mover(Heightmap heightmap, PathfindingMode pathfindingMode, StandingMode standingMode, Hiker movable) {
		this.heightmap = heightmap;
		this.pathfindingMode = pathfindingMode;
		this.standingMode = standingMode;
		this.hiker = movable;
		cm = new CollisionManager(this);
		sm = new SteeringMachine(this);
		updateElevation();
	}

	public Mover(Mover o, Hiker movable) {
		this.heightmap = o.heightmap;
		this.pathfindingMode = o.pathfindingMode;
		this.standingMode = o.standingMode;
		this.hiker = movable;
		cm = new CollisionManager(this);
		sm = new SteeringMachine(this);
		updateElevation();
	}

	public void updatePosition(double elapsedTime) {
		double lastYaw = hiker.yaw;
		Point3D lastPos = new Point3D(hiker.pos);

		if (!holdPosition) {
			Point3D steering = sm.getSteeringAndReset(elapsedTime);
			cm.applySteering(steering, elapsedTime, toAvoid);
		}
		head(elapsedTime);

		hasMoved = hiker.hasMoved(lastPos, lastYaw);
		if (hasMoved) {
			updateElevation();
		}

		if (hasDestination) {
			hasFoundPost = false;
		} else {
			hasFoundPost = true;
			for (Mover m : toFlockWith) {
				if (m.hasDestination) {
					hasFoundPost = false;
				}
			}
		}
		if (!tryHold) {
			holdPosition = false;
		}
	}

	public void tryToHoldPositionSoftly() {
		tryHold = true;
		if (fly()) {
			holdPosition = true;
		} else {
			for (FieldComp f : toAvoid) {
				if (hiker.collide(f)) {
					return;
				}
			}
			List<Mover> all = new ArrayList<>();
			all.addAll(toFlockWith);
			all.addAll(toLetPass);
			for (Mover m : all) {
				if (hiker.collide(m.hiker)) {
					return;
				}
			}
			for (Mover m : toFlockWith) {
				if (m.tryHold && !m.holdPosition) {
					return;
				}
			}
			holdPosition = true;
		}
	}

	public void tryToHoldPositionHardly() {
		tryHold = true;
		if (fly()) {
			holdPosition = true;
		} else {
			for (FieldComp f : toAvoid) {
				if (hiker.collide(f)) {
					return;
				}
			}
			ArrayList<Mover> all = new ArrayList<>();
			all.addAll(toFlockWith);
			all.addAll(toLetPass);
			for (Mover m : all) {
				if (m.holdPosition && hiker.collide(m.hiker)) {
					return;
				}
			}
			holdPosition = true;
		}
	}

	public void setDestination(FlowField ff) {
		flowfield = ff;
		hasDestination = true;
		hasFoundPost = false;
	}

	public void setDestinationReached() {
		hasDestination = false;
		for (Mover m : toFlockWith) {
			if (hiker.getDistance(m.hiker) < hiker.getSpacing(m.hiker) + 3) {
				m.hasDestination = false;
			}
		}
	}

	public boolean hasDestination() {
		return hasDestination;
	}

	public Point2D getDestination() {
		if (flowfield != null) {
			return flowfield.destination;
		}
		return null;
	}

	public void head(double elapsedTime) {
		if (!velocity.isOrigin()) {
			desiredYaw = velocity.get2D().getAngle();
		}

		if (!Angle.areSimilar(desiredYaw, hiker.yaw)) {
			double diff = Angle.getOrientedDifference(hiker.yaw, desiredYaw);
			if (diff > 0) {
				hiker.yaw += Math.min(diff, hiker.getRotSpeed() * elapsedTime);
			} else {
				hiker.yaw -= Math.min(-diff, hiker.getRotSpeed() * elapsedTime);
			}
		} else {
			hiker.yaw = desiredYaw;
		}
	}

	public void separate() {
		sm.applySeparation(toLetPass);
	}

	public void flock() {
		sm.applySeparation(toFlockWith);
	}

	public void seek(Mover target) {
		flock();
		separate();
		sm.seek(target);

		List<FieldComp> toAvoidExceptTarget = new ArrayList<>(toAvoid);
		toAvoidExceptTarget.remove(target);
		sm.avoidBlockers(toAvoidExceptTarget);
	}

	public void seek(Point3D position) {
		flock();
		separate();
		sm.seek(position);
		sm.avoidBlockers(toAvoid);
	}

	public void followPath() {
		flock();
		separate();
		sm.proceedToDestination();
		sm.avoidBlockers(toAvoid);
	}

	public void followPath(Mover target) {
		flock();
		separate();
		sm.proceedToDestination();

		List<FieldComp> toAvoidExceptTarget = new ArrayList<>(toAvoid);
		toAvoidExceptTarget.remove(target);
		sm.avoidBlockers(toAvoidExceptTarget);
	}

	private void updateElevation() {
		if (ModelManager.getBattlefield() != null) {
			Map map = ModelManager.getBattlefield().getMap();
			if (heightmap == Heightmap.GROUND) {
				hiker.pos = hiker.getCoord().get3D(0).getAddition(0, 0, map.getAltitudeAt(hiker.getCoord()));
				if (standingMode == StandingMode.PRONE) {
					desiredUp = map.getNormalVectorAt(hiker.getCoord());
					if (!hiker.upDirection.equals(desiredUp)) {
						hiker.upDirection = hiker.upDirection.getAddition(desiredUp).getNormalized();
					}
				}
				hiker.direction = Point2D.ORIGIN.getTranslation(hiker.yaw, 1).get3D(0);
			} else if (heightmap == Heightmap.SKY) {
				hiker.pos = hiker.getCoord().get3D(0).getAddition(0, 0, map.getTile(hiker.getCoord()).level + 3);
				hiker.upDirection = Point3D.UNIT_Z;
			} else {
				if (!velocity.isOrigin()) {
					hiker.direction = velocity;
				}
				hiker.upDirection = null;
			}
		}
	}

	public boolean fly() {
		return pathfindingMode == PathfindingMode.FLY;
	}

	public double getSpeed() {
		return hiker.getSpeed();
	}

	public void changeCoord(Point2D p) {
		velocity = Point3D.ORIGIN;
		hiker.pos = p.get3D(0);
		updateElevation();
	}

	public void addTrinketsToAvoidingList() {
		for (Trinket t : ModelManager.getBattlefield().getMap().trinkets) {
			if (t.getRadius() != 0) {
				toAvoid.add(t);
			}
		}
	}
}
