import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';
import ParticleGeom from './ParticleGeom.js';

const POINT_MATERIAL = new THREE.PointsMaterial({
    color: 0xBF6FFF,
    size: 4,
    blending: THREE.AdditiveBlending,
    transparent: true,
    sizeAttenuation: true, // points in distance are smaller
});


export default class PointGeom extends ParticleGeom {

    createPointCloud(sceneParams, particlesData) {
        if (!this.points) {
            this.points = createPointGeometry(particlesData);
        }
        this.pointCloud = new THREE.Points(this.points, POINT_MATERIAL);
        return this.pointCloud;
    }

    renderPointCloud(sceneParams, particlesData) {
        POINT_MATERIAL.size = sceneParams.particleSize;
        this.points.setDrawRange(0, sceneParams.particleCount);
        this.pointCloud.geometry.attributes.position.needsUpdate = true;
    }

}


function createPointGeometry(particlesData) {
    const points = new THREE.BufferGeometry();
    points.setDrawRange(0, 0); //particlesData.data.length);
    const bufferedAttr = new THREE.BufferAttribute(particlesData.positions, 3).setUsage(THREE.DynamicDrawUsage);
    points.setAttribute('position', bufferedAttr);
    return points;
}