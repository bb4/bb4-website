import blockTypes from "./blockTypes.js"


export default class TotemWorld {

  constructor(gameOptions, gameOverFn) {
    // this is how we create a Box2D world
    this.gameOptions = gameOptions;
    this.world = planck.World(planck.Vec2(0, gameOptions.gravity));
    this.gameOverFn = gameOverFn;
  }


  destroyBlock(worldX, worldY) {
    let worldPoint = planck.Vec2(worldX, worldY);

    // query for the world coordinates to check fixtures under the pointer
    const myWorld = this.world;
    myWorld.queryAABB(planck.AABB(worldPoint, worldPoint), function(fixture) {

        let body = fixture.getBody();
        let userData = body.getUserData();

        // is a breakable body?
        if (userData.blockType == blockTypes.BREAKABLE) {
            userData.sprite.destroy();
            myWorld.destroyBody(body);
        }
    });
  }


  update(timeStep, idol) {
    // advance world simulation
    this.world.step(timeStep);

    // clearForces  method should be added at the end on each step
    this.world.clearForces();

    // get idol contact list
    for (let ce = idol.getContactList(); ce; ce = ce.next) {

        // get the contact
        let contact = ce.contact;

        // get the fixture from the contact
        let fixture = contact.getFixtureA();

        // get the body from the fixture
        let body = fixture.getBody();

        // the the userdata from the body
        let userData = body.getUserData();

        // did the idol hit the terrain?
        if (userData.blockType == blockTypes.TERRAIN) {
            this.gameOverFn();
        }
    }

    // iterate through all bodies
    for (let b = this.world.getBodyList(); b; b = b.getNext()) {

        // get body position
        let bodyPosition = b.getPosition();

        // get body angle, in radians
        let bodyAngle = b.getAngle();

        // get body user data, the graphics object
        let userData = b.getUserData();

        // adjust graphic object position and rotation
        userData.sprite.x = bodyPosition.x * this.gameOptions.worldScale;
        userData.sprite.y = bodyPosition.y * this.gameOptions.worldScale;
        userData.sprite.rotation = bodyAngle;
    }
  }
}