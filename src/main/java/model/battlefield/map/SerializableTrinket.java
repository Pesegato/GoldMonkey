package model.battlefield.map;

import geometry.geom3d.Point3D;
import model.builders.definitions.BuilderManager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SerializableTrinket {
	@JsonProperty
	private String builderID;
	@JsonProperty
	private Point3D pos;
	@JsonProperty
	private double yaw;
	@JsonProperty
	private double scaleX, scaleY, scaleZ;
	@JsonProperty
	private String modelPath;

	public SerializableTrinket() {

	}

	public SerializableTrinket(Trinket t) {
		builderID = t.builderID;
		pos = t.pos;
		yaw = t.yaw;
		scaleX = t.scaleX;
		scaleY = t.scaleY;
		scaleZ = t.scaleZ;
		modelPath = t.modelPath;
	}

	@JsonIgnore
	public Trinket getTrinket() {
		Trinket res = BuilderManager.getTrinketBuilder(builderID).build(pos, yaw, modelPath, scaleX, scaleY, scaleZ);
		return res;

	}

}
