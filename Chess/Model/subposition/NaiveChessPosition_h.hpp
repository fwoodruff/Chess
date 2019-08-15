//
//  subposition_header.hpp
//  ChessGameA
//
//  Created by Frederick Benjamin Woodruff on 12/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef subposition_header_h
#define subposition_header_h


#include "Bitboard.hpp"
#include "ChessMove.hpp"
#include "PiecesAndSquares.hpp"
#include "VectorStack.hpp"
#include "ChessBoard.hpp"
#include <array>



namespace chs {
    struct Score {
        int value_;
        bool complete_;
    };
    
    
    struct LabelledPieces;
    
    struct NaiveChessPosition : public ChessBoard {
    protected:
        using MoveList = VectorStack<Move>;
    private:
        int pieceHeuristic() const;
        int moveHeuristic() const;
        template<e_moveType moveType> inline int heuristic(int) const;
        template<e_moveType moveType> inline int pmheuristic(int) const;
        using ScoreList = std::array<int,c_maxMoves>;
        static void swap(MoveList& list, ScoreList& scores, const int i, const int j);
        static int partitionMoves(MoveList& list, ScoreList& scores, const int lo, const int hi, const bool highFirst);
        static void sortMoves(MoveList& list, ScoreList& scores, const int lo, const int hi, const bool highFirst);
        template<e_pieceType type> [[nodiscard]]
        static int sliderMobility(Bitboard,const Bitboard&, const Bitboard&);
        template<e_pieceType type> [[nodiscard]]
        static int jumperMobility(Bitboard,const Bitboard&, const Bitboard&);
        template<typename... Args> bool isUnderAttack(e_colour,e_boardSquare, Args...) const;
        template<e_pieceType, e_moveType>
        void populatePieceMoves(MoveList& moveList, e_boardSquare  = a8) const noexcept;
        std::array<Bitboard,2> occupancy;
        Bitboard totalOccupancy;
    protected:
        
        
        bool isUnderAttack(e_colour,e_boardSquare) const;
        template<e_moveType moveType>
        [[nodiscard]] Score alphaBeta(const MoveList&, const int d, int a, int b,
                      std::atomic<bool>&, bool max,int p=0, e_boardSquare = a8) noexcept;
        
        
        [[nodiscard]] Score alphaBetaDebug(const MoveList&, const int d, int a, int b,
                           std::atomic<bool>&, bool max,int p=0, e_boardSquare = a8) noexcept;
        bool hasInsufficientMaterial() const noexcept;
        inline bool operator!=(const NaiveChessPosition& other) const noexcept;
        template <e_moveType = e_moveType::all>
        void getMoveList(MoveList&, bool sorted=true, bool highScoresFirst=true, e_boardSquare = a8) noexcept;
        [[nodiscard]] static NaiveChessPosition makePosition() noexcept;
        
        template<e_moveType> struct ExecuteMove;
        template<e_moveType> struct ConsiderMove;
    public:
        inline bool operator==(const NaiveChessPosition& other) const noexcept;
        virtual ~NaiveChessPosition() = default; // rule of three?
    };
    
    template <e_moveType moveType>
    struct NaiveChessPosition::ExecuteMove {
    protected:
        struct Reverso {
            Bitboard moveBoard_;
            e_pieceType pt_;
        };
        using ReverseBoard = typename std::conditional_t<moveType==e_moveType::quiet,Reverso, NaiveChessPosition>;
        // need to add a case for ordinary taking moves with a 'takeboard'
        NaiveChessPosition& position_;
        ReverseBoard oldPosition_;
    public:
        ExecuteMove(NaiveChessPosition&,const Move&);
    };
    
    template <e_moveType moveType>
    struct NaiveChessPosition::ConsiderMove final : public ExecuteMove<moveType> {
    public:
        ConsiderMove(NaiveChessPosition&,const Move&);
        ~ConsiderMove();
    };
}

#endif /* subposition_header_h */
