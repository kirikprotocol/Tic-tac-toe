/**
 * Created by Petr Matyukov on 14.04.2016.
 */


import peter.bitwise.Tictactoe



if (!session) {
    session = request.getSession(true)
}

if (!session.counter) {
    session.counter = 1
}

Tictactoe game = new Tictactoe(5, 5, 4)
game.autoPlay(25, 6, 26)


//def config = new ConfigSlurper().parse(new File("c:/Users/Petr Matyukov/IdeaProjects/PeterPrj/TicTacToe/web/tictactoe_config.groovy").text)
/*java.net.URL url = context.getResource("/TicTacToe/tictactoe_config.groovy")
File cfile = new File(url.toURI())
def config = new ConfigSlurper().parse(cfile.getText("UTF-8"))


String[] messages = config.dictionary.russian.sentences
println "Messages  = " + messages*/


session.counter = session.counter + 1