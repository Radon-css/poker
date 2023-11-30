package de.htwg.poker.util
import de.htwg.poker.controller.Controller;

trait Command {
  def doStep: Unit
  def undoStep: Unit
  def redoStep: Unit
}

class SetCommand(controller: Controller) extends Command {
    override def doStep: Unit = controller.
    override def undoStep: Unit = 
    override def redoStep: Unit = 
}

class UndoManager {
    private var undoStack: List[Command] = Nil
    private var redoStack: List[Command] = Nil
    def doStep(command: Command) =  {
        undoStack = command::undoStack
        command.doStep
    }
    def undoStep  = {
     undoStack match {
        case  Nil =>
        case head::stack => {
            head.undoStep
            undoStack=stack
            redoStack= head::redoStack
        }  
    }
    }
}