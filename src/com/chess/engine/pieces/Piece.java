//This is the piece class. It's the most important class as this class will be the parent of the chess pieces.
package com.chess.engine.pieces;

//These import statements pretty much means we want to have these classes or libraries with us so we can use them
//in this class.
import java.util.Collection;

import com.chess.engine.Alliance;

//This is the piece class. It's an abstract class, which means it can only be used if the Piece class
//is the parent of the chess pieces due to this class being general.  Below are abstract methods, which they
//must be added to the classes that inherit the Piece.
public abstract class Piece {

	//Information that a piece contains. It has a piece type, the alliance, the position of the piece,
	//piece being the first move or not, and a cached hash code.
    protected final PieceType pieceType;
    protected final Alliance pieceAlliance;
    protected final int piecePosition;
    protected final boolean isFirstMove;
    protected final int cachedHashCode;

    //Constructor of the Piece. Any of the children will be using the Super() function, which basically
    //initializes the information that's already here in the constructor.
    Piece(final PieceType type,
          final Alliance alliance,
          final int piecePosition,
          final boolean isFirstMove) {
        this.pieceType = type;
        this.piecePosition = piecePosition;
        this.pieceAlliance = alliance;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    //Getters for the pieces. Relates to getting the type, alliance, position, and if it's a first move.
    public PieceType getPieceType() {
        return this.pieceType;
    }

    public Alliance getPieceAllegiance() {
        return this.pieceAlliance;
    }

    public int getPiecePosition() {
        return this.piecePosition;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    //Gets the value of the piece and returns it.
    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }
    public abstract int locationBonus();

    public abstract Piece movePiece(Move move);

    //Legal moves that any piece can use to move to different parts of the tiles
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    //Equals method to check if one piece is the same type of piece of the other. Returns
    //true if equal.
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Piece)) {
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.piecePosition && pieceType == otherPiece.pieceType &&
               pieceAlliance == otherPiece.pieceAlliance && isFirstMove == otherPiece.isFirstMove;
    }

    //Returns the cachedHashCode variable. It's the address of the object in memory
    //As an integer.
    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    //Computes the hash code for the chess result
    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    //An enum to have constants based on the chess pieces. Represented by a number and a string
    //that represents the name of the piece.
    public enum PieceType {

        PAWN(100, "P") {
            @Override
            public boolean isPawn() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        KNIGHT(320, "N") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        BISHOP(330, "B") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        ROOK(500, "R") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        QUEEN(900, "Q") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        KING(10000, "K") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return true;
            }
        };

        private final int value;
        private final String pieceName;

        //Gets the value of the piece
        public int getPieceValue() {
            return this.value;
        }

        //Returns the string format of a piece.
        @Override
        public String toString() {
            return this.pieceName;
        }

        //Initializes the piece type.
        PieceType(final int val, final String pieceName) {
            this.value = val;
            this.pieceName = pieceName;
        }

        //Abstract boolean methods to check if a piece is either of the three.
        public abstract boolean isPawn();
        public abstract boolean isRook();
        public abstract boolean isKing();

    }

}