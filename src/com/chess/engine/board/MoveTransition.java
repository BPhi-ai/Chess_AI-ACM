package com.chess.engine.board;

import com.chess.engine.board.Move.MoveStatus;

//This is the MoveTransition class. For the purpose of this class, when you make a move, we'll transition from one board to another
//and all of the information that we'll need will be carried to another board. Pretty much going to another board.
public final class MoveTransition {

    private final Board fromBoard;
    private final Board toBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board fromBoard,
                          final Board toBoard,
                          final Move move,
                          final MoveStatus moveStatus) {
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    //The board that we'll be transitioning to. It's important for GUI and AI.
    public Board getToBoard() {
         return this.toBoard;
    }

    //It seems that checking through the references, it seems to not be used. Must've been used to get stuff from the board.
    public Board getFromBoard() {
        return this.fromBoard;
    }
    
    //moveStatus basically tells us if we're able to do the move, or if we're not able to do the move 
    //more likely because of an illegal move. Here we get our move status and see if we have made the right move.
    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }
}