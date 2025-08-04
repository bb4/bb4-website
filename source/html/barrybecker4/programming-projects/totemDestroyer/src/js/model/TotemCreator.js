import blockTypes from "./blockTypes.js"

export default class TotemCreator {

  constructor(world, width, height, worldScale, createGraphics) {
    this.world = world;
    this.width = width;
    this.height = height;
    this.worldScale = worldScale;
    this.createGraphics = createGraphics;
  }

  create(blocks) {
      this.createBox(this.width / 2, this.height + 10, this.width, 40,  blockTypes.TERRAIN);

      blocks.forEach(block => this.addBlock(block));
      return this.idol;
  }

  addBlock(block) {

      // Store block coordinates inside a Phaser Rectangle just to get its center
      let rectangle = new Phaser.Geom.Rectangle(block.x, block.y, block.width, block.height);

      let box2DBlock = this.createBox(rectangle.centerX, rectangle.centerY, block.width, block.height, blockTypes[block.type]);

      if (block.type == "IDOL") {
          this.idol = box2DBlock;
      }
  }

  // totem block creation
  createBox(posX, posY, width, height, blockType) {

      // this is how we create a generic Box2D body
      let box = this.world.createBody();
      if (blockType.isDynamic) {
          box.setDynamic();
      }

      // a body can have one or more fixtures. This is how we create a box fixture inside a body
      const worldScale = this.worldScale;
      box.createFixture(planck.Box(width / 2 / worldScale, height / 2 / worldScale));

      // now we place the body in the world
      box.setPosition(planck.Vec2(posX / worldScale, posY / worldScale));

      // time to set mass information
      box.setMassData({
          mass: 1,
          center: planck.Vec2(),
          // do not know the meaning of this "I", but if you set it to zero, bodies won't rotate
          I: 1
      });

      // now we create a graphics object representing the body
      let borderColor = Phaser.Display.Color.IntegerToColor(blockType.color);
      borderColor.darken(20);

      let userData = {
          blockType: blockType,
          sprite: this.createGraphics()
      }
      userData.sprite.fillStyle(blockType.color);
      userData.sprite.fillRect(- width / 2, - height / 2, width, height);
      userData.sprite.lineStyle(4, borderColor.color)
      userData.sprite.strokeRect(- width / 2 + 2, - height / 2 + 2, width - 4, height - 4);

      // a body can have anything in its user data, normally it's used to store its sprite
      box.setUserData(userData);

      return box;
  }
}