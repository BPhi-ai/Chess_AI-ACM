package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

public final class WhitePlayer extends Player {

    //whiteStandardLegals are the standard legal moves for the white player. For black, it's the other way around.
    public WhitePlayer(final Board board,
                       final Collection<Move> whiteStandardLegals,
                       final Collection<Move> blackStandardLegals) {
        super(board, whiteStandardLegals, blackStandardLegals);
    }

    /*This involves checking about the castling rule. We first check if the player's king has the first move as well as chekcing the position it's in, mainly tile 60, and
    if it's not in check. If not, we go to the other if statement. However, if the first if-statement is true, we check another if statement, based on the white's king.
    We check if tile 61 is not occupied and if tile 62 is not occupied. If that's true, we get tile 63, which would be the tile the rook will be in.
    For the last if-statment inside the first if-statement, we check if there are no attacks on the specified tiles and the opponent. We also check if the tile for the rook
    is a rook type. If all of these if-statement applies, we add a castling move for the white player, mainly for king side. Applies for the other if statement, but with
    queen's side castling. Also, these apply to the black player too. At the end, return an immutable list of the castle moves that the King can make on either white
    or black player, depends on the class.
    */
    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {

        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 60 && !this.isInCheck()) {
            //whites king side castle
            if(!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(63);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if(Player.calculateAttacksOnTile(61, opponentLegals).isEmpty() && Player.calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
                       rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
                    }
                }
            }
            //whites queen side castle
            if(!this.board.getTile(59).isTileOccupied() && !this.board.getTile(58).isTileOccupied() &&
               !this.board.getTile(57).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(56);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if(Player.calculateAttacksOnTile(58, opponentLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(59, opponentLegals).isEmpty() && rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }

    //Returns the opponent of the white player. The black player is the opponent and vice versa for black player.
    @Override
    public BlackPlayer getOpponent() {
        return this.board.blackPlayer();
    }

    //Returns all of the active pieces of the white player. Same for black player.
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    //Returns the alliance of the player. For the white player, it's white. For black, it's black.
    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    //Returns a string format of our alliance, which will return white as a string.
    @Override
    public String toString() {
        return Alliance.WHITE.toString();
    }

}
