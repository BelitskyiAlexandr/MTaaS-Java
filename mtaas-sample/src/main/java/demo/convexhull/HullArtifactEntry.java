package demo.convexhull;

import mtaas.annotations.ArtifactEntry;

import java.util.List;

//@ArtifactEntry(relationName = "hull-permute")
//@ArtifactEntry(relationName = "hull-rotate")
//@ArtifactEntry(relationName = "hull-scale")
//@ArtifactEntry(relationName = "hull-translate")
public final class HullArtifactEntry {
    private HullArtifactEntry() {}

    public static List<Point2D> run(List<Point2D> input) {
        return ConvexHull.compute(input);
    }
}
