package model.battlefield.map;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.geom3d.Triangle3D;
import geometry.math.Angle;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.atlas.Atlas;
import model.battlefield.map.cliff.Ramp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains everything to set up a terrain and explore it. Map is mainly : - a tile based grid with relief and cliffs - a texture atlas to paint on the ground -
 * a list of trinkets Also contains methods and fields dedicated to serialization/deserialization.
 */
public class Map {

	@JsonIgnore
	public MapStyle style = new MapStyle();

	@JsonProperty
	public String mapStyleID;

	@JsonProperty
	public List<Tile> tiles = new ArrayList<>();

	@JsonProperty
	public List<Ramp> ramps = new ArrayList<>();
	
	@JsonProperty
	public List<SerializableTrinket> serializableTrinkets = new ArrayList<>();

	@JsonIgnore
	public List<Trinket> trinkets = new ArrayList<>();

	@JsonProperty
	public Atlas atlas, cover;

	@JsonProperty
	public int width;
	@JsonProperty
	public int height;

	public Map(int width, int height) {
		this.width = width;
		this.height = height;
		atlas = new Atlas(width, height);
		atlas.finalize();
		cover = new Atlas(width, height);
		cover.finalize();
		
		tiles = new ArrayList<>(width * height);
	}

	public Map() {
	}

	public List<Tile> getTiles() {
		return tiles;
	}

	public Tile getTile(int x, int y) {
		if (!isInBounds(new Point2D(x, y))) {
			throw new IllegalArgumentException(new Point2D(x, y) + " is out of map bounds, ass hole.");
		}
		return tiles.get(y * width + x);
	}

	private Triangle3D getTriangleAt(Point2D coord) {
		Tile t = getTile(coord);
		if (t.n == null || t.s == null || t.e == null || t.w == null) {
			return null;
		}

		Point2D tPos2D = new Point2D(t.x, t.y);
		Point2D tnePos2D = new Point2D(t.e.n.x, t.e.n.y);

		Point3D nw = t.n.getPos();
		Point3D ne = t.n.e.getPos();
		Point3D sw = t.getPos();
		Point3D se = t.e.getPos();
		Triangle3D tr;

		if (Angle.getTurn(tPos2D, tnePos2D, coord) == Angle.CLOCKWISE) {
			tr = new Triangle3D(sw, se, ne);
		} else {
			tr = new Triangle3D(sw, ne, nw);
		}
		return tr;
	}
	
	public double getAltitudeAt(Point2D coord) {
		Triangle3D tr = getTriangleAt(coord);
		return tr == null ? 0 : getTriangleAt(coord).getElevated(coord).z;
	}

	public Point3D getNormalVectorAt(Point2D coord) {
		Triangle3D tr = getTriangleAt(coord);
		return tr == null ? Point3D.UNIT_Z : tr.normal;
	}

	public boolean isBlocked(int x, int y) {
		return getTile(x, y).isBlocked() ? true : false;
	}

	public Tile getTile(Point2D p) {
		if (p.x < 0 || p.x > width || p.y < 0 || p.y > height) {
			return null;
		}
		return getTile((int) Math.floor(p.x), (int) Math.floor(p.y));
	}

	/*
	 * Fast Voxel Traversal Algorithm for Ray Tracing John Amanatides Andrew Woo
	 */
	public boolean meetObstacle(Point2D p1, Point2D p2) {
		// calculate the direction of the ray (linear algebra)
		double dirX = p2.x - p1.x;
		double dirY = p2.y - p1.y;
		double length = Math.sqrt(dirX * dirX + dirY * dirY);
		dirX /= length; // normalize the direction vector
		dirY /= length;
		double tDeltaX = 1 / Math.abs(dirX); // how far we must move in the ray direction before we encounter a new voxel in x-direction
		double tDeltaY = 1 / Math.abs(dirY); // same but y-direction

		// start voxel coordinates
		int x = (int) Math.floor(p1.x); // use your transformer function here
		int y = (int) Math.floor(p1.y);

		// end voxel coordinates
		int endX = (int) Math.floor(p2.x);
		int endY = (int) Math.floor(p2.y);

		// decide which direction to start walking in
		int stepX = (int) Math.signum(dirX);
		int stepY = (int) Math.signum(dirY);

		double tMaxX, tMaxY;
		// calculate distance to first intersection in the voxel we start from
		if (dirX < 0) {
			tMaxX = (x - p1.x) / dirX;
		} else {
			tMaxX = (x + 1 - p1.x) / dirX;
		}

		if (dirY < 0) {
			tMaxY = (y - p1.y) / dirY;
		} else {
			tMaxY = (y + 1 - p1.y) / dirY;
		}

		// check if first is occupied
		if (getTile(x, y).isBlocked()) {
			return true;
		}
		boolean reachedX = false, reachedY = false;
		while (!reachedX || !reachedY) {
			if (tMaxX < tMaxY) {
				tMaxX += tDeltaX;
				x += stepX;
			} else {
				tMaxY += tDeltaY;
				y += stepY;
			}
			if (getTile(x, y).isBlocked()) {
				return true;
			}

			if (stepX > 0) {
				if (x >= endX) {
					reachedX = true;
				}
			} else if (x <= endX) {
				reachedX = true;
			}

			if (stepY > 0) {
				if (y >= endY) {
					reachedY = true;
				}
			} else if (y <= endY) {
				reachedY = true;
			}
		}
		return false;
	}

	public boolean isInBounds(Point2D p) {
		return p.x < 0 || p.y < 0 || p.x > width - 1 || p.y > height - 1 ? false : true;
	}

	public boolean isWalkable(Point2D p) {
		return  !isInBounds(p) || getTile(p).isBlocked() ? false : true;
	}

	public List<Tile> get8Around(Tile t) {
		ArrayList<Tile> res = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				if (t.x + i >= width || t.x + i < 0 || t.y + j >= height || t.y + j < 0) {
					continue;
				}
				res.add(getTile(t.x + i, t.y + j));
			}
		}
		return res;
	}

	public List<Tile> get9Around(Tile t) {
		ArrayList<Tile> res = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (t.x + i >= width || t.x + i < 0 || t.y + j >= height || t.y + j < 0) {
					continue;
				}
				res.add(getTile(t.x + i, t.y + j));
			}
		}
		return res;

	}

	public List<Tile> get16Around(Tile t) {
		ArrayList<Tile> res = new ArrayList<>();
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				if (t.x + i >= width || t.x + i < 0 || t.y + j >= height || t.y + j < 0) {
					continue;
				}
				res.add(getTile(t.x + i, t.y + j));
			}
		}
		res.removeAll(get8Around(t));
		return res;
	}

	public List<Tile> get4Around(Tile t) {
		ArrayList<Tile> res = new ArrayList<>();
		if (t.n != null) {
			res.add(t.n);
		}
		if (t.s != null) {
			res.add(t.s);
		}
		if (t.e != null) {
			res.add(t.e);
		}
		if (t.w != null) {
			res.add(t.w);
		}
		return res;
	}

	public int getRef(Tile t) {
		return t.y * width + t.x;
	}

	public Tile getTile(int ref) {
		int y = (int) Math.floor((double) ref / width);
		int x = (int) Math.round((double) ref % width);
		return getTile(x, y);
	}

	public void saveTrinkets() {
		serializableTrinkets.clear();
		for (Trinket t : trinkets) {
			serializableTrinkets.add(new SerializableTrinket(t));
		}
	}

	public void resetTrinkets() {
		trinkets.clear();
		for (SerializableTrinket st : serializableTrinkets) {
			Trinket t = st.getTrinket();
			trinkets.add(t);
			t.drawOnBattlefield();
		}
	}

	public void prepareForBattle() {
		for (Tile t : tiles) {
			t.hasBlockingTrinket = false;
		}
		for (Trinket t : trinkets) {
			if (t.getRadius() != 0) {
				for (Tile n : get9Around(getTile(t.getCoord()))) {
					if (n.getCoord().getAddition(0.5, 0.5).getDistance(t.getCoord()) < t.getRadius() + 0.3) {
						n.hasBlockingTrinket = true;
					}
				}
			}
		}
	}

	public List<Ramp> getRamps() {
		return ramps;
	}

}
