import os

class HuffmanNode:
    def __init__(self, char, freq):
        self.char = char   # stored as an integer - the ASCII character code value
        self.freq = freq   # the freqency associated with the node
        self.left = None   # Huffman tree (node) to the left
        self.right = None  # Huffman tree (node) to the right

    def set_left(self, node):
        self.left = node

    def set_right(self, node):
        self.right = node

    #def __repr__(self):
     #   return "({!r}, {!r})".format(self.char, self.freq)



def comes_before(a, b):
    """Returns True if tree rooted at node a comes before tree rooted at node b, False otherwise"""
    if a.freq < b.freq:
        return True
    elif a.freq == b.freq:
        if a.char < b.char:
            return True
        return False
    return False


def combine(a, b):
    """Creates and returns a new Huffman node with children a and b, with the "lesser node" on the left
    The new node's frequency value will be the sum of the a and b frequencies
    The new node's char value will be the lesser of the a and b char ASCII values"""
    new = HuffmanNode(char_less(a.char, b.char),a.freq + b.freq)
    if comes_before(a, b):
        new.set_left(a)
        new.set_right(b)
    else:
        new.set_left(b)
        new.set_right(a)
    return(new)


def cnt_freq(filename):
    """Opens a text file with a given file name (passed as a string) and counts the 
    frequency of occurrences of all the characters within that file"""
    frequency = [0]*256
    f = open(filename, "r")
    for line in f:
        for x in line:
            orded = ord(x)
            frequency[orded] += 1
    f.close()
    return frequency




def create_huff_tree(char_freq):
    """Create a Huffman tree for characters with non-zero frequency
    Returns the root node of the Huffman tree"""
    non_sorted = []
    for x in range(len(char_freq)):
        if char_freq[x] == 0:
            pass
        else:
            node = HuffmanNode(x, char_freq[x])
            non_sorted.append(node)
    final = sorted(non_sorted, key=lambda huffmannode: (huffmannode.freq, huffmannode.freq))
    y = build_tree(final)
    return y


def create_code(node):
    """Returns an array (Python list) of Huffman codes. For each character, use the integer ASCII representation 
    as the index into the arrary, with the resulting Huffman code for that character stored at that location"""
    codes = [""]*256
    string = ""
    codes_search(node, string, codes)
    return codes


def codes_search(node, string, codes):
    if node.left is None and node.right is None:
        codes[node.char] = string
    else:
        codes_search(node.right, string + "1", codes)
        codes_search(node.left, string + "0", codes)





def create_header(freqs):
    """Input is the list of frequencies. Creates and returns a header for the output file
    Example: For the frequency list asscoaied with "aaabbbbcc, would return “97 3 98 4 99 2” """
    final = ""
    for x in range(len(freqs)):
        if freqs[x] == 0:
            pass
        else:
            final = final + str(x) + " " + str(freqs[x]) + " "
    return final.rstrip()

def huffman_encode(in_file, out_file):
    """Takes inout file name and output file name as parameters
    Uses the Huffman coding process on the text from the input file and writes encoded text to output file
    Take not of special cases - empty file and file with only one unique character"""
    freqs = cnt_freq(in_file)
    if os.stat(in_file).st_size == 0:
        return
    if len(freqs) == 1:
        g = open(out_file, "w")
        header = create_header(freqs)
        g.write(header)
        g.close()
        return
    node = create_huff_tree(freqs)
    codes = create_code(node)
    header = create_header(freqs)
    f = open(out_file, "w")
    g = open(in_file)
    f.write(header + "\n")
    for line in g:
        for x in line:
            orded = ord(x)
            f.write(codes[orded])
    g.close()
    f.close()



def build_tree(node_list):
    while len(node_list) != 1:
        a = node_list[0]
        b = node_list[1]
        x = combine(a,b)
        node_list.remove(a)
        node_list.remove(b)
        node_list.insert(0, x)
        node_list = sorted(node_list, key=lambda huffmannode: (huffmannode.freq, huffmannode.char))
    return node_list[0]

def char_less(a, b):
    if a < b:
        return a
    else:
        return b


def parse_header(header_string):
    final = [0]*256
    split = header_string.split()
    temp = [split[i:i + 2] for i in range(0, len(split), 2)]
    for pair in temp:
        final[int(pair[0])] = int(pair[1])
    return final


def huffman_decode(encoded_file, decode_file):
    """Main decode function, takes first line in file and generates Huffman Tree
    then uses that tree to generate the decoded file."""
    if os.stat(encoded_file).st_size == 0:
        f = open(decode_file, "w")
        f.close()
        return
    with open(encoded_file, "r") as f:
        first_line = f.readline().strip()
    if len(first_line) == 4:
        file = open(decode_file, "w")
        split = first_line.split()
        for x in range(int(split[1])):
            file.write(chr(int(split[0])))
        file.close()
        return
    freqs = parse_header(first_line)
    node = create_huff_tree(freqs)
    with open(encoded_file, "r") as f:
        next(f)
        codes =[]
        for line in f:
            for value in line:
                codes.append(value)
    chars = get_chars(codes, node)
    file = open(decode_file, "w")
    for letter in chars:
        file.write(letter)
    file.close()





def get_chars(codes, node):
    """Function to generate all the letters based on the code from the file"""
    chars = []
    index = 0
    get_chars_help(chars, codes, node, index)
    return chars


def get_chars_help(chars, codes, node, index):
    """helper function to generate characters"""
    current = node
    for x in codes:
        if x == "0":
            current = current.left
        if x == "1":
            current = current.right
        if current.right is None and current.left is None:
            chars.append(chr(current.char))
            current = node
