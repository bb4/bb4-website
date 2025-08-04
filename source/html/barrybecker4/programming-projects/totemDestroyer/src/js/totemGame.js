// Derived from
// https://www.emanueleferonato.com/2021/03/13/build-a-html5-game-like-old-flash-glory-totem-destroyer-using-phaser-and-planck-js-physics-engine/

import TotemWorld from "./model/TotemWorld.js"
import TotemCreator from "./model/TotemCreator.js"
import gameOptions from "./gameOptions.js"

let game;
let selectedTotem;

window.onload = function() {
    const totemSelector = getTotemSelector();
    totemSelector.onchange = totemSelectionChanged;

    const restartButton = document.getElementById("restartButton");
    restartButton.onclick = totemSelectionChanged;
    restartButton.ontouchstart = totemSelectionChanged;

    totemSelectionChanged();
}

function getTotemSelector() {
    return document.getElementById("totemSelector");
}

function totemSelectionChanged() {
    if (game) {
        game.destroy(true);
    }
    const totemSelector = getTotemSelector();
    selectedTotem = totemSelector.options[ totemSelector.selectedIndex ].value;
    initializePhaser();
}

function initializePhaser(selectedTotem) {
    let gameConfig = {
        type: Phaser.AUTO,
        backgroundColor: gameOptions.backgroundColor,
        scale: {
            mode: Phaser.Scale.FIT,
            autoCenter: Phaser.Scale.CENTER_BOTH,
            parent: "TotemDestroyerGame",
            width: 800,
            height: 600
        },
        scene: PlayGame
    }
    game = new Phaser.Game(gameConfig);
    window.focus();
}


class PlayGame extends Phaser.Scene {

    constructor() {
        super("PlayGame");
    }

    preload() {
       this.load.tilemapTiledJSON("totem", "src/assets/" + selectedTotem + ".json");
    }

    create() {

        const gameOverFn = () => {
          this.cameras.main.setBackgroundColor(gameOptions.gameOverBackgroundColor)
        }

        this.world = new TotemWorld(gameOptions, gameOverFn);
        this.createTotem();
    }

    createTotem() {
      const creator = new TotemCreator(this.world.world,
           game.config.width, game.config.height, gameOptions.worldScale, () => this.add.graphics());

      const map = this.add.tilemap("totem");
      const blocks = map.objects[0].objects;
      this.idol = creator.create(blocks);

      this.input.on("pointerdown", this.destroyBlock, this);
    }

    // method to destroy a block
    destroyBlock(e) {
        // convert pointer coordinates to world coordinates
        let worldX = this.toWorldScale(e.x);
        let worldY = this.toWorldScale(e.y);

        this.world.destroyBlock(worldX, worldY);
    }

    // simple function to convert pixels to meters
    toWorldScale(n) {
        return n / gameOptions.worldScale;
    }

    update(t, dt) {
        this.world.update(dt / 1000 * 2, this.idol);
    }
};
