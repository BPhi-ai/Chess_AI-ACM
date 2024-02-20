package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

public class Knight extends Piece{

	//The candidate coordinates that the knight can move to. 
    private final static int[] CANDIDATE_MOVE_COORDINATES = { -17, -15, -10, -6, 6, 10, 15, 17 };

    //Constructors for the knight. initializes the alliance of the piece, position of it, the piece type, and
    //if the piece does the first move
    public Knight(final Alliance alliance,
                  final int piecePosition) {
        super(PieceType.KNIGHT, alliance, piecePosition, true);
    }

    public Knight(final Alliance alliance,
                  final int piecePosition,
                  final boolean isFirstMove) {
        super(PieceType.KNIGHT, alliance, piecePosition, isFirstMove);
    }

    /*
     * This is the calculateLegalMoves method. As the name suggests, the purpose
     * of this method is to make a legal move for the knight piece. Explanation of the exclusions are below, but 
     * it basically checks that for every tile that it can move based on CANDIDATE_MOVE_COORDINATE (Info
     * about CANDIDATE_MOVE_COORDINATE is above), we check if there's a tile that the piece can move to by using
     * a while loop for every tile that the piece can move being valid. Within the first while loop, we basically
     * check for the columns so the algorithm for the move coordinates won't fall. If there's no tile occupied on the tile
     * that the chess will go to, then a new MajorMove is created, mainly for non-attacking movement (View MajorMove
     * in Move class), and goes to a legalMove. Else, it initializes two variables: pieceAtDestionation, and pieceAlliance, which 
     * pieceDestination is a Piece variable, and pieceAlliance is an Alliance variable. It then checks
     * if the alliance of the current piece is the opposite, we do an attack move with MajorAttackMove
     * object being created and being added to the list of legal moves that are immutable.*/
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
               isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
               isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
               isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                continue;
            }
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAllegiance();
                    if (this.pieceAlliance != pieceAtDestinationAllegiance) {
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,
                                pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    //Location bonus for the knight piece
    @Override
    public int locationBonus() {
        return this.pieceAlliance.knightBonus(this.piecePosition);
    }

    //Returns the instance of the knight piece moving
    @Override
    public Knight movePiece(final Move move) {
        return PieceUtils.INSTANCE.getMovedKnight(move);
    }

    //toString() method allows one to make it in a way where we can print out the class in a string format.
    //The Override tag rewrites the original function of the method so you can make the function your own.
    @Override
    public String toString() {
        return this.pieceType.toString();
    }

    //Column exclusions. Based on the following move coordinates where it makes the algorithm of the
    //Knight movement fall off. Used within the for loop to see if the piece has to skip those to prevent it 
    //from being a legal move. (Applies to other parts of chess piece classes, and depends on the column for the
    //chess pieces.
    private static boolean isFirstColumnExclusion(final int currentPosition,
                                                  final int candidateOffset) {
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition) && ((candidateOffset == -17) ||
                (candidateOffset == -10) || (candidateOffset == 6) || (candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(final int currentPosition,
                                                   final int candidateOffset) {
        return BoardUtils.INSTANCE.SECOND_COLUMN.get(currentPosition) && ((candidateOffset == -10) || (candidateOffset == 6));
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition,
                                                    final int candidateOffset) {
        return BoardUtils.INSTANCE.SEVENTH_COLUMN.get(currentPosition) && ((candidateOffset == -6) || (candidateOffset == 10));
    }

    private static boolean isEighthColumnExclusion(final int currentPosition,
                                                   final int candidateOffset) {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition) && ((candidateOffset == -15) || (candidateOffset == -6) ||
                (candidateOffset == 10) || (candidateOffset == 17));
    }

}