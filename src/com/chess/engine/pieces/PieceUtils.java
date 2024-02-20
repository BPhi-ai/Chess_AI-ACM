package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

//An enum that contains many constants.
enum PieceUtils {

    INSTANCE;

	//These constants are meant to create all of the possible chess pieces to their respective places.
    private final Table<Alliance, Integer, Queen> ALL_POSSIBLE_QUEENS = PieceUtils.createAllPossibleMovedQueens();
    private final Table<Alliance, Integer, Rook> ALL_POSSIBLE_ROOKS = PieceUtils.createAllPossibleMovedRooks();
    private final Table<Alliance, Integer, Knight> ALL_POSSIBLE_KNIGHTS = PieceUtils.createAllPossibleMovedKnights();
    private final Table<Alliance, Integer, Bishop> ALL_POSSIBLE_BISHOPS = PieceUtils.createAllPossibleMovedBishops();
    private final Table<Alliance, Integer, Pawn> ALL_POSSIBLE_PAWNS = PieceUtils.createAllPossibleMovedPawns();

    //Returns the moved pieces of the pawn, knight, bishop, rook, and queen.
    Pawn getMovedPawn(final Move move) {
        return ALL_POSSIBLE_PAWNS.get(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    Knight getMovedKnight(final Move move) {
        return ALL_POSSIBLE_KNIGHTS.get(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    Bishop getMovedBishop(final Move move) {
        return ALL_POSSIBLE_BISHOPS.get(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    Rook getMovedRook(final Move move) {
        return ALL_POSSIBLE_ROOKS.get(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    Queen getMovedQueen(final Move move) {
        return ALL_POSSIBLE_QUEENS.get(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    //Creates all of the pawn pieces in the table, with the alliance, position, and initializing a pawn piece.
    private static Table<Alliance, Integer, Pawn> createAllPossibleMovedPawns() {
        final ImmutableTable.Builder<Alliance, Integer, Pawn> pieces = ImmutableTable.builder();
        for(final Alliance alliance : Alliance.values()) {
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
                pieces.put(alliance, i, new Pawn(alliance, i, false));
            }
        }
        return pieces.build();
    }
    
    //Similar to Pawns, but create all of the knights.
    private static Table<Alliance, Integer, Knight> createAllPossibleMovedKnights() {
        final ImmutableTable.Builder<Alliance, Integer, Knight> pieces = ImmutableTable.builder();
        for(final Alliance alliance : Alliance.values()) {
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
                pieces.put(alliance, i, new Knight(alliance, i, false));
            }
        }
        return pieces.build();
    }

    //Similar to Knights, but create all of the Bishops.
    private static Table<Alliance, Integer, Bishop> createAllPossibleMovedBishops() {
        final ImmutableTable.Builder<Alliance, Integer, Bishop> pieces = ImmutableTable.builder();
        for(final Alliance alliance : Alliance.values()) {
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
                pieces.put(alliance, i, new Bishop(alliance, i, false));
            }
        }
        return pieces.build();
    }

    //Creates all of the possible rooks
    private static Table<Alliance, Integer, Rook> createAllPossibleMovedRooks() {
        final ImmutableTable.Builder<Alliance, Integer, Rook> pieces = ImmutableTable.builder();
        for(final Alliance alliance : Alliance.values()) {
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
                pieces.put(alliance, i, new Rook(alliance, i, false));
            }
        }
        return pieces.build();
    }

    //Creates all of the possible Queens. All of these women in chess are not thots, but queens.
    private static Table<Alliance, Integer, Queen> createAllPossibleMovedQueens() {
        final ImmutableTable.Builder<Alliance, Integer, Queen> pieces = ImmutableTable.builder();
        for(final Alliance alliance : Alliance.values()) {
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
                pieces.put(alliance, i, new Queen(alliance, i, false));
            }
        }
        return pieces.build();
    }

}