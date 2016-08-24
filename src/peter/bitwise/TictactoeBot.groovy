package peter.bitwise

import groovy.transform.CompileStatic
import peter.util.ConfigData

import javax.servlet.ServletContext

/**
 * Created by Petr Matyukov on 19.04.2016.
 */

enum Language {English, Russian}

class TictactoeBotDictionary {
    private ConfigObject config
    private Language language
    private String[] sentences
    private Integer sentenceIndex = 0
    private String illegalMove
    private String newGameBotFirst
    private String newGameBotSecond
    private String help
    private String scoreTotal
    private String scorePersonal
    private String vote
    private String endBotWon
    private String endHumanWon
    private String  endDraw

    Language getLanguage() {
        return language
    }

    String saySentence(String wnumber) {
        Hashtable data = ConfigData[config.data_file]
        sentenceIndex = data["wnumber." + wnumber + ".sentenceIndex"]
        if(sentenceIndex == null || sentenceIndex >= sentences.size())
            sentenceIndex = 0
        String sentence = sentences[sentenceIndex]
        sentenceIndex += 1
        if(sentenceIndex == sentences.size())
            sentenceIndex = 0
        data["wnumber." + wnumber + ".sentenceIndex"] = sentenceIndex
        ConfigData.save(config.data_file)
        return sentence
    }

    String sayIllegalMove() {
        return illegalMove
    }

    String sayNewGameBotFirst() {
        return newGameBotFirst
    }

    String sayNewGameBotSecond() {
        return newGameBotSecond
    }

    String sayHelp() {
        return help
    }

    String sayScoreTotal(){
        return scoreTotal
    }

    String sayScorePersonal() {
        return scorePersonal
    }

    String sayVote() {
        return vote
    }

    String sayEndBotWon() {
        return endBotWon
    }

    String sayEndHumanWon() {
        return endHumanWon
    }

    String sayEndDraw() {
        return endDraw
    }

    TictactoeBotDictionary(Language lang, ConfigObject config) {
        this.config = config
        language = lang
        switch(language){
            case Language.Russian:
                sentences = config.dictionary.russian.sentences
                illegalMove = config.dictionary.russian.illegal_move
                newGameBotFirst = config.dictionary.russian.new_game_bot_first
                newGameBotSecond = config.dictionary.russian.new_game_bot_second
                help = config.dictionary.russian.help
                scoreTotal = config.dictionary.russian.score_total
                scorePersonal = config.dictionary.russian.score_personal
                vote = config.dictionary.russian.vote
                endBotWon = config.dictionary.russian.end_bot_won
                endHumanWon = config.dictionary.russian.end_human_won
                endDraw = config.dictionary.russian.end_draw
                break
            case Language.English:
                sentences = config.dictionary.english.sentences
                illegalMove = config.dictionary.english.illegal_move
                newGameBotFirst = config.dictionary.english.new_game_bot_first
                newGameBotSecond = config.dictionary.english.new_game_bot_second
                help = config.dictionary.english.help
                scoreTotal = config.dictionary.english.score_total
                scorePersonal = config.dictionary.english.score_personal
                vote = config.dictionary.english.vote
                endBotWon = config.dictionary.english.end_bot_won
                endHumanWon = config.dictionary.english.end_human_won
                endDraw = config.dictionary.english.end_draw
                break
        }
    }
}

//@CompileStatic
class TictactoeBot extends Tictactoe {
    static String dataFile = null
    static Hashtable data = null
    private List _empty_fields = []
    private TictactoeBotDictionary botDictionary
    boolean human
    boolean bot
    long lastMove = 0


    def TictactoeBot(Language lang, ConfigObject config, int vertical, int horizontal, int winNumber = 0){
        super(vertical, horizontal, winNumber)
        if(dataFile == null || data == null) {
            dataFile = config.data_file
            data = (Hashtable)(ConfigData[dataFile])
        }
        human = rnd.nextBoolean()
        bot = !human
        for(i in 0..height-1)
            for(j in 0..width-1) {
                def button = "."
                for(k in 0.._empty_fields.size())
                    button += "‌"
                _empty_fields += button
            }
        botDictionary = new TictactoeBotDictionary(lang, config)
    }

    def newGame() {
        super.newGame()
        human = rnd.nextBoolean()
        bot = !human
    }

    def String drawBoard() {
        return drawBoard(getBoard())
    }

    def String drawBoard(int[][] board) {
        int result
        long win
        (result, win) = gameResult()
        // Рисуем поле для игры
        String res = ""
        for(i in 0..height-1) {
            res += "<navigation>"
            for(j in 0..width-1) {
                if(win){
                  if(win & (1L << (i * width + j)))
                      res += '<link pageId="move.groovy?cell=' + (i * 100 + j) + '"><div protocol="telegram"></div>' + (board[i][j] == -1 ? "❌" : board[i][j] == 1 ? "⭕️" : _empty_fields[i * width + j]) + '</link>'
                  else
                      res += '<link pageId="move.groovy?cell=' + (i * 100 + j) + '"><div protocol="telegram"></div>' + (board[i][j] == -1 ? "✘" : board[i][j] == 1 ? "❍" : _empty_fields[i * width + j]) + '</link>'
                } else {
                    if (lastMove == (1L << (i * width + j)))
                        res += '<link pageId="move.groovy?cell=' + (i * 100 + j) + '"><div protocol="telegram"></div>' + (board[i][j] == -1 ? "❌" : board[i][j] == 1 ? "⭕️" : _empty_fields[i * width + j]) + '</link>'
                    else
                        res += '<link pageId="move.groovy?cell=' + (i * 100 + j) + '"><div protocol="telegram"></div>' + (board[i][j] == -1 ? "✘" : board[i][j] == 1 ? "❍" : _empty_fields[i * width + j]) + '</link>'
                }
            }
            if(i == 0)
                res += '<link pageId="score.groovy"><div protocol="telegram"></div>⚖</link>'
            else if(i == 1)
                res += '<link pageId="start.groovy"><div protocol="telegram"></div>💣</link>'
            else if(i == 2)
                res += '<link pageId="help.groovy"><div protocol="telegram"></div>❓</link>'
            else if(i == 3) {
                if(botDictionary.getLanguage() == Language.English)
                    res += '<link pageId="new_game.groovy?board=33&amp;language=en"><div protocol="telegram"></div>⛔</link>'
                else
                    res += '<link pageId="new_game.groovy?board=33&amp;language=ru"><div protocol="telegram"></div>⛔</link>'
            }
            else if(i == 4)
                res += '<link pageId="vote.groovy"><div protocol="telegram"></div>👍</link>'
            res += "</navigation>"
        }
        return res
    }

    def String saySentence(String wnumber) {
        return '<?xml version="1.0" encoding="UTF-8"?><page version="2.0" style="category"> <div protocol="telegram">' + botDictionary.saySentence(wnumber) + '</div>' + drawBoard() + '</page>'
    }

    def String saySentence(int[][] board, String say, String wnumber) {
        switch (say) {
            case "nothing":
                return '<?xml version="1.0" encoding="UTF-8"?><page version="2.0" style="category"> <div protocol="telegram"></div>' + drawBoard(board) + '</page>'
                break
            case "something":
                return '<?xml version="1.0" encoding="UTF-8"?><page version="2.0" style="category"> <div protocol="telegram">' + botDictionary.saySentence(wnumber) + '</div>' + drawBoard(board) + '</page>'
                break
            default:
                return '<?xml version="1.0" encoding="UTF-8"?><page version="2.0" style="category"> <div protocol="telegram">' + say + '</div>' + drawBoard(board) + '</page>'
                break
        }
    }

    def String sayNewGameBotFirst() {
        return """<?xml version="1.0" encoding="UTF-8"?>
                        <page version="2.0" style="category">
                          <div protocol="telegram">""" + botDictionary.sayNewGameBotFirst() + '</div>' + drawBoard() + '</page>'
    }

    def String sayNewGameBotSecond() {
        return """<?xml version="1.0" encoding="UTF-8"?>
                        <page version="2.0" style="category">
                          <div protocol="telegram">""" + botDictionary.sayNewGameBotSecond() + '</div>' + drawBoard() + '</page>'
    }

    def String sayIllegalMove(){
        return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayIllegalMove() + '</div>' + drawBoard() + '</page>'
    }

    def String sayHelp(){
        return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayHelp() + '</div>' + drawBoard() + '</page>'
    }

    def String sayVote(){
        return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayVote() + '</div>' + drawBoard() + '</page>'
    }

    def String sayScore(String wnumber) {
        if(!data.containsKey("total.bot"))
            data["total.bot"] = 0
        if(!data.containsKey("total.human"))
            data["total.human"] = 0
        if(!data.containsKey(wnumber + ".bot"))
            data[wnumber + ".bot"] = 0
        if(!data.containsKey(wnumber + ".human"))
            data[wnumber + ".human"] = 0

        int botTotal = (int)data["total.bot"]
        int humanTotal = (int)data["total.human"]
        int botPersonal = (int)data[wnumber + ".bot"]
        int humanPersonal = (int)data[wnumber + ".human"]

        return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '<br/>' +
                botDictionary.sayVote() + '</div>' + drawBoard() + "</page>"
    }


    def String sayEndOfGame(String wnumber, ServletContext context){
        if(!data.containsKey("total.bot"))
            data["total.bot"] = 0
        if(!data.containsKey("total.human"))
            data["total.human"] = 0
        if(!data.containsKey(wnumber + ".bot"))
            data[wnumber + ".bot"] = 0
        if(!data.containsKey(wnumber + ".human"))
            data[wnumber + ".human"] = 0

        int botTotal = (int)data["total.bot"]
        int humanTotal = (int)data["total.human"]
        int botPersonal = (int)data[wnumber + ".bot"]
        int humanPersonal = (int)data[wnumber + ".human"]

        int res
        (res) = gameResult()
        switch (res) {
            case -1: // Крестики выиграли
                if(bot) { // Бот играл крестиками
                    botTotal += 1
                    botPersonal += 1
                } else {
                    humanTotal += 1
                    humanPersonal += 1
                }
                break
            case 1: // Нолики выиграли
                if(human) { // Бот играл ноликами
                    botTotal += 1
                    botPersonal += 1
                } else {
                    humanTotal += 1
                    humanPersonal += 1
                }
                break
            case 0: // Ничья
                botTotal += 1
                botPersonal += 1
                humanTotal += 1
                humanPersonal += 1
                break
        }
        data["total.bot"] = botTotal
        data["total.human"] = humanTotal
        data[wnumber + ".bot"] = botPersonal
        data[wnumber + ".human"] = humanPersonal
        ConfigData.save(dataFile)

        switch (res) {
            case -1: // Крестики выиграли
                if(bot) { // Бот играл крестиками
                    return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayEndBotWon() + '<br/>' +
                            botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                            botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '</div>' + drawBoard() + """
                  </page>"""
                } else { // Человек играл крестиками
                    return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayEndHumanWon() + '<br/>' +
                            botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                            botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '</div>' + drawBoard() + """
                  </page>"""
                }
                break
            case 1: // Нолики выиграли
                if(human) { // Бот играл ноликами
                    return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayEndBotWon() + '<br/>' +
                            botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                            botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '</div>' + drawBoard() + """
                  </page>"""
                } else { // Человек играл ноликами
                    return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayEndHumanWon() + '<br/>' +
                            botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                            botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '</div>' + drawBoard() + """
                  </page>"""
                }
                break
            case 0: // Ничья
                return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayEndDraw() + '<br/>' +
                        botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                        botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '</div>' + drawBoard() + """
                  </page>"""
                break
        }
    }
}
