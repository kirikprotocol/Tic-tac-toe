import peter.bitwise.TictactoeBot

/**
 * Created by Petr Matyukov on 14.04.2016.
 */

if (!session) {
    session = request.getSession(true)
}

if (!session.counter) {
    session.counter = 1
}


TictactoeBot game = session.getAttribute("game")
String resp = ''
if(game) {
    resp = game.sayVote()
}

println resp

session.counter = session.counter + 1