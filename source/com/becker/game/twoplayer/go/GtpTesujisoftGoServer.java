package com.becker.game.twoplayer.go;



import java.io.*;
import java.util.ArrayList;
import java.util.List;

import go.Point;
import gtp.GtpServer;
import utils.StringUtils;
import utils.Options;
import version.Version;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.search.SearchStrategy;

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

    private boolean m_nextResponseFixed;

    private boolean m_nextStatus;

    /** Delay every command (seconds) */
    private int m_delay;
    private int m_size;
    private boolean[][] m_alreadyPlayed;
    private String m_nextResponse;
    private Thread m_thread;
    private List m_commands;

    private GoController m_controller;



    public GtpTesujisoftGoServer(InputStream in, OutputStream out, PrintStream log)
        throws Exception
    {
        super(in, out, log);

              // this will load the resources for the specified game.
        GameContext.loadGameResources("go", "com.becker.game.twoplayer.go.ui.GoPanel");
        GameContext.setDebugMode(0);

        m_commands = new ArrayList();
        m_commands.add("boardsize");
        m_commands.add("clear_board");
        m_commands.add("echo");
        m_commands.add("echo_err");
        m_commands.add("fixed_handicap");
        m_commands.add("final_score");
        m_commands.add("final_status_list");
        m_commands.add("genmove");
        m_commands.add("gogui_interrupt");
        m_commands.add("list_commands");
        m_commands.add("known_command");
        m_commands.add("komi");
        m_commands.add("name");
        m_commands.add("play");
        m_commands.add("protocol_version");
        //m_commands.add("reg_gendmove");
        //m_commands.add("time_settings");
        m_commands.add("undo");
        m_commands.add("quit");
        m_commands.add("tesujisoft_bwboard");
        m_commands.add("version");

        initSize(19);

        m_thread = Thread.currentThread();
    }


    public boolean handleCommand(String cmdLine, StringBuffer response)
    {
        String[] cmdArray = StringUtils.tokenize(cmdLine);
        String cmd = cmdArray[0];
        boolean status = true;
        if (m_nextResponseFixed
            && ! (cmd.equals("dummy_next_failure")
                  || cmd.equals("dummy_next_success")))
        {
            status = m_nextStatus;
            response.append(m_nextResponse);
            m_nextResponseFixed = false;
        }
        else if (cmd.equals("boardsize"))
            status = cmdBoardsize(cmdArray, response);
        else if (cmd.equals("clear_board"))
            status = cmdClearBoard(response);
        else if (cmd.equals("tesujisoft_bwboard"))
            bwBoard(response);
        else if (cmd.equals("tesujisoft_delay"))
            status = cmdDelay(cmdArray, response);
        else if (cmd.equals("tesujisoft_invalid"))
            cmdInvalid();
        else if (cmd.equals("tesujisoft_long_response"))
            status = cmdLongResponse(cmdArray, response);
        else if (cmd.equals("echo"))
            echo(cmdLine, response);
        else if (cmd.equals("echo_err"))
            echoErr(cmdLine);
        else if (cmd.equals("final_score")) {
            status = cmdFinalScore(response);
        }
        else if (cmd.equals("final_status_list")) {
            status = cmdFinalStatusList(cmd, response);
        }
        else if (cmd.equals("fixed_handicap")) {
            status = cmdFixedHandicap(cmdArray, response);
        }
        else if (cmd.equals("genmove"))
            status = cmdGenmove(response);
        else if (cmd.equals("gogui_interrupt"))
            ;
        else if (cmd.equals("komi")) {
            status = cmdKomi(cmdArray, response);
        }
        else if (cmd.equals("name"))
            response.append("GtpTesujisoft");
        else if (cmd.equals("play"))
            status = cmdPlay(cmdArray, response);
        else if (cmd.equals("protocol_version"))
            response.append("2");
        else if (cmd.equals("known_command")) {
            status = cmdKnown(cmd);
        }
        else if (cmd.equals("list_commands")) {
            for (int i=0; i<m_commands.size(); i++) {
                response.append(m_commands.get(i) + "\n");
            }
        }
        else if (cmd.equals("undo")) {
            status = cmdUndo(response);
        }
        else if (cmd.equals("version"))
            response.append(Version.get());
        else if (cmd.equals("quit"))
            ;
        else
        {
            response.append("unknown command");
            status = false;
        }
        return status;
    }


    public void interruptCommand()
    {
        m_thread.interrupt();
    }

    private void initSize(int size) {
        m_controller = new GoController(size, size, 0);
        m_controller.setAlphaBeta(true);
        m_controller.setLookAhead(3);
        m_controller.setPercentageBestMoves(40);
        m_controller.setQuiescence(true); // take stoo long if on
        m_controller.setSearchStrategyMethod(SearchStrategy.MINIMAX);
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

    private boolean cmdClearBoard(StringBuffer response)
    {
        //initSize(m_size);
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
        double blackScore = m_controller.getScore(true);
        double whiteScore = m_controller.getScore(false);
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
     * @@ need to implment (easy)
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
        //System.out.println("in genmove. response="+response);

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

    private boolean cmdLongResponse(String[] cmdArray, StringBuffer response)
    {
        IntegerArgument argument = parseIntegerArgument(cmdArray, response);
        if (argument == null)
            return false;
        for (int i = 1; i <= argument.m_integer; ++i)
        {
            response.append(i);
            response.append("\n");
        }
        return true;
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

    private boolean cmdKnown(String cmd) {
        return m_commands.contains(cmd);
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

