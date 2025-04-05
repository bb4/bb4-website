import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';
import ParticlesData from './ParticlesData.js';
import LinesData from './LinesData.js';
import StraightLineGeom from './lineTypes/StraightLineGeom.js';
import ArcedLineGeom from './lineTypes/ArcedLineGeom.js';
import PointGeom from './particleTypes/PointGeom.js';
import CubeGeom from './particleTypes/CubeGeom.js';
import SphereGeom from './particleTypes/SphereGeom.js';
import SpriteGeom from './particleTypes/SpriteGeom.js'
import createGlobe from './createGlobe.js';

// edge length of the bounding cube
const R = 800;

const POINT_TYPE_TO_CONSTRUCTOR = {
    Point : PointGeom,
    Cube: CubeGeom,
    Sphere: SphereGeom,
    Sprite: SpriteGeom,
}

const LINE_TYPE_TO_CONSTRUCTOR = {
    Line : StraightLineGeom,
    Arc: ArcedLineGeom,
}

export default function(sceneParams) {

    const particlesData = new ParticlesData(sceneParams.maxParticleCount, R);
    const linesData = new LinesData(sceneParams.maxParticleCount);

    const group = new THREE.Group();
    group.add(createBoxHelper(R));

    const globe = createGlobe();
    group.add(globe);

    let particleGeom = createParticleGeometry(sceneParams);
    let pointCloud = particleGeom.createPointCloud(sceneParams, particlesData);

    group.add(pointCloud);

    let lineGeom = createLineGeometry(sceneParams);
    let lineCloud = lineGeom.createLineCloud(sceneParams, linesData);
    group.add(lineCloud);

    group.showLineMesh = value => lineGeom.lineCloud.visible = value;
    group.showPointCloud = value => pointCloud.visible = value;

    group.animate = function() {
        const numConnected = particlesData.connectPoints(linesData, sceneParams);

        const lineType = sceneParams.lineGeometry;
        if (lineType != sceneParams.oldLineGeometry) {
            group.remove(lineCloud);

            lineGeom = createLineGeometry(sceneParams);
            lineCloud = lineGeom.createLineCloud(sceneParams, linesData);

            group.add(lineCloud);
            sceneParams.oldLineGeometry = lineType;
        }

        lineGeom.renderLineCloud(sceneParams, linesData, numConnected);

        const particleType = sceneParams.particleGeometry;
        if (particleType != sceneParams.oldParticleGeometry) {
            group.remove(pointCloud);

            particleGeom = createParticleGeometry(sceneParams);
            pointCloud = particleGeom.createPointCloud(sceneParams, particlesData);

            group.add(pointCloud);
            sceneParams.oldParticleGeometry = particleType;
        }

        particleGeom.renderPointCloud(sceneParams, particlesData);

        globe.render(sceneParams);

        // auto rotate if needed
        const rotateSpeed = sceneParams.autoRotateSpeed;
        if (rotateSpeed > 0) {
            group.rotation.y += rotateSpeed / 100.0;
        }
    }

    return group;
}

function createParticleGeometry(sceneParams) {
    const constructor = POINT_TYPE_TO_CONSTRUCTOR[sceneParams.particleGeometry];
    if (!constructor) {
        throw new Error("Invalid particle type: " + sceneParams.particleGeometry);
    }
    return new constructor();
}

function createLineGeometry(sceneParams) {
    const constructor = LINE_TYPE_TO_CONSTRUCTOR[sceneParams.lineGeometry];
    if (!constructor) {
        throw new Error("Invalid line type: " + sceneParams.lineGeometry);
    }
    return new constructor();
}

function createBoxHelper(r) {
    const helper = new THREE.BoxHelper( new THREE.Mesh( new THREE.BoxBufferGeometry( r, r, r ) ) );
    helper.material.color.setHex( 0x101010 );
    helper.material.blending = THREE.AdditiveBlending;
    helper.material.transparent = true;
    return helper;
}

