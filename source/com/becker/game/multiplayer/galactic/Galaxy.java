package com.becker.game.multiplayer.galactic;

import com.becker.game.common.*;
import com.becker.game.multiplayer.galactic.player.*;

import java.util.*;

/**
 * Representation of a Galaxy as a Game Board
 *
 * @author Barry Becker
 */
public class Galaxy extends Board
{
    private static final int DEFAULT_NUM_PLANETS = 20;
    public static final int MAX_NUM_PLANETS = 80;
    public static final int MIN_NUM_PLANETS = 3;
    private static final Random RANDOM = new Random(0);

    private static char[] PLANET_NAMES ;
    static {
        PLANET_NAMES = new char[60];
        int ct=0;
        for (int c='A'; c<='Z'; c++) {
            PLANET_NAMES[ct++]=(char)c;
        }
        for (int c='a'; c<='z'; c++) {
            PLANET_NAMES[ct++]=(char)c;
        }
        for (int c='1'; c<='8'; c++) {
            PLANET_NAMES[ct++]=(char)c;
        }
    }


    private static int numPlanets_ = DEFAULT_NUM_PLANETS;

    // the list of planets on the board.
    // Does not change during the game.
    private static List<Planet> planets_ = null;

    private static Map<Character,Planet> hmPlanets_ = new HashMap<Character,Planet>();


    /** constructor
     *  @param numRows num rows
     *  @param numCols num cols
     */
    public Galaxy( int numRows, int numCols )
    {
        setSize( numRows, numCols );
    }


    /**
     *  reset the board to its initial state
     */
    public void reset()
    {
        super.reset();
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new BoardPosition( i, j, null);
            }
        }
    }

    public void initPlanets(GalacticPlayer[] players, GalacticOptions options)
    {
        hmPlanets_.clear();

        numPlanets_ = options.getNumPlanets();

        if (planets_ == null)  {
            planets_ = new ArrayList<Planet>();
        }

        planets_.clear();
        for (int i=0; i<getNumPlanets(); i++)
        {

            // find a random position
            int randRow;
            int randCol;
            BoardPosition position;
            // find an unoccupied position to place the new planet
            do {
                randRow = (int)(Math.random() * getNumRows())+1;
                randCol = (int)(Math.random() * getNumCols())+1;
                position = this.getPosition(randRow, randCol);
            } while (position.isOccupied());

            // initial ships and production factor
            int production = (int)( 1 + Math.max(0, RANDOM.nextGaussian()) * options.getPlanetProductionRate());
            int initialFleet = (int)( 1 + Math.max(0, RANDOM.nextGaussian()) * options.getInitialFleetSize());
            Planet planet = new Planet(PLANET_NAMES[i], initialFleet,
                                       production, position.getLocation());
            position.setPiece(planet);

            // substitute in the players home planets that have already been created.
            for (final GalacticPlayer newVar : players) {
                if (planet.getName() == newVar.getHomePlanet().getName()) {
                    Planet home = newVar.getHomePlanet();
                    position.setPiece(home);    // replace
                    home.setLocation(position.getLocation());
                }
            }
            // add the planet to our list
            planets_.add((Planet)position.getPiece());

            hmPlanets_.put(planet.getName(), (Planet)position.getPiece());
        }
    }

    /**
     * This method returns a copy of the planet list
     * @return an array of all the planets in the galaxy.
     */
    public static List<Planet> getPlanets()
    {
        return new ArrayList<Planet>(planets_);
    }

    /**
     * @param player  (if null return all planets in the galaxy)
     * @return the planets owned by the specified player.
     */
    public static List getPlanets(GalacticPlayer player)
    {
        if (player==null)
            return getPlanets();
        List playerPlanets = new ArrayList();
        Iterator it = planets_.iterator();
        while (it.hasNext()) {
            Planet planet = (Planet)it.next();
            if (planet.getOwner() == player)
                playerPlanets.add(planet);
        }
        return playerPlanets;
    }

    /**

     * @return  the number of planets in this galaxy.
     */
    public static int getNumPlanets()
    {
        return numPlanets_;
    }

    /**
     * @param numPlanets to have in this galaxy
     */
    public void setNumPlanets(int numPlanets)
    {
        if (numPlanets > MAX_NUM_PLANETS) {
            System.out.println( "You are not allowed to have more than "+ MAX_NUM_PLANETS );
            numPlanets_ = MAX_NUM_PLANETS;
        }
        numPlanets_ = numPlanets;
    }

    /**
     *
     * @param name  name of the planet to find
     * @return the planet that has the specified name
     */
    public static Planet getPlanet(char name)
    {
        Character c = name;
        Planet p = hmPlanets_.get(c);
        assert(p!=null);
        return p;
    }


    // must call reset() after changing the size
    public void setSize( int numRows, int numCols )
    {
        numRows_ = numRows;
        numCols_ = numCols;
        rowsTimesCols_ = numRows_ * numCols_;
        // we don't use the 0 edges of the board
        positions_ = new BoardPosition[numRows_ + 1][numCols_ + 1];
        reset();
    }

    public int getMaxNumMoves()
    {
        return rowsTimesCols_;
    }

    /**
     * given a move specification, execute it on the board
     * This applies the results for all the battles for one year (turn).
     *
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    protected boolean makeInternalMove( Move move )
    {
        // first allow all the planets to build for the year
        build();
        // go through all the battle results in order and adjust the planets to account for one elapsed year.

        //GalacticTurn gmove = (GalacticTurn)move;
        //destPlanet.setOwner( battle.getOwnerAfterAttack());
        //destPlanet.setNumShips( battle.getNumShipsAfterAttack() );
        return true;
    }

    private static void build()
    {
        for (int i=0; i<planets_.size(); i++)
            planets_.get(i).incrementYear();
    }


    public void higlightPlanet() {

    }

    /**
     * For galactic empire, undoing a move means turning time back a year and
     * restoring the state of the game one full turn earlier
     * @@ todo
     */
    protected void undoInternalMove( Move move )
    {
        GameContext.log(0,  "undo no implemented yet." );
        //clear(positions_[move.getToRow()][move.getToCol()]);
    }

    /**
     * @return true if all the planets are owned by a single player
     */
    public static boolean allPlanetsOwnedByOnePlayer()
    {
        Iterator it = planets_.iterator();
        Player player = planets_.get(0).getOwner();
        while (it.hasNext()) {
            Planet p = (Planet)it.next();
            if (p.getOwner() != player)
                return false;
        }
        return true;
    }

}
