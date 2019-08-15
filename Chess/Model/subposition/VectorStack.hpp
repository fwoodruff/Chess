

#ifndef list_stack_hpp
#define list_stack_hpp

#include <cstddef>


template <typename T>
struct VectorStack {
private:
    T& start_;
    T* end_;
    VectorStack(T& start, T* end) : start_(start), end_(end) {}
public:
    /*
     try replacing these pointers with _malloca and placement new.
     This prevents us having to maintain multiple stack pointers per stack frame.
    */
    VectorStack() = delete;
    ~VectorStack() = default;
    VectorStack(const VectorStack& other) = delete;
    VectorStack& operator=(const VectorStack& other) = delete;
    VectorStack& operator=(const VectorStack&& other) {
        if (this != &other) { start_ = other.start; end_ = other.end; }
        return *this;
    };
    VectorStack(VectorStack&& other) : start_(other.start_) , end_(other.end_) { }
    VectorStack(T* base_ptr) : start_(*base_ptr), end_(base_ptr) {}
    
    void clear() {end_ = &start_;}
    void push(const T& obj) { *end_++ = obj; }
    [[nodiscard]] bool empty() const {return (&start_)==end_;}
    size_t size() const { return end_ - &start_; }
    const T& operator[](int idx) const { return *(&start_ + idx); }
    T& operator [](int idx) {
        auto ptr = &start_;
        return *(ptr + idx);
    }
    
    T* begin() const { return &start_; }
    T* end() const { return end_; }

    VectorStack nextVector() const {
        return VectorStack(*end_, end_);
    }
};

#endif /* list_stack_hpp */
