import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';
import ParticleGeom from './ParticleGeom.js';

const map = new THREE.TextureLoader().load( './images/appliance-icon.png' );
const SPRITE_MATERIAL = new THREE.SpriteMaterial( { map: map, color: 0xffffff } );;


export default class CubeGeom extends ParticleGeom {

    createPointCloud(sceneParams, particlesData) {

        const pointCloud = new THREE.Group();

        for (let i = 0; i < particlesData.data.length; i ++) {

            const object = new THREE.Sprite(SPRITE_MATERIAL.clone());

            const pt = particlesData.getPoint(i);
            object.position.set(pt.x, pt.y, pt.z);
            object.visible = i < sceneParams.particleCount;

            object.scale.set(1, 1, 1)

            pointCloud.add( object );
        }
        this.pointCloud = pointCloud;
        return pointCloud;
    }

}