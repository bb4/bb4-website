import circleUtils from '../circleUtils.js'

/**
 * Build an SVG path for the intersection lens between two circles.
 */
export function pathFunc(x1, y1, rad1, x2, y2, rad2, largeArcFlag1, sweepFlag1, largeArcFlag2, sweepFlag2) {
    const interPoints = circleUtils.circleIntersection(x1, y1, rad1, x2, y2, rad2);
    if (interPoints[0] == interPoints[2]) {
        return "M0,0";
    }
    const rotation = 0;
    return "M" +
        interPoints[0] + "," + interPoints[2] + "A" + rad2 + "," + rad2 + " " + rotation + " " +
        largeArcFlag2 + " " + sweepFlag2 + " " +
        interPoints[1] + "," + interPoints[3] + "A" + rad1 + "," + rad1 + " " + rotation + " " +
        largeArcFlag1 + " " + sweepFlag1 + " " +
        interPoints[0] + "," + interPoints[2];
}
