import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';

const FOG_COLOR = '#041421';
const FOG_NEAR = 500;
const FOG_FAR = 4000;


export default function() {

    const scene = new THREE.Scene();

    scene.fog = new THREE.Fog(FOG_COLOR, FOG_NEAR, FOG_FAR);
    scene.background = new THREE.Color(FOG_COLOR);

    scene.add(new THREE.AmbientLight(0xffffff, .1));
    scene.add(createDirectionalLight(-1000, 2000, 4000));

    return scene;
}

function createDirectionalLight(xpos, ypos, zpos) {
    const color = 0xFFFFFF;
    const intensity = 0.9;
    const light = new THREE.DirectionalLight(color, intensity);
    light.position.set(xpos, ypos, zpos);
    light.target.position.set(0, 0, 0);
    return light;
}