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

// Считываем wnumber
String wnumber = params["subscriber"]
TictactoeBot game = session.getAttribute("game")
String resp = ''
if(game) {
    resp = game.sayScore(wnumber)
}

println resp

session.counter = session.counter + 1