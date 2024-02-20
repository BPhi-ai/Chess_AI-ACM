package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.*;
import com.google.common.collect.ImmutableList;

public final class Pawn
        extends Piece {

	//Coordinates where you can move the pawn piece. Based on where the tiles where the piece can move.
	//More info on wikipedia.
    private final static int[] CANDIDATE_MOVE_COORDINATES = {8, 16, 7, 9};

    //Constructors of the pawn. As usual, they initialize the alliance it's on, the position of the piece,
    //the type of piece, and if it made the first move or not.
    public Pawn(final Alliance allegiance,
                final int piecePosition) {
        super(PieceType.PAWN, allegiance, piecePosition, true);
    }

    public Pawn(final Alliance alliance,
                final int piecePosition,
                final boolean isFirstMove) {
        super(PieceType.PAWN, alliance, piecePosition, isFirstMove);
    }

    //location bonus for the pawn
    @Override
    public int locationBonus() {
        return this.pieceAlliance.pawnBonus(this.piecePosition);
    }

    /*
     * This is the calculateLegalMoves method. As the name suggests, the purpose
     * of this method is to make a legal move for the Pawn piece. Explanation of the exclusions are below, but 
     * it basically checks that for every tile that it can move based on CANDIDATE_MOVE_COORDINATE (Info
     * about CANDIDATE_MOVE_COORDINATE is above), we check if there's a tile that the piece can move to by using
     * a while loop for every tile that the piece can move being valid. Within the first while loop, we basically
     * check for the columns so the algorithm for the move coordinates won't fall. If there's no tile occupied on the tile
     * that the chess will go to, then a new MajorMove is created, mainly for non-attacking movement (View MajorMove
     * in Move class), and goes to a legalMove. Else, it initializes two variables: pieceAtDestionation, and pieceAlliance, which 
     * pieceDestination is a Piece variable, and pieceAlliance is an Alliance variable. It then checks
     * if the alliance of the current piece is the opposite, we do an attack move with MajorAttackMove
     * object being created and being added to the list of legal moves that are immutable. However, due to this
     * being a pawn, we also have to check if it can do an En Passant Move. En Passant is a special move that
     * the pawn piece can do. A pawn may capture en passant a horizontally adjacent enemy pawn that has just advanced two squares in one move.[2][3] 
     * The capturing pawn moves to the square that the enemy pawn passed over, as if the enemy pawn had advanced only 
     * one square. Such a capture is permitted only on the turn immediately after the two-square advance; 
     * it cannot be done on a later turn.*/
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate =
                    this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, candidateDestinationCoordinate)));
                }
                else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            }
            else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.INSTANCE.SECOND_ROW.get(this.piecePosition) && this.pieceAlliance.isBlack()) ||
                     (BoardUtils.INSTANCE.SEVENTH_ROW.get(this.piecePosition) && this.pieceAlliance.isWhite()))) {
                final int behindCandidateDestinationCoordinate =
                        this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(candidateDestinationCoordinate).isTileOccupied() &&
                    !board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isWhite()) ||
                      (BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        }
                        else {
                            legalMoves.add(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                           (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(
                                    new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
            else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition) && this.pieceAlliance.isWhite()) ||
                      (BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    if (this.pieceAlliance !=
                            board.getTile(candidateDestinationCoordinate).getPiece().getPieceAllegiance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getTile(candidateDestinationCoordinate).getPiece())));
                        }
                        else {
                            legalMoves.add(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getTile(candidateDestinationCoordinate).getPiece()));
                        }
                    }
                } else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                        (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(
                                    new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }

                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    //Overriding toString so we can return a string type of the piece.
    @Override
    public String toString() {
        return this.pieceType.toString();
    }

    //Returns the instance of the moved piece of the pawn
    @Override
    public Pawn movePiece(final Move move) {
        return PieceUtils.INSTANCE.getMovedPawn(move);
    }

    //In chess, the pawn has the ability to promote by going to the other side of the board. Normally
    //promotes to the queen piece. This gives us a new queen piece based on the pawn promoting.
    public Piece getPromotionPiece() {
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }

}