/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.Angle;
import geometry.math.MyRandom;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.battlefield.map.cliff.Cliff;
import model.builders.actors.ModelActorBuilder;
import model.builders.definitions.BuilderManager;
import model.builders.definitions.DefElement;
import model.builders.definitions.Definition;

/**
 * @author Benoît
 */
public class TrinketBuilder extends Builder {
	private static final String EDITABLE = "Editable";
	private static final String RADIUS = "Radius";
	private static final String MODEL_LIST = "ModelList";
	private static final String COLOR = "Color";
	private static final String ACTOR_LINK = "ActorLink";

	private static final String ROTATION_X = "RotationX";
	private static final String ROTATION_Y = "RotationY";
	private static final String ROTATION_Z = "RotationZ";

	private static final String POSITION_X = "PositionX";
	private static final String POSITION_Y = "PositionY";
	private static final String POSITION_Z = "PositionZ";

	private static final String SCALE = "Scale";
	private static final String SCALE_X = "ScaleX";
	private static final String SCALE_Y = "ScaleY";
	private static final String SCALE_Z = "ScaleZ";

	private static final String MIN = "min";
	private static final String MAX = "max";

	private static final String RED = "R";
	private static final String GREEN = "G";
	private static final String BLUE = "B";

	private boolean editable = true;
	private double radius = 0;
	private List<String> modelPaths = new ArrayList<>();
	private String actorBuilderID = "StdTrinket";
	private ModelActorBuilder actorBuilder;

	private double minPosX = 0;
	private double maxPosX = 0;
	private double minPosY = 0;
	private double maxPosY = 0;
	private double minPosZ = 0;
	private double maxPosZ = 0;

	private double minScale = Double.NaN;
	private double maxScale = Double.NaN;
	private double minScaleX = 1;
	private double maxScaleX = 1;
	private double minScaleY = 1;
	private double maxScaleY = 1;
	private double minScaleZ = 1;
	private double maxScaleZ = 1;

	private double minRotX = 0;
	private double maxRotX = 0;
	private double minRotY = 0;
	private double maxRotY = 0;
	private double minRotZ = 0;
	private double maxRotZ = 0;

	private Color color;

	public TrinketBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case EDITABLE:
					editable = de.getBoolVal();
					break;
				case RADIUS:
					radius = de.getDoubleVal();
					break;
				case MODEL_LIST:
					modelPaths.add(de.getVal());
					break;
				case ACTOR_LINK:
					actorBuilderID = de.getVal();
					break;
				case ROTATION_X:
					minRotX = de.getDoubleVal(MIN);
					maxRotX = de.getDoubleVal(MAX);
					break;
				case ROTATION_Y:
					minRotY = de.getDoubleVal(MIN);
					maxRotY = de.getDoubleVal(MAX);
					break;
				case ROTATION_Z:
					minRotZ = de.getDoubleVal(MIN);
					maxRotZ = de.getDoubleVal(MAX);
					break;

				case POSITION_X:
					minPosX = de.getDoubleVal(MIN);
					maxPosX = de.getDoubleVal(MAX);
					break;
				case POSITION_Y:
					minPosY = de.getDoubleVal(MIN);
					maxPosY = de.getDoubleVal(MAX);
					break;
				case POSITION_Z:
					minPosZ = de.getDoubleVal(MIN);
					maxPosZ = de.getDoubleVal(MAX);
					break;

				case SCALE:
					minScale = de.getDoubleVal(MIN);
					maxScale = de.getDoubleVal(MAX);
					break;
				case SCALE_X:
					minScaleX = de.getDoubleVal(MIN);
					maxScaleX = de.getDoubleVal(MAX);
					break;
				case SCALE_Y:
					minScaleY = de.getDoubleVal(MIN);
					maxScaleY = de.getDoubleVal(MAX);
					break;
				case SCALE_Z:
					minScaleZ = de.getDoubleVal(MIN);
					maxScaleZ = de.getDoubleVal(MAX);
					break;
				case COLOR:
					color = new Color(de.getIntVal(RED), de.getIntVal(GREEN), de.getIntVal(BLUE));
					break;
			}
		}
	}

	public Trinket build(Point3D pos, double yaw, String modelPath, double scaleX, double scaleY, double scaleZ) {
		return new Trinket(editable, radius, getId(), modelPath, pos, scaleX, scaleY, scaleZ, 0, 0, yaw, color, actorBuilder);

	}

	public Trinket build(Point3D position) {
		Point3D offsetPos = new Point3D(MyRandom.between(minPosX, maxPosX), MyRandom.between(minPosY, maxPosY), MyRandom.between(minPosZ, maxPosZ))
		.getAddition(position);
		double rotX = MyRandom.between(minRotX, maxRotX);
		double rotY = MyRandom.between(minRotY, maxRotY);
		double rotZ = MyRandom.between(minRotZ, maxRotZ);
		double scaleX, scaleY, scaleZ;
		if (Double.isNaN(minScale)) {
			scaleX = MyRandom.between(minScaleX, maxScaleX);
			scaleY = MyRandom.between(minScaleY, maxScaleY);
			scaleZ = MyRandom.between(minScaleZ, maxScaleZ);
		} else {
			scaleX = scaleY = scaleZ = MyRandom.between(minScale, maxScale);
		}

		int i = 0;
		if (modelPaths.size() > 1) {
			i = MyRandom.nextInt(modelPaths.size() - 1);
		}
		String randomModelPath = modelPaths.get(i);
		return new Trinket(editable, radius, getId(), randomModelPath, offsetPos, scaleX, scaleY, scaleZ, rotX, rotY, rotZ, color, actorBuilder);
	}

	public Trinket build(Cliff cliff) {
		Trinket res = build(Point3D.ORIGIN);
		double posY = res.pos.y;
		res.pos.y = 0;
		switch (cliff.type) {
			case Orthogonal:
				res.pos = res.pos.getAddition(0, posY, 0);
				break;
			case Salient:
				res.pos = res.pos.getRotationAroundZ(Angle.RIGHT * posY);
				break;
			case Corner:
				res.pos = new Point3D(1 - res.pos.x, res.pos.y, res.pos.z);
				res.pos = res.pos.getRotationAroundZ(Angle.RIGHT * posY);
				break;
		}
		if (cliff.getTile().ramp != null) {
			double ratio = cliff.getTile().ramp.getCliffSlopeRate(cliff.getTile());
			res.pos = res.pos.getAddition(0, 0, -Tile.STAGE_HEIGHT * ratio * res.pos.z / Tile.STAGE_HEIGHT);
		}
		res.pos = res.pos.getRotationAroundZ(cliff.angle, new Point2D(0.5, 0.5));
		res.pos = res.pos.getAddition(cliff.getTile().x, cliff.getTile().y, cliff.getTile().level * Tile.STAGE_HEIGHT);
		return res;
	}

	public boolean isEditable() {
		return editable;
	}

	@Override
	public void readFinalizedLibrary() {
		actorBuilder = (ModelActorBuilder) BuilderManager.getActorBuilder(actorBuilderID);
	}

}
