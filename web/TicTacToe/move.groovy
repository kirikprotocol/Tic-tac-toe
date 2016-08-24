import peter.bitwise.TictactoeBot
import peter.util.ConfigData

/**
 * Created by Petr Matyukov on 14.04.2016.
 */


if (!session) {
    session = request.getSession(true)
}

if (!session.counter) {
    session.counter = 1
}

// Считываем wnumber
String cell = params["cell"]
String wnumber = params["subscriber"]
TictactoeBot game = session.getAttribute("game")
String resp = ''
if(cell && game) {
    int res
    // Если игра уже окончена - начнем новую
    (res) = game.gameResult()
    if(res != 2) {
        game.newGame()
        if(game.human) //Игрок ходит первым
            resp = game.sayNewGameBotSecond()
        else { //Бот наносит первый удар
            long move
            (move) = game.findMove(2)
            game.makeMove(move, game.bot)
            resp = game.sayNewGameBotFirst()
        }
    } else {
        int cell_num = cell.toInteger()
        int i = (int)(cell_num/100)
        int j = cell_num % 100
        long move = 1L << (i*game.width + j)
        // Сделаем ход человека на доске
        if(!game.makeMove(move, game.human)) { // Клетка занята и сюда ходить нельзя
            resp = game.sayIllegalMove()
        } else {
            (res) = game.gameResult()
            if(res != 2) { // Определился победитель
                resp = game.sayEndOfGame(wnumber, context)
            }
            else {
                // Покажем ход человека на доске
                ConfigObject config = ConfigData.getConfig("tictactoe_config.groovy")
                String miniappName = config.miniapp_name
                session.setAttribute("board", game.getBoard())
                game.lastMove = move
                def pushURL = ("http://ec2.globalussd.mobi/push?protocol=telegram&service=" + miniappName + "&subscriber=" + wnumber + "&say=nothing").toURL()
                pushURL.readLines()
                long systemTime = System.currentTimeMillis()
                // Бот наносит ответный удар
                int score
                if(game.height == 3)
                    (move, score) = game.findMove()
                else {
                    (move, score) = game.findMove(5, 10)
                }
                if(System.currentTimeMillis() - systemTime < 1000)
                    System.sleep(1000)
                game.makeMove(move, game.bot)
                game.lastMove = move
                (res) = game.gameResult()
                if(res != 2) { // Определился победитель
                    resp = game.sayEndOfGame(wnumber, context)
                }
                else {
                    resp = game.saySentence(wnumber)
                }
            }
        }
    }
}

println resp

session.counter = session.counter + 1