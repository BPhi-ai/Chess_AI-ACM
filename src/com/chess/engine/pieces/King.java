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
import com.google.common.collect.ImmutableList; //<- This is when we need Guava. Right click your project folder
//then click on build path. Next, click on configure build path. Then, go to java build path, and highlight on 
//module path, 

public final class King extends Piece {

	//candidate move coordinates based on the tiles that the king can move at.
    private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };
    /*
     * Castling is a special move in chess. Consists of moving one's king two squares towards a rook on
     * the same rank and then moving the rook to the square that the king pass over. Only permitted
     * if the king and the rook haven't moved, squares between king and rook are vacant, and king 
     * does not leave, cross over, or end up on a square attacked by an opposing piece*/
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

    //Constructors for the King class. Initializes objects that the king has such as an alliance, position
    //of the piece, not doing the castling movement at first, and checking if it's capable of castling.
    public King(final Alliance alliance,
                final int piecePosition,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, alliance, piecePosition, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    //This one sees that the King being castled.
    public King(final Alliance alliance,
                final int piecePosition,
                final boolean isFirstMove,
                final boolean isCastled,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, alliance, piecePosition, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    //Boolean getters to see if the king has done the castling movement.
    public boolean isCastled() {
        return this.isCastled;
    }

    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;
    }
    
    /*
     * This is the calculateLegalMoves method. As the name suggests, the purpose
     * of this method is to make a legal move for the king piece. Explanation of the exclusions are below, but 
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
            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                continue;
            }
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                }
                else {
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
    
    //toString() method allows one to make it in a way where we can print out the class in a string format.
    //The Override tag rewrites the original function of the method so you can make the function your own.
    @Override
    public String toString() {
        return this.pieceType.toString();
    }

    //Location bonus for the king piece
    @Override
    public int locationBonus() {
        return this.pieceAlliance.kingBonus(this.piecePosition);
    }

    //This method has returns a new King.
    @Override
    public King movePiece(final Move move) {
        return new King(this.pieceAlliance, move.getDestinationCoordinate(), false, move.isCastlingMove(), false, false);
    }

    //The equals method. In java, we need to override equals method to make it so it can be based on 
    //checking if the king piece is equal to the other object.
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof King)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        final King king = (King) other;
        return isCastled == king.isCastled;
    }

    //Hash code. 
    @Override
    public int hashCode() {
        return (31 * super.hashCode()) + (isCastled ? 1 : 0);
    }

    //Column exclusions. Based on the following move coordinates where it makes the algorithm of the
    //king movement fall off. Used within the for loop to see if the piece has to skip those to prevent it 
    //from being a legal move. (Applies to other parts of chess piece classes, and depends on the column for the
    //chess pieces.
    private static boolean isFirstColumnExclusion(final int currentCandidate,
                                                  final int candidateDestinationCoordinate) {
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentCandidate)
                && ((candidateDestinationCoordinate == -9) || (candidateDestinationCoordinate == -1) ||
                (candidateDestinationCoordinate == 7));
    }

    private static boolean isEighthColumnExclusion(final int currentCandidate,
                                                   final int candidateDestinationCoordinate) {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentCandidate)
                && ((candidateDestinationCoordinate == -7) || (candidateDestinationCoordinate == 1) ||
                (candidateDestinationCoordinate == 9));
    }
}