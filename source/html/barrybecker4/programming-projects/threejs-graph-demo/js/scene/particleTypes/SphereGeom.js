import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';
import ParticleGeom from './ParticleGeom.js';

const SPHERE_MATERIAL = new THREE.MeshPhongMaterial({
    side: THREE.DoubleSide,
    color: 0x22bb99,
    specular: 0xaa55ff,
    shininess: 5,
    vertexColors: false,
});


export default class CubeGeom extends ParticleGeom {

    createPointCloud(sceneParams, particlesData) {

        const pointCloud = new THREE.Group();
        const geometry = new THREE.SphereBufferGeometry( 1, 16, 16 );
        geometry.computeBoundingBox();

        for ( let i = 0; i < particlesData.data.length; i ++ ) {

            const object = new THREE.Mesh(geometry, SPHERE_MATERIAL.clone());

            const pt = particlesData.getPoint(i);
            object.position.set(pt.x, pt.y, pt.z);
            object.visible = i < sceneParams.particleCount;

            object.scale.set(1, 1, 1);

            pointCloud.add( object );
        }
        this.pointCloud = pointCloud;
        return pointCloud;
    }

}