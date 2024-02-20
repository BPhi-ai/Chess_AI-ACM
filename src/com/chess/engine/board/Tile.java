package com.chess.engine.board;

import java.util.HashMap;
import java.util.Map;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

/*This is the Tile class. The reason it has the abstract keyword is mainly because it's a broad concept in the game. With this, it'll be the parent of two classes:
OccupiedTile, and EmptyTile. These two are more specific so they'll be our subclasses, a.k.a the children of Tile as it'll inherit some methods from Tile to be more based 
on whether a tile is empty or if there's a piece in that tile.*/
abstract public class Tile {

    protected final int tileCoordinate;

    private static final Map<Integer, EmptyTile> EMPTY_TILES = createAllPossibleEmptyTiles();

    private static final Table<Integer, Piece, OccupiedTile> OCCUPIED_TILES = createAllPossibleOccupiedTiles();

    private Tile(final int coordinate) {
        this.tileCoordinate = coordinate;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public static Tile createTile(final int coordinate,
                                  final Piece piece) {

        if(piece == null) {
            return EMPTY_TILES.get(coordinate);
        }

        final OccupiedTile cachedOccupiedTile = OCCUPIED_TILES.get(coordinate, piece);

        if(cachedOccupiedTile != null) {
            return cachedOccupiedTile;
        }

        return new OccupiedTile(coordinate, piece);
    }

    public int getTileCoordinate() {
        return this.tileCoordinate;
    }
    /*As the name suggests, this creates all of the possible empty tiles by iterating through the board and putting a new empty tile by having emptyTileMap add the 
    empty tile to its list.*/
    private static Map<Integer,EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }

    /*Similar to the previous method above, but this time we'll add the possible tiles that are occupied by chess pieces. A bunch of different pieces are added*/
    private static Table<Integer, Piece, OccupiedTile> createAllPossibleOccupiedTiles() {

        final Table<Integer, Piece, OccupiedTile> occupiedTileTable = HashBasedTable.create();

        for (final Alliance alliance : Alliance.values()) {
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final Knight whiteKnightFirstMove = new Knight(alliance, i, true);
                final Knight whiteKnightMoved = new Knight(alliance, i, false);
                occupiedTileTable.put(i, whiteKnightFirstMove, new OccupiedTile(i, whiteKnightFirstMove));
                occupiedTileTable.put(i, whiteKnightMoved, new OccupiedTile(i, whiteKnightMoved));
            }

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final Bishop whiteBishopFirstMove = new Bishop(alliance, i, true);
                final Bishop whiteBishopMoved = new Bishop(alliance, i, false);
                occupiedTileTable.put(i, whiteBishopFirstMove, new OccupiedTile(i, whiteBishopFirstMove));
                occupiedTileTable.put(i, whiteBishopMoved, new OccupiedTile(i, whiteBishopMoved));
            }

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final Rook whiteRookFirstMove = new Rook(alliance, i, true);
                final Rook whiteRookMoved = new Rook(alliance, i, false);
                occupiedTileTable.put(i, whiteRookFirstMove, new OccupiedTile(i, whiteRookFirstMove));
                occupiedTileTable.put(i, whiteRookMoved, new OccupiedTile(i, whiteRookMoved));
            }

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final Queen whiteQueenFirstMove = new Queen(alliance, i, true);
                final Queen whiteQueenMoved = new Queen(alliance, i, false);
                occupiedTileTable.put(i, whiteQueenFirstMove, new OccupiedTile(i, whiteQueenFirstMove));
                occupiedTileTable.put(i, whiteQueenMoved, new OccupiedTile(i, whiteQueenMoved));
            }

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final Pawn whitePawnFirstMove = new Pawn(alliance, i, true);
                final Pawn whitePawnMoved = new Pawn(alliance, i, false);
                occupiedTileTable.put(i, whitePawnFirstMove, new OccupiedTile(i, whitePawnFirstMove));
                occupiedTileTable.put(i, whitePawnMoved, new OccupiedTile(i, whitePawnMoved));
            }

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final King whiteKingFirstMove = new King(alliance, i, true, true);
                final King whiteKingMoved = new King(alliance, i, false, false, false, false);
                final King whiteKingMovedCastled = new King(alliance, i, false, true, false, false);
                occupiedTileTable.put(i, whiteKingFirstMove, new OccupiedTile(i, whiteKingFirstMove));
                occupiedTileTable.put(i, whiteKingMoved, new OccupiedTile(i, whiteKingMoved));
                occupiedTileTable.put(i, whiteKingMovedCastled, new OccupiedTile(i, whiteKingMovedCastled));
            }

        }

        return ImmutableTable.copyOf(occupiedTileTable);
    }

    //EmptyTile is a subclass of Tile, which means it's the child of Tile, and it has some methods that Tile has such as isTileOccupied and getPiece method.
    //We also have a unique method in this class called toString, which returns us an empty tile but as a string, which is "-" to represent emptiness.
    public static final class EmptyTile extends Tile {

        private EmptyTile(final int coordinate) {
            super(coordinate);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        public Piece getPiece() {
            return null;
        }

    }

    //OccupiedTile is the subclass of Tile, the 2nd child. Unlike EmptyTile, this one has a new variable, which is called pieceOnTile. As the name explains it, it
    //basically means that there's a piece on a certain tile, which will be based on the coordinate that it'll be placed, and the coordinate that'll be added in this
    //class. Has similar methods like Tile, except the toString method, which gives us a string form of the piece's alliance, the piece, and the piece, but in lower
    //case.
    public static final class OccupiedTile extends Tile {

        private final Piece pieceOnTile;

        private OccupiedTile(final int coordinate,
                             final Piece pieceOnTile) {
            super(coordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString() {
            return this.pieceOnTile.getPieceAllegiance().isWhite() ?
                   this.pieceOnTile.toString() :
                   this.pieceOnTile.toString().toLowerCase();
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return pieceOnTile;
        }
    }

}