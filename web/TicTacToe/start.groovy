import peter.bitwise.Tictactoe
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

String say = params["say"]
String wnumber = params["subscriber"]
TictactoeBot game = session.getAttribute("game")
int[][] board = (int[][])session.getAttribute("board")
String language = params["language"]
ConfigObject config = ConfigData.getConfig("tictactoe_config.groovy")
Hashtable data = ConfigData[config.data_file]
if(language) {
    data["wnumber." + wnumber + ".language"] = language
    ConfigData.save(config.data_file)
} else {
    language = data["wnumber." + wnumber + ".language"]
}

if(say && game && board) {
    println game.saySentence(board, say, wnumber)
} else {
    /*println """<?xml version="1.0" encoding="UTF-8"?>
        <page version="2.0">
          <div attributes="telegram.message.id: 12; telegram.message.edit: true; telegram.keep.session: true">
        	🤖 Одинокий Бот против Человечества?<br/>
        	Да, я - такой! <br/>
        	Loner bot against humanity
          </div>
          <navigation attributes="telegram.inline: true">
            <link pageId="start.groovy"> English </link>
            <link pageId="start.groovy"> Русский </link>
          </navigation>
          <div>HHHHHHHHHHHHHHHHH</div>
          <navigation>
            <link pageId="new_game.groovy?board=33&amp;language=en"><div protocol="telegram">☕</div>Play 3x3</link>
            <link pageId="new_game.groovy?board=33&amp;language=ru"><div protocol="telegram">☕</div>Играть 3х3</link>
          </navigation>
          <navigation>
            <link pageId="new_game.groovy?board=55&amp;language=en"><div protocol="telegram">🍵</div>Play 5x5</link>
            <link pageId="new_game.groovy?board=55&amp;language=ru"><div protocol="telegram">🍵</div>Играть 5х5</link>
          </navigation>
        </page>"""*/
    if(language) {
       switch(language) {
           case "en":
               println """<?xml version="1.0" encoding="UTF-8"?>
                    <page version="2.0">
                      <div protocol="telegram">
                        🤖 Lonely bot against humanity<br/>
                        Lets play Tic-Tac-Toe
                      </div>
                      <navigation>
                        <link pageId="new_game.groovy?board=33&amp;language=en"><div protocol="telegram">☕</div>Play 3x3</link>
                        <link pageId="new_game.groovy?board=55&amp;language=en"><div protocol="telegram">🍵</div>Play 5x5</link>
                        <link pageId="new_game.groovy?board=56&amp;language=en"><div protocol="telegram">🍵</div>Play 5x6</link>
                      </navigation>
                      <navigation>
                        <link pageId="start.groovy?language=ru"><div protocol="telegram">🍵</div>Давай по-нашему, чё...</link>
                      </navigation>
                    </page>"""
               break
               break
           case "ru":
               println """<?xml version="1.0" encoding="UTF-8"?>
                    <page version="2.0">
                      <div protocol="telegram">
                        🤖 Одинокий Бот против Человечества?<br/>
                        Да, я - такой! <br/>
                        Рискнешь ли сразиться в Крестики-Нолики, белковый разум?
                      </div>
                      <navigation>
                        <link pageId="new_game.groovy?board=33&amp;language=ru"><div protocol="telegram">☕</div>Играть 3х3</link>
                        <link pageId="new_game.groovy?board=55&amp;language=ru"><div protocol="telegram">🍵</div>Играть 5х5</link>
                        <link pageId="new_game.groovy?board=56&amp;language=ru"><div protocol="telegram">🍵</div>Играть 5х6</link>
                      </navigation>
                      <navigation>
                        <link pageId="start.groovy?language=en"><div protocol="telegram">🍵</div>English</link>
                        <link pageId="start.groovy?language=other"><div protocol="telegram">🍵</div>Hände hoch</link>
                      </navigation>
                    </page>"""
               break
           case "other":
               println """<?xml version="1.0" encoding="UTF-8"?>
                    <page version="2.0">
                      <div protocol="telegram">
                        🤖 Эй, я ж по-испански не бельмесю...
                      </div>
                      <navigation>
                        <link pageId="new_game.groovy?board=33&amp;language=ru"><div protocol="telegram">☕</div>Играть 3х3</link>
                        <link pageId="new_game.groovy?board=55&amp;language=ru"><div protocol="telegram">🍵</div>Играть 5х5</link>
                        <link pageId="new_game.groovy?board=56&amp;language=ru"><div protocol="telegram">🍵</div>Играть 5х6</link>
                      </navigation>
                      <navigation>
                        <link pageId="start.groovy?language=en"><div protocol="telegram">🍵</div>English</link>
                        <link pageId="start.groovy?language=other"><div protocol="telegram">🍵</div>Hände hoch</link>
                      </navigation>
                    </page>"""
               break
           default:
               println """<?xml version="1.0" encoding="UTF-8"?>
                    <page version="2.0">
                      <div protocol="telegram">
                        🤖 Lonely bot against humanity<br/>
                        Одинокий Бот против Человечества?<br/>
                        Да, я - такой! <br/>
                      </div>
                      <navigation>
                        <link pageId="new_game.groovy?board=33&amp;language=en"><div protocol="telegram">☕</div>Play 3x3</link>
                        <link pageId="new_game.groovy?board=33&amp;language=ru"><div protocol="telegram">☕</div>Играть 3х3</link>
                      </navigation>
                      <navigation>
                        <link pageId="new_game.groovy?board=55&amp;language=en"><div protocol="telegram">🍵</div>Play 5x5</link>
                        <link pageId="new_game.groovy?board=55&amp;language=ru"><div protocol="telegram">🍵</div>Играть 5х5</link>
                      </navigation>
                    </page>"""
               break
       }
    } else {
        println """<?xml version="1.0" encoding="UTF-8"?>
                    <page version="2.0">
                      <div protocol="telegram">
                        🤖 Lonely bot against humanity<br/>
                        Одинокий Бот против Человечества?<br/>
                        Да, я - такой! <br/>
                      </div>
                      <navigation>
                        <link pageId="new_game.groovy?board=33&amp;language=en"><div protocol="telegram">☕</div>Play 3x3</link>
                        <link pageId="new_game.groovy?board=33&amp;language=ru"><div protocol="telegram">☕</div>Играть 3х3</link>
                      </navigation>
                      <navigation>
                        <link pageId="new_game.groovy?board=55&amp;language=en"><div protocol="telegram">🍵</div>Play 5x5</link>
                        <link pageId="new_game.groovy?board=55&amp;language=ru"><div protocol="telegram">🍵</div>Играть 5х5</link>
                      </navigation>
                    </page>"""
    }
    /*println """<page version="2.0">
                  <div>...</div>
                  <navigation attributes="telegram.inline: true">
                    <link pageId="kbd.jsp/1">1</link>
                    <link pageId="kbd.jsp/2">2</link>
                    <link pageId="kbd.jsp/3">3</link>
                    <link pageId="kbd.jsp/4">4</link>
                    <link pageId="kbd.jsp/5">5</link>
                  </navigation>

                  <navigation attributes="telegram.inline: true">
                    <link pageId="kbd.jsp/6">6</link>
                    <link pageId="kbd.jsp/7">7</link>
                    <link pageId="kbd.jsp/8">8</link>
                    <link pageId="kbd.jsp/9">9</link>
                    <link pageId="kbd.jsp/0">0</link>
                  </navigation>
            </page>"""*/
}
session.counter = session.counter + 1