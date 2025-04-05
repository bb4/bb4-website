import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';
import ParticleGeom from './ParticleGeom.js';

const CUBE_MATERIAL = new THREE.MeshLambertMaterial({
    side: THREE.DoubleSide,
    color: 0x44cc99,
    specular: 0xaaffff,
    shininess: 1,
    vertexColors: false,
});


export default class CubeGeom extends ParticleGeom {

    createPointCloud(sceneParams, particlesData) {

        const pointCloud = new THREE.Group();
        const geometry = new THREE.BoxBufferGeometry(1, 1, 1);
        geometry.computeBoundingBox();

        for ( let i = 0; i < particlesData.data.length; i ++ ) {

            const object = new THREE.Mesh(geometry, CUBE_MATERIAL.clone());

            const pt = particlesData.getPoint(i);
            object.position.set(pt.x, pt.y, pt.z);
            object.visible = i < sceneParams.particleCount;

            object.rotation.set(Math.random() * 2 * Math.PI, Math.random() * 2 * Math.PI, Math.random() * 2 * Math.PI);
            object.scale.set(1, 1, 1);

            pointCloud.add( object );
        }

        this.pointCloud = pointCloud;
        return pointCloud;
    }

}