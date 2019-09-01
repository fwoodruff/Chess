# Chess

C++ chess AI game with a simple SpriteKit interface.

Features:

* Inheritance (and polymorphism)
* Separation of implementation and interface
* Full bitboard data representations
* Standard library features including std::insert and std::unordered_map
* Enum classes
* Model-Controller-View design
* Headers and #include directives

* Constexpr collisionless hashtables for slider move lookups (hardcoded magics to keep compile times reasonable)
* Bitwise and vector operations with X86 ABM and AVX2 options, encapsulated into member functions
* Concurrency demonstrating usage of std::atomic<>, std::memory_order, std::condition_variable, and std::shared_lock

* Templates and metaprogramming
* Constexpr variadic SFINAE bitboard constructors (to improve expressiveness elsewhere)
* RAII templates for performing and undoing moves
* Some move semantics and explicit usage of rvalue references

* Minimax based move search (evidence of work in progress for static exchange evaluation and quiescence searches).
* Full chess rules including stalemates, castling, promotions to all piece types, en passant, insufficient material, 
no castling through check, rule of three, fifty-move rule, and an undo function.
* C++17 with awareness of opportunities for C++20 improvements (e.g. constexpr std::vector, std::popcount)

Some design choices are to teach myself a language or hardware technique or pattern where a simpler approach would have been more effective.
