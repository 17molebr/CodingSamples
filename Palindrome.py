"""
A simple program to check if a imputed string is a palindrome. Input: String, Output True/False
"""

def palindrome(Word):
    #Fuction to check if a string is a palindrome assuming all letters.
    if Word.lower() == Word[::-1].lower():
        return True
    return False

def has_number(word):
    #Function to check for numbers in the input string.
    return any(letter.isdigit() for letter in word)

def main():
    """Main function sets a variable no_number that indicates if a string has a number to false, then prompts for input and checks
     if it contains a number. If the input contains a number it then re prompts for input, otherwise it sets the
     no_number variable to true and runs the palindrome function.
    """
    no_number = False
    test = input("Enter a word:")
    while not no_number:
        if has_number(test):
            print("Word has number!")
            test = input("Enter a word:")
        else:
            no_number = True
            print(palindrome(test))


if __name__ == '__main__':
    main()
