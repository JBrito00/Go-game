package isel.tds.go.model

class Group(val stone: Stone, val boardCells: Map<Position, Stone>) {

    companion object {

        fun hasLiberties(pos: Position, stone: Stone, boardCells: Map<Position, Stone>): Boolean {
            val neighbors = calculateNeighbors(pos)
            return neighbors.any { it !in boardCells.keys }
        }

        fun eatGroup(boardCells: Map<Position, Stone>, stone: Stone, positions: List<Position>): Map<Position, Stone> {
            val libertiesList = positions.map {
                hasLiberties(it, stone, boardCells)
            }

            if (libertiesList.find { it } == true) {
                return boardCells
            } else {
                return boardCells.filter { (pos, _) -> pos !in positions }
            }
        }

        private fun calculateNeighbors(pos: Position): List<Position> {
            val neighbors = listOfNotNull(
                if (pos.col > 0) Position(pos.index - 1) else null,
                if (pos.col < BOARD_SIZE - 1) Position(pos.index + 1) else null,
                if (pos.row > 0) Position(pos.index - BOARD_SIZE) else null,
                if (pos.row < BOARD_SIZE - 1) Position(pos.index + BOARD_SIZE) else null
            )

            return neighbors
        }

        fun compLists(boardCellsClean: Map<Position, Stone>, boardCells: Map<Position, Stone>): List<Position> {
            return boardCells.filter { (position, _) -> !boardCellsClean.containsKey(position) }
                .keys.toList()
        }

        fun getGroup(pos: Position, stone: Stone, boardCells: BoardCells): List<Position> {
            fun explore(currentPosition: Position, visited: List<Position>): List<Position> {
                if (currentPosition in visited) return visited

                val neighbors = calculateNeighbors(currentPosition)
                val unvisitedNeighbors =
                    neighbors.filter { it !in visited && it in boardCells.keys && boardCells[it] == stone.other }

                return unvisitedNeighbors.fold(visited + currentPosition) { acc, neighbor ->
                    explore(neighbor, acc)
                }
            }

            return explore(pos, emptyList())

        }

        fun getEmptyGroup(pos: Position, boardCells: BoardCells): List<Position> {
            fun explore(currentPosition: Position, visited: List<Position>): List<Position> {
                if (currentPosition in visited) return visited

                val neighbors = calculateNeighbors(currentPosition)
                val unvisitedEmptyNeighbors =
                    neighbors.filter { it !in visited && it !in boardCells.keys  }

                return unvisitedEmptyNeighbors.fold(visited + currentPosition) { acc, neighbor ->
                    explore(neighbor, acc)
                }
            }
            return explore(pos, emptyList())

        }

        fun getAllEmptyGroups(boardCells: BoardCells, position: Position, emptyGroups: List<List<Position>>):List<List<Position>> {
            var newPosition = position
            val newEmptyGroups: List<List<Position>> = emptyGroups
            while (newPosition in boardCells.keys) newPosition = Position(newPosition.index + 1)
            if (newEmptyGroups.isNotEmpty()) {
                for (group in newEmptyGroups) {
                    for (groupPosition in group) {
                        while (newPosition == groupPosition)
                            if(newPosition.index < BOARD_CELLS - 1) return getAllEmptyGroups(boardCells, Position(newPosition.index + 1), newEmptyGroups) else return newEmptyGroups
                    }
                }
            }

            val emptyGroup: List<Position> = getEmptyGroup(newPosition, boardCells)
            val updatedEmptyGroups: List<List<Position>> = newEmptyGroups + listOf(emptyGroup)
            if(newPosition.index < BOARD_CELLS - 1) return getAllEmptyGroups(boardCells, Position(newPosition.index + 1), updatedEmptyGroups) else return updatedEmptyGroups
        }

        fun calculateTerritories(boardCells: Map<Position, Stone>, emptyGroups: List<List<Position>>):Pair<Int, Int> {
            var blackTerritory = 0
            var whiteTerritory = 0
            for (group in emptyGroups) {
                var black: Boolean = false
                var white: Boolean = false
                for(groupPosition in group){
                    val neighbors = calculateNeighbors(groupPosition)
                    val blackNeighbor = neighbors.any { boardCells[it] == Stone.BLACK}
                    if(blackNeighbor) black = true
                    val whiteNeighbor = neighbors.any { boardCells[it] == Stone.WHITE}
                    if(whiteNeighbor) white = true
                }
                if(black && !white) blackTerritory += group.size
                if(white && !black) whiteTerritory += group.size
            }
            return Pair(blackTerritory, whiteTerritory)
        }

        fun capturedPieces(boardCells: Map<Position, Stone>, boardOpp: Map<Position, Stone>, boardSelf: Map<Position, Stone>, stone: Stone): Pair<Int, Int> {
            val boardState = BoardRun(boardCells, stone)

            val boardAfterCapture = when {
                boardOpp != boardCells -> boardOpp
                boardSelf != boardCells -> boardSelf
                else -> boardCells
            }

            val numberOfCaptures = boardCells.size - boardAfterCapture.size

            val whiteCaptures = when {
                stone == Stone.WHITE && numberOfCaptures == boardCells.size - boardOpp.size -> numberOfCaptures
                stone == Stone.BLACK && numberOfCaptures == boardCells.size - boardSelf.size -> numberOfCaptures
                else -> boardState.whiteCaptures
            }

            val blackCaptures = when {
                stone == Stone.BLACK && numberOfCaptures == boardCells.size - boardOpp.size -> numberOfCaptures
                stone == Stone.WHITE && numberOfCaptures == boardCells.size - boardSelf.size -> numberOfCaptures
                else -> boardState.blackCaptures
            }

            val pair = Pair(whiteCaptures, blackCaptures)

            return pair
        }
    }
}

