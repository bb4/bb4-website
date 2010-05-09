All the games played on the Kiseido go server in 2002 by top level dan players.

about to restore :2002-03-26-4
java.lang.NullPointerException
	at com.becker.game.twoplayer.go.GoString.updateTerritory(GoString.java:195)
	at com.becker.game.twoplayer.go.GoGroup.updateTerritory(GoGroup.java:834)
	at com.becker.game.twoplayer.go.GoBoard.updateTerritory(GoBoard.java:676)
	at com.becker.game.twoplayer.go.GoBoard.updateGroupsAfterMoving(GoBoard.java:611)
	at com.becker.game.twoplayer.go.GoBoard.makeInternalMove(GoBoard.java:346)
	at com.becker.game.common.Board.makeMove(Board.java:194)
	at com.becker.game.twoplayer.common.TwoPlayerController.makeMove(TwoPlayerController.java:521)
	at com.becker.game.common.GameController.restoreGame(GameController.java:182)
	at com.becker.game.twoplayer.go.GoController.restoreFromFile(GoController.java:496)
	at com.becker.game.twoplayer.go.GoTestCase.restore(GoTestCase.java:44)
	at com.becker.game.twoplayer.go.TestKiseido2002.check(TestKiseido2002.java:88)
	at com.becker.game.twoplayer.go.TestKiseido2002.test2002_03_26_4(TestKiseido2002.java:15)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at com.intellij.rt.execution.junit2.JUnitStarter.main(JUnitStarter.java:31)


about to restore :2002-08-19-0
java.lang.NullPointerException
	at com.becker.game.twoplayer.go.GoBoard.findGroupFromInitialPosition(GoBoard.java:1570)
	at com.becker.game.twoplayer.go.GoBoard.findGroupFromInitialPosition(GoBoard.java:1542)
	at com.becker.game.twoplayer.go.GoBoard.updateAfterRemovingCaptures(GoBoard.java:1206)
	at com.becker.game.twoplayer.go.GoBoard.makeInternalMove(GoBoard.java:341)
	at com.becker.game.common.Board.makeMove(Board.java:194)
	at com.becker.game.twoplayer.common.TwoPlayerController.makeMove(TwoPlayerController.java:521)
	at com.becker.game.common.GameController.restoreGame(GameController.java:182)
	at com.becker.game.twoplayer.go.GoController.restoreFromFile(GoController.java:496)
	at com.becker.game.twoplayer.go.GoTestCase.restore(GoTestCase.java:44)
	at com.becker.game.twoplayer.go.TestKiseido2002.check(TestKiseido2002.java:88)
	at com.becker.game.twoplayer.go.TestKiseido2002.test2002_8_19_0(TestKiseido2002.java:19)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at com.intellij.rt.execution.junit2.JUnitStarter.main(JUnitStarter.java:31)

 has eyes! It was assumed not to
	at com.becker.game.twoplayer.go.GoBoard.cleanupGroups(GoBoard.java:1877)
	at com.becker.game.twoplayer.go.GoBoard.updateStringsAfterMoving(GoBoard.java:588)
	at com.becker.game.twoplayer.go.GoBoard.makeInternalMove(GoBoard.java:339)
	at com.becker.game.common.Board.makeMove(Board.java:194)
	at com.becker.game.twoplayer.go.GoController.generateMoves(GoController.java:656)
	at com.becker.game.twoplayer.common.search.MiniMaxStrategy.search(MiniMaxStrategy.java:58)
	at com.becker.game.twoplayer.common.TwoPlayerController.findComputerMove(TwoPlayerController.java:456)
	at com.becker.game.twoplayer.common.TwoPlayerController.access$100(TwoPlayerController.java:25)
	at com.becker.game.twoplayer.common.TwoPlayerController$1.construct(TwoPlayerController.java:582)
	at com.becker.common.concurrency.Worker$1.run(Worker.java:111)
	at java.lang.Thread.run(Thread.java:595)
