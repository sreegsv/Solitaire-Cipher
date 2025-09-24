//Name: Sreegovind Vineetha, Created for project in COMP250 McGill


package assignment2;

import java.util.Random;

public class Deck {
    public static String[] suitsInOrder = {"clubs", "diamonds", "hearts", "spades"};
    public static Random gen = new Random(10);
    public int numOfCards; // contains the total number of cards in the deck
    public Card head; // contains a pointer to the card on the top of the deck

    /*
     TODO: Initializes a Deck object using the inputs provided
     */
    public Deck(int numOfCardsPerSuit, int numOfSuits) {
        // Check for invalid inputs
        if (numOfCardsPerSuit < 1 || numOfSuits < 1) {
            throw new IllegalArgumentException("Number of cards per suit and number of suits must be non-negative.");
        }
        if (numOfCardsPerSuit > 13 || numOfSuits > 4) {
            throw new IllegalArgumentException("Number of cards per suit must be between 1 and 13, and number of suits must be valid.");
        }

        // Initialize the deck with cards
        this.numOfCards = numOfCardsPerSuit * numOfSuits + 2; 
        Card previousCard = null;

        for (int suitIndex = 0; suitIndex < numOfSuits; suitIndex++) {
            for (int rank = 1; rank <= numOfCardsPerSuit; rank++) {
                PlayingCard card = new PlayingCard(suitsInOrder[suitIndex], rank);

                if (head == null) {
                    head = card; // Set head for the new deck
                } else {
                    previousCard.next = card; // Link previous card to new card
                    card.prev = previousCard; // Link new card back to previous
                }
                previousCard = card; // Move to the new card
            }
        }

        Joker redJoker = new Joker("red");
        Joker blackJoker = new Joker("black");

        if (head == null) {
            // Special case: no playing cards were added
            head = redJoker;
            redJoker.next = blackJoker;
            redJoker.prev = blackJoker;
            blackJoker.next = redJoker;
            blackJoker.prev = redJoker;
        } else {
            previousCard.next = redJoker;
            redJoker.prev = previousCard;

            redJoker.next = blackJoker;
            blackJoker.prev = redJoker;

            // Complete the circular structure
            blackJoker.next = head;
            head.prev = blackJoker;
        }
    }

    /*
     * TODO: Implements a copy constructor for Deck using Card.getCopy().
     * This method runs in O(n), where n is the number of cards in d.
     */
    public Deck(Deck d) {
        this.numOfCards = d.numOfCards;
        if (d.head == null) {
            this.head = null; 
            return;
        }

        Card current = d.head;
        Card previousCard = null; 

        // Copy all cards
        do {
            Card newCard = current.getCopy(); 
            if (head == null) {
                head = newCard; // Set head for the new deck
            } else {
                previousCard.next = newCard; // Link previous card to new card
                newCard.prev = previousCard; // Link new card back to previous
            }
            previousCard = newCard; // Move to the new card
            current = current.next; // Move to the next card
        } while (current != d.head); 

        // Complete the circular linking
        if (previousCard != null) {
            previousCard.next = head; 
            head.prev = previousCard; 
        }


    }

    /*
     * For testing purposes we need a default constructor.
     */
    public Deck() {}

    /*
     * TODO: Adds the specified card at the bottom of the deck. This
     * method runs in $O(1)$.
     */
    public void addCard(Card c) {
        if (c == null) { return; }
        if (numOfCards == 0) {
            head = c;
            c.next = c; 
            c.prev = c; 
        } else if (numOfCards == 1) {
            head.next = c;
            head.prev = c;
            c.next = head;
            c.prev = head;
        } else {
            // Add the card to the bottom of the deck
            Card tail = head.prev; // Get the last card 
            tail.next = c; // Link last card to the new card
            c.prev = tail; // Link new card back to the last card
            c.next = head; // Link new card to the head
            head.prev = c; // Link head back to the new card
        }
        numOfCards++; // Increment the number of cards
    }

    /*
     * TODO: Shuffles the deck using the algorithm described in the pdf.
     * This method runs in O(n) and uses O(n) space, where n is the total
     * number of cards in the deck.
     */
    public void shuffle() {
        // Check if the deck is empty
        if (head == null) {
            return; 
        }
        if (numOfCards == 1) {
            return; // Do nothing if there is only one card
        }

        // Create an array to hold the cards
        Card[] cardsArray = new Card[numOfCards];
        Card current = head;

        
        for (int i = 0; i < numOfCards; i++) {
            cardsArray[i] = current;
            current = current.next;
        }

        // Fisher-Yates shuffle
        for (int i = numOfCards - 1; i > 0; i--) {
            int j = gen.nextInt(i + 1); 
            // Swap cards
            Card temp = cardsArray[i];
            cardsArray[i] = cardsArray[j];
            cardsArray[j] = temp;
        }

        // Rebuild the deck from the shuffled array
        head = cardsArray[0]; 
        for (int i = 0; i < numOfCards; i++) {
            cardsArray[i].next = cardsArray[(i + 1) % numOfCards];
            cardsArray[i].prev = cardsArray[(i - 1 + numOfCards) % numOfCards]; 
        }
    }

    /*
     * TODO: Returns a reference to the joker with the specified color in
     * the deck. This method runs in O(n), where n is the total number of
     * cards in the deck.
     */
    public Joker locateJoker(String color) {
        if (head == null) {
            return null; // Deck is empty
        }
        Card current = head;
        do {
            if (current instanceof Joker && ((Joker) current).getColor().equals(color.toLowerCase())) {
                return (Joker) current;
            }
            current = current.next;
        } while (current != head);
        return null;
    }

    /*
     * TODO: Moved the specified Card, p positions down the deck. You can
     * assume that the input Card does belong to the deck (hence the deck is
     * not empty). This method runs in O(p).
     */
    public void moveCard(Card c, int p) {
        if (p <= 0 || numOfCards <= 1) return; // No need to move if p is 0 or deck has 0-1 cards

        // Remove the card from its current position

        c.prev.next = c.next;
        c.next.prev = c.prev;

        // Find the new position
        Card current = c;
        for (int i = 0; i < p % numOfCards; i++) {
            current = current.next;
        }

        // Insert the card after 'current'
        c.next = current.next;
        c.prev = current;
        current.next.prev = c;
        current.next = c;
    }


    /*
     * TODO: Performs a triple cut on the deck using the two input cards. You
     * can assume that the input cards belong to the deck and the first one is
     * nearest to the top of the deck. This method runs in O(1)
     */
    public void tripleCut(Card firstCard, Card secondCard) {
        Card firstPartStart = head;
        Card firstPartEnd = firstCard.prev;   // Last card before firstCard
        Card secondPartStart = firstCard;     // First card of the middle section
        Card secondPartEnd = secondCard;      // Last card of the middle section
        Card thirdPartStart = secondCard.next; // First card after secondCard
        Card thirdPartEnd = head.prev;        // Last card of the deck (before circular head)

        // Handle the case where the first and second cards are adjacent
        if (firstCard == head && secondCard == thirdPartEnd) {
            return; // No changes needed
        } else if (firstCard == head && secondCard != head.prev) {
            head = secondCard.next;
        } else if (firstCard != head && secondCard == head.prev) {
            head = firstCard;
        } else {
            //System.out.println("Case:4");
            // Set the new head to thirdPartStart (first card after secondCard)
            head = thirdPartStart;

            // Link thirdPartEnd to secondPartStart (last card of the deck to firstCard)
            thirdPartEnd.next = secondPartStart;
            secondPartStart.prev = thirdPartEnd;

            // Link secondPartEnd to firstPartStart (secondCard to head)
            secondPartEnd.next = firstPartStart;
            firstPartStart.prev = secondPartEnd;

            // Link firstPartEnd to thirdPartStart (last card before firstCard to new head)
            firstPartEnd.next = thirdPartStart;
            thirdPartStart.prev = firstPartEnd;
        }
    }




    /*
     * TODO: Performs a count cut on the deck. Note that if the value of the
     * bottom card is equal to a multiple of the number of cards in the deck,
     * then the method should not do anything. This method runs in O(n).
     */
    public void countCut() {
        int bottomValue = head.prev.getValue();

        int cutSize = bottomValue % numOfCards;

        if (cutSize == 0 || numOfCards <= 1) return;

        if (cutSize == numOfCards - 1) { return;
        }

        // Find the cut point
        Card current = head;
        for (int i = 0; i < cutSize - 1; i++) {
            current = current.next;
        }

        // Get the new head and other important cards
        Card newHead = current.next;   // This will be the new head after cut
        Card oldHead = head;           // Store the old head
        Card bottomCard = head.prev;   // The last card in the deck
        Card beforeBottom = bottomCard.prev; // Second-last card

        // Close the gap where the cut was made
        current.next = bottomCard;  // Connect the remaining top segment to bottom card
        bottomCard.prev = current;

        // Move the cut segment before the bottom card
        beforeBottom.next = oldHead;
        oldHead.prev = beforeBottom;

        // The last card of our cut segment should point to the bottom card
        Card lastCutCard = newHead.prev;
        lastCutCard.next = bottomCard;
        bottomCard.prev = lastCutCard;

        // Update head
        head = newHead;
        head.prev = bottomCard;
        head.prev.next = head;
    }


    /*
     * TODO: Returns the card that can be found by looking at the value of the
     * card on the top of the deck, and counting down that many cards. If the
     * card found is a Joker, then the method returns null, otherwise it returns
     * the Card found. This method runs in O(n).
     */
    public Card lookUpCard() {
        int topValue = head.getValue();
        Card current = head;

        for (int i = 0; i < topValue; i++) {
            current = current.next;
        }

        if (current instanceof Joker) {
            return null;
        }
        return current;
    }
    private Card findFirstJoker(Card redJoker, Card blackJoker) {

        Deck.Card current = head;
        do {
            if (current == redJoker) {
                return redJoker;
            }
            if (current == blackJoker) {
                return blackJoker;
            }
            current = current.next;
        } while (current != head);
        return null;
    }
    private void moveJoker () {
        Joker redJoker = locateJoker("red");
        Joker blackJoker = locateJoker("black");
        moveCard(redJoker, 1);
        moveCard(blackJoker, 2);
    }

    /*
     * TODO: Uses the Solitaire algorithm to generate one value for the keystream
     * using this deck. This method runs in O(n).
     */
    public int generateNextKeystreamValue() {
        while (true) {
            //  Move jokers
            Card temp1 = head;
            Joker redJoker = locateJoker("red");
            Joker blackJoker = locateJoker("black");
            moveJoker();
            
            // Perform triple cut
            if (findFirstJoker(redJoker, blackJoker) == redJoker) {
                tripleCut(redJoker, blackJoker);
            } else if (findFirstJoker(redJoker, blackJoker) == blackJoker) {
                tripleCut(blackJoker, redJoker);
            }

            // Perform count cut
            countCut();
            Card loopupCard = lookUpCard();
            if (loopupCard == null) {
                continue;
            }

            // Return the value of the found card
            return lookUpCard().getValue();
        }

    }



    public abstract class Card {
        public Card next;
        public Card prev;

        public abstract Card getCopy();
        public abstract int getValue();

    }

    public class PlayingCard extends Card {
        public String suit;
        public int rank;

        public PlayingCard(String s, int r) {
            this.suit = s.toLowerCase();
            this.rank = r;
        }

        public String toString() {
            String info = "";
            if (this.rank == 1) {
                //info += "Ace";
                info += "A";
            } else if (this.rank > 10) {
                String[] cards = {"Jack", "Queen", "King"};
                //info += cards[this.rank - 11];
                info += cards[this.rank - 11].charAt(0);
            } else {
                info += this.rank;
            }
            //info += " of " + this.suit;
            info = (info + this.suit.charAt(0)).toUpperCase();
            return info;
        }

        public PlayingCard getCopy() {
            return new PlayingCard(this.suit, this.rank);
        }

        public int getValue() {
            int i;
            for (i = 0; i < suitsInOrder.length; i++) {
                if (this.suit.equals(suitsInOrder[i]))
                    break;
            }

            return this.rank + 13*i;
        }

    }


    public class Joker extends Card{
        public String redOrBlack;

        public Joker(String c) {
            if (!c.equalsIgnoreCase("red") && !c.equalsIgnoreCase("black"))
                throw new IllegalArgumentException("Jokers can only be red or black");

            this.redOrBlack = c.toLowerCase();
        }

        public String toString() {
            //return this.redOrBlack + " Joker";
            return (this.redOrBlack.charAt(0) + "J").toUpperCase();
        }

        public Joker getCopy() {
            return new Joker(this.redOrBlack);
        }

        public int getValue() {
            return numOfCards - 1;
        }

        public String getColor() {
            return this.redOrBlack;
        }
    }

    public void printDeck() {
        Card temp = head;
        for (int i = 0; i < numOfCards; i++) {
            System.out.println(temp.prev + "-  Prev - "+temp+"- Next -"+temp.next);
            temp = temp.next;
        }
    }


}

