import isel.tds.go.model.*
import isel.tds.go.storage.TextFileStorage
import isel.tds.go.view.readCommandLine
import isel.tds.go.view.getCommands
import isel.tds.go.storage.GameSerializer
import isel.tds.go.view.show
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

fun main() {

    var game: Game = Game()
    val storage = TextFileStorage<String, Game>("saves", GameSerializer)
    val commands = getCommands(storage)
    while(true){
        val (name,args)  = readCommandLine()
        val cmd = commands[name]
        if(cmd==null)println("Invalid Command $name")
        else
            try {
                if( cmd.isToFinish) break
                game = cmd.execute(args, game)
            }catch (e: IllegalStateException){
                println(e.message)
            }
            catch (e: IllegalArgumentException){
                println(e.message)
            }
        game.show()

    }
}