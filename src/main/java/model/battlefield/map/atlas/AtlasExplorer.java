package model.battlefield.map.atlas;

import geometry.geom2d.Point2D;

import java.util.ArrayList;

import model.battlefield.map.Map;

/**
 * Translate coordinates between map's and atlas' spaces.
 *
 * Also finds groups of pixel of a certain shape.
 *
 * @author Benoît
 */
public class AtlasExplorer {

	Map map;

	public AtlasExplorer(Map map){
		this.map = map;
	}

	public Point2D getInMapSpace(Point2D p){
		return p.getMult(map.width, map.height).getDivision(map.atlas.getWidth(), map.atlas.getHeight());
	}

	public Point2D getInAtlasSpace(Point2D p){
		return p.getMult(map.atlas.getHeight(), map.atlas.getHeight()).getDivision(map.width, map.height);
	}

	public double getInAtlasSpace(double distance){
		return distance * map.atlas.getWidth() / map.width;
	}

	public double getInMapSpace(double distance){
		return distance * map.width / map.atlas.getWidth();
	}

	public ArrayList<Point2D> getPixelsInMapSpaceSquare(Point2D center, double radius){
		center = getInAtlasSpace(center);
		radius = getInAtlasSpace(radius);
		ArrayList<Point2D> res = new ArrayList<>();
		int minX = (int)Math.round(Math.max(center.x-radius, 0));
		int maxX = (int) Math.round(Math.min(center.x + radius, map.atlas.getWidth() - 1));
		int minY = (int)Math.round(Math.max(center.y-radius, 0));
		int maxY = (int) Math.round(Math.min(center.y + radius, map.atlas.getHeight() - 1));
		for(int x=minX; x<maxX; x++) {
			for(int y=minY; y<maxY; y++){
				Point2D p = new Point2D(x, y);
				res.add(p);
			}
		}
		return res;
	}

	public ArrayList<Point2D> getPixelsInMapSpaceCircle(Point2D center, double radius){
		ArrayList<Point2D> res = new ArrayList<>();
		for(Point2D p : getPixelsInMapSpaceSquare(center, radius)) {
			if(p.getDistance(getInAtlasSpace(center)) < getInAtlasSpace(radius)) {
				res.add(p);
			}
		}
		return res;
	}

	public ArrayList<Point2D> getPixelsInMapSpaceDiamond(Point2D center, double radius){
		radius *= 1.414;
		ArrayList<Point2D> res = new ArrayList<>();
		for(Point2D p : getPixelsInMapSpaceSquare(center, radius)) {
			if(p.getManathanDistance(getInAtlasSpace(center)) < getInAtlasSpace(radius)) {
				res.add(p);
			}
		}
		return res;
	}
}
