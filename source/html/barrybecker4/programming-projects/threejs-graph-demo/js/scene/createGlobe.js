import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';


const SEGMENTS = 36;
const BASE_RADIUS = 1.0;
const CLOUD_OFFSET = 0.01;
const BUMP_SCALE = 0.01;

const SPHERE_MATERIAL = new THREE.MeshPhongMaterial({
    map:         THREE.ImageUtils.loadTexture('images/2_no_clouds_4k.jpg'),
    bumpMap:     THREE.ImageUtils.loadTexture('images/elev_bump_4k.jpg'),
    bumpScale:   BUMP_SCALE,
    specularMap: THREE.ImageUtils.loadTexture('images/water_4k.png'),
    specular:    0xffffff,
    shininess:   1,
});

const CLOUD_MATERIAL = new THREE.MeshPhongMaterial({
   map:         THREE.ImageUtils.loadTexture('images/fair_clouds_4k.png'),
   transparent: true
});

const STAR_MATERIAL = new THREE.MeshBasicMaterial({
    map:  THREE.ImageUtils.loadTexture('images/galaxy_starfield.png'),
    side: THREE.BackSide
});


// See https://github.com/turban/webgl-earth
export default function() {

    // Earth params
    const rotation = 6;

    const globeGroup = new THREE.Group();

    const sphere = createSphere(BASE_RADIUS, SEGMENTS);
    sphere.rotation.y = rotation;
    globeGroup.add(sphere)

    const clouds = createClouds(BASE_RADIUS, SEGMENTS);
    clouds.rotation.y = rotation;
    globeGroup.add(clouds)

    //var stars = createStars(90, 64);
    //globeGroup.add(stars);

    globeGroup.render = function(sceneParams) {
        const r = sceneParams.globeRadius * 0.99;
        sphere.scale.set(r, r, r);
        clouds.scale.set(r, r, r);
        SPHERE_MATERIAL.bumpScale = r * BUMP_SCALE;
    }

    return globeGroup;


    function createSphere(radius, segments) {
        return new THREE.Mesh(
            new THREE.SphereGeometry(radius, segments, segments),
            SPHERE_MATERIAL
        );
    }

    function createClouds(radius, segments) {
        return new THREE.Mesh(
            new THREE.SphereGeometry(radius + CLOUD_OFFSET, segments, segments),
            CLOUD_MATERIAL
        );
    }

    // optional starry background
    function createStars(radius, segments) {
        return new THREE.Mesh(
            new THREE.SphereGeometry(radius, segments, segments),
            STAR_MATERIAL
        );
    }
};



