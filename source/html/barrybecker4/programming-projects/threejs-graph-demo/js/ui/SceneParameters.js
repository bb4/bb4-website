/*
 * Parameters used to control the appearance of the scene.
 */
export default class SceneParameters {

    constructor(maxParticleCount) {

        this.maxParticleCount = maxParticleCount;

        this.showPoints = true;
        this.showLines = true;
        this.minDistance = 150;
        this.limitConnections = false;
        this.maxConnections = 20;
        this.lineOpacity = 0.9;
        this.particleCount = Math.min(500, maxParticleCount);
        this.autoRotateSpeed = 0.0;
        this.particleSpeed = 2;
        this.particleGeometry = 'Cube';
        this.lineGeometry = 'Line';
        this.oldParticleGeometry = null;
        this.oldLineGeometry = null;
        this.particleSize = 6;
        this.globeRadius = 0;
        this.atmosphereThickness = 1.0;
        this.arcScale = 0.1;
    }
}

