package peter.games

import groovy.transform.CompileStatic

/**
 * Created by Petr Matyukov on 14.04.2016.
 */

@CompileStatic
class Move {
    int ver = 0
    int hor = 0
    int score = TicTacToe.EMPTY
    def Move(int ver, int hor) {
        this.ver = ver
        this.hor = hor
    }
    def Move(int ver, int hor, int score) {
        this.ver = ver
        this.hor = hor
        this.score = score
    }
}

@CompileStatic
class OrderedMoves {
    Move onlyMove = null
    List forcingMoves = []
    List preferredMoves = []
    List otherMoves = []
    boolean isEmpty() { return onlyMove == null && forcingMoves.size() == 0 && preferredMoves.size() == 0 && otherMoves.size() == 0}
}

@CompileStatic
class TicTacToe {
    static final Random rnd = new Random()
    static final int EMPTY = 0   // Пустая клетка
    static final int CROSS = -1  // Крестик
    static final int ZERO = 1    // Нолик

    private List winingRows
    protected int[][] board
    int vertical
    int horizontal
    private int winNumber
    protected int firstPlayer
    protected int secondPlayer

    private static def int[][] copyBoard(int[][] board) {
        int ver = board.length
        int hor = board[0].size()
        int[][] brd = new int[ver][hor]
        for(int i in 0..ver-1)
            for(int j in 0..hor-1)
                brd[i][j] = board[i][j]
        return brd
    }

    def TicTacToe(int vertical, int horizontal, int winNumber = 0) {
        // Для удобства вертикаль должна быть не больше горизонтали
        if(vertical <= horizontal) {
            this.vertical = vertical
            this.horizontal = horizontal
        }
        else {
            this.vertical = horizontal
            this.horizontal = vertical
        }
        if(winNumber == 0)
            this.winNumber = this.vertical
        else
            this.winNumber = winNumber
        this.newGame()
    }

    def newGame() {
        // Новая игра
        // Определяем какие ряды могут выиграть в игре
        // Горизонтали
        winingRows = []
        for(int shift in 0..horizontal-winNumber)
            for(int i in 0..vertical-1) {
                List h = []
                for(j in shift..winNumber + shift-1)
                    h += [[i, j]]
                winingRows += [h]
            }

        // Вертикали
        for(int shift in 0..vertical-winNumber)
            for(int i in 0..horizontal-1) {
                List h = []
                for (int j in shift..winNumber + shift - 1)
                    h += [[j, i]]
                winingRows += [h]
            }

        // Диагонали
        for(int shiftHorizontal in 0..horizontal-winNumber)
            for(int shiftVertical in 0..vertical-winNumber) {
                List hdown = []
                List hup = []
                for (i in 0..winNumber- 1) {
                    hdown += [[i + shiftVertical, i + shiftHorizontal]]
                    hup += [[winNumber -1 - i + shiftVertical, i + shiftHorizontal]]
                }
                winingRows += [hdown]
                winingRows += [hup]
            }


        // Создаем пустую доску для игры
        board = new int[vertical][horizontal]

        // Определяем, кто ходит первым. Первый ход начинается с крестика
        List players = [CROSS, ZERO]
        Collections.shuffle(players)
        firstPlayer = players[0]
        secondPlayer = players[1]

        //winingRows.each {println(it)}
    }

    def boolean makeMove(Move move, int player) {
        makeMove(board, move, player)
    }

    def boolean makeMove(int[][] board, Move move, int player) {
        if(board[move.ver][move.hor] == EMPTY) {
            board[move.ver][move.hor] = player
            return true
        } else
            return false
    }

    def Move findMove(int depth = 10, int width = 1000) {
        Move move = minimax(depth, width)
        return move
    }

    def autoPlay(int movesNumber, int depth, int width){
        // Определяем кто ходит
        List moves = findLegalMoves()
        if(!moves)  // Ходить некуда, игра закончилась
            return null
        int player = moves.size() % 2
        if (vertical*horizontal%2 == 1) {
            if (player == 0)
                player = ZERO
            else
                player = CROSS
        }
        else if(player == 0)
            player = CROSS
        else
            player = ZERO

        for(int i in 0..movesNumber) {
            Move move = findMove(depth, width)
            if(move) {
                if (move.ver >= 0) {
                    println "Делаем ход [" + move.ver + "," + move.hor + "] за " + (player == CROSS ? "Крестики" : "Нолики")
                    makeMove(move, player)
                    board.each { println it }
                    player = nextPlayer(player)
                } else {
                    // Игра закончилась
                    println("Результат игры " + gameResult())
                    break
                }
            } else {
                // Игра закончилась
                println("Результат игры " + gameResult())
                break
            }
        }
    }

    def List findLegalMoves(){
        return findLegalMoves(board)
    }

    def List findLegalMoves(int[][] board) {
        List moves = []
        for (i in 0..vertical - 1)
            for (j in 0..horizontal - 1)
                if (board[i][j] == EMPTY)
                    moves += new Move(i, j)
        return moves
    }

    def int gameResult(){
        return gameResult(board)
    }

    def int gameResult(int[][] board) {
        //Определяет результат игры
        //Проверка есть ли заполненный ряд
        // print("Горизонтали, Вертикали и Диагонали: ", rows)
        int res = EMPTY
        for(row in winingRows) {
            boolean win = true
            for(int i in 0..((List)row).size()-1){
                int[] cell = (int[])(((List)row)[i])
                if(board[cell[0]][cell[1]] != CROSS ) {
                    win = false
                    break
                }
            }
            if(win) return CROSS
            win = true
            for(int i in 0..((List)row).size()-1){
                int[] cell = (int[])(((List)row)[i])
                if(board[cell[0]][cell[1]] != ZERO ) {
                    win = false
                    break
                }
            }
            if(win) return ZERO
        }
        return res
    }

    def OrderedMoves findOrderedMoves(int[][] board, int player, boolean isStrongOrdered) {
        OrderedMoves moves = new OrderedMoves()
        for (i in 0..vertical - 1)
            for (j in 0..horizontal - 1)
                if (board[i][j] == EMPTY) {
                    // Если это выигрывающий ход - возвращаем его
                    Move move = new Move(i, j)
                    // Делаем ход-кандидат
                    board[i][j] = player
                    // Проверяем не закончилась ли игра
                    int res = gameResult(board)
                    if (res == player) { // Нашли выигрывающий ход
                        board[i][j] = EMPTY // Возвращаем доску в первоначальное состояние
                        moves.onlyMove = move
                        moves.onlyMove.score = player
                        return moves
                    }
                    // Если уже найдены единственные ходы, то ищем только выигрывающие ходы, остальные пропускаем
                    if(moves.onlyMove){
                        board[i][j] = EMPTY // Возвращаем доску в первоначальное состояние
                        continue
                    }
                    // Если этим ходом соперник выигрывает - запоминаем, как единственный ход и продолжаем искать (вдруг у нас есть выигрывающий ход)
                    // Делаем ход-кандидат
                    board[i][j] = nextPlayer(player)
                    // Проверяем не закончилась ли игра
                    res = gameResult(board)
                    if (res == nextPlayer(player)) { // Нашли выигрывающий ход за соперника, надо блокировать
                        board[i][j] = EMPTY // Возвращаем доску в первоначальное состояние
                        moves.onlyMove = move
                        continue
                    }
                    if(isStrongOrdered) {
                        // Если этим ходом мы или соперник создает угрозу выигрыша - это форсирующий ход
                        // Если после этого хода, следующим мы сможем создать угрозу, то это предпочтительный ход
                        // Делаем наш ход-кандидат
                        board[i][j] = player
                        boolean isThreat = false
                        for (k in 0..vertical - 1)
                            for (l in 0..horizontal - 1) {
                                if (board[k][l] == EMPTY && !isThreat) {
                                    // Если это выигрывающий ход - возвращаем его
                                    // Проверяем наш ход
                                    board[k][l] = player
                                    // Проверяем не закончилась ли игра
                                    res = gameResult(board)
                                    if (res == player) { // Нашли выигрывающий ход
                                        board[k][l] = EMPTY // Возвращаем доску в первоначальное состояние
                                        isThreat = true
                                        break
                                    }
                                    // Проверяем ход соперника
                                    board[i][j] = nextPlayer(player)
                                    board[k][l] = nextPlayer(player)
                                    // Проверяем не закончилась ли игра
                                    res = gameResult(board)
                                    if (res == nextPlayer(player)) { // Нашли выигрывающий ход
                                        board[i][j] = player
                                        board[k][l] = EMPTY // Возвращаем доску в первоначальное состояние
                                        isThreat = true
                                        break
                                    }
                                    board[i][j] = player
                                    board[k][l] = EMPTY // Возвращаем доску в первоначальное состояние
                                }
                            }
                        if (isThreat) { //Этот ход создает угрозу - добавляем его
                            moves.forcingMoves.add(new Move(i, j))
                            board[i][j] = EMPTY // Возвращаем доску в первоначальное состояние
                            continue
                        }
                        // Проверяем, предпочтительный ли это ход
                        boolean isPreferred = false
                        for (k in 0..vertical - 1)
                            for (l in 0..horizontal - 1) {
                                if (board[k][l] == EMPTY && !isPreferred) {
                                    // Проверяем наш ход
                                    board[k][l] = player
                                    for (m in 0..vertical - 1)
                                        for (n in 0..horizontal - 1) {
                                            if (board[m][n] == EMPTY && !isPreferred) {
                                                // Если это выигрывающий ход - возвращаем его
                                                // Проверяем наш ход
                                                board[m][n] = player
                                                // Проверяем не закончилась ли игра
                                                res = gameResult(board)
                                                if (res == player) { // Нашли выигрывающий ход
                                                    board[m][n] = EMPTY // Возвращаем доску в первоначальное состояние
                                                    isPreferred = true
                                                    break
                                                }
                                                board[m][n] = EMPTY // Возвращаем доску в первоначальное состояние
                                            }
                                        }
                                    board[k][l] = EMPTY // Возвращаем доску в первоначальное состояние
                                }
                            }
                        if (isPreferred) { // Предпочтительный ход
                            moves.preferredMoves.add(new Move(i, j))
                            board[i][j] = EMPTY // Возвращаем доску в первоначальное состояние
                            continue
                        }
                    }
                    // Помещаем клетку в оставшиеся ходы
                    board[i][j] = EMPTY // Возвращаем доску в первоначальное состояние
                    moves.otherMoves.add(new Move(i, j))
                }

        return moves
    }

    def Move findWiningMove(int[][] board, int player) {
        List moves = findLegalMoves(board)
        if (moves.size() > 0) {
            for(int i in 0..moves.size()-1) {
                Move move = (Move)moves[i]
                int[][] brd = copyBoard(board)
                // Делаем ход-кандидат
                makeMove(brd, move, player)
                // Проверяем не закончилась ли игра
                int res = gameResult(brd)
                if (res == player) { // Нашли выигрывающий ход
                    move.score = res
                    return move
                }
            }
        }
        return null
    }

    /*def Move isOnlyMove(int[][] board, int player, int ver, int hor){
        Move move = null
        if (board[ver][hor] == EMPTY) {
            // Если это выигрывающий ход - возвращаем его
            // Делаем ход-кандидат
            board[ver][hor] = player
            // Проверяем не закончилась ли игра
            int res = gameResult(board)
            if (res == player) { // Нашли выигрывающий ход
                board[ver][hor] = EMPTY // Возвращаем доску в первоначальное состояние
                move = new Move(ver, hor)
                move.score = player
                return move
            }
            // Если этим ходом соперник выигрывает - запоминаем, как единственный ход и продолжаем искать (вдруг у нас есть выигрывающий ход)
            // Делаем ход-кандидат
            board[ver][hor] = nextPlayer(player)
            // Проверяем не закончилась ли игра
            res = gameResult(board)
            if (res == nextPlayer(player)) { // Нашли выигрывающий ход за соперника, надо блокировать
                board[ver][hor] = EMPTY // Возвращаем доску в первоначальное состояние
                move = new Move(ver, hor)
                move.score = EMPTY
                return move
            }
        }
        return null
    }*/

    def Move findWiningFork(int[][] board, int player) {
        List moves1 = findLegalMoves(board)
        if (moves1.size() > 2) {
            for(int i in 0..moves1.size()-1) {
                Move move1 = (Move)moves1[i]
                int[][] brd1 = copyBoard(board)
                // Делаем ход-кандидат
                makeMove(brd1, move1, player)
                List moves2 = findLegalMoves(brd1)
                int threatCount = 0
                for(int j in 0..moves2.size()-1) {
                     Move move2 = (Move)moves2[j]
                    int[][] brd2 = copyBoard(brd1)
                    // Делаем ход-кандидат
                    makeMove(brd2, move2, player)
                    if(player == gameResult(brd2))
                        threatCount += 1
                }
                if(threatCount >=2){
                    move1.score = player
                    return move1
                }
            }
        }
        return null
    }

    def Move minimax(int depth = 10, int width = 1000){
        List moves = findLegalMoves()
        if(!moves)  // Ходить некуда, игра закончилась
            return null

        int player = moves.size() % 2
        if (vertical*horizontal%2 == 1) {
            if (player == 0)
                player = ZERO
            else
                player = CROSS
        }
        else if(player == 0)
            player = CROSS
        else
            player = ZERO
        println("Ищу ход для " + player + "/" + depth +"/" + width)

        // Если это первый ход, делаем его случайно
        if(moves.size() == vertical*horizontal) {
            if(vertical == 3) {
                Collections.shuffle(moves)
                return (Move)moves[0]
            }
            else {
                int ver = (int)(vertical/2 + rnd.nextInt(1))
                int hor = (int)(horizontal/2 + rnd.nextInt(1))
                return new Move(ver, hor)
            }
        }

        return minimax(board, player, depth, width, true)
    }

    def Move minimax(int[][] board, int player, int depth = 10, int width = 1000, boolean isStrongOrdered = false) {
        OrderedMoves moves = findOrderedMoves(board, player, isStrongOrdered)
        //Проверяем не окончилась ли уже игра
        int res = gameResult(board)
        if (res != EMPTY)
            return new Move(-1, -1, res)
        else if (moves.isEmpty()) // Ходить некуда, игра закончилась
            return new Move(-1, -1, res)
        // Ищем лучший ход
        // Если есть единственный ход - делаем его
        if(moves.onlyMove) {
            // Делаем ход-кандидат
            if(moves.onlyMove.score != player) { // блокирующий, а не выигрывающий ход
                board[moves.onlyMove.ver][moves.onlyMove.hor] = player
                Move mv = minimax(board, nextPlayer(player), depth - 1, width)
                moves.onlyMove.score = mv.score
                board[moves.onlyMove.ver][moves.onlyMove.hor] = EMPTY // Откатываем назад сделанный ход
            }
            return moves.onlyMove
        }
        // Сначала считаем форсированные ходы
        Move bestForcingMove = null
        if(moves.forcingMoves.size() > 0) {
            bestForcingMove = (Move)moves.forcingMoves[0]
            bestForcingMove.score = -100
            if(depth > 0) {
                for (int i in 0..moves.forcingMoves.size() - 1) {
                    if ((bestForcingMove.score != player)) {
                        Move move = (Move) moves.forcingMoves[i]
                        // Делаем ход-кандидат
                        board[move.ver][move.hor] = player
                        Move mv = minimax(board, nextPlayer(player), depth - 1, width)
                        if (!isBetter(bestForcingMove.score, mv.score, player)) {
                            bestForcingMove = move
                            bestForcingMove.score = mv.score
                        }
                        board[move.ver][move.hor] = EMPTY // Откатываем назад сделанный ход
                    } else break
                }
            } else bestForcingMove.score = EMPTY
            if(bestForcingMove.score == player || depth <= 0 || moves.otherMoves.size() == 0)
                return bestForcingMove
        }
        // Если есть предпочтительные ходы - считаем их
        Move bestMove = null
        if(moves.preferredMoves.size() != 0) {
            Collections.shuffle(moves.preferredMoves)
            bestMove = (Move)moves.preferredMoves[0]
            bestMove.score = -100
            if(depth > 0) {
                for (int i in 0..moves.preferredMoves.size() - 1) {
                    if (bestMove.score != player) {
                        Move move = (Move) moves.preferredMoves[i]
                        // Делаем ход-кандидат
                        board[move.ver][move.hor] = player
                        Move mv = minimax(board, nextPlayer(player), depth - 1, width)
                        if (!isBetter(bestMove.score, mv.score, player)) {
                            bestMove = move
                            bestMove.score = mv.score
                        }
                        board[move.ver][move.hor] = EMPTY // Откатываем назад сделанный ход
                    } else break
                }
            } else bestMove.score = EMPTY
        }
        // Иначе (если нет предпочтительных ходов) ищем другие ходы
        if(bestMove == null) {
            int w = moves.forcingMoves.size() // учитываем, что мы уже перебирали форсированные ходы
            if (moves.otherMoves.size() != 0) {
                Collections.shuffle(moves.otherMoves)
                bestMove = (Move) moves.otherMoves[0]
                bestMove.score = -100
                if (depth > 0) {
                    for (int i in 0..moves.otherMoves.size() - 1) {
                        w += 1
                        if ((bestMove.score != player) && (w < width)) {
                            Move move = (Move) moves.otherMoves[i]
                            // Делаем ход-кандидат
                            board[move.ver][move.hor] = player
                            Move mv = minimax(board, nextPlayer(player), depth - 1, width)
                            if (!isBetter(bestMove.score, mv.score, player)) {
                                bestMove = move
                                bestMove.score = mv.score
                            }
                            board[move.ver][move.hor] = EMPTY // Откатываем назад сделанный ход
                        } else break
                    }
                } else bestMove.score = EMPTY
            }
        }
        if(bestForcingMove == null && bestMove != null)
            return bestMove
        if(bestForcingMove != null && bestMove == null)
            return bestForcingMove
        if(bestMove && bestForcingMove) {
            if (isBetter(bestForcingMove.score, bestMove.score, player))
                return bestForcingMove
            else
                return bestMove
        }
        else println "Error! BestMove && BestForcingMove == NULL"
    }

    def Move minimaxOLDVERSION(int[][] board, int player, int depth = 10, int width = 1000) {
        List moves = findLegalMoves(board)
        //Проверяем не окончилась ли уже игра
        int res = gameResult(board)
        if (res != EMPTY)
            return new Move(-1, -1, res)
        else if (moves.size() == 0) // Ходить некуда, игра закончилась
            return new Move(-1, -1, res)
        else if (depth <= 0) {
            Move move = findWiningMove(board, player)
            if(move)
                return move
            Collections.shuffle(moves)
            move = (Move)moves[0]
            move.score = EMPTY
            return move
        }
        // Ищем лучший ход
        Collections.shuffle(moves)
        Move bestMove = (Move)moves[0]
        bestMove.score = -100
        // Если есть выигрывающий ход - делаем его
        Move move = findWiningMove(board, player)
        if(move)
            return move
        // Если у противника есть выигрывающий ход - блокируем его
        move = findWiningMove(board, nextPlayer(player))
        if(move) {
            int[][] brd = copyBoard(board)
            // Делаем ход-кандидат
            makeMove(brd, move, player)
            Move mv = minimax(brd, nextPlayer(player), depth - 1, width)
            move.score = mv.score
            return move
        }
        // Если можем поставить вилку - делаем ее
        move = findWiningFork(board, player)
        if(move)
            return move
        // Если противник грозит вилкой - блокируем ее
        move = findWiningFork(board, nextPlayer(player))
        if(move) {
            int[][] brd = copyBoard(board)
            // Делаем ход-кандидат
            makeMove(brd, move, player)
            Move mv = minimax(brd, nextPlayer(player), depth - 1, width)
            if(mv.score != nextPlayer(player)) {
                move.score = mv.score
                return move
            }
        }
        // Ищем в глубину
        int w = 0
        for(int i in 0..moves.size()-1) {
            w += 1
            if ((bestMove.score != player) && (w < width)) {
                move = (Move) moves[i]
                int[][] brd = copyBoard(board)
                // Делаем ход-кандидат
                makeMove(brd, move, player)
                Move mv = minimax(brd, nextPlayer(player), depth - 1, width)
                if (!isBetter(bestMove.score, mv.score, player)) {
                    bestMove = move
                    bestMove.score = mv.score
                }
            } else break
        }
        return bestMove
    }

    def boolean isBetter(int score1, int score2, player) {
        if (score1 == player)
            return true
        else if (score2 == player)
            return false
        else if (score1 == score2)
            return rnd.nextBoolean()
        else if (score1 == EMPTY)
            return true
        else if (score2 == EMPTY)
            return false
        return false
    }

    def int nextPlayer(int player) {
        if(player == CROSS)
            return ZERO
        else return CROSS
    }

}
