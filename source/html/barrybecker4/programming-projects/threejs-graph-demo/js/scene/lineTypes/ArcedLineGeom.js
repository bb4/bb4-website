import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';
import LineGeom from './LineGeom.js';

const LINE_MATERIAL = new THREE.LineBasicMaterial( {
    color: 0xFF3BFF,
    linewidth: 2,
    vertexColors: false,
    blending: THREE.AdditiveBlending,
    transparent: true,
    opacity: 0.5,
    side: THREE.DoubleSide,
});

// Number of line segments in the arc
const NUM_SEGMENTS = 9;

export default class ArcedLineGeom extends LineGeom {

    createLineCloud(sceneParams, linesData) {

        const lineCloud = new THREE.Group();
        this.arcs = [];

        for ( let i = 0; i < linesData.maxLines; i++ ) {

            const curve = new THREE.QuadraticBezierCurve3(
                new THREE.Vector3( 1, 1, 1 ),
                new THREE.Vector3( 1, 1, 1  ),
                new THREE.Vector3( 1, 1, 1  )
            );

            const points = curve.getPoints( NUM_SEGMENTS );
            const geometry = new THREE.BufferGeometry().setFromPoints( points );
            const arc = new THREE.Line(geometry, LINE_MATERIAL);

            arc.curve = curve;
            arc.visible = false;
            this.arcs.push(arc);

            lineCloud.add(arc);
        }

        this.lineCloud = lineCloud;
        return lineCloud;
    }

    renderLineCloud(sceneParams, linesData, numConnected) {
        const arcScale = sceneParams.arcScale;
        LINE_MATERIAL.opacity = sceneParams.lineOpacity;

        // there are numConnected arcs - each connecting 2 points
        for (let i = 0; i < numConnected; i++) {
            const arc = this.arcs[i];

            const pt1 = linesData.getPoint(2 * i);
            const pt2 = linesData.getPoint(2 * i + 1);
            const controlPt = findControlPoint(pt1, pt2, arcScale);

            const curve = arc.curve;
            curve.v0.set(pt1.x, pt1.y, pt1.z);
            curve.v1.set(controlPt.x, controlPt.y, controlPt.z);
            curve.v2.set(pt2.x, pt2.y, pt2.z);

            arc.geometry.dispose();
            arc.material.dispose();

            arc.geometry.setFromPoints(curve.getPoints(NUM_SEGMENTS));
            arc.geometry.verticesNeedUpdate;

            arc.visible = true;
        }

        this.hideRemainingLines(linesData, numConnected);
    }

    hideRemainingLines(linesData, numConnected) {
        let visible = true;
        let i = numConnected;
        while (i < linesData.maxLines && visible) {
            const object = this.arcs[i];
            visible = object.visible;
            object.visible = false;
            i++;
        }
    }

}

// Height of control point determined by arcScale and distance between end points.
function findControlPoint(pt1, pt2, arcScale) {
    const distance = pt1.distanceTo(pt2);
    const midPt = pt1.midPoint(pt2);
    const magnitude = midPt.getMagnitude();

    const r = (magnitude + distance * arcScale) / magnitude;
    return { x: midPt.x * r, y: midPt.y * r, z: midPt.z * r };
}

