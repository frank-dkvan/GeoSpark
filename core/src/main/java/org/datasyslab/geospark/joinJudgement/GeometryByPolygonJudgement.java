/**
 * FILE: GeometryByPolygonJudgement.java
 * PATH: org.datasyslab.geospark.joinJudgement.GeometryByPolygonJudgement.java
 * Copyright (c) 2015-2017 GeoSpark Development Team
 * All rights reserved.
 */
package org.datasyslab.geospark.joinJudgement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.spark.api.java.function.FlatMapFunction2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.datasyslab.geospark.geometryObjects.PairGeometry;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class GeometryByPolygonJudgement.
 */
public class GeometryByPolygonJudgement implements FlatMapFunction2<Iterator<Object>, Iterator<Object>, PairGeometry>, Serializable {

    /**
     * The consider boundary intersection.
     */
    boolean considerBoundaryIntersection = false;

    /**
     * Instantiates a new geometry by polygon judgement.
     *
     * @param considerBoundaryIntersection the consider boundary intersection
     */
    public GeometryByPolygonJudgement(boolean considerBoundaryIntersection) {
        this.considerBoundaryIntersection = considerBoundaryIntersection;
    }

    @Override
    public Iterator<PairGeometry> call(Iterator<Object> iteratorObject, Iterator<Object> iteratorWindow) throws Exception {
        List<PairGeometry> result = new ArrayList<PairGeometry>();
        List<Object> queryObjects = new ArrayList<Object>();
        while(iteratorObject.hasNext())
        {
            queryObjects.add(iteratorObject.next());
        }
        while (iteratorWindow.hasNext()) {
            Polygon window = (Polygon) iteratorWindow.next();
            HashSet<Geometry> resultHashSet = new HashSet<Geometry>();
            for (int i =0;i<queryObjects.size();i++) {
                Geometry object = (Geometry) queryObjects.get(i);
                if (considerBoundaryIntersection) {
                    if (window.intersects(object)) {
                        resultHashSet.add(object);
                    }
                } else {
                    if (window.covers(object)) {
                        resultHashSet.add(object);
                    }
                }
            }
            if (resultHashSet.size() == 0) continue;
            result.add(new PairGeometry(window, resultHashSet));
        }
        return result.iterator();
    }
}
