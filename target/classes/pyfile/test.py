

print("Hello, world!")
print("This is a simple Python code example.")

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.

def fibonacci(n):
    if n <= 0:
        return []
    elif n == 1:
        return [0]
    elif n == 2:
        return [0, 1]
    else:
        fib_sequence = [0, 1]
        for i in range(2, n):
            next_fib = fib_sequence[i - 1] + fib_sequence[i - 2]
            fib_sequence.append(next_fib)
        return fib_sequence



# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')
    n = 9
    print(f"Fibonacci sequence of length {n}: {fibonacci(n)}")

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
