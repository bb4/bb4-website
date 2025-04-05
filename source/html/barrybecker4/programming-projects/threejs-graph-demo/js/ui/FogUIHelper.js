import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';

/*
 * Use this class to pass to dat.gui so when it manipulates near or far near is never > far and far is never < near.
 * Also when dat.gui manipulates color, update both the fog and background colors.
 */
export default class FogGUIHelper {

    constructor(scene) {
        if (!scene.fog) {
            console.log("Scene has no fog to modify");
        }
        this.fog = scene.fog || { near: 0, far: 0, color: new THREE.Color( 0, 0, 0 ) };
        this.backgroundColor = scene.background;
    }

    get fogNear() {
        return this.fog.near;
    }

    set fogNear(v) {
        this.fog.near = v;
        this.fog.far = Math.max(this.fog.far, v);
    }

    get fogFar() {
        return this.fog.far;
    }

    set fogFar(v) {
        this.fog.far = v;
        this.fog.near = Math.min(this.fog.near, v);
    }

    get color() {
        return `#${this.fog.color.getHexString()}`;
    }

    set color(hexString) {
       this.fog.color.set(hexString);
       this.backgroundColor.set(hexString);
    }
}