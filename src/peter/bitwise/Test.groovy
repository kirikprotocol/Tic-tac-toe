package peter.bitwise
import peter.util.ConfigData

/**
 * Created by Petr Matyukov on 01.05.2016.
 */
/*Tictactoe game = new Tictactoe(3, 3)
int[][] board = [[0, 0, -1],
                 [0, 0, 0],
                 [0, 0, 0]]
game.setBoard(board)
game.findMove()*/

Tictactoe game = new Tictactoe(6, 6, 4)
game.autoPlay(65, 5, 64)
/*DB db = DBMaker.fileDB("c:/Data/file.db").make();
ConcurrentMap map = db.hashMap("map").make();
map.put("game", game);
db.close();*/

/*ConfigObject config = ConfigData.getConfig("tictactoe_config.groovy")
println config
Hashtable data = ConfigData["test.data"]
data["tiuyityut"] = "lsdkflsdklfkdslkf"
data["1"] = "lsxcxc cvcvcv dkflsdklfkdslkf"
ConfigData.save("test.data")
data = ConfigData["test.data"]
println data*/