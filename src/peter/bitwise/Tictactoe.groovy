package peter.bitwise

import groovy.transform.CompileStatic

/**
 * Created by Petr Matyukov on 14.04.2016.
 */

@CompileStatic
class SortedMoves {
    long winingMove = 0
    long fork = 0
    List<Long> probablyForks = []
    List<Long> preferableMoves = []
    List<Long> otherMoves = []
}

//@CompileStatic
class Tictactoe {
    static final Random rnd = new Random()
    int width  // ширина доски
    int height // высота доски
    int length // длина выигрывающего ряда

    private List<Long> win = [] // всевозможные выигрыши
    private long X = 0 // Крестики на доске
    private long O = 0 // Нолики на доске
    private long filled = 0 // Заполненная доска

    def Tictactoe(int h, int w, int l = 0) {
        // Для удобства вертикаль должна быть не больше горизонтали
        if(h <= w) {
            height = h
            width = w
        }
        else {
            height = w
            width = h
        }
        // Максимальная размерность 8х8
        if(height > 8) height = 8
        if(width > 8) width = 8

        if(l == 0)
            length = height
        else
            length = l
        if(length > height) length = height

        // Индикатор заполненности доски
        for(long i in 0..height*width-1)
            filled |= 1L << i

        // Определяем какие ряды могут выиграть в игре

        // Горизонтали
        long horizontal = 0
        for (int i in 0..length - 1) {
            horizontal |= 1L << i
        }

        for (int shift in 0..width - length)
            for (int i in 0..height - 1) {
                win += horizontal << i * width + shift
            }

        // Вертикали
        long vertical = 0
        for (int i in 0..length - 1) {
            vertical |= 1L << i * width
        }

        for (int shift in 0..height - length)
            for (int i in 0..width - 1) {
                win += vertical << i + width * shift
            }

        // Диагонали
        long diag1 = 0
        long diag2 = 0
        for (int i in 0..length - 1) {
            diag1 |= 1L << i * width + i
            diag2 |= 1L << width-1 << i * width >> i
        }

        for (int j in 0..width - length)
            for (int i in 0..height - length) {
                win += diag1 << j + i*width
                win += diag2 << i*width >> j
            }
    }

    def newGame() {
        // Новая игра
        // Создаем пустую доску для игры
        X = 0
        O = 0
    }

    def boolean isFilled() {
        return filled == (X | O)
    }

    def List<Long> findLegalMoves() {
        List<Long> res = []
        long emptyCells = (X|O)^filled
        while(emptyCells) {
            long move = emptyCells
            emptyCells &= emptyCells - 1
            move ^= emptyCells
            res += move
        }
        return res
    }

    def boolean isWinner(boolean player){
        boolean res = false
        long board
        if(player)
            board = X
        else
            board = O
        for(int i in 0..win.size()-1) {
            if (win[i] == (win[i] & board)) {
                res = true
                break
            }
        }
        return res
    }

    def gameResult(){ // -1 - крестики, 0 - ничья, 1 - нолики, 2 - не закончена
        int res = 2
        for(int i in 0..win.size()-1)
            if( win[i] == (win[i] & X)) {
                return [-1, win[i]]
            } else if( win[i] == (win[i] & O)) {
                return [1, win[i]]
            }
        if(isFilled())
            return [0, 0]
        return [res, 0]
    }

    def setBoard(int[][] board) {
        newGame()
        for(int i in 0..height-1)
            for(int j in 0..width-1) {
                if(board[i][j] == -1){
                    X |= 1L << i*width + j
                } else if(board[i][j] == 1) {
                    O |= 1L << i*width + j
                }
            }
    }

    def int[][] getBoard() {
        int[][] board = new int[height][width]
        for(int i in 0..height-1)
            for(int j in 0..width-1) {
                if(X & 1L << i*width + j){
                    board[i][j] = -1
                } else if(O & 1L << i*width + j) {
                    board[i][j] = 1
                }
            }
        return board
    }

    def boolean makeMove(long move, boolean player) {
        if(move & (X|O)) { // клетка занята
            return false
        } else {
            if (player)
                X |= move
            else
                O |= move
            return true
        }
    }

    def undoMove(long move, boolean player) {
        if(player)
            X ^= move
        else
            O ^= move
    }

    def findMove(int searchDepth = 15, int searchWidth = 1000){
        if(isFilled())  // Ходить некуда, игра закончилась
            return [0, gameResult()]
        List<Long> moves = findLegalMoves()
        boolean player = true
        int state = moves.size() % 2
        if ((height * width) % 2 == 1) {
            if (state == 0)
                player = false // Нолики ходят
            else
                player = true // Крестики ходят
        }
        else if(state == 0)
            player = true // Крестики ходят
        else
            player = false // Нолики ходят
        println("Ищу ход для " + (player?"Крестиков ":"Ноликов ") + "moves/" + moves.size() + "depth/" + searchDepth +"width/" + searchWidth)

        // Если это первый ход, делаем его случайно
        if(moves.size() == height * width) {
            if(height == 3) {
                return [moves[rnd.nextInt(moves.size()-1)], 0]
            }
            else {
                //return [moves[(int)(moves.size()/2) + rnd.nextInt(1)], 0]
                long move = 1L << (int)(height/2 + height%2 - 1)*width + (int)(width/2 + width%2 - 1)
                return [move, 0]
            }
        }
        // Если это второй ход в игре - делаем его рядом с ходом оппонента
        if(moves.size() == height * width - 1) {
            if(height != 3) {
                long move = 1L << (int)(height/2 + height%2 - 1)*width + (int)(width/2 + width%2 - 1)
                if((move & X) || (move & O)) {
                    move = 1L << (int) (height / 2 + height % 2 - 2) * width + (int) (width / 2 + width % 2)
                } else {
                    move = 1L << (int) (height / 2 + height % 2 - 1) * width + (int) (width / 2 + width % 2)
                    if ((move & X) || (move & O))
                        move = 1L << (int) (height / 2 + height % 2 - 2) * width + (int) (width / 2 + width % 2 -1)
                }
                return [move, 0]
            }
        }

        SortedMoves ms = findSortedMoves(player)

        return findMove(player, searchDepth, searchWidth)
    }

    def findMove(boolean player, int searchDepth = 10, int searchWidth = 1000) {
        //Проверяем не окончилась ли уже игра
        int res
        (res) = gameResult()
        if (res != 2) // Конец игре
            return [0, res]
        SortedMoves sortedMoves = findSortedMoves(player)
        SortedMoves opponentSortedMoves = findSortedMoves(!player)
        if (searchDepth <= 0) {
            if(sortedMoves.winingMove) {
                return [sortedMoves.winingMove, player ? -1 : 1]
            }
            else if(opponentSortedMoves.winingMove) {
                return [opponentSortedMoves.winingMove, 0]
            }
            else if(sortedMoves.fork){
                return [sortedMoves.fork, player ? -1 : 1]
            }
            else if(sortedMoves.probablyForks.size()){
                return [sortedMoves.probablyForks[rnd.nextInt(sortedMoves.probablyForks.size())], 0] // возвращаем случайный ход
            }
            else if(sortedMoves.preferableMoves.size()){
                return [sortedMoves.preferableMoves[rnd.nextInt(sortedMoves.preferableMoves.size())], 0] // возвращаем случайный ход
            }
            else if(sortedMoves.otherMoves.size()){
                return [sortedMoves.otherMoves[rnd.nextInt(sortedMoves.otherMoves.size())], 0] // возвращаем случайный ход
            }
            else {
                List<Long> mvs = findLegalMoves()
                return [mvs[rnd.nextInt(mvs.size())], 0] // возвращаем случайный ход
            }
        }
        // Ищем лучший ход
        // Если есть выигрывающий ход - делаем его
        if(sortedMoves.winingMove)
            return [sortedMoves.winingMove, player?-1:1]
        // Если у соперника есть выигрывающий ход - блокируем его
        if(opponentSortedMoves.winingMove) {
            // Делаем ход, блокирующий выигрыш соперника
            makeMove(opponentSortedMoves.winingMove, player)
            long mv
            int score
            (mv, score) = findMove(!player, searchDepth - 1, searchWidth)
            undoMove(opponentSortedMoves.winingMove, player)
            return [opponentSortedMoves.winingMove, score]
        }
        // Если есть вилка - делаем его
        if(sortedMoves.fork)
            return [sortedMoves.fork, player?-1:1]

        List<Long> moves = []
        if(height == 3 && width == 3) {
            moves = findLegalMoves()
        } else {
            if(sortedMoves.probablyForks.size()) { // если есть наши потенциальные вилки - считаем только их
                moves = sortedMoves.probablyForks + sortedMoves.preferableMoves
            } else if (opponentSortedMoves.probablyForks.size()) {
                moves = opponentSortedMoves.probablyForks + sortedMoves.preferableMoves + opponentSortedMoves.preferableMoves
            } else if (sortedMoves.preferableMoves.size()) {
                moves = sortedMoves.preferableMoves + sortedMoves.otherMoves
            } else if(sortedMoves.otherMoves.size()){
                moves = sortedMoves.otherMoves
            }
            else {
                moves = findLegalMoves()
                Collections.shuffle(moves)
            }
        }

        long bestMove = 0
        int bestScore = -100
        //Collections.shuffle(moves)
        // Ищем в глубину
        int w = 0
        int scorePlayer = (player?-1:1)
        long mv
        int score
        for(int i in 0..moves.size()-1) {
            w += 1
            if ((bestScore != scorePlayer) && (w < searchWidth)) {
                // Делаем ход-кандидат
                makeMove(moves[i], player)
                (mv, score) = findMove(!player, searchDepth - 1, searchWidth)
                undoMove(moves[i], player)
                if (!isBetter(bestScore, score, player)) {
                    bestMove = moves[i]
                    bestScore = score
                }
            } else break
        }
        return [bestMove, bestScore]
    }

    def long findWiningMove(boolean  player){
        long move = 0
        long playerBoard, opponentBoard
        if(player){
            playerBoard = X
            opponentBoard = O
        }
        else {
            playerBoard = O
            opponentBoard = X
        }
        for(int i in 0..win.size()-1){
            long potentialMoves = (win[i]&playerBoard)^win[i]
            if(potentialMoves) {
                if ((potentialMoves & opponentBoard) == 0) {
                    int count = 0
                    long pmove = potentialMoves
                    while (potentialMoves && count < 2) {
                        potentialMoves &= potentialMoves - 1
                        count++
                    }
                    if(count == 1) {
                        move = pmove
                        break
                    }
                }
            }
        }
        return move
    }

    def SortedMoves findSortedMoves(boolean  player){
        SortedMoves moves = new SortedMoves()
        long playerBoard, opponentBoard
        if(player){
            playerBoard = X
            opponentBoard = O
        }
        else {
            playerBoard = O
            opponentBoard = X
        }
        long potentialForks = 0
        long preferableMoves = 0
        long otherMoves = 0
        for(int i in 0..win.size()-1){
            // Ищем хорошие ходы за нас
            long potentialMoves = win[i]&playerBoard // Шанс собрать выигрышный ряд
            if(potentialMoves) {
                potentialMoves ^= win[i] // Ходы, необходимые для заполнения ряда
                if ((potentialMoves & opponentBoard) == 0) { // Все нужные клетки свободны
                    int count = 0
                    long pmove = potentialMoves
                    while (potentialMoves) {
                        potentialMoves &= potentialMoves - 1
                        count++
                    }
                    if(count == 1) { // Нашли выигрывающий ход
                        moves.winingMove = pmove
                        return moves
                    }
                    else if(count == 2) { // Предпочтительный ход
                        long potentialFork = pmove & preferableMoves
                        if(potentialFork){
                            int number = 0
                            long pfork = potentialFork
                            while (pfork) {
                                pfork &= pfork - 1
                                number++
                            }
                            if(number == 1) // Точно вилка
                                moves.fork = potentialFork
                            potentialForks |= potentialFork
                        } else {
                            preferableMoves |= pmove
                        }
                    }
                    else {
                        otherMoves |= pmove
                    }
                }
            }
        }
        while(potentialForks) {
            long move = potentialForks
            potentialForks &= potentialForks - 1
            move ^= potentialForks
            moves.probablyForks += move
        }
        while(preferableMoves) {
            long move = preferableMoves
            preferableMoves &= preferableMoves - 1
            move ^= preferableMoves
            moves.preferableMoves += move
        }
        while(otherMoves) {
            long move = otherMoves
            otherMoves &= otherMoves - 1
            move ^= otherMoves
            moves.otherMoves += move
        }
        return moves
    }

    def boolean isBetter(int score1, int score2, player) {
        int scorePlayer = (player?-1:1)
        if (score1 == scorePlayer)
            return true
        else if (score2 == scorePlayer)
            return false
        else if (score1 == score2)
            return rnd.nextBoolean()
        else if (score1 == 0)
            return true
        else if (score2 == 0)
            return false
        return false
    }

    def autoPlay(int movesNumber, int depth, int width){
        boolean player = true
        long move = 0

        for(int i in 0..movesNumber) {
            (move) = findMove(depth, width)
            if(move) {
                println("Делаем ход " + Long.toUnsignedString(move, 2) + (player? " за Крестики" : " за Нолики"))
                makeMove(move, player)
                int[][] board = getBoard()
                board.each { println it }
                player = !player
            } else {
                // Игра закончилась
                println("Результат игры " + gameResult())
                break
            }
        }
    }
}
