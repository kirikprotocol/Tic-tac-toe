/**
 * Created by Petr Matyukov on 14.04.2016.
 */


import peter.bitwise.Language
import peter.bitwise.TictactoeBot
import peter.util.ConfigData


if (!session) {
    session = request.getSession(true)
}

if (!session.counter) {
    session.counter = 1
}

if(params.containsKey("board")) {
    // Создаем бота для запрошенной размерности игры
    ConfigObject config = ConfigData.getConfig("tictactoe_config.groovy")
    String size = params["board"]
    String wnumber = params["subscriber"]
    TictactoeBot game = null
    Language lang = Language.Russian
    if(params.containsKey("language")){
        if(params["language"] == "en")
            lang = Language.English
    }
    Hashtable data = ConfigData[config.data_file]
    data["wnumber." + wnumber + ".language"] = (lang == Language.English ? "en" : "ru")
    ConfigData.save(config.data_file)

    if(size == "45")
        game = new TictactoeBot(lang, config, 4, 5)
    else if(size == "56")
        game = new TictactoeBot(lang, config, 5, 6, 4)
    else if(size == "55")
        game = new TictactoeBot(lang, config, 5, 5, 4)
    else
        game = new TictactoeBot(lang, config, 3, 3)
    String resp = ""
    if(game.human) //Игрок ходит первым
        resp = game.sayNewGameBotSecond()
    else { //Бот наносит первый удар
        long move
        (move) = game.findMove(2)
        game.makeMove(move, game.bot)
        resp = game.sayNewGameBotFirst()
    }

    session.setAttribute("game", game)

    println resp
}

session.counter = session.counter + 1