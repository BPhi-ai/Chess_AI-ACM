package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveStatus;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    protected final boolean isInCheck;

    /*For isInCheckmate, the reason we don't have a boolean variable on that is because we kind of have a chicken and egg problem. We're calling a constructor for the player
    when we're constructing the board. We're constructing a baord and amidst on constructing a board, we construct a player (black and white). When construct the player, and we
    were trying to calculate isInCheckmate, we know we have to call hasEscapeMoves and hasEscapeMoves is going to be making a move which is going to be constructing a board,
    and when we're constructing the board. When we come constuct the board we'll have to again calculate the player again. We want to break out of that loop by not defining
    a boolean isInCheckmate defined in the constructor.*/
    Player(final Board board,
           final Collection<Move> playerLegals,
           final Collection<Move> opponentLegals) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(
                Iterables.concat(playerLegals, calculateKingCastles(playerLegals, opponentLegals)));
        //We're saying that does the opponent's move attack the current player king's position and get all of those different attacks, and if that is not empty, then that
        //means the current player is in check. And the isInCheck method returns this.
        /*We're*/
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegals).isEmpty();
    }

    //Testing if the move that's being passed in is contained in the legal move in the collection.
    public boolean isMoveLegal(final Move move) {
        return !(move.isCastlingMove() && isInCheck()) && this.legalMoves.contains(move);
    }

    //An important method; returns the isInCheck variable.
    public boolean isInCheck() {
        return this.isInCheck;
    }

    //Method for checkmate; What this method means that in chess, the king is currently in check and the king has no way to escape check. THis is what checkmate means.
    //We check the isInCheck variable and if it doesn't have escape moves.
    public boolean isInCheckMate() {
       return this.isInCheck && !hasEscapeMoves();
    }

    //Method for stalemate; the current player is not in check and doesn't have any escape moves. You're not in check but you also don't have any escape moves.
    //This means that you can't make a move that will not lead your king in check. It's simple to calculate. This is stalemate.
    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled() {
        return this.playerKing.isCastled();
    }

    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }

    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }

    public King getPlayerKing() {
        return this.playerKing;
    }
    
    //This method is just going to ensure that there is a King for the player on the board. Otherwise, we  won't
    //be in a legal state for the game.
    /*In establishKing, we're gonna go through all of the player's active pieces. We're gonna see if the piece is the king, and if it is we're gonna return it. 
    If we go through the entire loop and couldn't find the king, we're gonna throw a runtime exception, stating that the board is invalid and doesn't correspond to a legal
    chess state.*/
    protected King establishKing() {
        for(final Piece piece : getActivePieces()) {
            if(piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! " +this.getAlliance()+ " king could not be established!");
    }

    /*In order to calculate on whether the king can escape, we're going to go through each of the player's legal moves and make those moves on a new board (imaginary board)
    Imagine we make those moves on a theoretical abstract board (not the one we're currently on) we make those moves and after we make that move, we look through the board
    and we go, "yes, we were able to successfully make that move and now it's done." If that's the case, then we return true. If you can't do the move, because it leave
    you in check or it's illegal, then you're gonna return false.*/
    protected boolean hasEscapeMoves() {
        for(final Move move : getLegalMoves()) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    //Returns the legal moves of the player
    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    //We're gonna pass in the king's current tile position and turn in the enemy's moves, and we're gonna go through each one of the enemy's moves
    //And see if the destination coordinate of that enemy move overlaps with the king's position.. If it does, then it's attacking the king.
    //Returns a collection of moves that attacks that king's position, and here (isInCheck variable) to calculate the calculate the current player's check,
    //we're gonna say that if that list is not empty, then the current player is in check.
    static Collection<Move> calculateAttacksOnTile(final int tile,
                                                   final Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (tile == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    //When we make a move, we're going to return a MoveTransition which is going to wrap the board that we have transitioned to, if we're able to actually
    //make the move and the Move Status is done.
    /*If the move is illegal, the transition move status will be illegal and we return the same board. NO new board. Then what we're gonna say when a move is not illegal,
    we polymorphically execute the move and create a collection of moves called kingAttacks. If there are any attacks on current player's king, we shouldn't be able to make that move. 
    You can't make a move that exposes your king to check. Return the board with a move status that leaves the player in check. Otherwise, return the move transition board
    wrapped in a new move transition. To calculate attacks on King, we called the helper method called calculateAttacksOnTile, and call in the current player's opponent's king.
    This is a critical method for us; it's the center piece on playing the game of chess.
    We can't make this move so we're gonna return the board, move, moveStatus which says LEAVES_PLAYER_IN_CHECK*/
    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionedBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
                transitionedBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionedBoard.currentPlayer().getLegalMoves());
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        //Otherwise, return a new MoveTransition but has the MoveStatus as DONE
        return new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);
    }

    //Undo a move that was made before. Pretty much the undo button
    public MoveTransition unMakeMove(final Move move) {
        return new MoveTransition(this.board, move.undo(), move, MoveStatus.DONE);
    }

    //Abstract method for the BlackPlayer and WhitePlayer classes as they are the children of Player
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);

}
