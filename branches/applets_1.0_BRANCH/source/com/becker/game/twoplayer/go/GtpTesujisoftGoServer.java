package com.becker.game.twoplayer.go;

import com.becker.common.Location;
import com.becker.common.util.FileUtil;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.go.board.elements.GoStone;
import go.Point;
import gtp.GtpServer;
import utils.Options;
import utils.StringUtils;

import java.io.*;
import java.util.List;

/**
 * This class wraps the GoController and provides an interface to a GTP based controller (front end ui).
 * GoGui is typically the controller I have used, but it could be any GTP based UI.
 * 
 * This wraps my Tesujisoft go engine so that a client program can use it.
 * If you want to have the Tesujisoft go engine play on KGS use GtpKgsTesujisoftGoClient.
 *
 * The key commands are:
 *   boardSize
 *   clear_board
 *   gen_move
 *   play
 *
 * 
 * Inherits the ability to connect a Go program supporting GTP to a socket from GtpServer.
 *
 * @@ implement req_genmove
 * @@ implement final_status_list
 *
 *  @author Barry Becker
 */
public class GtpTesujisoftGoServer
    extends GtpServer
{

    /** Delay every command (seconds) */
    private int delay_;

    private int boardSize_;

    private Thread thread_;

    private GoController controller_;

    /**
     * the allowed GTP commands (most are required, some are optional)
     */
    private enum Command {boardsize, clear_board, echo, echo_err, fixed_handicap,
                         final_score, final_status_list, genmove, gogui_interrupt,
                         list_commands, known_command, komi, name, play,
                         protocol_version, reg_genmove, time_settings, time_left, undo, quit,
                         tesujisoft_bwboard, tesujisoft_delay, tesujisoft_invalid,
                         version}


    public GtpTesujisoftGoServer(InputStream in, OutputStream out, PrintStream logger)
        throws Exception
    {
        super(in, out, logger);

        // this will load the resources for the specified game.
        GameContext.loadGameResources("go");
        GameContext.setDebugMode(0);

        initSize(19);

        thread_ = Thread.currentThread();
    }

    /** where does this get set?
    public int getPort() {
        assert false; // dont call
        return 0;
    } */

    @Override
    public boolean handleCommand(String cmdLine, StringBuffer response)
    {
        String[] cmdArray = StringUtils.tokenize(cmdLine);
        String cmdStr = cmdArray[0];
        boolean status = true;
        // log("handling command="+ cmdStr);

        Command cmd = Command.valueOf(cmdStr);

        switch (cmd) {
            case boardsize :
                status = cmdBoardsize(cmdArray, response);
                break;
            case clear_board :
                status = cmdClearBoard();
                break;
            case echo :
                echo(cmdLine, response);
                break;
            case echo_err :
                echoErr(cmdLine);
                break;
            case tesujisoft_bwboard :
                bwBoard(response);
                break;
            case tesujisoft_delay :
                status = cmdDelay(cmdArray, response);
                break;
            case tesujisoft_invalid :
                cmdInvalid();
                break;
            case final_score :
                status = cmdFinalScore(response);
                break;
            case final_status_list :
                status = cmdFinalStatusList(cmdArray[1], response);
                break;
            case fixed_handicap :
                status = cmdFixedHandicap(cmdArray, response);
                break;
            case genmove :
                status = cmdGenmove(response);
                break;
            case gogui_interrupt :
                break;
            case komi :
                status = cmdKomi(cmdArray, response);
                break;
            case name :
                response.append("GtpTesujisoft");
                break;
            case play :
                status = cmdPlay(cmdArray, response);
                break;
            case protocol_version :
                response.append('2'); break;
            case known_command :
                Command.valueOf(cmdArray[1]); break;
            case list_commands :
                listCommands(response);
                break;
            case time_settings :
                cmdTimeSettings(cmdArray, response); break;
            case time_left :
                cmdTimeLeft(cmdArray, response); break;
            case undo :
                cmdUndo(response); break;
            case version :
                response.append(GoController.VERSION); break;
            case quit : break;
            default :
                response.append("unknown command");
                status = false;
                break;
        }
        return status;
    }


    @Override
    public void interruptCommand()
    {
        thread_.interrupt();
    }

    private void initSize(int size) {
        controller_ = new GoController(size, size, 0);
        SearchOptions options = controller_.getTwoPlayerOptions().getSearchOptions();

        options.getBruteSearchOptions().setAlphaBeta(true);
        options.getBruteSearchOptions().setLookAhead(2);
        options.getBruteSearchOptions().setQuiescence(false);
        options.getBestMovesSearchOptions().setPercentageBestMoves(50);
        options.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
        boardSize_ = size;
    }

    private void bwBoard(StringBuffer response) {
        response.append('\n');
        for (int x = 0; x < boardSize_; ++x)
        {
            for (int y = 0; y < boardSize_; ++y)
                response.append(Math.random() > 0.5 ? "B " : "W ");
            response.append('\n');
        }
    }

    private boolean cmdBoardsize(String[] cmdArray, StringBuffer response) {
        IntegerArgument argument = parseIntegerArgument(cmdArray, response);
        if (argument == null)
            return false;
        if (argument.m_integer < 1 || argument.m_integer > 100)
        {
            response.append("Invalid size");
            return false;
        }
        initSize(argument.m_integer);
        return true;
    }

    private boolean cmdClearBoard() {
        controller_.reset();
        return true;
    }

    private boolean cmdDelay(String[] cmdArray, StringBuffer response) {
        IntegerArgument argument = parseIntegerArgument(cmdArray, response);
        if (argument == null)
        {
            response.delete(0, response.length());
            response.append(delay_);
            return true;
        }
        if (argument.m_integer < 0)
        {
            response.append("Argument must be positive");
            return false;
        }
        delay_ = argument.m_integer;
        return true;
    }

    private static void listCommands(StringBuffer response) {
        for (int i=0; i<Command.values().length; i++) {
            response.append(Command.values()[i]).append("\n");
        }
    }

    private boolean cmdFinalScore(StringBuffer response) {
        double blackScore = controller_.getFinalScore(true);
        double whiteScore = controller_.getFinalScore(false);
        if (blackScore > whiteScore) {
            response.append("B + ").append(blackScore - whiteScore);
        } else if (blackScore < whiteScore) {
            response.append("W + ").append(whiteScore - blackScore);
        } else {
            response.append('0');
        }
        return true;
    }

    /**
     * @@ need to implement
     */
    private static boolean cmdFinalStatusList(String cmd, StringBuffer response) {
        assert false : "final_status_list command not yet implemented";
        return true;
    }


    private boolean cmdFixedHandicap(String[] cmdArray, StringBuffer response) {
        IntegerArgument argument =
            parseIntegerArgument(cmdArray, response);
        if (argument == null)
             return false;

        int numHandicapStones = argument.m_integer;
        controller_.setHandicap(numHandicapStones);

        List moves = controller_.getMoveList();

        if  (moves == null || moves.size() == 0)
        {
            response.append("Invalid number of handicap stones");
            return false;
        }
        StringBuffer pointList = new StringBuffer(128);
        for (int i = 0; i < moves.size(); ++i)
        {
            GoMove pos = (GoMove) moves.get(i);
            Point point = new Point(pos.getToCol(), pos.getToRow());
            if (pointList.length() > 0)
                pointList.append(' ');
            pointList.append(point);
        }
        response.append(pointList);

        return true;
    }

    private boolean cmdGenmove(StringBuffer response) {
        boolean blackPlays = controller_.getCurrentPlayer().equals(controller_.getPlayers().getPlayer1());
        controller_.requestComputerMove( blackPlays, true );

        GoMove m = (GoMove) controller_.getLastMove();
        //System.out.println("got " + m);

        Point  point = new Point(m.getToRow()-1, m.getToCol()-1);
        response.append(Point.toString(point));

        return true;
    }

    private void cmdInvalid() {
        printInvalidResponse("This is an invalid GTP response.\n" +
                             "It does not start with a status character.\n");
    }

    private boolean cmdPlay(String[] cmdArray, StringBuffer response) {
        ColorPointArgument argument =
            parseColorPointArgument(cmdArray, response, boardSize_);
        if (argument == null)
            return false;

        Point point = argument.m_point;

        if (point != null)  {
            boolean isBlack = controller_.getCurrentPlayer().equals(controller_.getPlayers().getPlayer1());
            GoMove move = new GoMove(new Location(point.getX()+1, point.getY()+1),  0, new GoStone(isBlack));
            controller_.manMoves(move);
        }
        return true;
    }

    private boolean cmdKomi(String[] cmdArray, StringBuffer response) {
        DoubleArgument argument =
            parseDoubleArgument(cmdArray, response);
        if (argument == null)
             return false;

        float komi = (float)argument.m_double;
        ((GoOptions)controller_.getOptions()).setKomi(komi);
        return true;
    }

    /**
     * Arguments: int main_time, int byo_yomi_time, int byo_yomi_stones
     *Fails:     syntax error
     * Returns:   nothing
     */
     private boolean cmdTimeSettings(String[] cmdArray, StringBuffer response) {

         //System.err.println("arg len for time_settings="+ cmdArray.length);
         //System.err.println("time_settings = main="+ cmdArray[1] ); //+"  byo_yomi=" + byo_yomi_time +" stones=" + byo_yomi_stones);
         return true;
    }

     /**
     * Arguments: int main_time, int byo_yomi_time, int byo_yomi_stones
     *Fails:     syntax error
     * Returns:   nothing
     */
     private boolean cmdTimeLeft(String[] cmdArray, StringBuffer response) {

         //System.err.println("arg len for time_left ="+ cmdArray.length);
         //System.err.println("time_left = main="+ cmdArray[1] ); //+"  byo_yomi=" + byo_yomi_time +" stones=" + byo_yomi_stones);
        return true;
    }

    private boolean cmdUndo(StringBuffer response) {
        Move m = controller_.undoLastMove();
        if (m==null) {
            response.append("cannot undo");
            return false;
        }
        return true;
    }

    private static void echo(String cmdLine, StringBuffer response)
    {
        int index = cmdLine.indexOf(" ");
        if (index < 0)
            return;
        response.append(cmdLine.substring(index + 1));
    }

    private static void echoErr(String cmdLine)
    {
        int index = cmdLine.indexOf(" ");
        if (index < 0)
            return;
        System.err.println(cmdLine.substring(index + 1));
    }


//----------------------------------------------------------------------------

    public static void main(String[] args)
    {
        try
        {
            String[] options = {
                "config:",
                "help",
                "log",
                "version"
            };
            Options opt = Options.parse(args, options);
            if (opt.isSet("help"))
            {
                String helpText =
                    "Usage: java -classpath "+
                        FileUtil.PROJECT_HOME +
                        "/classes com.becker.game.twoplayer.go.GtpTesujisoftGoServer [options]\n" +
                        '\n' +
                    "-config       config file\n" +
                    "-help         display this help and exit\n" +
                    "-log file     log GTP stream to file\n" +
                    "-version      print version and exit\n";
                System.out.print(helpText);
                return;
            }
            if (opt.isSet("version"))
            {
                System.out.println("GtpTesujisoft " + GoController.VERSION);
                return;
            }
            PrintStream log = null;
            if (opt.isSet("log"))
            {
                File file = new File(opt.getString("log"));
                log = new PrintStream(new FileOutputStream(file));
            }
            else {
                String logFile = FileUtil.getHomeDir() + "/temp/" + "log.txt";
                File file = new File(logFile);
                log = new PrintStream(new FileOutputStream(file));
            }

            GtpTesujisoftGoServer gtpTSGoServer = new GtpTesujisoftGoServer(System.in, System.out, log);
            gtpTSGoServer.mainLoop();
            if (log != null)
                log.close();
        }
        catch (Throwable t)
        {
            StringUtils.printException(t);
            System.exit(-1);
        }
    }
}
