/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game.daemons;

import chessapi.board.Position;
import chessapi.pieces.Move;
import java.util.Objects;

/**
 *
 * @author freddiewoodruff
 */
public class CacheFactoryKey {
    final Position position;
    final Move move;
    
    CacheFactoryKey(Position position,Move move) {
        this.move = move;
        this.position = position;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CacheFactoryKey other = (CacheFactoryKey) obj;
        if (!Objects.equals(this.position, other.position)) {
            return false;
        }
        if (!Objects.equals(this.move, other.move)) {
            return false;
        }
        return true;
    }
    volatile int hashCode=0;
    @Override
    public int hashCode() {
        if(hashCode==0){
            int hash = 5;
            hash = 71 * hash + Objects.hashCode(this.position);
            hash = 71 * hash + Objects.hashCode(this.move);
            hashCode = hash;
        }
        return hashCode;
    }
}