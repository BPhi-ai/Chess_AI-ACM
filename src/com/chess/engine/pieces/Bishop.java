package com.chess.engine.pieces;

//All imports needed for this class to work. Most are imports from other classes. 
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

public final class Bishop extends Piece {
	
	//Move coordinates that the piece can move. Look up wikipedia and count the tiles to get to the dot.
	//That's your coordinates after counting them
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -7, 7, 9};

    //The constructors of the Bishop class. It has the variables that's needed for a piece like
    //the alliance, piece type, position of the piece, and whether if it made the first move or not.
    //Applies to the other chess pieces.
    public Bishop(final Alliance alliance,
                  final int piecePosition) {
         super(PieceType.BISHOP, alliance, piecePosition, true);
    }

    public Bishop(final Alliance alliance,
                  final int piecePosition,
                   final boolean isFirstMove) {
        super(PieceType.BISHOP, alliance, piecePosition, isFirstMove);
    }
    
    /*
     * This is the calculateLegalMoves method. As the name suggests, the purpose
     * of this method is to make a legal move for the bishop piece. Explanation of the exclusions are below, but 
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
            int candidateDestinationCoordinate = this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate) ||
                    isEighthColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)) {
                    break;
                }
                candidateDestinationCoordinate += currentCandidateOffset;
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    }
                    else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAllegiance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,
                                    pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    //Location bonus for the bishop
    @Override
    public int locationBonus() {
        return this.pieceAlliance.bishopBonus(this.piecePosition);
    }

    //This method has moved Piece. Returns the instance of getMovedBishop(move), which is the movement of Bishop.
    @Override
    public Bishop movePiece(final Move move) {
        return PieceUtils.INSTANCE.getMovedBishop(move);
    }

    //toString() method allows one to make it in a way where we can print out the class in a string format.
    //The Override tag rewrites the original function of the method so you can make the function your own.
    @Override
    public String toString() {
        return this.pieceType.toString();
    }

    //Column exclusions. Based on the following move coordinates where it makes the algorithm of the
    //bishop movement fall off. Used within the for loop to see if the piece has to skip those to prevent it 
    //from being a legal move. (Applies to other parts of chess piece classes, and depends on the column for the
    //chess pieces.
    private static boolean isFirstColumnExclusion(final int currentCandidate,
                                                  final int candidateDestinationCoordinate) {
        return (BoardUtils.INSTANCE.FIRST_COLUMN.get(candidateDestinationCoordinate) &&
                ((currentCandidate == -9) || (currentCandidate == 7)));
    }

    private static boolean isEighthColumnExclusion(final int currentCandidate,
                                                   final int candidateDestinationCoordinate) {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(candidateDestinationCoordinate) &&
                        ((currentCandidate == -7) || (currentCandidate == 9));
    }
}
    