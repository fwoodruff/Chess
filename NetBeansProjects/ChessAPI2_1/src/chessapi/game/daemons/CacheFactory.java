/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game.daemons;

import chessapi.board.BoardPosition;
import chessapi.board.Position;
import chessapi.pieces.Move;
import com.google.common.cache.*;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ThreadSafe Values and keys are lazy but immutable. Values are a deterministic function of keys
 * @author freddiewoodruff
 */
public enum CacheFactory {
    BOARDCACHE;
    CacheLoader<CacheFactoryKey, Position> loader = new CacheLoader<CacheFactoryKey, Position>() {
        @Override
        public Position load(CacheFactoryKey k) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    LoadingCache<CacheFactoryKey, Position> BoardCache = CacheBuilder.newBuilder().maximumSize(10000).build(loader);
    
    
    

    //Cache cb = new CacheBuilder().weakKeys().build();
    //private final Map<CacheFactoryKey,Position> BoardCache= new ConcurrentWeakHashMap<>();
    public Position makePosition(Position position, Move move) {
        
        CacheFactoryKey CFK = new CacheFactoryKey(position,move);
        
        return BoardCache.asMap().computeIfAbsent(CFK, k ->
                new BoardPosition(position, move));
    }
}