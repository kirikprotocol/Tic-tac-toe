package peter.bots

import groovy.transform.CompileStatic
import peter.games.*

import javax.servlet.ServletContext

/**
 * Created by Petr Matyukov on 19.04.2016.
 */

enum Language {English, Russian}

class TicTacToeBotDictionary {
    private Language language
    private String[] sentences
    private int sentenceIndex = 0
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

    String saySentence() {
        String sentence = sentences[sentenceIndex]
        sentenceIndex += 1
        if(sentenceIndex == sentences.size())
            sentenceIndex = 0
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

    TicTacToeBotDictionary(Language lang, ConfigObject config) {
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

@CompileStatic
class TicTacToeBot extends TicTacToe {
    static Hashtable score = null
    private List _empty_fields = []
    private TicTacToeBotDictionary botDictionary
    int human
    int bot


    def TicTacToeBot(Language lang, ConfigObject config, int vertical, int horizontal, int winNumber = 0){
        super(vertical, horizontal, winNumber)
        for(i in 0..vertical-1)
            for(j in 0..horizontal-1) {
                def button = "."
                for(k in 0.._empty_fields.size())
                    button += "‌"
                _empty_fields += button
            }
        botDictionary = new TicTacToeBotDictionary(lang, config)
    }

    def newGame() {
        super.newGame()
        human = super.firstPlayer
        bot = super.secondPlayer
    }

    def String drawBoard() {
        // Рисуем поле для игры
        String res = ""
        for(i in 0..vertical-1) {
            res += "<navigation>"
            for(j in 0..horizontal-1)
                res += '<link pageId="move.groovy?cell=' + (i * 100 + j) + '"><div protocol="telegram"></div>' + drawCell(i, j) + '</link>'
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

    def String saySentence() {
        return '<?xml version="1.0" encoding="UTF-8"?><page version="2.0" style="category"> <div protocol="telegram">' + botDictionary.saySentence() + '</div>' + drawBoard() + '</page>'
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

    private def String drawCell(int i, int j) {
        if (board[i][j] == EMPTY)
            return _empty_fields[i * horizontal + j]
        else if(board[i][j] == CROSS)
            return "✘"
        else
            return "❍"
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
        if(!TicTacToeBot.score.containsKey("total.bot"))
            TicTacToeBot.score["total.bot"] = 0
        if(!TicTacToeBot.score.containsKey("total.human"))
            TicTacToeBot.score["total.human"] = 0
        if(!TicTacToeBot.score.containsKey(wnumber + ".bot"))
            TicTacToeBot.score[wnumber + ".bot"] = 0
        if(!TicTacToeBot.score.containsKey(wnumber + ".human"))
            TicTacToeBot.score[wnumber + ".human"] = 0

        int botTotal = (int)TicTacToeBot.score["total.bot"]
        int humanTotal = (int)TicTacToeBot.score["total.human"]
        int botPersonal = (int)TicTacToeBot.score[wnumber + ".bot"]
        int humanPersonal = (int)TicTacToeBot.score[wnumber + ".human"]

        return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '<br/>' +
                botDictionary.sayVote() + '</div>' + drawBoard() + "</page>"
    }


    def String sayEndOfGame(String wnumber, ServletContext context){
        if(!TicTacToeBot.score.containsKey("total.bot"))
            TicTacToeBot.score["total.bot"] = 0
        if(!TicTacToeBot.score.containsKey("total.human"))
            TicTacToeBot.score["total.human"] = 0
        if(!TicTacToeBot.score.containsKey(wnumber + ".bot"))
            TicTacToeBot.score[wnumber + ".bot"] = 0
        if(!TicTacToeBot.score.containsKey(wnumber + ".human"))
            TicTacToeBot.score[wnumber + ".human"] = 0

        int botTotal = (int)TicTacToeBot.score["total.bot"]
        int humanTotal = (int)TicTacToeBot.score["total.human"]
        int botPersonal = (int)TicTacToeBot.score[wnumber + ".bot"]
        int humanPersonal = (int)TicTacToeBot.score[wnumber + ".human"]

        int res = gameResult()
        switch (res) {
            case bot:
                botTotal += 1
                botPersonal += 1
                break
            case human:
                humanTotal += 1
                humanPersonal += 1
                break
            case EMPTY:
                botTotal += 1
                botPersonal += 1
                humanTotal += 1
                humanPersonal += 1
                break
        }
        TicTacToeBot.score["total.bot"] = botTotal
        TicTacToeBot.score["total.human"] = humanTotal
        TicTacToeBot.score[wnumber + ".bot"] = botPersonal
        TicTacToeBot.score[wnumber + ".human"] = humanPersonal
        try {
            String scoreFilePath = ((String)System.properties.store_dir) + "score.dat"
            FileOutputStream outputStream = new FileOutputStream(new File(scoreFilePath))
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(TicTacToeBot.score)
            objectOutputStream.close()
        }
        catch(IOException ex1) {
            ex1.printStackTrace();
        }

        switch (res) {
            case bot:
                return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayEndBotWon() + '<br/>' +
                        botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                        botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '</div>' + drawBoard() + """
                  </page>"""
                break
            case human:
                return """<?xml version="1.0" encoding="UTF-8"?>
                  <page version="2.0" style="category">
                   <div protocol="telegram">""" + botDictionary.sayEndHumanWon() + '<br/>' +
                        botDictionary.sayScoreTotal() + " = " + botTotal + ":" + humanTotal + '<br/>' +
                        botDictionary.sayScorePersonal() + " = " + botPersonal + ":" + humanPersonal + '</div>' + drawBoard() + """
                  </page>"""
                break
            case EMPTY:
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
