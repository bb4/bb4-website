package com.becker.game.twoplayer.go;



import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.search.SearchStrategy;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import go.Point;
import gtp.GtpServer;
import utils.Options;
import utils.StringUtils;
import version.Version;

import java.io.*;
import java.util.List;

//----------------------------------------------------------------------------

/**  Wrapper for testing GTP controlling programs (like GoGui for example).
 *
 * The key commands are:
 *   boardSize
 *   clear_board
 *   gen_move
 *   play
 *
 * This class wraps the GoController and provides an interface to a GTP based controller (front end ui).
 * GoGui is typically the controller I have used, but it could be any GTP based UI.
 *
 * @@ add time settings in controller. implement time_settings comand
 * @@ implement reg_genmove
 *
 *  @author Barry Becker
 */
public class GtpTesujisoftGoServer
    extends GtpServer
{

    /** Delay every command (seconds) */
    private int m_delay;
    private int m_size;

    private Thread m_thread;

    private GoController m_controller;

    /**
     * the allowed GTP commands (most are required, some are optional)
     */
    private enum Command {boardsize, clear_board, echo, echo_err, fixed_handicap,
                         final_score, final_status_list, genmove, gogui_interrupt,
                         list_commands, known_command, komi, name, play,
                         protocol_version, reg_genmove, time_settings, undo, quit,
                         tesujisoft_bwboard, tesujisoft_delay, tesujisoft_invalid,
                         version}


    public GtpTesujisoftGoServer(InputStream in, OutputStream out, PrintStream log)
        throws Exception
    {
        super(in, out, log);

              // this will load the resources for the specified game.
        GameContext.loadGameResources("go", "com.becker.game.twoplayer.go.ui.GoPanel");
        GameContext.setDebugMode(0);

        initSize(19);

        m_thread = Thread.currentThread();
    }


    public boolean handleCommand(String cmdLine, StringBuffer response)
    {
        String[] cmdArray = StringUtils.tokenize(cmdLine);
        String cmdStr = cmdArray[0];
        boolean status = true;

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
                response.append("2"); break;
            case known_command :
                Command.valueOf(cmdArray[1]); break;
            case list_commands :
                for (int i=0; i<Command.values().length; i++) {
                    response.append(Command.values()[i] + "\n");
                }
                break;
            case undo :
                cmdUndo(response); break;
            case version :
                response.append(Version.get()); break;
            case quit :
                break;
            default :  {
                response.append("unknown command");
                status = false;
            }
        }

        return status;
    }


    public void interruptCommand()
    {
        m_thread.interrupt();
    }

    private void initSize(int size) {
        m_controller = new GoController(size, size, 0);
        TwoPlayerOptions options = m_controller.getOptions();
        options.setAlphaBeta(true);
        options.setLookAhead(2);
        options.setPercentageBestMoves(50);
        options.setQuiescence(false);
        options.setSearchStrategyMethod(SearchStrategy.MINIMAX);
        m_size = size;
    }

    private void bwBoard(StringBuffer response)
    {
        response.append("\n");
        for (int x = 0; x < m_size; ++x)
        {
            for (int y = 0; y < m_size; ++y)
                response.append(Math.random() > 0.5 ? "B " : "W ");
            response.append("\n");
        }
    }


    private boolean cmdBoardsize(String[] cmdArray, StringBuffer response)
    {
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

    private boolean cmdClearBoard()
    {
       m_controller.reset();
        return true;
    }

    private boolean cmdDelay(String[] cmdArray, StringBuffer response)
    {
        IntegerArgument argument = parseIntegerArgument(cmdArray, response);
        if (argument == null)
        {
            response.delete(0, response.length());
            response.append(m_delay);
            return true;
        }
        if (argument.m_integer < 0)
        {
            response.append("Argument must be positive");
            return false;
        }
        m_delay = argument.m_integer;
        return true;
    }

    private boolean cmdFinalScore(StringBuffer response) {
        double blackScore = m_controller.getFinalScore(true);
        double whiteScore = m_controller.getFinalScore(false);
        if (blackScore > whiteScore) {
            response.append("B+" + (blackScore - whiteScore));
        } else if (blackScore < whiteScore) {
            response.append("W+" + (whiteScore - blackScore));
        } else {
            response.append("0");
        }
        return true;
    }

    /**
     * @@ need to implment
     */
    private boolean cmdFinalStatusList(String cmd, StringBuffer response) {
        assert false : "final_status_list command not yet implemented";
        return true;
    }


    private boolean cmdFixedHandicap(String[] cmdArray, StringBuffer response) {
        IntegerArgument argument =
            parseIntegerArgument(cmdArray, response);
        if (argument == null)
             return false;

        int numHandicapStones = argument.m_integer;
        m_controller.setHandicap(numHandicapStones);

        List moves = m_controller.getMoveList();

        if  (moves == null || moves.size() == 0)
        {
            response.append("Invalid number of handicap stones");
            return false;
        }
        StringBuffer pointList = new StringBuffer(128);
        for (int i = 0; i < moves.size(); ++i)
        {
            GoMove pos = (GoMove) moves.get(i);
            go.Point point = new go.Point(pos.getToCol(), pos.getToRow());
            if (pointList.length() > 0)
                pointList.append(" ");
            pointList.append(point);
        }
        response.append(pointList);

        return true;
    }

    private boolean cmdGenmove(StringBuffer response)
    {
        boolean blackPlays = m_controller.getCurrentPlayer().equals(m_controller.getPlayer1());
        m_controller.requestComputerMove( blackPlays, true );

        GoMove m = (GoMove) m_controller.getBoard().getLastMove();
        //System.out.println("got " + m);

        Point  point = new Point(m.getToRow()-1, m.getToCol()-1);
        response.append(Point.toString(point));

        return true;
    }

    private void cmdInvalid()
    {
        printInvalidResponse("This is an invalid GTP response.\n" +
                             "It does not start with a status character.\n");
    }



    private boolean cmdPlay(String[] cmdArray, StringBuffer response)
    {
        ColorPointArgument argument =
            parseColorPointArgument(cmdArray, response, m_size);
        if (argument == null)
            return false;

        Point point = argument.m_point;

        if (point != null)  {
            boolean isBlack = m_controller.getCurrentPlayer().equals(m_controller.getPlayer1());
            GoMove move = new GoMove(point.getX(), point.getY(), null, 0, new GoStone(isBlack));
            m_controller.manMoves(move);
        }
        return true;
    }


    private boolean cmdKomi(String[] cmdArray, StringBuffer response) {
        DoubleArgument argument =
            parseDoubleArgument(cmdArray, response);
        if (argument == null)
             return false;

        float komi = (float)argument.m_double;
        m_controller.setKomi(komi);
        return true;
    }

    private boolean cmdUndo(StringBuffer response) {
        Move m = m_controller.undoLastMove();
        if (m==null) {
            response.append("cannot undo");
            return false;
        }
        return true;
    }

    private void echo(String cmdLine, StringBuffer response)
    {
        int index = cmdLine.indexOf(" ");
        if (index < 0)
            return;
        response.append(cmdLine.substring(index + 1));
    }

    private void echoErr(String cmdLine)
    {
        int index = cmdLine.indexOf(" ");
        if (index < 0)
            return;
        System.err.println(cmdLine.substring(index + 1));
    }




//----------------------------------------------------------------------------

    public static final void main(String[] args)
    {
        try
        {
            String options[] = {
                "config:",
                "help",
                "log:",
                "version"
            };
            Options opt = Options.parse(args, options);
            if (opt.isSet("help"))
            {
                String helpText =
                    "Usage: java -classpath /home/becker/projects/java_projects/classes com.becker.game.twoplayer.go.GtpTesujisoftGoServer [options]\n" +
                    "\n" +
                    "-config       config file\n" +
                    "-help         display this help and exit\n" +
                    "-log file     log GTP stream to file\n" +
                    "-version      print version and exit\n";
                System.out.print(helpText);
                return;
            }
            if (opt.isSet("version"))
            {
                System.out.println("GtpTesujisoft " + Version.get());
                return;
            }
            PrintStream log = null;
            if (opt.isSet("log"))
            {
                File file = new File(opt.getString("log"));
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

