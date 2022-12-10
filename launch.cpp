#include <iostream>

int main(int argc, char** argv)
{
    std::cout<<"[Sudoku] Starting...\n";
    system("cd src && javac Sudoku.java && cd ../bin && java Sudoku");
    return 0;
}
