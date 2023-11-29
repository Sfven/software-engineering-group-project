# tic_tac_toe.py

import tkinter as tk
from itertools import cycle
from tkinter import font
from typing import NamedTuple

# Player class
class Player(NamedTuple):
    label: str # X and O identifier
    color: str # color for each player

# Move class
class Move(NamedTuple):
    row: int # row coordinate
    col: int # column coordinate
    label: str = "" # identifies player

BOARD_SIZE = 3 # size of board
DEFAULT_PLAYERS = (
    Player(label="X", color="blue"), # Player X
    Player(label="O", color="green"), # Player O
)

class TicTacToeGame:
    def __init__(self, players=DEFAULT_PLAYERS, board_size=BOARD_SIZE):
        self._players = cycle(players) # tuple of two Player objects: X and O
        self.board_size = board_size # Size of board (classic/default 3x3)
        self.current_player = next(self._players) # current player
        self.winner_combo = [] # combination of cells that defines a winner
        self._current_moves = [] # list of players' moves in a given game
        self._has_winner = False # determines if game has winner or not
        self._winning_combos = [] # a list containing the cell combinations that define a win
        self._setup_board() 

    def _setup_board(self):
        self._current_moves = [
            [Move(row, col) for col in range(self.board_size)]
            for row in range(self.board_size)
        ]
        self._winning_combos = self._get_winning_combos()

    def _get_winning_combos(self):
        rows = [
            [(move.row, move.col) for move in row]
            for row in self._current_moves
        ]
        columns = [list(col) for col in zip(*rows)]
        first_diagonal = [row[i] for i, row in enumerate(rows)]
        second_diagonal = [col[j] for j, col in enumerate(reversed(columns))]
        return rows + columns + [first_diagonal, second_diagonal]
    
    def is_valid_move(self, move):
        """Return True if move is valid, and False otherwise"""
        row, col = move.row, move.col
        move_was_not_played = self._current_moves[row][col].label == "" # checks if current coords still hold empty label
        no_winner = not self._has_winner # checks if game doesn't have winner yet
        return no_winner and move_was_not_played
    
    def process_move(self, move):
        """Process the current move and check if it's a win."""
        row, col = move.row, move.col
        self._current_moves[row][col] = move # add move to list of performed moves
        for combo in self._winning_combos: # loop over winning combos
            # runs through every winning combo and checks for repeats using Set
            # if set contains one X, (and no empty strings), 
            # then we have a row of three Xs, and therefore a win
            results = set( 
                self._current_moves[n][m].label
                for n, m in combo
            )
            # checks length of results (and no. of empty strings) 
            # to see if win is achieved
            is_win = (len(results) == 1) and ("" not in results)
            if is_win:
                self._has_winner = True
                self.winner_combo = combo
                break # win!
    def has_winner(self):
        """Return True if the game has a winner, and False otherwise."""
        return self._has_winner
    
    def is_tied(self):
        """Return True if the game is tied, and False otherwise."""
        no_winner = not self._has_winner # check if game has no winner
        played_moves = ( 
            move.label for row in self._current_moves for move in row
        )
        return no_winner and all(played_moves) # basically checks if board is full
    
    def toggle_player(self):
        """Return a toggled player."""
        self.current_player = next(self._players)


class TicTacToeBoard(tk.Tk):
    def __init__(self, game):
        super().__init__()
        self.title("Tic-Tac-Toe")
        self._cells = {}
        self._game = game
        self._create_board_display()
        self._create_board_grid()

    def _create_board_display(self):
        display_frame = tk.Frame(master=self)
        display_frame.pack(fill=tk.X)
        self.display = tk.Label(
            master=display_frame,
            text="Ready?",
            font=font.Font(size=28, weight="bold"),
        )
        self.display.pack()
    
    def _create_board_grid(self):
        grid_frame = tk.Frame(master=self)
        grid_frame.pack()
        for row in range(self._game.board_size):
            self.rowconfigure(row, weight=1, minsize=50)
            self.columnconfigure(row, weight=1, minsize=75)
            for col in range(self._game.board_size):
                button = tk.Button(
                    master=grid_frame,
                    text="",
                    font=font.Font(size=36, weight="bold"),
                    fg="black",
                    width=3,
                    height=1,
                    highlightbackground="lightblue",
                )
                self._cells[button] = (row, col)
                button.bind("<ButtonPress-1>", self.play)
                button.grid(
                    row=row,
                    column=col,
                    padx=5,
                    pady=5,
                    sticky="nsew"
                )

    def play(self, event):
        """Handle a player's move"""
        clicked_btn = event.widget # button press
        row, col = self._cells[clicked_btn] # assigns button coordinates to row and col
        move = Move(row, col, self._game.current_player.label) # generate move
        if self._game.is_valid_move(move): # check if move is valid
            self._update_button(clicked_btn) # turns button to player label + color
            self._game.process_move(move) # process move
            if self._game.is_tied(): # check if game is tied
                self._update_display(msg="Tied game!", color="red")
                print("tie")
            elif self._game.has_winner(): # check if current player won
                self._highlight_cells()
                msg = f'Player "{self._game.current_player.label}" won!'
                color = self._game.current_player.color
                self._update_display(msg, color)
                print(msg)
            else: # next turn
                self._game.toggle_player()
                msg = f"{self._game.current_player.label}'s turn"
                self._update_display(msg)
    
    def _update_button(self, clicked_btn):
        clicked_btn.config(text=self._game.current_player.label) # sets text of button to current player's label
        clicked_btn.config(fg=self._game.current_player.color) # sets foreground color of button to current player's color

    def _update_display(self, msg, color="black"):
        self.display["text"] = msg # update display text using subscript notation
        self.display["fg"] = color # update display color using subscript notation

    def _highlight_cells(self):
        for button, coordinates in self._cells.items(): # iterates over items in ._cells dictionary
            if coordinates in self._game.winner_combo: # if current coordinates are a winning combo,
                button.config(highlightbackground="red") # set those buttons background color to red


def main():
    """Create the game's board and run main loop"""
    game = TicTacToeGame()
    board = TicTacToeBoard(game)
    board.mainloop()

if __name__ == "__main__":
    main()