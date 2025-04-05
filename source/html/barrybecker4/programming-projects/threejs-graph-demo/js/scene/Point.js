
export default class Point {

    constructor(x, y, z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    getMagnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    add(pt) {
        return new Point(this.x + pt.x, this.y + pt.y, this.z + pt.z);
    }

    /**
     * Adjust point position by globeRadius and atmosphereThickness.
     * First find unit vector and magnitude.
     * Point will be unitVec * globeRadius + unitVec * magnitude * atmosphereThickness
     */
    getSpherePosition(globeRadius, atmosphereThickness) {
        const magnitude = this.getMagnitude();
        const unitVec = { x: this.x / magnitude, y: this.y / magnitude, z: this.z / magnitude };
        const r = globeRadius + magnitude * atmosphereThickness;
        return new Point(unitVec.x * r, unitVec.y * r, unitVec.z * r);
    }

    distanceTo(pt) {
        const dx = this.x - pt.x;
        const dy = this.y - pt.y;
        const dz = this.z - pt.z;
        return Math.sqrt( dx * dx + dy * dy + dz * dz );
    }

    midPoint(pt) {
        return new Point((this.x + pt.x) / 2.0, (this.y + pt.y) / 2.0, (this.z + pt.z) / 2.0);
    }

}